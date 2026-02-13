package com.korovko.arm_controller.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

  @Value("${rest-client.armUrl}")
  private String armUrl;
  @Value("${rest-client.apiGatewayUrl}")
  private String apiGatewayUrl;

  @Bean
  public RestClient armRestClient() {
    return RestClient.builder()
        .baseUrl(armUrl)
        .build();
  }

  @Bean
  public RestClient apiGatewayRestClient() {
    return RestClient.builder()
        .baseUrl(apiGatewayUrl)
        .build();
  }

}
