//package com.korovko.arm_controller.fuzz;
//
//import com.code_intelligence.jazzer.api.FuzzedDataProvider;
//import com.code_intelligence.jazzer.junit.FuzzTest;
//import com.korovko.arm_controller.client.ApiGatewayClient;
//import com.korovko.arm_controller.client.ArmClient;
//import com.korovko.arm_controller.config.TimeoutConfigProperties;
//import com.korovko.arm_controller.model.PrometheusResultItem;
//import com.korovko.arm_controller.service.DynamicRetryConfigurationService;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//class DynamicRetryConfigurationServiceFuzzTest {
//
//  @FuzzTest(maxDuration = "30s")
//  void processTimeout_should_not_throw_on_any_input(FuzzedDataProvider data) {
//    ApiGatewayClient apiGatewayClient = mock(ApiGatewayClient.class);
//    ArmClient armClient = mock(ArmClient.class);
//    TimeoutConfigProperties props = mock(TimeoutConfigProperties.class);
//
//    when(props.getTargetErrorRate()).thenReturn(2);
//    when(props.getMax()).thenReturn(1500);
//    when(props.getMinChangeWindowMins()).thenReturn(2);
//    when(props.getStepSize()).thenReturn(100);
//
//    DynamicRetryConfigurationService service = new DynamicRetryConfigurationService(props, apiGatewayClient, armClient);
//
//    @SuppressWarnings("unchecked")
//    Map<String, Integer> routeToTimeout =
//        (Map<String, Integer>) ReflectionTestUtils.getField(service, "routeToTimeout");
//    if (routeToTimeout != null) {
//      routeToTimeout.put("route-1", 1000);
//    }
//
//    String routeId = data.consumeBoolean() ? null : (data.consumeBoolean() ? "route-1" : "unknown");
//    int valueSize = data.consumeInt(0, 3);
//
//    List<String> value = switch (valueSize) {
//      case 0 -> List.of();
//      case 1 -> List.of("ts-only");
//      case 2 -> List.of("ts", data.consumeString(10));
//      default -> List.of("ts", data.consumeString(10), data.consumeString(10));
//    };
//
//    PrometheusResultItem item = new PrometheusResultItem();
//    Map<String, String> metric = new HashMap<>();
//    if (routeId != null) metric.put("routeId", routeId);
//    item.setMetric(metric);
//    item.setValue(value);
//
//    assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service, "processItem", item));
//  }
//}
