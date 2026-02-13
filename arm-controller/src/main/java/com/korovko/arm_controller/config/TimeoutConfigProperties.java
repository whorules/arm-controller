package com.korovko.arm_controller.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dynamic.timeout")
public class TimeoutConfigProperties {

  private Integer max = 1500;
  private Integer min = 500;
  private Integer stepSize = 100;
  private Integer minIncreaseWindowMins = 1;
  private Integer minDecreaseWindowMins = 2;
  private Integer targetErrorRate = 4;
  private Double deadbandPct = 0.75;
  private Integer decreaseStablePeriods = 1;
  private Double panicMultiplier = 2.0;

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

  public Integer getTargetErrorRate() {
    return targetErrorRate;
  }

  public void setTargetErrorRate(Integer targetErrorRate) {
    this.targetErrorRate = targetErrorRate;
  }

  public Double getDeadbandPct() {
    return deadbandPct;
  }

  public void setDeadbandPct(Double deadbandPct) {
    this.deadbandPct = deadbandPct;
  }

  public Integer getDecreaseStablePeriods() {
    return decreaseStablePeriods;
  }

  public void setDecreaseStablePeriods(Integer decreaseStablePeriods) {
    this.decreaseStablePeriods = decreaseStablePeriods;
  }

  public Integer getMinIncreaseWindowMins() {
    return minIncreaseWindowMins;
  }

  public void setMinIncreaseWindowMins(Integer minIncreaseWindowMins) {
    this.minIncreaseWindowMins = minIncreaseWindowMins;
  }

  public Integer getMinDecreaseWindowMins() {
    return minDecreaseWindowMins;
  }

  public void setMinDecreaseWindowMins(Integer minDecreaseWindowMins) {
    this.minDecreaseWindowMins = minDecreaseWindowMins;
  }

  public Double getPanicMultiplier() {
    return panicMultiplier;
  }

  public void setPanicMultiplier(Double panicMultiplier) {
    this.panicMultiplier = panicMultiplier;
  }

}
