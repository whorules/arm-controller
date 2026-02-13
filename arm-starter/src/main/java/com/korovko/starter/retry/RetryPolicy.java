package com.korovko.starter.retry;

import java.time.Duration;
import java.util.Set;

public record RetryPolicy(int maxAttempts, Duration firstBackoff, Duration maxBackoff, int factor,
                          boolean basedOnPreviousValue, Set<Integer> statuses, Set<String> methods) {

    public RetryPolicy {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1");
        }
        if (firstBackoff == null || firstBackoff.isNegative()) {
            throw new IllegalArgumentException("firstBackoff invalid");
        }
        if (maxBackoff == null || maxBackoff.isNegative()) {
            throw new IllegalArgumentException("maxBackoff invalid");
        }
        if (factor < 1) {
            throw new IllegalArgumentException("factor must be >= 1");
        }
        if (statuses == null || statuses.isEmpty()) {
            throw new IllegalArgumentException("statuses must not be empty");
        }
        if (methods == null || methods.isEmpty()) {
            throw new IllegalArgumentException("methods must not be empty");
        }
    }

    public int toGatewayRetries() {
        return Math.max(0, maxAttempts - 1);
    }

}
