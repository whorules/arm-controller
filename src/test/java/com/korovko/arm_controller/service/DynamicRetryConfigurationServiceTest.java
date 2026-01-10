package com.korovko.arm_controller.service;

import com.korovko.arm_controller.client.ApiGatewayClient;
import com.korovko.arm_controller.client.ArmClient;
import com.korovko.arm_controller.config.RetryConfigProperties;
import com.korovko.arm_controller.model.ChangeRetryRequest;
import com.korovko.arm_controller.model.PrometheusData;
import com.korovko.arm_controller.model.PrometheusQueryResponse;
import com.korovko.arm_controller.model.PrometheusResultItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamicRetryConfigurationServiceTest {

  private ArmClient armClient;
  private ApiGatewayClient apiGatewayClient;
  private RetryConfigProperties props;
  private DynamicRetryConfigurationService service;

  @BeforeEach
  void setUp() {
    armClient = mock(ArmClient.class);
    apiGatewayClient = mock(ApiGatewayClient.class);
    props = mock(RetryConfigProperties.class);
    service = new DynamicRetryConfigurationService(apiGatewayClient, armClient, props);
  }

  @Test
  void schedule_doesNothing_whenPrometheusOptionalEmpty() {
    when(armClient.getPrometheusQuery(anyString())).thenReturn(Optional.empty());

    service.schedule();

    verifyNoInteractions(apiGatewayClient);
  }

  @Test
  void schedule_doesNothing_whenStatusNotSuccess() {
    PrometheusQueryResponse resp = mock(PrometheusQueryResponse.class);
    when(resp.getStatus()).thenReturn("error");
    when(armClient.getPrometheusQuery(anyString())).thenReturn(Optional.of(resp));

    service.schedule();

    verifyNoInteractions(apiGatewayClient);
  }

  @Test
  void schedule_doesNothing_whenResultEmpty() {
    PrometheusQueryResponse resp = mock(PrometheusQueryResponse.class);
    PrometheusData data = mock(PrometheusData.class);

    when(resp.getStatus()).thenReturn("success");
    when(resp.getData()).thenReturn(data);
    when(data.getResult()).thenReturn(List.of());
    when(armClient.getPrometheusQuery(anyString())).thenReturn(Optional.of(resp));

    service.schedule();

    verifyNoInteractions(apiGatewayClient);
  }

  @Test
  void schedule_ignoresItem_whenRouteIdNull() {
    seedRouteAttempts(Map.of("customers_route", 1));

    PrometheusResultItem item = mock(PrometheusResultItem.class);
    when(item.getMetric()).thenReturn(Map.of());
    when(item.getValue()).thenReturn(List.of("0", "10.0"));

    PrometheusQueryResponse resp = mock(PrometheusQueryResponse.class);
    PrometheusData data = mock(PrometheusData.class);

    when(resp.getStatus()).thenReturn("success");
    when(resp.getData()).thenReturn(data);
    when(data.getResult()).thenReturn(List.of(item));
    when(armClient.getPrometheusQuery(anyString())).thenReturn(Optional.of(resp));

    service.schedule();

    verifyNoInteractions(apiGatewayClient);
  }

  @Test
  void schedule_ignoresItem_whenValueMissingOrTooShort() {
    seedRouteAttempts(Map.of("customers_route", 1));

    PrometheusResultItem item = mock(PrometheusResultItem.class);
    when(item.getMetric()).thenReturn(Map.of("routeId", "customers_route"));
    when(item.getValue()).thenReturn(List.of("0"));

    PrometheusQueryResponse resp = mock(PrometheusQueryResponse.class);
    PrometheusData data = mock(PrometheusData.class);

    when(resp.getStatus()).thenReturn("success");
    when(resp.getData()).thenReturn(data);
    when(data.getResult()).thenReturn(List.of(item));
    when(armClient.getPrometheusQuery(anyString())).thenReturn(Optional.of(resp));

    service.schedule();

    verifyNoInteractions(apiGatewayClient);
  }

  @Test
  void schedule_doesNothing_whenRouteNotTracked() {
    PrometheusResultItem item = mock(PrometheusResultItem.class);
    when(item.getMetric()).thenReturn(Map.of("routeId", "unknown_route"));
    when(item.getValue()).thenReturn(List.of("0", "10.0"));

    PrometheusQueryResponse resp = mock(PrometheusQueryResponse.class);
    PrometheusData data = mock(PrometheusData.class);

    when(resp.getStatus()).thenReturn("success");
    when(resp.getData()).thenReturn(data);
    when(data.getResult()).thenReturn(List.of(item));
    when(armClient.getPrometheusQuery(anyString())).thenReturn(Optional.of(resp));

    service.schedule();

    verifyNoInteractions(apiGatewayClient);
  }

  @Test
  void schedule_increasesAttempts_fastPath_whenErrPctAboveUpper_andIncreaseAllowed() {
    seedRouteAttempts(Map.of("customers_route", 1));
    seedLastChangedAt("customers_route", Instant.EPOCH);

    PrometheusResultItem item = mock(PrometheusResultItem.class);
    when(item.getMetric()).thenReturn(Map.of("routeId", "customers_route"));
    when(item.getValue()).thenReturn(List.of("0", "7.1"));

    PrometheusQueryResponse resp = mock(PrometheusQueryResponse.class);
    PrometheusData data = mock(PrometheusData.class);

    when(resp.getStatus()).thenReturn("success");
    when(resp.getData()).thenReturn(data);
    when(data.getResult()).thenReturn(List.of(item));
    when(armClient.getPrometheusQuery(anyString())).thenReturn(Optional.of(resp));

    when(props.getTargetErrorPct()).thenReturn(5.0);
    when(props.getDeadbandPct()).thenReturn(1.0);
    when(props.getPanicMultiplier()).thenReturn(3.0);
    when(props.getMinIncreaseWindowMins()).thenReturn(0);
    when(props.getStepSize()).thenReturn(1);
    when(props.getMaxAttempts()).thenReturn(5);

    when(props.getFirstBackoffMs()).thenReturn(100);
    when(props.getMaxBackoffMs()).thenReturn(1000);
    when(props.getFactor()).thenReturn(2);

    service.schedule();

    ArgumentCaptor<ChangeRetryRequest> captor = ArgumentCaptor.forClass(ChangeRetryRequest.class);
    verify(apiGatewayClient).changeRetry(eq("customers_route"), captor.capture());
    assertThat(readAttempts(captor.getValue())).isEqualTo(2);
    assertThat(readRetryableStatusCodes(captor.getValue())).containsExactlyInAnyOrder(502, 503);
    assertThat(readHttpMethods(captor.getValue())).containsExactly("GET");

    Map<String, Integer> attempts = getMap(service, "routeToRetryAttempts");
    assertThat(attempts.get("customers_route")).isEqualTo(2);
  }

  @Test
  void schedule_doesNotIncrease_whenIncreaseWindowBlocks() {
    seedRouteAttempts(Map.of("customers_route", 1));
    seedLastChangedAt("customers_route", Instant.now());

    PrometheusResultItem item = mock(PrometheusResultItem.class);
    when(item.getMetric()).thenReturn(Map.of("routeId", "customers_route"));
    when(item.getValue()).thenReturn(List.of("0", "7.1"));

    PrometheusQueryResponse resp = mock(PrometheusQueryResponse.class);
    PrometheusData data = mock(PrometheusData.class);

    when(resp.getStatus()).thenReturn("success");
    when(resp.getData()).thenReturn(data);
    when(data.getResult()).thenReturn(List.of(item));
    when(armClient.getPrometheusQuery(anyString())).thenReturn(Optional.of(resp));

    when(props.getTargetErrorPct()).thenReturn(5.0);
    when(props.getDeadbandPct()).thenReturn(1.0);
    when(props.getPanicMultiplier()).thenReturn(3.0);
    when(props.getMinIncreaseWindowMins()).thenReturn(10);

    service.schedule();

    verifyNoInteractions(apiGatewayClient);

    Map<String, Integer> attempts = getMap(service, "routeToRetryAttempts");
    assertThat(attempts.get("customers_route")).isEqualTo(1);
  }

  @Test
  void schedule_panicIncrease_capsAtMaxAttempts() {
    seedRouteAttempts(Map.of("customers_route", 4));
    seedLastChangedAt("customers_route", Instant.EPOCH);

    PrometheusResultItem item = mock(PrometheusResultItem.class);
    when(item.getMetric()).thenReturn(Map.of("routeId", "customers_route"));
    when(item.getValue()).thenReturn(List.of("0", "25.0"));

    PrometheusQueryResponse resp = mock(PrometheusQueryResponse.class);
    PrometheusData data = mock(PrometheusData.class);

    when(resp.getStatus()).thenReturn("success");
    when(resp.getData()).thenReturn(data);
    when(data.getResult()).thenReturn(List.of(item));
    when(armClient.getPrometheusQuery(anyString())).thenReturn(Optional.of(resp));

    when(props.getTargetErrorPct()).thenReturn(5.0);
    when(props.getDeadbandPct()).thenReturn(1.0);
    when(props.getPanicMultiplier()).thenReturn(3.0);
    when(props.getMinIncreaseWindowMins()).thenReturn(0);
    when(props.getStepSize()).thenReturn(2);
    when(props.getMaxAttempts()).thenReturn(5);

    when(props.getFirstBackoffMs()).thenReturn(100);
    when(props.getMaxBackoffMs()).thenReturn(1000);
    when(props.getFactor()).thenReturn(2);

    service.schedule();

    ArgumentCaptor<ChangeRetryRequest> captor = ArgumentCaptor.forClass(ChangeRetryRequest.class);
    verify(apiGatewayClient).changeRetry(eq("customers_route"), captor.capture());
    assertThat(readAttempts(captor.getValue())).isEqualTo(5);

    Map<String, Integer> attempts = getMap(service, "routeToRetryAttempts");
    assertThat(attempts.get("customers_route")).isEqualTo(5);
  }

  @Test
  void schedule_withinDeadband_resetsGoodCount_andDoesNotChangeAttempts() {
    seedRouteAttempts(Map.of("customers_route", 2));
    seedStableGoodCount();

    PrometheusResultItem item = mock(PrometheusResultItem.class);
    when(item.getMetric()).thenReturn(Map.of("routeId", "customers_route"));
    when(item.getValue()).thenReturn(List.of("0", "4.5"));

    PrometheusQueryResponse resp = mock(PrometheusQueryResponse.class);
    PrometheusData data = mock(PrometheusData.class);

    when(resp.getStatus()).thenReturn("success");
    when(resp.getData()).thenReturn(data);
    when(data.getResult()).thenReturn(List.of(item));
    when(armClient.getPrometheusQuery(anyString())).thenReturn(Optional.of(resp));

    when(props.getTargetErrorPct()).thenReturn(5.0);
    when(props.getDeadbandPct()).thenReturn(1.0);

    service.schedule();

    verifyNoInteractions(apiGatewayClient);

    Map<String, Integer> good = getMap(service, "stableGoodCount");
    assertThat(good.get("customers_route")).isEqualTo(0);

    Map<String, Integer> attempts = getMap(service, "routeToRetryAttempts");
    assertThat(attempts.get("customers_route")).isEqualTo(2);
  }

  @Test
  void schedule_decreasesOnlyAfterStablePeriods_andDecreaseAllowed() {
    seedRouteAttempts(Map.of("customers_route", 3));
    seedLastChangedAt("customers_route", Instant.EPOCH);

    PrometheusResultItem item = mock(PrometheusResultItem.class);
    when(item.getMetric()).thenReturn(Map.of("routeId", "customers_route"));
    when(item.getValue()).thenReturn(List.of("0", "0.0"));

    PrometheusQueryResponse resp = mock(PrometheusQueryResponse.class);
    PrometheusData data = mock(PrometheusData.class);

    when(resp.getStatus()).thenReturn("success");
    when(resp.getData()).thenReturn(data);
    when(data.getResult()).thenReturn(List.of(item));
    when(armClient.getPrometheusQuery(anyString())).thenReturn(Optional.of(resp));

    when(props.getTargetErrorPct()).thenReturn(5.0);
    when(props.getDeadbandPct()).thenReturn(1.0);
    when(props.getDecreaseStablePeriods()).thenReturn(2);
    when(props.getMinDecreaseWindowMins()).thenReturn(0);
    when(props.getStepSize()).thenReturn(1);
    when(props.getMinAttempts()).thenReturn(1);

    when(props.getFirstBackoffMs()).thenReturn(100);
    when(props.getMaxBackoffMs()).thenReturn(1000);
    when(props.getFactor()).thenReturn(2);

    service.schedule();

    verifyNoInteractions(apiGatewayClient);

    service.schedule();

    ArgumentCaptor<ChangeRetryRequest> captor = ArgumentCaptor.forClass(ChangeRetryRequest.class);
    verify(apiGatewayClient).changeRetry(eq("customers_route"), captor.capture());
    assertThat(readAttempts(captor.getValue())).isEqualTo(2);

    Map<String, Integer> attempts = getMap(service, "routeToRetryAttempts");
    assertThat(attempts.get("customers_route")).isEqualTo(2);

    Map<String, Integer> good = getMap(service, "stableGoodCount");
    assertThat(good.get("customers_route")).isEqualTo(0);
  }

  @Test
  void schedule_doesNotDecrease_whenDecreaseWindowBlocks_evenAfterStablePeriods() {
    seedRouteAttempts(Map.of("customers_route", 3));
    seedLastChangedAt("customers_route", Instant.now());

    PrometheusResultItem item = mock(PrometheusResultItem.class);
    when(item.getMetric()).thenReturn(Map.of("routeId", "customers_route"));
    when(item.getValue()).thenReturn(List.of("0", "0.0"));

    PrometheusQueryResponse resp = mock(PrometheusQueryResponse.class);
    PrometheusData data = mock(PrometheusData.class);

    when(resp.getStatus()).thenReturn("success");
    when(resp.getData()).thenReturn(data);
    when(data.getResult()).thenReturn(List.of(item));
    when(armClient.getPrometheusQuery(anyString())).thenReturn(Optional.of(resp));

    when(props.getTargetErrorPct()).thenReturn(5.0);
    when(props.getDeadbandPct()).thenReturn(1.0);
    when(props.getDecreaseStablePeriods()).thenReturn(2);
    when(props.getMinDecreaseWindowMins()).thenReturn(10);

    service.schedule();
    service.schedule();

    verifyNoInteractions(apiGatewayClient);

    Map<String, Integer> good = getMap(service, "stableGoodCount");
    assertThat(good.get("customers_route")).isEqualTo(2);

    Map<String, Integer> attempts = getMap(service, "routeToRetryAttempts");
    assertThat(attempts.get("customers_route")).isEqualTo(3);
  }

  @Test
  void schedule_nonNumericErrPct_treatedAsZero_andCanDecrease() {
    seedRouteAttempts(Map.of("customers_route", 3));
    seedLastChangedAt("customers_route", Instant.EPOCH);

    PrometheusResultItem item = mock(PrometheusResultItem.class);
    when(item.getMetric()).thenReturn(Map.of("routeId", "customers_route"));
    when(item.getValue()).thenReturn(List.of("0", "abc"));

    PrometheusQueryResponse resp = mock(PrometheusQueryResponse.class);
    PrometheusData data = mock(PrometheusData.class);

    when(resp.getStatus()).thenReturn("success");
    when(resp.getData()).thenReturn(data);
    when(data.getResult()).thenReturn(List.of(item));
    when(armClient.getPrometheusQuery(anyString())).thenReturn(Optional.of(resp));

    when(props.getTargetErrorPct()).thenReturn(5.0);
    when(props.getDeadbandPct()).thenReturn(1.0);
    when(props.getDecreaseStablePeriods()).thenReturn(1);
    when(props.getMinDecreaseWindowMins()).thenReturn(0);
    when(props.getStepSize()).thenReturn(1);
    when(props.getMinAttempts()).thenReturn(1);

    when(props.getFirstBackoffMs()).thenReturn(100);
    when(props.getMaxBackoffMs()).thenReturn(1000);
    when(props.getFactor()).thenReturn(2);

    service.schedule();

    ArgumentCaptor<ChangeRetryRequest> captor = ArgumentCaptor.forClass(ChangeRetryRequest.class);
    verify(apiGatewayClient).changeRetry(eq("customers_route"), captor.capture());
    assertThat(readAttempts(captor.getValue())).isEqualTo(2);
  }

  private void seedRouteAttempts(Map<String, Integer> values) {
    Map<String, Integer> routeAttempts = getMap(service, "routeToRetryAttempts");
    routeAttempts.clear();
    routeAttempts.putAll(values);
  }

  private void seedLastChangedAt(String routeId, Instant at) {
    Map<String, Instant> lastChanged = getMap(service, "routeIdByRetryLastChangedAt");
    lastChanged.put(routeId, at);
  }

  private void seedStableGoodCount() {
    Map<String, Integer> goodCount = getMap(service, "stableGoodCount");
    goodCount.put("customers_route", 5);
  }

  @SuppressWarnings("unchecked")
  private static <K, V> Map<K, V> getMap(Object target, String fieldName) {
    try {
      Field f = target.getClass().getDeclaredField(fieldName);
      f.setAccessible(true);
      Object v = f.get(target);
      if (v == null) {
        Map<K, V> m = new HashMap<>();
        f.set(target, m);
        return m;
      }
      return (Map<K, V>) v;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static int readAttempts(ChangeRetryRequest req) {
    Object v = readFieldOrAccessor(req, "maxAttempts", "attempts", "getMaxAttempts", "maxAttempts");
    if (v instanceof Integer i) return i;
    if (v instanceof Number n) return n.intValue();
    Field[] fields = req.getClass().getDeclaredFields();
    for (Field f : fields) {
      if (f.getType() == int.class || f.getType() == Integer.class) {
        try {
          f.setAccessible(true);
          Object x = f.get(req);
          if (x instanceof Integer i) return i;
        } catch (Exception ignored) {
        }
      }
    }
    throw new AssertionError("Unable to read attempts from ChangeRetryRequest");
  }

  @SuppressWarnings("unchecked")
  private static Set<Integer> readRetryableStatusCodes(ChangeRetryRequest req) {
    Object v = readFieldOrAccessor(
        req,
        "retryableStatusCodes",
        "retryStatuses",
        "getRetryableStatusCodes",
        "getRetryStatuses"
    );
    if (v instanceof Set<?> s) return (Set<Integer>) s;
    throw new AssertionError("Unable to read status codes from ChangeRetryRequest");
  }

  private static Object readFieldOrAccessor(Object target, String field1, String field2, String method1, String method2) {
    try {
      try {
        var m = target.getClass().getMethod(method1);
        return m.invoke(target);
      } catch (NoSuchMethodException ignored) {
      }
      try {
        var m = target.getClass().getMethod(method2);
        return m.invoke(target);
      } catch (NoSuchMethodException ignored) {
      }
      try {
        Field f = target.getClass().getDeclaredField(field1);
        f.setAccessible(true);
        return f.get(target);
      } catch (NoSuchFieldException ignored) {
      }
      try {
        Field f = target.getClass().getDeclaredField(field2);
        f.setAccessible(true);
        return f.get(target);
      } catch (NoSuchFieldException ignored) {
      }
      for (Field f : target.getClass().getDeclaredFields()) {
        if (Set.class.isAssignableFrom(f.getType())) {
          f.setAccessible(true);
          Object val = f.get(target);
          if (val instanceof Set<?> s && !s.isEmpty()) return val;
        }
      }
      return null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private static Set<String> readHttpMethods(ChangeRetryRequest req) {
    Object v = readFieldOrAccessor(req, "httpMethods", "methods", "getHttpMethods", "httpMethods");
    if (v instanceof Set<?> s) return (Set<String>) s;
    throw new AssertionError("Unable to read http methods from ChangeRetryRequest");
  }
}
