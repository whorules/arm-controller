package com.korovko.arm_controller.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Optional;

import com.korovko.arm_controller.model.PrometheusQueryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class ArmClientTest {

  @Test
  void getPrometheusQuery_buildsExpectedUri_andReturnsBody() {
    RestClient restClient = mock(RestClient.class);
    RestClient.RequestHeadersUriSpec<?> getSpec = mock(RestClient.RequestHeadersUriSpec.class);
    RestClient.RequestHeadersSpec<?> headersSpec = mock(RestClient.RequestHeadersSpec.class);
    RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

    ArmClient client = new ArmClient(restClient);

    PrometheusQueryResponse expected = mock(PrometheusQueryResponse.class);
    doReturn(getSpec).when(restClient).get();
    doReturn(headersSpec).when(getSpec).uri(any(URI.class));
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(Optional.of(expected));

    Optional<PrometheusQueryResponse> actual = client.getPrometheusQuery("up");

    assertThat(actual).containsSame(expected);

    ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
    verify(getSpec).uri(uriCaptor.capture());
    URI captured = uriCaptor.getValue();

    URI expectedUri = UriComponentsBuilder
        .fromPath("/api/v1/query")
        .queryParam("query", "up")
        .build()
        .toUri();

    assertThat(captured).isEqualTo(expectedUri);
  }

  @Test
  void getPrometheusQuery_encodesQueryParam() {
    RestClient restClient = mock(RestClient.class);
    RestClient.RequestHeadersUriSpec<?> getSpec = mock(RestClient.RequestHeadersUriSpec.class);
    RestClient.RequestHeadersSpec<?> headersSpec = mock(RestClient.RequestHeadersSpec.class);
    RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

    ArmClient client = new ArmClient(restClient);

    doReturn(getSpec).when(restClient).get();
    doReturn(headersSpec).when(getSpec).uri(any(URI.class));
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(Optional.empty());

    String query = "sum(rate(http_requests_total[5m])) by (status)";

    client.getPrometheusQuery(query);

    ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
    verify(getSpec).uri(uriCaptor.capture());
    URI captured = uriCaptor.getValue();

    URI expectedUri = UriComponentsBuilder
        .fromPath("/api/v1/query")
        .queryParam("query", query)
        .build()
        .toUri();

    assertThat(captured).isEqualTo(expectedUri);
  }

}
