package com.korovko.arm_controller.service;

import com.korovko.arm_controller.client.ApiGatewayClient;
import com.korovko.arm_controller.client.ArmClient;
import com.korovko.arm_controller.config.TimeoutConfigProperties;
import com.korovko.arm_controller.model.ChangeTimeoutRequest;
import com.korovko.arm_controller.model.PrometheusData;
import com.korovko.arm_controller.model.PrometheusQueryResponse;
import com.korovko.arm_controller.model.PrometheusResultItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamicTimeoutConfigurationServiceTest {

  @Mock
  private ArmClient armClient;
  @Mock
  private ApiGatewayClient apiGatewayClient;
  @Mock
  private TimeoutConfigProperties props;

  @Captor
  private ArgumentCaptor<ChangeTimeoutRequest> changeReqCaptor;

  private DynamicTimeoutConfigurationService newService() {
    return new DynamicTimeoutConfigurationService(armClient, apiGatewayClient, props);
  }

  @Test
  void schedule_noRoutes_doesNotCallPrometheus() {
    DynamicTimeoutConfigurationService service = newService();

    service.schedule();

    verifyNoInteractions(armClient);
    verifyNoInteractions(apiGatewayClient);
  }

  @Test
  void panic_increasesTimeout_whenErrorAbovePanic_andIncreaseAllowed() {
    DynamicTimeoutConfigurationService service = newService();

    seedTimeoutState(service, 1200, Instant.now().minus(Duration.ofMinutes(10)));

    when(props.getTargetErrorRate()).thenReturn(4);
    when(props.getDeadbandPct()).thenReturn(0.75);
    when(props.getPanicMultiplier()).thenReturn(2.0);
    when(props.getMinIncreaseWindowMins()).thenReturn(1);
    when(props.getMax()).thenReturn(1500);
    when(props.getStepSize()).thenReturn(100);

    stubPrometheusSingleRoute(armClient, 9.0);

    service.schedule();

    verify(apiGatewayClient).changeTimeout(changeReqCaptor.capture());
    ChangeTimeoutRequest req = changeReqCaptor.getValue();
    assertThat(req.getRouteId()).isEqualTo("customers_route");
    assertThat(req.getTimeoutMillis()).isEqualTo(1300);

    assertThat(getRouteToTimeout(service).get("customers_route")).isEqualTo(1300);
    assertThat(getStableGoodCount(service).get("customers_route")).isEqualTo(0);
  }

  @Test
  void increase_whenAboveUpperBand_andIncreaseAllowed() {
    DynamicTimeoutConfigurationService service = newService();
    seedTimeoutState(service, 1100, Instant.now().minus(Duration.ofMinutes(10)));

    when(props.getTargetErrorRate()).thenReturn(4);
    when(props.getDeadbandPct()).thenReturn(0.75);
    when(props.getPanicMultiplier()).thenReturn(2.0);
    when(props.getMinIncreaseWindowMins()).thenReturn(1);
    when(props.getMax()).thenReturn(1500);
    when(props.getStepSize()).thenReturn(100);

    stubPrometheusSingleRoute(armClient, 5.0);

    service.schedule();

    verify(apiGatewayClient).changeTimeout(changeReqCaptor.capture());
    ChangeTimeoutRequest req = changeReqCaptor.getValue();
    assertThat(req.getTimeoutMillis()).isEqualTo(1200);
  }

  @Test
  void noop_whenInsideDeadband() {
    DynamicTimeoutConfigurationService service = newService();
    seedTimeoutState(service, 1100, Instant.now().minus(Duration.ofMinutes(10)));

    when(props.getTargetErrorRate()).thenReturn(4);
    when(props.getDeadbandPct()).thenReturn(0.75);
    when(props.getPanicMultiplier()).thenReturn(2.0);

    stubPrometheusSingleRoute(armClient, 4.0);

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
    assertThat(getStableGoodCount(service).get("customers_route")).isEqualTo(0);
    assertThat(getRouteToTimeout(service).get("customers_route")).isEqualTo(1100);
  }

  @Test
  void decrease_onlyAfterStablePeriods_andDecreaseAllowed() {
    DynamicTimeoutConfigurationService service = newService();
    seedTimeoutState(service, 1300, Instant.now().minus(Duration.ofMinutes(10)));

    when(props.getTargetErrorRate()).thenReturn(4);
    when(props.getDeadbandPct()).thenReturn(0.75);
    when(props.getPanicMultiplier()).thenReturn(2.0);

    when(props.getDecreaseStablePeriods()).thenReturn(3);
    when(props.getMinDecreaseWindowMins()).thenReturn(3);

    when(props.getMin()).thenReturn(700);
    when(props.getStepSize()).thenReturn(100);

    stubPrometheusSingleRoute(armClient, 0.5);

    service.schedule();
    verify(apiGatewayClient, never()).changeTimeout(any());
    assertThat(getStableGoodCount(service).get("customers_route")).isEqualTo(1);

    service.schedule();
    verify(apiGatewayClient, never()).changeTimeout(any());
    assertThat(getStableGoodCount(service).get("customers_route")).isEqualTo(2);

    service.schedule();
    verify(apiGatewayClient).changeTimeout(changeReqCaptor.capture());
    ChangeTimeoutRequest req = changeReqCaptor.getValue();
    assertThat(req.getTimeoutMillis()).isEqualTo(1200);

    assertThat(getStableGoodCount(service).get("customers_route")).isEqualTo(0);
  }

  @Test
  void increase_blockedByCooldown_whenLastChangeTooRecent() {
    DynamicTimeoutConfigurationService service = newService();
    seedTimeoutState(service, 1100, Instant.now());

    when(props.getTargetErrorRate()).thenReturn(4);
    when(props.getDeadbandPct()).thenReturn(0.75);
    when(props.getPanicMultiplier()).thenReturn(2.0);
    when(props.getMinIncreaseWindowMins()).thenReturn(1);

    stubPrometheusSingleRoute(armClient, 9.0);

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
    assertThat(getRouteToTimeout(service).get("customers_route")).isEqualTo(1100);
  }

  @Test
  void doesNotIncreaseAboveMax() {
    DynamicTimeoutConfigurationService service = newService();
    seedTimeoutState(service, 1500, Instant.now().minus(Duration.ofMinutes(10)));

    when(props.getTargetErrorRate()).thenReturn(4);
    when(props.getDeadbandPct()).thenReturn(0.75);
    when(props.getPanicMultiplier()).thenReturn(2.0);
    when(props.getMinIncreaseWindowMins()).thenReturn(1);
    when(props.getMax()).thenReturn(1500);

    stubPrometheusSingleRoute(armClient, 9.0);

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
    assertThat(getRouteToTimeout(service).get("customers_route")).isEqualTo(1500);
  }

  @Test
  void doesNotDecreaseBelowMin() {
    DynamicTimeoutConfigurationService service = newService();
    seedTimeoutState(service, 700, Instant.now().minus(Duration.ofMinutes(10)));

    when(props.getTargetErrorRate()).thenReturn(4);
    when(props.getDeadbandPct()).thenReturn(0.75);
    when(props.getPanicMultiplier()).thenReturn(2.0);

    when(props.getDecreaseStablePeriods()).thenReturn(1);
    when(props.getMinDecreaseWindowMins()).thenReturn(3);

    when(props.getMin()).thenReturn(700);

    stubPrometheusSingleRoute(armClient, 0.1);

    service.schedule();

    verify(apiGatewayClient, never()).changeTimeout(any());
    assertThat(getRouteToTimeout(service).get("customers_route")).isEqualTo(700);
  }

  @Test
  void initializeTimeouts_seedsMaps_andAllowsImmediateActions() {
    DynamicTimeoutConfigurationService service = newService();

    when(apiGatewayClient.getLatencies()).thenReturn(Map.of("customers_route", 1100));

    when(props.getMinDecreaseWindowMins()).thenReturn(3);
    when(props.getMinIncreaseWindowMins()).thenReturn(1);

    ReflectionTestUtils.invokeMethod(service, "initializeTimeouts");

    assertThat(getRouteToTimeout(service)).containsEntry("customers_route", 1100);
    assertThat(getStableGoodCount(service)).containsEntry("customers_route", 0);

    Instant last = getLastChanged(service).get("customers_route");
    assertThat(last).isBefore(Instant.now().minus(Duration.ofMinutes(1)));
  }

  private void stubPrometheusSingleRoute(ArmClient armClient, double errorPct) {
    PrometheusResultItem item = mock(PrometheusResultItem.class);
    when(item.getMetric()).thenReturn(Map.of("routeId", "customers_route"));
    when(item.getValue()).thenReturn(List.of("0", String.valueOf(errorPct)));

    PrometheusData data = mock(PrometheusData.class);
    when(data.getResult()).thenReturn(List.of(item));

    PrometheusQueryResponse resp = mock(PrometheusQueryResponse.class);
    when(resp.getStatus()).thenReturn("success");
    when(resp.getData()).thenReturn(data);

    when(armClient.getPrometheusQuery(any())).thenReturn(Optional.of(resp));
  }

  private void seedTimeoutState(DynamicTimeoutConfigurationService service,
                                int timeoutMs,
                                Instant lastChangedAt) {
    getRouteToTimeout(service).put("customers_route", timeoutMs);
    getLastChanged(service).put("customers_route", lastChangedAt);
    getStableGoodCount(service).put("customers_route", 0);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Integer> getRouteToTimeout(DynamicTimeoutConfigurationService service) {
    Object val = ReflectionTestUtils.getField(service, "routeToTimeout");
    if (val == null) {
      throw new IllegalStateException("Field routeToTimeout not found");
    }
    return (Map<String, Integer>) val;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Instant> getLastChanged(DynamicTimeoutConfigurationService service) {
    Object val = ReflectionTestUtils.getField(service, "routeIdByLastChangedAt");
    if (val == null) throw new IllegalStateException("Field routeIdByLastChangedAt not found");
    return (Map<String, Instant>) val;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Integer> getStableGoodCount(DynamicTimeoutConfigurationService service) {
    Object val = ReflectionTestUtils.getField(service, "stableGoodCount");
    if (val == null) throw new IllegalStateException("Field stableGoodCount not found");
    return (Map<String, Integer>) val;
  }

}
