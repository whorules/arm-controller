package com.korovko.starter.timeout;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class DynamicTimeoutUpdater {

    private static final Logger log = LoggerFactory.getLogger(DynamicTimeoutUpdater.class);

    private final TimeLimiterRegistry timeLimiterRegistry;

    public DynamicTimeoutUpdater(TimeLimiterRegistry timeLimiterRegistry) {
        this.timeLimiterRegistry = timeLimiterRegistry;
    }

    public void updateTimeout(final String name, final long timeoutMs, boolean cancelRunningFuture) {
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeoutMs must be > 0");
        }
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name of the route must not be null");
        }

        TimeLimiterConfig newConfig = TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofMillis(timeoutMs))
            .cancelRunningFuture(cancelRunningFuture)
            .build();

        TimeLimiter newTl = TimeLimiter.of(name, newConfig);

        timeLimiterRegistry.replace(name, newTl);

        log.info("Updated TimeLimiter [{}]: timeout={}ms, cancelRunningFuture={}",
            name, timeoutMs, cancelRunningFuture);
    }
}
