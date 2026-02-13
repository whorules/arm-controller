package com.korovko.arm_controller.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
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

  public int getMinAttempts() {
    return minAttempts;
  }

  public void setMinAttempts(int minAttempts) {
    this.minAttempts = minAttempts;
  }

  public int getMaxAttempts() {
    return maxAttempts;
  }

  public void setMaxAttempts(int maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

  public int getStepSize() {
    return stepSize;
  }

  public void setStepSize(int stepSize) {
    this.stepSize = stepSize;
  }

  public double getTargetErrorPct() {
    return targetErrorPct;
  }

  public void setTargetErrorPct(double targetErrorPct) {
    this.targetErrorPct = targetErrorPct;
  }

  public double getDeadbandPct() {
    return deadbandPct;
  }

  public void setDeadbandPct(double deadbandPct) {
    this.deadbandPct = deadbandPct;
  }

  public double getPanicMultiplier() {
    return panicMultiplier;
  }

  public void setPanicMultiplier(double panicMultiplier) {
    this.panicMultiplier = panicMultiplier;
  }

  public int getDecreaseStablePeriods() {
    return decreaseStablePeriods;
  }

  public void setDecreaseStablePeriods(int decreaseStablePeriods) {
    this.decreaseStablePeriods = decreaseStablePeriods;
  }

  public int getMinIncreaseWindowMins() {
    return minIncreaseWindowMins;
  }

  public void setMinIncreaseWindowMins(int minIncreaseWindowMins) {
    this.minIncreaseWindowMins = minIncreaseWindowMins;
  }

  public int getMinDecreaseWindowMins() {
    return minDecreaseWindowMins;
  }

  public void setMinDecreaseWindowMins(int minDecreaseWindowMins) {
    this.minDecreaseWindowMins = minDecreaseWindowMins;
  }

  public int getFirstBackoffMs() {
    return firstBackoffMs;
  }

  public void setFirstBackoffMs(int firstBackoffMs) {
    this.firstBackoffMs = firstBackoffMs;
  }

  public int getMaxBackoffMs() {
    return maxBackoffMs;
  }

  public void setMaxBackoffMs(int maxBackoffMs) {
    this.maxBackoffMs = maxBackoffMs;
  }

  public int getFactor() {
    return factor;
  }

  public void setFactor(int factor) {
    this.factor = factor;
  }

}
