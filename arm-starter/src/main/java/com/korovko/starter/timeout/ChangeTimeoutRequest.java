package com.korovko.starter.timeout;

import java.util.Objects;

public class ChangeTimeoutRequest {

    private String routeId;
    private Integer timeoutMillis;

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

}
