package com.korovko.arm_controller.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dynamic.retry")
public class RetryConfigProperties {

  private int minAttempts = 1;
  private int maxAttempts = 3;
  private int stepSize = 1;

  private double lowTransientErrorPct = 1.0;   // if >1% we consider helping with retries
  private double highTransientErrorPct = 12.0; // if >12% reduce to avoid storms

  private long minChangeWindowMins = 2;

  private long firstBackoffMs = 50;
  private long maxBackoffMs = 250;
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

  public double getLowTransientErrorPct() {
    return lowTransientErrorPct;
  }

  public void setLowTransientErrorPct(double v) {
    this.lowTransientErrorPct = v;
  }

  public double getHighTransientErrorPct() {
    return highTransientErrorPct;
  }

  public void setHighTransientErrorPct(double v) {
    this.highTransientErrorPct = v;
  }

  public long getMinChangeWindowMins() {
    return minChangeWindowMins;
  }

  public void setMinChangeWindowMins(long v) {
    this.minChangeWindowMins = v;
  }

  public long getFirstBackoffMs() {
    return firstBackoffMs;
  }

  public void setFirstBackoffMs(long v) {
    this.firstBackoffMs = v;
  }

  public long getMaxBackoffMs() {
    return maxBackoffMs;
  }

  public void setMaxBackoffMs(long v) {
    this.maxBackoffMs = v;
  }

  public int getFactor() {
    return factor;
  }

  public void setFactor(int factor) {
    this.factor = factor;
  }

}
