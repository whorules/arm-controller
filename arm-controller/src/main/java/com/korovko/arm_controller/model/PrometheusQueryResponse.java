package com.korovko.arm_controller.model;

import java.util.Objects;

public class PrometheusQueryResponse {

  private String status;
  private PrometheusData data;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public PrometheusData getData() {
    return data;
  }

  public void setData(PrometheusData data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    PrometheusQueryResponse that = (PrometheusQueryResponse) o;
    return Objects.equals(status, that.status) && Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, data);
  }

  @Override
  public String toString() {
    return "PrometheusQueryResponse{" +
        "status='" + status + '\'' +
        ", data=" + data +
        '}';
  }

}
