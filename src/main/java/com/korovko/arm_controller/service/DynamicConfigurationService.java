package com.korovko.arm_controller.service;

import com.korovko.arm_controller.client.ApiGatewayClient;
import com.korovko.arm_controller.client.ArmClient;
import com.korovko.arm_controller.config.TimeoutConfigProperties;
import com.korovko.arm_controller.model.ChangeRetryRequest;
import com.korovko.arm_controller.model.ChangeTimeoutRequest;
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
//@Service
public class DynamicConfigurationService {

  private static final String TIMEOUT_RATE_QUERY = """
      (sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count{httpStatusCode=~"503"}[1m]))/sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count[1m]))) * 100
      """;
  private static final String RETRY_RATE_QUERY = """
      (sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count{httpStatusCode=~"502|504"}[1m])) / sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count[1m]))) * 100
      """;

  private final ArmClient armClient;
  private final ApiGatewayClient apiGatewayClient;
  private final TimeoutConfigProperties timeoutConfigProperties;
  private final RetryConfigProperties retryConfigProperties;

  private final Map<String, Integer> routeToTimeout = new HashMap<>();
  private final Map<String, Instant> routeIdByTimeoutLastChangedAt = new HashMap<>();
  private final Map<String, Integer> routeToRetryAttempts = new HashMap<>();
  private final Map<String, Instant> routeIdByRetryLastChangedAt = new HashMap<>();

  public DynamicConfigurationService(TimeoutConfigProperties timeoutConfigProperties, final ApiGatewayClient apiGatewayClient, ArmClient armClient, RetryConfigProperties retryConfigProperties) {
    this.timeoutConfigProperties = timeoutConfigProperties;
    this.apiGatewayClient = apiGatewayClient;
    this.armClient = armClient;
    this.retryConfigProperties = retryConfigProperties;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    CompletableFuture
        .delayedExecutor(5, TimeUnit.SECONDS)
        .execute(() -> {
          callRemoteService();
          routeToRetryAttempts.put("customers_route", retryConfigProperties.getMinAttempts());
          routeToRetryAttempts.put("vets_route", retryConfigProperties.getMinAttempts());
          routeToRetryAttempts.put("visits_route", retryConfigProperties.getMinAttempts());
        });
  }

  @Scheduled(fixedRate = 30_000, initialDelay = 6_000)
  public void schedule() {
    log.info("Running a scheduled task to check timeouts");
    if (routeToTimeout.isEmpty()) {
      log.info("No routes were found, skipping scheduled task");
      return;
    }
//    armClient.getPrometheusQuery(TIMEOUT_RATE_QUERY)
//        .filter(response -> "success".equals(response.getStatus()))
//        .map(PrometheusQueryResponse::getData)
//        .map(PrometheusData::getResult)
//        .filter(result -> !result.isEmpty())
//        .stream()
//        .flatMap(List::stream)
//        .forEach(this::processTimeout);

    armClient.getPrometheusQuery(RETRY_RATE_QUERY)
        .filter(r -> "success".equals(r.getStatus()))
        .map(PrometheusQueryResponse::getData)
        .map(PrometheusData::getResult)
        .filter(result -> !result.isEmpty())
        .stream()
        .flatMap(List::stream)
        .forEach(this::processRetry);
  }

  private void processTimeout(final PrometheusResultItem item) {
    log.info("Received prometheus metrics {}", item);
    Map<String, String> metric = item.getMetric();
    String routeId = metric.get("routeId");
    List<String> value = item.getValue();
    if (routeId == null || value == null || value.size() < 2) {
      log.info("Missing routeId in Prometheus result");
      return;
    }
    double errorPercentageStr = toDouble(value.get(1));

    if (errorPercentageStr == 0) {
      log.info("Metric has 0 value");
      return;
    }

    if (errorPercentageStr > timeoutConfigProperties.getAcceptableErrorRate()) {
      log.info("Increasing timeout for routeId {} because current error rate id {}", routeId, errorPercentageStr);
      increaseTimeout(routeId);
    } else {
      log.info("Error rate is acceptable, not increasing timeout for routeId {}", routeId);
    }
  }

  private void processRetry(final PrometheusResultItem item) {
    String routeId = item.getMetric().get("routeId");
    List<String> value = item.getValue();

    if (routeId == null || value == null || value.size() < 2) {
      return;
    }

    double transientPct = toDouble(value.get(1));
    if (!routeToRetryAttempts.containsKey(routeId)) {
      return;
    }
    if (!allowed(routeId)) {
      return;
    }

    int current = routeToRetryAttempts.get(routeId);

    if (transientPct >= retryConfigProperties.getHighTransientErrorPct()) {
      // too many failures => reduce retries to avoid amplification
      int next = Math.max(retryConfigProperties.getMinAttempts(), current - retryConfigProperties.getStepSize());
      if (next != current) {
        apply(routeId, next);
      }
      return;
    }

    if (transientPct >= retryConfigProperties.getLowTransientErrorPct()) {
      // mild transient failures => increase retries to recover
      int next = Math.min(retryConfigProperties.getMaxAttempts(), current + retryConfigProperties.getStepSize());
      if (next != current) {
        apply(routeId, next);
      }
    }
  }

  private void apply(String routeId, int newAttempts) {
    ChangeRetryRequest req = new ChangeRetryRequest(
        newAttempts,
        Duration.ofMillis(retryConfigProperties.getFirstBackoffMs()),
        Duration.ofMillis(retryConfigProperties.getMaxBackoffMs()),
        retryConfigProperties.getFactor(),
        true,
        Set.of(HttpStatus.BAD_GATEWAY.value(), HttpStatus.GATEWAY_TIMEOUT.value()),
        Set.of(HttpMethod.GET.name())
    );

    apiGatewayClient.changeRetry(routeId, req);
    routeToRetryAttempts.put(routeId, newAttempts);
    routeIdByRetryLastChangedAt.put(routeId, Instant.now());

    log.info("Updated retry for {} -> maxAttempts={}", routeId, newAttempts);
  }

  private boolean allowed(String routeId) {
    Instant last = routeIdByRetryLastChangedAt.getOrDefault(routeId, Instant.EPOCH);
    return Duration.between(last, Instant.now()).toMinutes() >= retryConfigProperties.getMinChangeWindowMins();
  }

  private void increaseTimeout(final String routeId) {
    Integer currentTimeout = routeToTimeout.get(routeId);
    if (currentTimeout == null) {
      log.info("Wasn't able to find current latency value for routeId {}", routeId);
      return;
    }
    if (currentTimeout < timeoutConfigProperties.getMax() && isAllowedToChangeLatency(routeId)) {
      int newTimeout = Math.min((currentTimeout + timeoutConfigProperties.getStepSize()), timeoutConfigProperties.getMax());
      apiGatewayClient.changeTimeout(new ChangeTimeoutRequest(routeId, newTimeout));
      routeToTimeout.put(routeId, newTimeout);
      routeIdByTimeoutLastChangedAt.put(routeId, Instant.now());
      log.info("Successfully changed current timeout for routeId {}, new timeout: {}", routeId, newTimeout);
    }
  }

  private boolean isAllowedToChangeLatency(final String routeId) {
    Instant lastChangeTime = routeIdByTimeoutLastChangedAt.getOrDefault(routeId, Instant.EPOCH);
    if (Duration.between(lastChangeTime, Instant.now()).toMinutes() < timeoutConfigProperties.getMinChangeWindowMins()) {
      log.info("Not changing latency because less than 2 minutes passed since the last change");
      return false;
    }
    return true;
  }

  private void callRemoteService() {
    Map<String, Integer> latencies = apiGatewayClient.getLatencies();
    log.info("Latencies: {}", latencies);
    this.routeToTimeout.putAll(latencies);
  }

  private double toDouble(final String value) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

}
