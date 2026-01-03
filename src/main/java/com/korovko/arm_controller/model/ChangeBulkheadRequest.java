package com.korovko.arm_controller.model;

public record ChangeBulkheadRequest(int maxConcurrentCalls, long maxWaitMs) {}
