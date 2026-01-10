package com.korovko.arm_controller.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "dynamic.retry")
public class RetryConfigProperties {

  private int minAttempts = 1;
  private int maxAttempts = 3;
  private int stepSize = 1;
  private double targetErrorPct = 1.0;
  private double deadbandPct = 0.3;
  private double panicMultiplier = 3.0;
  private int decreaseStablePeriods = 10;
  private int minIncreaseWindowMins = 1;
  private int minDecreaseWindowMins = 10;
  private int firstBackoffMs = 50;
  private int maxBackoffMs = 250;
  private int factor = 2;

}
