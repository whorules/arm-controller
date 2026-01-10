package com.korovko.arm_controller.service;

import com.korovko.arm_controller.client.ApiGatewayClient;
import com.korovko.arm_controller.client.ArmClient;
import com.korovko.arm_controller.model.ChangeRetryRequest;
import com.korovko.arm_controller.model.PrometheusData;
import com.korovko.arm_controller.model.PrometheusQueryResponse;
import com.korovko.arm_controller.model.PrometheusResultItem;
import com.korovko.arm_controller.config.RetryConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DynamicRetryConfigurationService {

  private static final String RETRY_RATE_QUERY = """
      (sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count{httpStatusCode=~"502|503"}[1m])) / sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count[1m]))) * 100
      """;

  private final ArmClient armClient;
  private final ApiGatewayClient apiGatewayClient;
  private final RetryConfigProperties retryConfigProperties;

  private final Map<String, Integer> stableGoodCount = new HashMap<>();
  private final Map<String, Integer> routeToRetryAttempts = new HashMap<>();
  private final Map<String, Instant> routeIdByRetryLastChangedAt = new HashMap<>();


  public DynamicRetryConfigurationService(final ApiGatewayClient apiGatewayClient, ArmClient armClient, RetryConfigProperties retryConfigProperties) {
    this.apiGatewayClient = apiGatewayClient;
    this.armClient = armClient;
    this.retryConfigProperties = retryConfigProperties;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    CompletableFuture
        .delayedExecutor(5, TimeUnit.SECONDS)
        .execute(() -> {
          routeToRetryAttempts.put("customers_route", retryConfigProperties.getMinAttempts());
          routeToRetryAttempts.put("vets_route", retryConfigProperties.getMinAttempts());
          routeToRetryAttempts.put("visits_route", retryConfigProperties.getMinAttempts());
        });
  }

  @Scheduled(fixedRate = 30_000, initialDelay = 6_000)
  public void schedule() {
    log.info("Running a scheduled task to check retries");

    armClient.getPrometheusQuery(RETRY_RATE_QUERY)
        .filter(r -> "success".equals(r.getStatus()))
        .map(PrometheusQueryResponse::getData)
        .map(PrometheusData::getResult)
        .filter(result -> !result.isEmpty())
        .stream()
        .flatMap(List::stream)
        .forEach(this::processRetry);
  }

  private void processRetry(final PrometheusResultItem item) {
    String routeId = item.getMetric().get("routeId");
    List<String> value = item.getValue();
    if (routeId == null || value == null || value.size() < 2) {
      return;
    }

    double errPct = toDouble(value.get(1));
    Integer current = routeToRetryAttempts.get(routeId);
    if (current == null) {
      return;
    }

    double target = retryConfigProperties.getTargetErrorPct();
    double lower = Math.max(0.0, target - retryConfigProperties.getDeadbandPct());
    double upper = target + retryConfigProperties.getDeadbandPct();
    double panic = target * retryConfigProperties.getPanicMultiplier();

    if (errPct > upper) {
      stableGoodCount.put(routeId, 0);

      if (!allowedIncrease(routeId)) {
        return;
      }

      int cap = (errPct >= panic)
          ? retryConfigProperties.getMaxAttempts()
          : Math.min(2, retryConfigProperties.getMaxAttempts());

      int next = Math.min(cap, current + retryConfigProperties.getStepSize());
      if (next != current) {
        apply(routeId, next);
      }
      return;
    }

    if (errPct >= lower) {
      stableGoodCount.put(routeId, 0);
      return;
    }

    int good = stableGoodCount.getOrDefault(routeId, 0) + 1;
    stableGoodCount.put(routeId, good);

    if (good < retryConfigProperties.getDecreaseStablePeriods()) {
      return;
    }
    if (!allowedDecrease(routeId)) {
      return;
    }

    int next = Math.max(retryConfigProperties.getMinAttempts(), current - retryConfigProperties.getStepSize());
    if (next != current) {
      apply(routeId, next);
    }
    stableGoodCount.put(routeId, 0);
  }

  private boolean allowedIncrease(String routeId) {
    Instant last = routeIdByRetryLastChangedAt.getOrDefault(routeId, Instant.EPOCH);
    return Duration.between(last, Instant.now()).toMinutes() >= retryConfigProperties.getMinIncreaseWindowMins();
  }

  private boolean allowedDecrease(String routeId) {
    Instant last = routeIdByRetryLastChangedAt.getOrDefault(routeId, Instant.EPOCH);
    return Duration.between(last, Instant.now()).toMinutes() >= retryConfigProperties.getMinDecreaseWindowMins();
  }

  private void apply(String routeId, int newAttempts) {
    ChangeRetryRequest req = new ChangeRetryRequest(
        newAttempts,
        Duration.ofMillis(retryConfigProperties.getFirstBackoffMs()),
        Duration.ofMillis(retryConfigProperties.getMaxBackoffMs()),
        retryConfigProperties.getFactor(),
        true,
        Set.of(HttpStatus.BAD_GATEWAY.value(), HttpStatus.SERVICE_UNAVAILABLE.value()),
        Set.of(HttpMethod.GET.name())
    );

    apiGatewayClient.changeRetry(routeId, req);
    routeToRetryAttempts.put(routeId, newAttempts);
    routeIdByRetryLastChangedAt.put(routeId, Instant.now());

    log.info("Updated retry for {} -> maxAttempts={}", routeId, newAttempts);
  }

  private double toDouble(final String value) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

}
