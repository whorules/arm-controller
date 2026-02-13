package com.korovko.arm_controller.bdd.steps;

import com.korovko.arm_controller.client.ApiGatewayClient;
import com.korovko.arm_controller.client.ArmClient;
import com.korovko.arm_controller.config.TimeoutConfigProperties;
import com.korovko.arm_controller.model.PrometheusData;
import com.korovko.arm_controller.model.PrometheusQueryResponse;
import com.korovko.arm_controller.model.PrometheusResultItem;
import com.korovko.arm_controller.service.DynamicTimeoutConfigurationService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DynamicTimeoutSteps {

  private final ApiGatewayClient apiGatewayClient;
  private final ArmClient armClient;
  private final TimeoutConfigProperties timeoutConfigProperties;
  private final DynamicTimeoutConfigurationService service;

  public DynamicTimeoutSteps() {
    this.apiGatewayClient = mock(ApiGatewayClient.class);
    this.armClient = mock(ArmClient.class);
    this.timeoutConfigProperties = mock(TimeoutConfigProperties.class);

    this.service = new DynamicTimeoutConfigurationService(
        armClient,
        apiGatewayClient,
        timeoutConfigProperties
    );

    when(timeoutConfigProperties.getDeadbandPct()).thenReturn(0.5);
    when(timeoutConfigProperties.getPanicMultiplier()).thenReturn(3.0);
    when(timeoutConfigProperties.getDecreaseStablePeriods()).thenReturn(2);
    when(timeoutConfigProperties.getMin()).thenReturn(0);
  }

  @Given("there is a route {string} with initial timeout {int} ms")
  public void there_is_a_route_with_initial_timeout(String routeId, int timeoutMs) {
    @SuppressWarnings("unchecked")
    Map<String, Integer> routeToTimeout =
        (Map<String, Integer>) ReflectionTestUtils.getField(service, "routeToTimeout");
    @SuppressWarnings("unchecked")
    Map<String, Instant> lastChanged =
        (Map<String, Instant>) ReflectionTestUtils.getField(service, "routeIdByLastChangedAt");
    @SuppressWarnings("unchecked")
    Map<String, Integer> good =
        (Map<String, Integer>) ReflectionTestUtils.getField(service, "stableGoodCount");

    if (routeToTimeout != null) routeToTimeout.put(routeId, timeoutMs);
    if (lastChanged != null) lastChanged.put(routeId, Instant.EPOCH);
    if (good != null) good.put(routeId, 0);
  }

  @Given("the acceptable error rate is {int} percent")
  public void the_acceptable_error_rate_is_percent(int acceptableRate) {
    when(timeoutConfigProperties.getTargetErrorRate()).thenReturn(acceptableRate);
  }

  @Given("the maximum timeout is {int} ms")
  public void the_maximum_timeout_is_ms(int maxTimeoutMs) {
    when(timeoutConfigProperties.getMax()).thenReturn(maxTimeoutMs);
  }

  @Given("the minimum change window is {int} minutes")
  public void the_minimum_change_window_is_minutes(int minutes) {
    when(timeoutConfigProperties.getMinIncreaseWindowMins()).thenReturn(minutes);
    when(timeoutConfigProperties.getMinDecreaseWindowMins()).thenReturn(minutes);
  }

  @Given("the timeout step size is {int} ms")
  public void the_timeout_step_size_is_ms(int stepMs) {
    when(timeoutConfigProperties.getStepSize()).thenReturn(stepMs);
  }

  @Given("the current error rate for {string} is {double} percent")
  public void the_current_error_rate_for_is_percent(String routeId, double errorRate) {
    PrometheusResultItem item = buildItem(
        routeId,
        List.of("ignored-ts", Double.toString(errorRate))
    );
    PrometheusQueryResponse prometheusResponse = buildPrometheusResponse(List.of(item));
    doReturn(Optional.of(prometheusResponse)).when(armClient).getPrometheusQuery(anyString());
  }

  @Given("the last timeout change for {string} was {int} minutes ago")
  public void the_last_timeout_change_for_was_minutes_ago(String routeId, int minutesAgo) {
    @SuppressWarnings("unchecked")
    Map<String, Instant> lastChanged =
        (Map<String, Instant>) ReflectionTestUtils.getField(service, "routeIdByLastChangedAt");

    if (lastChanged != null) {
      lastChanged.put(routeId, Instant.now().minus(minutesAgo, ChronoUnit.MINUTES));
    }
  }

  @Given("an incoming Prometheus metric with routeId {string} and value kind {string}")
  public void an_incoming_prometheus_metric_with_routeid_and_value_kind(String routeIdLiteral,
                                                                        String valueKind) {
    String routeId = "null".equals(routeIdLiteral) ? null : routeIdLiteral;

    List<String> value = switch (valueKind) {
      case "too_short" -> List.of("only-one-element");
      case "not_a_number" -> List.of("ignored-ts", "not-a-number");
      case "normal" -> List.of("ignored-ts", "10.0");
      default -> throw new IllegalArgumentException("Unknown valueKind: " + valueKind);
    };

    PrometheusResultItem item = buildItem(routeId, value);
    PrometheusQueryResponse prometheusResponse = buildPrometheusResponse(List.of(item));
    doReturn(Optional.of(prometheusResponse)).when(armClient).getPrometheusQuery(anyString());
  }

  @When("the scheduler checks Prometheus metrics")
  public void the_scheduler_checks_prometheus_metrics() {
    service.schedule();
  }

  @When("I trigger the dynamic configuration check")
  public void i_trigger_the_dynamic_configuration_check() {
    service.schedule();
  }

  @Then("the timeout for {string} should become {int} ms")
  public void the_timeout_for_should_become_ms(String routeId, int expectedTimeoutMs) {
    Map<String, Integer> routeToTimeout = routeToTimeout();
    Integer actual = routeToTimeout.get(routeId);
    assertEquals(expectedTimeoutMs, actual, "Unexpected timeout value for route " + routeId);
  }

  @Then("no timeout change should be performed for {string}")
  public void no_timeout_change_should_be_performed_for(String routeId) {
    verify(apiGatewayClient, never()).changeTimeout(any());
  }

  @Then("the stored timeout for {string} should remain {int} ms")
  public void the_stored_timeout_for_should_remain_ms(String routeId, int expectedTimeoutMs) {
    Map<String, Integer> routeToTimeout = routeToTimeout();
    Integer actual = routeToTimeout.get(routeId);
    assertEquals(expectedTimeoutMs, actual, "Timeout for route " + routeId + " must remain unchanged");
  }

  @Then("the last timeout change timestamp for {string} should be updated")
  public void the_last_timeout_change_timestamp_for_should_be_updated(String routeId) {
    @SuppressWarnings("unchecked")
    Map<String, Instant> lastChanged =
        (Map<String, Instant>) ReflectionTestUtils.getField(service, "routeIdByLastChangedAt");

    Instant ts = lastChanged != null ? lastChanged.get(routeId) : null;
    assertTrue(ts != null && ts.isAfter(Instant.EPOCH), "Last changed timestamp for " + routeId + " was not updated");
  }

  @Then("the stored timeout for {string} should not exceed {int} ms")
  public void the_stored_timeout_for_should_not_exceed_ms(String routeId, int maxMs) {
    Map<String, Integer> routeToTimeout = routeToTimeout();
    Integer actual = routeToTimeout.get(routeId);
    assertTrue(actual != null && actual <= maxMs, "Timeout for route " + routeId + " exceeds max: " + maxMs);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Integer> routeToTimeout() {
    return (Map<String, Integer>) ReflectionTestUtils.getField(service, "routeToTimeout");
  }

  private PrometheusQueryResponse buildPrometheusResponse(List<PrometheusResultItem> items) {
    PrometheusData data = new PrometheusData();
    data.setResult(items);

    PrometheusQueryResponse response = new PrometheusQueryResponse();
    response.setStatus("success");
    response.setData(data);

    return response;
  }

  private PrometheusResultItem buildItem(String routeId, List<String> value) {
    PrometheusResultItem item = new PrometheusResultItem();
    Map<String, String> metric = new HashMap<>();
    if (routeId != null) metric.put("routeId", routeId);
    item.setMetric(metric);
    item.setValue(value);
    return item;
  }
}
