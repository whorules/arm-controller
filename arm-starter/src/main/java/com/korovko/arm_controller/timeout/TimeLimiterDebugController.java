package com.korovko.arm_controller.timeout;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TimeLimiterDebugController {

    private final TimeLimiterRegistry registry;

    public TimeLimiterDebugController(TimeLimiterRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/internal/timelimiters/{name}")
    public Map<String, Object> getTimeLimiter(@PathVariable String name) {
        TimeLimiter tl = registry.timeLimiter(name);
        Duration timeout = tl.getTimeLimiterConfig().getTimeoutDuration();
        boolean cancelRunning = tl.getTimeLimiterConfig().shouldCancelRunningFuture();

        return Map.of(
            "name", name,
            "timeoutMs", timeout.toMillis(),
            "cancelRunningFuture", cancelRunning
        );
    }

    @GetMapping("/internal/timelimiters")
    public Map<String, Long> getAll() {
        return registry.getAllTimeLimiters().stream()
            .collect(Collectors.toMap(
                TimeLimiter::getName,
                tl -> tl.getTimeLimiterConfig().getTimeoutDuration().toMillis()
            ));
    }
}
