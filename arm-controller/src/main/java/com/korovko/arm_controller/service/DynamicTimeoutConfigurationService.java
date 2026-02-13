package com.korovko.arm_controller.service;

import com.korovko.arm_controller.client.ApiGatewayClient;
import com.korovko.arm_controller.client.ArmClient;
import com.korovko.arm_controller.config.TimeoutConfigProperties;
import com.korovko.arm_controller.model.ChangeTimeoutRequest;
import com.korovko.arm_controller.model.PrometheusData;
import com.korovko.arm_controller.model.PrometheusQueryResponse;
import com.korovko.arm_controller.model.PrometheusResultItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DynamicTimeoutConfigurationService {

  private static final String TIMEOUT_RATE_QUERY = """
      100 *
      (sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count{httpStatusCode="504"}[1m]))
       /
       sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count[1m])))
      """;

  private final ArmClient armClient;
  private final ApiGatewayClient apiGatewayClient;
  private final TimeoutConfigProperties props;

  private final Map<String, Integer> routeToTimeout = new HashMap<>();
  private final Map<String, Instant> routeIdByLastChangedAt = new HashMap<>();
  private final Map<String, Integer> stableGoodCount = new HashMap<>();

  public DynamicTimeoutConfigurationService(ArmClient armClient,
                                            ApiGatewayClient apiGatewayClient,
                                            TimeoutConfigProperties props) {
    this.armClient = armClient;
    this.apiGatewayClient = apiGatewayClient;
    this.props = props;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS)
        .execute(this::initializeTimeouts);
  }

  @Scheduled(fixedRate = 30_000, initialDelay = 5_000)
  public void schedule() {
    if (routeToTimeout.isEmpty()) {
      return;
    }

    armClient.getPrometheusQuery(TIMEOUT_RATE_QUERY)
        .filter(r -> "success".equals(r.getStatus()))
        .map(PrometheusQueryResponse::getData)
        .map(PrometheusData::getResult)
        .filter(list -> !list.isEmpty())
        .stream()
        .flatMap(List::stream)
        .forEach(this::processTimeoutMetric);
  }

  private void processTimeoutMetric(final PrometheusResultItem item) {
    String routeId = item.getMetric().get("routeId");
    Double errorPct = extractValue(item);

    if (routeId == null || errorPct == null) return;

    Integer currentTimeout = routeToTimeout.get(routeId);
    if (currentTimeout == null) return;

    double target = props.getTargetErrorRate();
    double lower = Math.max(0.0, target - props.getDeadbandPct());
    double upper = target + props.getDeadbandPct();
    double panic = target * props.getPanicMultiplier();

    log.info("route={} timeout={}ms timeoutErr={}%, band=[{}..{}], panic>={}",
        routeId, currentTimeout, round2(errorPct), round2(lower), round2(upper), round2(panic));

    // 1) Panic: fast increase
    if (errorPct >= panic) {
      stableGoodCount.put(routeId, 0);
      if (allowedToIncrease(routeId)) {
        increaseTimeout(routeId, currentTimeout, errorPct);
      }
      return;
    }

    // 2) Above band: increase
    if (errorPct > upper) {
      stableGoodCount.put(routeId, 0);
      if (allowedToIncrease(routeId)) {
        increaseTimeout(routeId, currentTimeout, errorPct);
      }
      return;
    }

    // 3) In band: noop
    if (errorPct >= lower) {
      stableGoodCount.put(routeId, 0);
      return;
    }

    // 4) Below band: candidate decrease after N stable periods + slow cooldown
    int good = stableGoodCount.getOrDefault(routeId, 0) + 1;
    stableGoodCount.put(routeId, good);

    if (good >= props.getDecreaseStablePeriods() && allowedToDecrease(routeId)) {
      stableGoodCount.put(routeId, 0);
      decreaseTimeout(routeId, currentTimeout, errorPct);
    }
  }

  private void increaseTimeout(String routeId, int currentTimeout, double errPct) {
    if (currentTimeout >= props.getMax()) {
      return;
    }

    int next = Math.min(currentTimeout + props.getStepSize(), props.getMax());
    if (next == currentTimeout) {
      return;
    }

    apiGatewayClient.changeTimeout(new ChangeTimeoutRequest(routeId, next));
    routeToTimeout.put(routeId, next);
    routeIdByLastChangedAt.put(routeId, Instant.now());

    log.info("INCREASE route={} {}ms -> {}ms (timeoutErr={}%)",
        routeId, currentTimeout, next, round2(errPct));
  }

  private void decreaseTimeout(String routeId, int currentTimeout, double errPct) {
    if (currentTimeout <= props.getMin()) {
      return;
    }

    int next = Math.max(currentTimeout - props.getStepSize(), props.getMin());
    if (next == currentTimeout) {
      return;
    }

    apiGatewayClient.changeTimeout(new ChangeTimeoutRequest(routeId, next));
    routeToTimeout.put(routeId, next);
    routeIdByLastChangedAt.put(routeId, Instant.now());

    log.info("DECREASE route={} {}ms -> {}ms (timeoutErr={}%)", routeId, currentTimeout, next, round2(errPct));
  }

  private boolean allowedToIncrease(String routeId) {
    Instant last = routeIdByLastChangedAt.getOrDefault(routeId, Instant.EPOCH);
    return Duration.between(last, Instant.now()).toMinutes() >= props.getMinIncreaseWindowMins();
  }

  private boolean allowedToDecrease(String routeId) {
    Instant last = routeIdByLastChangedAt.getOrDefault(routeId, Instant.EPOCH);
    return Duration.between(last, Instant.now()).toMinutes() >= props.getMinDecreaseWindowMins();
  }

  private void initializeTimeouts() {
    Map<String, Integer> latencies = apiGatewayClient.getLatencies();
    routeToTimeout.putAll(latencies);

    // Seed so both increase and decrease are allowed initially
    long seedMinutes = Math.max(props.getMinDecreaseWindowMins(), props.getMinIncreaseWindowMins()) + 1;
    Instant past = Instant.now().minus(Duration.ofMinutes(seedMinutes));

    for (String routeId : routeToTimeout.keySet()) {
      routeIdByLastChangedAt.put(routeId, past);
      stableGoodCount.put(routeId, 0);
    }

    log.info("Initialized timeouts: {}", routeToTimeout);
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
