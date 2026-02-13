package com.korovko.arm_controller.retry;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal/resilience/retry")
public class RetryAdminController {

    private final RetryPolicyStore store;

    public RetryAdminController(final RetryPolicyStore store) {
        this.store = store;
    }

    @GetMapping
    public Map<String, RetryPolicy> getAll() {
        return store.getAll();
    }

    @GetMapping("/{routeId}")
    public RetryPolicy get(@PathVariable final String routeId) {
        return store.get(routeId);
    }

    @PostMapping("/{routeId}")
    public ResponseEntity<Void> upsert(@PathVariable final String routeId, @RequestBody final RetryPolicy policy) {
        store.upsert(routeId, policy);
        return ResponseEntity.noContent().build();
    }
}
