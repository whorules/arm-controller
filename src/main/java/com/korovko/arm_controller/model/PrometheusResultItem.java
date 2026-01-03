package com.korovko.arm_controller.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PrometheusResultItem {

  private Map<String, String> metric;
  private List<String> value;

  public Map<String, String> getMetric() {
    return metric;
  }

  public void setMetric(Map<String, String> metric) {
    this.metric = metric;
  }

  public List<String> getValue() {
    return value;
  }

  public void setValue(List<String> value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    PrometheusResultItem that = (PrometheusResultItem) o;
    return Objects.equals(metric, that.metric) && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(metric, value);
  }

  @Override
  public String toString() {
    return "PrometheusResultItem{" +
        "metric=" + metric +
        ", data=" + value +
        '}';
  }

}
