package com.korovko.arm_controller.service;

import com.korovko.arm_controller.client.ApiGatewayClient;
import com.korovko.arm_controller.client.ArmClient;
import com.korovko.arm_controller.config.BulkheadConfigProperties;
import com.korovko.arm_controller.model.ChangeBulkheadRequest;
import com.korovko.arm_controller.model.PrometheusData;
import com.korovko.arm_controller.model.PrometheusQueryResponse;
import com.korovko.arm_controller.model.PrometheusResultItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
//@Service
public class DynamicBulkheadConfigurationService {

  private static final String TIMEOUT_RATE_504_PCT = """
       (sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count{httpStatusCode="504"}[1m]))
        /
        sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count[1m])))
       * 100
      """;

  private static final String GW_P99_2XX_SECONDS = """
      histogram_quantile(0.99,
        sum by (le, routeId) (
          rate(spring_cloud_gateway_requests_seconds_bucket{httpStatusCode=~"2.."}[1m])
        )
      )
      """;

  private static final String REJECT_429_PCT = """
      100 *
      (sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count{httpStatusCode="429"}[1m]))
       /
       sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count[1m])))
      """;

  private final ArmClient armClient;
  private final ApiGatewayClient apiGatewayClient;
  private final BulkheadConfigProperties props;

  private final Map<String, Integer> routeToConcurrent = new HashMap<>();
  private final Map<String, Instant> lastChangedAt = new HashMap<>();

  private final Map<String, Double> routeTo429Pct = new HashMap<>();
  private final Map<String, Double> routeToTimeout504Pct = new HashMap<>();
  private final Map<String, Double> routeToP99Ms = new HashMap<>();

  public DynamicBulkheadConfigurationService(ArmClient armClient,
                                             ApiGatewayClient apiGatewayClient,
                                             BulkheadConfigProperties props) {
    this.armClient = armClient;
    this.apiGatewayClient = apiGatewayClient;
    this.props = props;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onReady() {
    CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS)
        .execute(this::initDefaults);
  }

  private void initDefaults() {
    Map<String, ChangeBulkheadRequest> bulkheadProps = apiGatewayClient.getBulkheadProps();
    bulkheadProps.forEach((route, bulk) -> routeToConcurrent.put(route, bulk.maxConcurrentCalls()));
    log.info("Initialized bulkhead concurrency: {}", routeToConcurrent);
  }

  @Scheduled(fixedRate = 30_000, initialDelay = 6_000)
  public void schedule() {
    if (routeToConcurrent.isEmpty()) {
      return;
    }

    routeToTimeout504Pct.clear();
    routeToP99Ms.clear();
    routeTo429Pct.clear();

    collectTimeoutRate();
    collectP99();
    collect429Pct();

    for (String routeId : routeToConcurrent.keySet()) {
      Double timeout504Pct = routeToTimeout504Pct.get(routeId);
      Double p99ms = routeToP99Ms.get(routeId);
      Double pct429 = routeTo429Pct.getOrDefault(routeId, 0.0);

      if (timeout504Pct == null || p99ms == null) {
        continue;
      }

      if (!isAllowed(routeId)) {
        log.info("Skipping bulkhead route {} as the change is not allowed", routeId);
        continue;
      }

      int current = routeToConcurrent.get(routeId);
      Decision decision = decide(timeout504Pct, pct429);

      if (decision == Decision.DECREASE) {
        int next = Math.max(props.getMinConcurrent(), current - props.getStepSize());
        if (next != current) {
          log.info("Updating bulkhead decision DECREASE for route {} to {}", routeId, next);
          apply(routeId, next, timeout504Pct, p99ms);
        }
      } else if (decision == Decision.INCREASE) {
        int next = Math.min(props.getMaxConcurrent(), current + props.getStepSize());
        if (next != current) {
          log.info("Updating bulkhead decision INCREASE for route {} to {}", routeId, next);
          apply(routeId, next, timeout504Pct, p99ms);
        }
      }
    }
  }

  private void collect429Pct() {
    armClient.getPrometheusQuery(REJECT_429_PCT)
        .filter(r -> "success".equals(r.getStatus()))
        .map(PrometheusQueryResponse::getData)
        .map(PrometheusData::getResult)
        .filter(list -> !list.isEmpty())
        .stream()
        .flatMap(List::stream)
        .forEach(item -> {
          String routeId = item.getMetric().get("routeId");
          Double val = extractValue(item);
          if (routeId != null && val != null) {
            routeTo429Pct.put(routeId, val);
          }
        });
  }

  private void collectTimeoutRate() {
    armClient.getPrometheusQuery(TIMEOUT_RATE_504_PCT)
        .filter(r -> "success".equals(r.getStatus()))
        .map(PrometheusQueryResponse::getData)
        .map(PrometheusData::getResult)
        .filter(list -> !list.isEmpty())
        .stream()
        .flatMap(List::stream)
        .forEach(item -> {
          String routeId = item.getMetric().get("routeId");
          Double val = extractValue(item);
          if (routeId != null && val != null) {
            routeToTimeout504Pct.put(routeId, val);
          }
        });
  }

  private void collectP99() {
    armClient.getPrometheusQuery(GW_P99_2XX_SECONDS)
        .filter(r -> "success".equals(r.getStatus()))
        .map(PrometheusQueryResponse::getData)
        .map(PrometheusData::getResult)
        .filter(list -> !list.isEmpty())
        .stream()
        .flatMap(List::stream)
        .forEach(item -> {
          String routeId = item.getMetric().get("routeId");
          Double seconds = extractValue(item);
          if (routeId != null && seconds != null) {
            routeToP99Ms.put(routeId, seconds * 1000.0);
          }
        });
  }

  private enum Decision {INCREASE, DECREASE, NOOP}

  private Decision decide(double timeout504Pct, double pct429) {

    // Safety: if shedding is getting high, immediately increase concurrency
    if (pct429 >= props.getHardMax429()) {
      return Decision.INCREASE;
    }

    // Main control loop: keep 429 inside [low, high]
    if (pct429 > props.getTarget429High()) {
      return Decision.INCREASE;   // too much rejection -> allow more concurrency
    }

    if (pct429 < props.getTarget429Low()) {
      // too little rejection -> you can try decreasing a bit to protect system
      // (this is what keeps you near the knee)
      return Decision.DECREASE;
    }

    return Decision.NOOP;
  }

  private void apply(String routeId, int newConcurrent, double timeoutPct, double p99ms) {
    apiGatewayClient.changeBulkhead(routeId, new ChangeBulkheadRequest(newConcurrent, props.getMaxWaitMs()));
    routeToConcurrent.put(routeId, newConcurrent);
    lastChangedAt.put(routeId, Instant.now());

    log.info("Bulkhead updated for {} => maxConcurrentCalls={} (timeout504Pct={}%, p99={}ms)",
        routeId, newConcurrent, round2(timeoutPct), round2(p99ms));
  }

  private boolean isAllowed(String routeId) {
    Instant last = lastChangedAt.getOrDefault(routeId, Instant.EPOCH);
    return Duration.between(last, Instant.now()).toMinutes() >= props.getMinChangeWindowMins();
  }

  private Double extractValue(PrometheusResultItem item) {
    List<String> v = item.getValue();
    if (v == null || v.size() < 2) {
      return null;
    }
    try {
      return Double.parseDouble(v.get(1));
    } catch (Exception e) {
      return null;
    }
  }

  private double round2(double x) {
    return Math.round(x * 100.0) / 100.0;
  }

}
