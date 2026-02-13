package com.korovko.arm_controller.client;

import com.korovko.arm_controller.model.PrometheusQueryResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Component
public class ArmClient {

  private static final String QUERY_PATH = "/api/v1/query";
  private static final String QUERY_PARAM = "query";

  private final RestClient armRestClient;

  public ArmClient(RestClient armRestClient) {
    this.armRestClient = armRestClient;
  }

  public Optional<PrometheusQueryResponse> getPrometheusQuery(final String query) {
    URI uri = this.buildQueryUri(query);
    return armRestClient.get()
        .uri(uri)
        .retrieve()
        .body(new ParameterizedTypeReference<>() {});
  }

  private URI buildQueryUri(final String query) {
    return UriComponentsBuilder
        .fromPath(QUERY_PATH)
        .queryParam(QUERY_PARAM, query)
        .build()
        .toUri();
  }

}
