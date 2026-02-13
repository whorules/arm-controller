package com.korovko.starter.retry;

import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RetryPolicyStore {

    private final Map<String, RetryPolicy> policies = new ConcurrentHashMap<>();

    @PostConstruct
    public void initDefaults() {
        RetryPolicy defaultPolicy = new RetryPolicy(
            1,
            Duration.ofMillis(50),
            Duration.ofMillis(250),
            2,
            true,
            Set.of(HttpStatus.BAD_GATEWAY.value(), HttpStatus.SERVICE_UNAVAILABLE.value()),
            Set.of(HttpMethod.GET.name())
        );

        policies.put("customers_route", defaultPolicy);
        policies.put("vets_route", defaultPolicy);
        policies.put("visits_route", defaultPolicy);
    }

    public RetryPolicy get(String routeId) {
        RetryPolicy p = policies.get(routeId);
        if (p == null) throw new IllegalArgumentException("Unknown routeId: " + routeId);
        return p;
    }

    public Map<String, RetryPolicy> getAll() {
        return Map.copyOf(policies);
    }

    public void upsert(String routeId, RetryPolicy policy) {
        policies.put(routeId, policy);
    }
}
