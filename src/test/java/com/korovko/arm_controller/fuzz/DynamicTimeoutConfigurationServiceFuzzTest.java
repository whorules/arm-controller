package com.korovko.arm_controller.fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.korovko.arm_controller.client.ApiGatewayClient;
import com.korovko.arm_controller.client.ArmClient;
import com.korovko.arm_controller.config.TimeoutConfigProperties;
import com.korovko.arm_controller.model.PrometheusResultItem;
import com.korovko.arm_controller.service.DynamicTimeoutConfigurationService;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DynamicTimeoutConfigurationServiceFuzzTest {

  @SuppressWarnings("unchecked")
  private static Map<String, Integer> routeToTimeout(DynamicTimeoutConfigurationService s) {
    return (Map<String, Integer>) ReflectionTestUtils.getField(s, "routeToTimeout");
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Instant> lastChanged(DynamicTimeoutConfigurationService s) {
    return (Map<String, Instant>) ReflectionTestUtils.getField(s, "routeIdByLastChangedAt");
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Integer> stableGoodCount(DynamicTimeoutConfigurationService s) {
    return (Map<String, Integer>) ReflectionTestUtils.getField(s, "stableGoodCount");
  }

  private static PrometheusResultItem item(double errPct) {
    PrometheusResultItem it = new PrometheusResultItem();
    Map<String, String> metric = new HashMap<>();
    metric.put("routeId", "route-1");
    it.setMetric(metric);
    it.setValue(List.of("ts", Double.toString(errPct)));
    return it;
  }

  @FuzzTest(maxDuration = "2m")
  void processTimeoutMetric_guided_branch_behavior(FuzzedDataProvider data) {
    ArmClient arm = mock(ArmClient.class);
    ApiGatewayClient api = mock(ApiGatewayClient.class);
    TimeoutConfigProperties props = mock(TimeoutConfigProperties.class);

    int target = data.consumeInt(0, 10);
    double deadband = data.consumeInt(0, 5);
    double panicMult = Math.max(1.0, data.consumeInt(1, 5));
    int min = data.consumeInt(0, 500);
    int max = data.consumeInt(min, min + 5000);
    int step = data.consumeInt(1, 500);
    int stableN = data.consumeInt(1, 5);

    when(props.getTargetErrorRate()).thenReturn(target);
    when(props.getDeadbandPct()).thenReturn(deadband);
    when(props.getPanicMultiplier()).thenReturn(panicMult);
    when(props.getMin()).thenReturn(min);
    when(props.getMax()).thenReturn(max);
    when(props.getStepSize()).thenReturn(step);
    when(props.getDecreaseStablePeriods()).thenReturn(stableN);
    when(props.getMinIncreaseWindowMins()).thenReturn(0);
    when(props.getMinDecreaseWindowMins()).thenReturn(0);

    DynamicTimeoutConfigurationService service = new DynamicTimeoutConfigurationService(arm, api, props);

    Map<String, Integer> rtt = routeToTimeout(service);
    assertNotNull(rtt);

    int current = data.consumeInt(min, max);
    rtt.put("route-1", current);

    Map<String, Instant> lc = lastChanged(service);
    assertNotNull(lc);
    lc.put("route-1", Instant.EPOCH);

    double lower = Math.max(0.0, target - deadband);
    double upper = target + deadband;
    double panic = target * panicMult;

    int scenario = data.consumeInt(0, 3);
    double errPct = switch (scenario) {
      case 0 -> panic + 0.01;
      case 1 -> upper + 0.01;
      case 2 -> lower + (upper - lower) / 2.0;
      default -> Math.max(0.0, lower - 0.01);
    };

    ReflectionTestUtils.invokeMethod(service, "processTimeoutMetric", item(errPct));

    int after1 = rtt.get("route-1");

    if (scenario == 0 || scenario == 1) {
      if (current < max) {
        verify(api, atLeastOnce()).changeTimeout(any());
        assertEquals(Math.min(current + step, max), after1);
        assertEquals(0, stableGoodCount(service).getOrDefault("route-1", -1));
      } else {
        verify(api, never()).changeTimeout(any());
        assertEquals(current, after1);
      }
      return;
    }

    if (scenario == 2) {
      verify(api, never()).changeTimeout(any());
      assertEquals(current, after1);
      assertEquals(0, stableGoodCount(service).getOrDefault("route-1", -1));
      return;
    }

    verify(api, never()).changeTimeout(any());
    assertEquals(current, after1);
    int g1 = stableGoodCount(service).getOrDefault("route-1", 0);
    assertEquals(1, g1);

    for (int i = 1; i < stableN; i++) {
      ReflectionTestUtils.invokeMethod(service, "processTimeoutMetric", item(errPct));
    }

    int afterN = rtt.get("route-1");
    if (current > min) {
      verify(api, times(1)).changeTimeout(any());
      assertEquals(Math.max(current - step, min), afterN);
      assertEquals(0, stableGoodCount(service).getOrDefault("route-1", -1));
    } else {
      verify(api, never()).changeTimeout(any());
      assertEquals(current, afterN);
    }
  }

  @FuzzTest(maxDuration = "2m")
  void processTimeoutMetric_windows_block_changes(FuzzedDataProvider data) {
    ArmClient arm = mock(ArmClient.class);
    ApiGatewayClient api = mock(ApiGatewayClient.class);
    TimeoutConfigProperties props = mock(TimeoutConfigProperties.class);

    when(props.getTargetErrorRate()).thenReturn(2);
    when(props.getDeadbandPct()).thenReturn(0.0);
    when(props.getPanicMultiplier()).thenReturn(2.0);
    when(props.getMin()).thenReturn(100);
    when(props.getMax()).thenReturn(1000);
    when(props.getStepSize()).thenReturn(50);
    when(props.getDecreaseStablePeriods()).thenReturn(1);

    int incWin = data.consumeInt(1, 1_000_000);
    int decWin = data.consumeInt(1, 1_000_000);
    when(props.getMinIncreaseWindowMins()).thenReturn(incWin);
    when(props.getMinDecreaseWindowMins()).thenReturn(decWin);

    DynamicTimeoutConfigurationService service = new DynamicTimeoutConfigurationService(arm, api, props);

    Map<String, Integer> rtt = routeToTimeout(service);
    assertNotNull(rtt);
    rtt.put("route-1", 300);

    Map<String, Instant> lc = lastChanged(service);
    assertNotNull(lc);
    lc.put("route-1", Instant.now());

    double errPct = data.consumeBoolean() ? 9999.0 : 0.0;

    ReflectionTestUtils.invokeMethod(service, "processTimeoutMetric", item(errPct));

    verify(api, never()).changeTimeout(any());
    assertEquals(300, rtt.get("route-1"));
  }

}