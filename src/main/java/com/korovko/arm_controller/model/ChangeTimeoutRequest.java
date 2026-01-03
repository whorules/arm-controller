package com.korovko.arm_controller.model;

import java.util.Objects;

public class ChangeTimeoutRequest {

  private String routeId;
  private Integer timeoutMillis;

  public ChangeTimeoutRequest() {
  }

  public ChangeTimeoutRequest(String routeId, Integer timeoutMillis) {
    this.routeId = routeId;
    this.timeoutMillis = timeoutMillis;
  }

  public String getRouteId() {
    return routeId;
  }

  public void setRouteId(String routeId) {
    this.routeId = routeId;
  }

  public Integer getTimeoutMillis() {
    return timeoutMillis;
  }

  public void setTimeoutMillis(Integer timeoutMillis) {
    this.timeoutMillis = timeoutMillis;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ChangeTimeoutRequest that = (ChangeTimeoutRequest) o;
    return Objects.equals(routeId, that.routeId) && Objects.equals(timeoutMillis, that.timeoutMillis);
  }

  @Override
  public int hashCode() {
    return Objects.hash(routeId, timeoutMillis);
  }

  @Override
  public String toString() {
    return "ChangeLatencyRequest{" +
        "routeId='" + routeId + '\'' +
        ", timeoutMillis=" + timeoutMillis +
        '}';
  }

}
