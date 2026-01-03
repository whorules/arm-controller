package com.korovko.arm_controller.model;

import java.time.Duration;
import java.util.Set;

public record ChangeRetryRequest(
    int maxAttempts,
    Duration firstBackoff,
    Duration maxBackoff,
    int factor,
    boolean basedOnPreviousValue,
    Set<Integer> statuses,
    Set<String> methods
) {}
