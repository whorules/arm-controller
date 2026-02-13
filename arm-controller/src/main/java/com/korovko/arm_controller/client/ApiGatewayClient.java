package com.korovko.arm_controller.client;

import com.korovko.arm_controller.model.ChangeRetryRequest;
import com.korovko.arm_controller.model.ChangeTimeoutRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class ApiGatewayClient {

  private final RestClient apiGatewayRestClient;

  public ApiGatewayClient(RestClient apiGatewayRestClient) {
    this.apiGatewayRestClient = apiGatewayRestClient;
  }

  public Map<String, Integer> getLatencies() {
    return apiGatewayRestClient.get()
        .uri("/internal/timelimiters")
        .retrieve()
        .body(new ParameterizedTypeReference<>() {});
  }

  public void changeTimeout(final ChangeTimeoutRequest request) {
    apiGatewayRestClient.post()
        .uri("/dynamic-timeouts")
        .body(request)
        .retrieve()
        .toBodilessEntity();
  }

  public void changeRetry(final String routeId, final ChangeRetryRequest request) {
    apiGatewayRestClient.post()
        .uri("/internal/resilience/retry/{routeId}", routeId)
        .body(request)
        .retrieve()
        .toBodilessEntity();
  }

}
