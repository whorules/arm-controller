package com.korovko.arm_controller.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "latency")
public class TimeoutConfigProperties {

  private Integer max = 1500;
  private Integer min = 700;
  private Integer stepSize = 700;
  private Integer minChangeWindowMins = 700;
  private Integer acceptableErrorRate = 700;

  public Integer getMax() {
    return max;
  }

  public void setMax(Integer max) {
    this.max = max;
  }

  public Integer getMin() {
    return min;
  }

  public void setMin(Integer min) {
    this.min = min;
  }

  public Integer getStepSize() {
    return stepSize;
  }

  public void setStepSize(Integer stepSize) {
    this.stepSize = stepSize;
  }

  public Integer getMinChangeWindowMins() {
    return minChangeWindowMins;
  }

  public void setMinChangeWindowMins(Integer minChangeWindowMins) {
    this.minChangeWindowMins = minChangeWindowMins;
  }

  public Integer getAcceptableErrorRate() {
    return acceptableErrorRate;
  }

  public void setAcceptableErrorRate(Integer acceptableErrorRate) {
    this.acceptableErrorRate = acceptableErrorRate;
  }

}
