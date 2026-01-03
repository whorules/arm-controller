package com.korovko.arm_controller.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dynamic.bulkhead")
public class BulkheadConfigProperties {

  private int minConcurrent = 20;
  private int maxConcurrent = 30;
  private int stepSize = 1;

  // Decision thresholds
  private double highTimeout504Pct = 15.0;   // above this => reduce concurrency
  private double lowTimeout504Pct = 8.0;    // below this => consider increasing

  private double target429Low = 1.0;
  private double target429High = 4.0;
  private double hardMax429 = 8.0;

  private double highP99Ms = 1450;          // above this => reduce concurrency
  private double lowP99Ms = 1200;           // below this => consider increasing

  private long minChangeWindowMins = 1;

  private long maxWaitMs = 0;               // fail fast

  public int getMinConcurrent() {
    return minConcurrent;
  }

  public void setMinConcurrent(int v) {
    this.minConcurrent = v;
  }

  public int getMaxConcurrent() {
    return maxConcurrent;
  }

  public void setMaxConcurrent(int v) {
    this.maxConcurrent = v;
  }

  public int getStepSize() {
    return stepSize;
  }

  public void setStepSize(int v) {
    this.stepSize = v;
  }

  public double getHighTimeout504Pct() {
    return highTimeout504Pct;
  }

  public void setHighTimeout504Pct(double v) {
    this.highTimeout504Pct = v;
  }

  public double getLowTimeout504Pct() {
    return lowTimeout504Pct;
  }

  public void setLowTimeout504Pct(double v) {
    this.lowTimeout504Pct = v;
  }

  public double getHighP99Ms() {
    return highP99Ms;
  }

  public void setHighP99Ms(double v) {
    this.highP99Ms = v;
  }

  public double getLowP99Ms() {
    return lowP99Ms;
  }

  public void setLowP99Ms(double v) {
    this.lowP99Ms = v;
  }

  public long getMinChangeWindowMins() {
    return minChangeWindowMins;
  }

  public void setMinChangeWindowMins(long v) {
    this.minChangeWindowMins = v;
  }

  public long getMaxWaitMs() {
    return maxWaitMs;
  }

  public void setMaxWaitMs(long v) {
    this.maxWaitMs = v;
  }

  public double getTarget429High() {
    return target429High;
  }

  public void setTarget429High(double target429High) {
    this.target429High = target429High;
  }

  public double getTarget429Low() {
    return target429Low;
  }

  public void setTarget429Low(double target429Low) {
    this.target429Low = target429Low;
  }

  public double getHardMax429() {
    return hardMax429;
  }

  public void setHardMax429(double hardMax429) {
    this.hardMax429 = hardMax429;
  }

}
