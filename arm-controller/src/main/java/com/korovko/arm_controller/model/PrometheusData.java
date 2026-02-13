package com.korovko.arm_controller.model;

import java.util.List;
import java.util.Objects;

public class PrometheusData {

  private String resultType;
  private List<PrometheusResultItem> result;

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  public List<PrometheusResultItem> getResult() {
    return result;
  }

  public void setResult(List<PrometheusResultItem>  result) {
    this.result = result;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    PrometheusData that = (PrometheusData) o;
    return Objects.equals(resultType, that.resultType) && Objects.equals(result, that.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultType, result);
  }

  @Override
  public String toString() {
    return "PrometheusData{" +
        "resultType='" + resultType + '\'' +
        ", result=" + result +
        '}';
  }

}
