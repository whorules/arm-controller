package com.korovko.arm_controller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

  @Bean
  public RestClient armRestClient() {
    return RestClient.builder()
        .baseUrl("http://127.0.0.1:9091")
        .build();
  }

  @Bean
  public RestClient apiGatewayRestClient() {
    return RestClient.builder()
        .baseUrl("http://127.0.0.1:8080")
        .build();
  }

}
