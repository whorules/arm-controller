package com.korovko.arm_controller.service;

import com.korovko.arm_controller.client.ApiGatewayClient;
import com.korovko.arm_controller.client.ArmClient;
import com.korovko.arm_controller.config.TimeoutConfigProperties;
import com.korovko.arm_controller.model.ChangeTimeoutRequest;
import com.korovko.arm_controller.model.PrometheusData;
import com.korovko.arm_controller.model.PrometheusQueryResponse;
import com.korovko.arm_controller.model.PrometheusResultItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamicConfigurationServiceTest {

  @Mock
  private ApiGatewayClient apiGatewayClient;

  @Mock
  private ArmClient armClient;

  @Mock
  private TimeoutConfigProperties timeoutConfigProperties;

  private DynamicConfigurationService service;

  private static final int MAX_LATENCY = 1500;
  private static final int STEP_SIZE = 100;
  private static final int MIN_CHANGE_WINDOW_MINS = 2;
  private static final int ACCEPTABLE_ERROR_RATE = 2;

  private static final String ERROR_RATE_QUERY = """
      (sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count{httpStatusCode=~"5.."}[1m]))/sum by (routeId) (increase(spring_cloud_gateway_requests_seconds_count[1m]))) * 100
      """;

  @BeforeEach
  void setUp() {
    service = new DynamicConfigurationService(timeoutConfigProperties, apiGatewayClient, armClient);
  }

  @Test
  void shouldSkipWhenNoRoutesConfigured() {
    assertTrue(routeToTimeout().isEmpty());

    service.schedule();

    verifyNoInteractions(armClient);
    verifyNoInteractions(apiGatewayClient);
  }

  @Test
  void shouldIncreaseTimeoutWhenErrorRateAboveThreshold() {
    when(timeoutConfigProperties.getAcceptableErrorRate()).thenReturn(ACCEPTABLE_ERROR_RATE);
    when(timeoutConfigProperties.getMax()).thenReturn(MAX_LATENCY);
    when(timeoutConfigProperties.getStepSize()).thenReturn(STEP_SIZE);

    routeToTimeout().put("route-1", 1000);

    PrometheusResultItem item = buildItem("route-1", List.of("ignored-ts", "5.0"));
    PrometheusQueryResponse response = buildPrometheusResponse("success", List.of(item));

    when(armClient.getPrometheusQuery(ERROR_RATE_QUERY)).thenReturn(Optional.of(response));

    service.schedule();

    ArgumentCaptor<ChangeTimeoutRequest> captor = ArgumentCaptor.forClass(ChangeTimeoutRequest.class);
    verify(apiGatewayClient).changeTimeout(captor.capture());

    ChangeTimeoutRequest req = captor.getValue();
    assertEquals("route-1", req.getRouteId());
    assertEquals(1000 + STEP_SIZE, req.getTimeoutMillis());
    assertEquals(1000 + STEP_SIZE, routeToTimeout().get("route-1"));

    Instant lastChanged = routeIdByLastChangedAt().get("route-1");

    assertNotNull(lastChanged);
    assertTrue(lastChanged.isAfter(Instant.EPOCH));
  }

  @Test
  void shouldNotChangeTimeoutWhenErrorRateZero() {
    routeToTimeout().put("route-1", 1000);

    PrometheusResultItem item = buildItem("route-1", List.of("ignored-ts", "0.0"));
    PrometheusQueryResponse response = buildPrometheusResponse("success", List.of(item));

    when(armClient.getPrometheusQuery(ERROR_RATE_QUERY)).thenReturn(Optional.of(response));

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
    assertEquals(1000, routeToTimeout().get("route-1"));
  }

  @Test
  void shouldNotChangeTimeoutWhenErrorRateBelowOrEqualThreshold() {
    when(timeoutConfigProperties.getAcceptableErrorRate()).thenReturn(ACCEPTABLE_ERROR_RATE);

    routeToTimeout().put("route-1", 1000);

    PrometheusResultItem item = buildItem("route-1", List.of("ignored-ts", "1.5"));
    PrometheusQueryResponse response = buildPrometheusResponse("success", List.of(item));

    when(armClient.getPrometheusQuery(ERROR_RATE_QUERY)).thenReturn(Optional.of(response));

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
    assertEquals(1000, routeToTimeout().get("route-1"));
  }

  @Test
  void shouldIgnoreWhenPrometheusStatusNotSuccess() {
    routeToTimeout().put("route-1", 1000);

    PrometheusResultItem item = buildItem("route-1", List.of("ignored-ts", "10.0"));
    PrometheusQueryResponse response = buildPrometheusResponse("error", List.of(item));

    when(armClient.getPrometheusQuery(ERROR_RATE_QUERY)).thenReturn(Optional.of(response));

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
    assertEquals(1000, routeToTimeout().get("route-1"));
  }

  @Test
  void shouldIgnoreWhenResultListIsEmpty() {
    routeToTimeout().put("route-1", 1000);

    PrometheusQueryResponse response = buildPrometheusResponse("success", Collections.emptyList());

    when(armClient.getPrometheusQuery(ERROR_RATE_QUERY)).thenReturn(Optional.of(response));

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
    assertEquals(1000, routeToTimeout().get("route-1"));
  }

  @Test
  void shouldIgnoreItemWithoutRouteId() {
    routeToTimeout().put("some-route", 1000);

    PrometheusResultItem item = buildItem(null, List.of("ignored-ts", "10.0"));
    PrometheusQueryResponse response = buildPrometheusResponse("success", List.of(item));

    when(armClient.getPrometheusQuery(ERROR_RATE_QUERY)).thenReturn(Optional.of(response));

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
  }

  @Test
  void shouldIgnoreItemWithTooShortValueList() {
    routeToTimeout().put("route-1", 1000);

    PrometheusResultItem item = buildItem("route-1", List.of("only-one-element"));
    PrometheusQueryResponse response = buildPrometheusResponse("success", List.of(item));

    when(armClient.getPrometheusQuery(ERROR_RATE_QUERY))
        .thenReturn(Optional.of(response));

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
    assertEquals(1000, routeToTimeout().get("route-1"));
  }

  @Test
  void shouldIgnoreItemWithInvalidNumericValue() {
    routeToTimeout().put("route-1", 1000);

    PrometheusResultItem item = buildItem("route-1", List.of("ignored-ts", "not-a-number"));
    PrometheusQueryResponse response = buildPrometheusResponse("success", List.of(item));

    when(armClient.getPrometheusQuery(ERROR_RATE_QUERY))
        .thenReturn(Optional.of(response));

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
    assertEquals(1000, routeToTimeout().get("route-1"));
  }

  @Test
  void shouldNotIncreaseTimeoutWhenCurrentTimeoutMissingForRoute() {
    routeToTimeout().put("other-route", 1000);

    PrometheusResultItem item = buildItem("unknown-route", List.of("ignored-ts", "10.0"));
    PrometheusQueryResponse response = buildPrometheusResponse("success", List.of(item));

    when(armClient.getPrometheusQuery(ERROR_RATE_QUERY)).thenReturn(Optional.of(response));

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
    assertEquals(1000, routeToTimeout().get("other-route"));
    assertFalse(routeToTimeout().containsKey("unknown-route"));
  }

  @Test
  void shouldNotIncreaseTimeoutWhenChangeWindowNotElapsed() {
    when(timeoutConfigProperties.getAcceptableErrorRate()).thenReturn(ACCEPTABLE_ERROR_RATE);
    when(timeoutConfigProperties.getMax()).thenReturn(MAX_LATENCY);
    when(timeoutConfigProperties.getMinChangeWindowMins()).thenReturn(MIN_CHANGE_WINDOW_MINS);

    routeToTimeout().put("route-1", 1000);

    routeIdByLastChangedAt().put("route-1", Instant.now().minus(1, ChronoUnit.MINUTES));

    PrometheusResultItem item = buildItem("route-1", List.of("ignored-ts", "10.0"));
    PrometheusQueryResponse response = buildPrometheusResponse("success", List.of(item));

    when(armClient.getPrometheusQuery(ERROR_RATE_QUERY)).thenReturn(Optional.of(response));

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
    assertEquals(1000, routeToTimeout().get("route-1"));
  }

  @Test
  void shouldCapTimeoutAtMaxLatency() {
    when(timeoutConfigProperties.getAcceptableErrorRate()).thenReturn(ACCEPTABLE_ERROR_RATE);
    when(timeoutConfigProperties.getMax()).thenReturn(MAX_LATENCY);
    when(timeoutConfigProperties.getMinChangeWindowMins()).thenReturn(MIN_CHANGE_WINDOW_MINS);
    when(timeoutConfigProperties.getStepSize()).thenReturn(STEP_SIZE);

    routeToTimeout().put("route-1", MAX_LATENCY - 50);

    PrometheusResultItem item = buildItem("route-1", List.of("ignored-ts", "10.0"));
    PrometheusQueryResponse response = buildPrometheusResponse("success", List.of(item));

    when(armClient.getPrometheusQuery(ERROR_RATE_QUERY)).thenReturn(Optional.of(response));

    service.schedule();

    ArgumentCaptor<ChangeTimeoutRequest> captor = ArgumentCaptor.forClass(ChangeTimeoutRequest.class);
    verify(apiGatewayClient).changeTimeout(captor.capture());

    ChangeTimeoutRequest req = captor.getValue();
    assertEquals("route-1", req.getRouteId());
    assertEquals(MAX_LATENCY, req.getTimeoutMillis());
    assertEquals(MAX_LATENCY, routeToTimeout().get("route-1"));
  }

  @Test
  void shouldNotIncreaseTimeoutWhenAlreadyAtMaxLatency() {
    when(timeoutConfigProperties.getAcceptableErrorRate()).thenReturn(ACCEPTABLE_ERROR_RATE);
    when(timeoutConfigProperties.getMax()).thenReturn(MAX_LATENCY);

    routeToTimeout().put("route-1", MAX_LATENCY);

    PrometheusResultItem item = buildItem("route-1", List.of("ignored-ts", "10.0"));
    PrometheusQueryResponse response = buildPrometheusResponse("success", List.of(item));

    when(armClient.getPrometheusQuery(ERROR_RATE_QUERY)).thenReturn(Optional.of(response));

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
    assertEquals(MAX_LATENCY, routeToTimeout().get("route-1"));
  }

  @Test
  void callRemoteService_shouldPopulateRouteTimeoutsFromApiGateway() {
    assertTrue(routeToTimeout().isEmpty());

    Map<String, Integer> latencies = new HashMap<>();
    latencies.put("route-1", 700);
    latencies.put("route-2", 900);

    when(apiGatewayClient.getLatencies()).thenReturn(latencies);

    ReflectionTestUtils.invokeMethod(service, "callRemoteService");

    Map<String, Integer> stored = routeToTimeout();
    assertEquals(2, stored.size());
    assertEquals(700, stored.get("route-1"));
    assertEquals(900, stored.get("route-2"));
  }

  @Test
  void toDouble_shouldParseValidNumber() {
    double result = invokeToDouble("12.34");
    assertEquals(12.34, result, 0.0001);
  }

  @Test
  void toDouble_shouldReturnZeroOnInvalidNumber() {
    double result = invokeToDouble("not-a-number");
    assertEquals(0.0, result);
  }

  @Test
  void onApplicationReady_shouldNotThrow() {
    assertDoesNotThrow(() -> service.onApplicationReady());
  }

  @SuppressWarnings("unchecked")
  private Map<String, Integer> routeToTimeout() {
    return (Map<String, Integer>) ReflectionTestUtils.getField(service, "routeToTimeout");
  }

  @SuppressWarnings("unchecked")
  private Map<String, Instant> routeIdByLastChangedAt() {
    return (Map<String, Instant>) ReflectionTestUtils.getField(service, "routeIdByLastChangedAt");
  }

  private PrometheusQueryResponse buildPrometheusResponse(String status, List<PrometheusResultItem> items) {
    PrometheusData data = new PrometheusData();
    data.setResult(items);

    PrometheusQueryResponse response = new PrometheusQueryResponse();
    response.setStatus(status);
    response.setData(data);

    return response;
  }

  private PrometheusResultItem buildItem(String routeId, List<String> value) {
    PrometheusResultItem item = new PrometheusResultItem();
    Map<String, String> metric = new HashMap<>();
    if (routeId != null) {
      metric.put("routeId", routeId);
    }
    item.setMetric(metric);
    item.setValue(value);
    return item;
  }

  private double invokeToDouble(String value) {
    Double result = ReflectionTestUtils.invokeMethod(service, "toDouble", value);
    assertNotNull(result);
    return result;
  }

}

