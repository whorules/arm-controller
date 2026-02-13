package com.korovko.arm_controller.fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.korovko.arm_controller.client.ApiGatewayClient;
import com.korovko.arm_controller.client.ArmClient;
import com.korovko.arm_controller.config.RetryConfigProperties;
import com.korovko.arm_controller.model.PrometheusResultItem;
import com.korovko.arm_controller.service.DynamicRetryConfigurationService;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DynamicRetryConfigurationServiceFuzzTest {

  @FuzzTest(maxDuration = "10m")
  void processRetry_never_throws_and_never_updates_unknown_route(FuzzedDataProvider data) {
    ApiGatewayClient apiGatewayClient = mock(ApiGatewayClient.class);
    ArmClient armClient = mock(ArmClient.class);
    RetryConfigProperties props = mock(RetryConfigProperties.class);

    DynamicRetryConfigurationService service = newService(apiGatewayClient, armClient, props);

    Map<String, Integer> attempts = routeToRetryAttempts(service);
    assertNotNull(attempts);
    attempts.put("route-1", 2);

    String routeId = data.consumeBoolean() ? null : (data.consumeBoolean() ? "route-1" : "unknown");
    int valueShape = data.consumeInt(0, 4);

    List<String> value = switch (valueShape) {
      case 0 -> null;
      case 1 -> List.of();
      case 2 -> List.of("ts-only");
      case 3 -> List.of("ts", fuzzErrPctString(data));
      default -> List.of("ts", fuzzErrPctString(data), data.consumeString(10));
    };

    PrometheusResultItem item = itemWithRouteAndValue(routeId, value);

    assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service, "processRetry", item));

    boolean eligibleToTouch =
        "route-1".equals(routeId) && value != null && value.size() >= 2;

    if (!eligibleToTouch) {
      verify(apiGatewayClient, never()).changeRetry(anyString(), any());
    } else {
      ArgumentCaptor<String> routeCaptor = ArgumentCaptor.forClass(String.class);
      verify(apiGatewayClient, atMost(1)).changeRetry(routeCaptor.capture(), any());
      assertTrue(routeCaptor.getAllValues().stream().allMatch("route-1"::equals));
    }
  }

  @FuzzTest(maxDuration = "10m")
  void processRetry_sequence_keeps_attempts_within_bounds(FuzzedDataProvider data) {
    ApiGatewayClient apiGatewayClient = mock(ApiGatewayClient.class);
    ArmClient armClient = mock(ArmClient.class);
    RetryConfigProperties props = mock(RetryConfigProperties.class);

    int minAttempts = data.consumeInt(0, 3);
    int maxAttempts = data.consumeInt(minAttempts, minAttempts + 8);
    int step = data.consumeInt(1, 3);
    int decreasePeriods = data.consumeInt(1, 5);

    when(props.getTargetErrorPct()).thenReturn((double) data.consumeInt(0, 10));
    when(props.getDeadbandPct()).thenReturn((double) data.consumeInt(0, 5));
    when(props.getPanicMultiplier()).thenReturn(Math.max(1.0, data.consumeInt(1, 5)));
    when(props.getMaxAttempts()).thenReturn(maxAttempts);
    when(props.getMinAttempts()).thenReturn(minAttempts);
    when(props.getStepSize()).thenReturn(step);
    when(props.getDecreaseStablePeriods()).thenReturn(decreasePeriods);
    when(props.getMinIncreaseWindowMins()).thenReturn(0);
    when(props.getMinDecreaseWindowMins()).thenReturn(0);
    when(props.getFirstBackoffMs()).thenReturn(100);
    when(props.getMaxBackoffMs()).thenReturn(1000);
    when(props.getFactor()).thenReturn(2);

    DynamicRetryConfigurationService service =
        new DynamicRetryConfigurationService(apiGatewayClient, armClient, props);

    Map<String, Integer> attempts = routeToRetryAttempts(service);
    assertNotNull(attempts);

    int startAttempts = data.consumeInt(minAttempts, maxAttempts);
    attempts.put("route-1", startAttempts);

    int steps = data.consumeInt(1, 50);
    for (int i = 0; i < steps; i++) {
      String errPct = fuzzErrPctString(data);
      PrometheusResultItem item = itemWithRouteAndValue("route-1", List.of("ts", errPct));

      assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service, "processRetry", item));

      int current = attempts.get("route-1");
      assertTrue(current >= minAttempts, "attempts dropped below min");
      assertTrue(current <= maxAttempts, "attempts exceeded max");

      Map<String, Integer> good = stableGoodCount(service);
      if (good != null) {
        int g = good.getOrDefault("route-1", 0);
        assertTrue(g >= 0, "stableGoodCount went negative");
        assertTrue(g <= decreasePeriods, "stableGoodCount grew unexpectedly large");
      }
    }
  }

  @FuzzTest(maxDuration = "10m")
  void processRetry_respects_min_windows_no_updates_when_blocked(FuzzedDataProvider data) {
    ApiGatewayClient apiGatewayClient = mock(ApiGatewayClient.class);
    ArmClient armClient = mock(ArmClient.class);
    RetryConfigProperties props = mock(RetryConfigProperties.class);

    when(props.getTargetErrorPct()).thenReturn(2.0);
    when(props.getDeadbandPct()).thenReturn(0.0);
    when(props.getPanicMultiplier()).thenReturn(2.0);
    when(props.getMaxAttempts()).thenReturn(5);
    when(props.getMinAttempts()).thenReturn(1);
    when(props.getStepSize()).thenReturn(1);
    when(props.getDecreaseStablePeriods()).thenReturn(1);

    when(props.getMinIncreaseWindowMins()).thenReturn(1_000_000);
    when(props.getMinDecreaseWindowMins()).thenReturn(1_000_000);

    when(props.getFirstBackoffMs()).thenReturn(100);
    when(props.getMaxBackoffMs()).thenReturn(1000);
    when(props.getFactor()).thenReturn(2);

    DynamicRetryConfigurationService service =
        new DynamicRetryConfigurationService(apiGatewayClient, armClient, props);

    Map<String, Integer> attempts = routeToRetryAttempts(service);
    assertNotNull(attempts);
    attempts.put("route-1", 3);

    Map<String, Instant> last = lastChanged(service);
    assertNotNull(last);
    last.put("route-1", Instant.now());

    String errPct = data.consumeBoolean() ? "9999" : "0";
    PrometheusResultItem item = itemWithRouteAndValue("route-1", List.of("ts", errPct));

    assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service, "processRetry", item));
    verify(apiGatewayClient, never()).changeRetry(anyString(), any());
  }

  private static DynamicRetryConfigurationService newService(ApiGatewayClient apiGatewayClient,
                                                             ArmClient armClient,
                                                             RetryConfigProperties props) {
    when(props.getTargetErrorPct()).thenReturn(2.0);
    when(props.getDeadbandPct()).thenReturn(1.0);
    when(props.getPanicMultiplier()).thenReturn(3.0);
    when(props.getMaxAttempts()).thenReturn(5);
    when(props.getMinAttempts()).thenReturn(1);
    when(props.getStepSize()).thenReturn(1);
    when(props.getDecreaseStablePeriods()).thenReturn(2);
    when(props.getMinIncreaseWindowMins()).thenReturn(0);
    when(props.getMinDecreaseWindowMins()).thenReturn(0);
    when(props.getFirstBackoffMs()).thenReturn(100);
    when(props.getMaxBackoffMs()).thenReturn(1000);
    when(props.getFactor()).thenReturn(2);

    return new DynamicRetryConfigurationService(apiGatewayClient, armClient, props);
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Integer> routeToRetryAttempts(DynamicRetryConfigurationService service) {
    return (Map<String, Integer>) ReflectionTestUtils.getField(service, "routeToRetryAttempts");
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Integer> stableGoodCount(DynamicRetryConfigurationService service) {
    return (Map<String, Integer>) ReflectionTestUtils.getField(service, "stableGoodCount");
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Instant> lastChanged(DynamicRetryConfigurationService service) {
    return (Map<String, Instant>) ReflectionTestUtils.getField(service, "routeIdByRetryLastChangedAt");
  }

  private static PrometheusResultItem itemWithRouteAndValue(String routeId, List<String> value) {
    PrometheusResultItem item = new PrometheusResultItem();
    Map<String, String> metric = new HashMap<>();
    if (routeId != null) metric.put("routeId", routeId);
    item.setMetric(metric);
    item.setValue(value);
    return item;
  }

  private static String fuzzErrPctString(FuzzedDataProvider data) {
    return switch (data.consumeInt(0, 7)) {
      case 0 -> "NaN";
      case 1 -> "Infinity";
      case 2 -> "-Infinity";
      case 3 -> "1e309";
      case 4 -> "-1e309";
      case 5 -> String.valueOf(data.consumeInt(-1000, 10000));
      default -> data.consumeString(30);
    };
  }

}
