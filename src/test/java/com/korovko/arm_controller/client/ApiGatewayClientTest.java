package com.korovko.arm_controller.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import com.korovko.arm_controller.model.ChangeRetryRequest;
import com.korovko.arm_controller.model.ChangeTimeoutRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class ApiGatewayClientTest {

  @Test
  void getLatencies_callsExpectedEndpoint_andReturnsBody() {
    RestClient restClient = mock(RestClient.class);
    RestClient.RequestHeadersUriSpec<?> getSpec = mock(RestClient.RequestHeadersUriSpec.class);
    RestClient.RequestHeadersSpec<?> headersSpec = mock(RestClient.RequestHeadersSpec.class);
    RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

    ApiGatewayClient client = new ApiGatewayClient(restClient);

    Map<String, Integer> expected = Map.of("route-1", 1000);

    doReturn(getSpec).when(restClient).get();
    doReturn(headersSpec).when(getSpec).uri("/internal/timelimiters");
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expected);

    Map<String, Integer> actual = client.getLatencies();

    assertThat(actual).isEqualTo(expected);
    verify(getSpec).uri("/internal/timelimiters");
  }

  @Test
  void changeTimeout_postsRequest_toExpectedEndpoint() {
    RestClient restClient = mock(RestClient.class);
    RestClient.RequestBodyUriSpec postSpec = mock(RestClient.RequestBodyUriSpec.class);
    RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
    RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

    ApiGatewayClient client = new ApiGatewayClient(restClient);

    ChangeTimeoutRequest request = mock(ChangeTimeoutRequest.class);

    when(restClient.post()).thenReturn(postSpec);
    when(postSpec.uri("/dynamic-timeouts")).thenReturn(bodySpec);
    when(bodySpec.body(request)).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(ResponseEntity.ok().build());

    client.changeTimeout(request);

    verify(postSpec).uri("/dynamic-timeouts");
    verify(bodySpec).body(request);
    verify(responseSpec).toBodilessEntity();
  }

  @Test
  void changeRetry_postsRequest_toTemplatedEndpoint() {
    RestClient restClient = mock(RestClient.class);
    RestClient.RequestBodyUriSpec postSpec = mock(RestClient.RequestBodyUriSpec.class);
    RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
    RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

    ApiGatewayClient client = new ApiGatewayClient(restClient);

    String routeId = "customers_route";
    ChangeRetryRequest request = mock(ChangeRetryRequest.class);

    when(restClient.post()).thenReturn(postSpec);
    when(postSpec.uri("/internal/resilience/retry/{routeId}", routeId)).thenReturn(bodySpec);
    when(bodySpec.body(request)).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(ResponseEntity.ok().build());

    client.changeRetry(routeId, request);

    verify(postSpec).uri("/internal/resilience/retry/{routeId}", routeId);
    verify(bodySpec).body(request);
    verify(responseSpec).toBodilessEntity();
  }

}
