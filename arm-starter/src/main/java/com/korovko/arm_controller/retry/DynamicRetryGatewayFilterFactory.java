package com.korovko.arm_controller.retry;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

@Component("DynamicRetry")
public class DynamicRetryGatewayFilterFactory extends AbstractGatewayFilterFactory<DynamicRetryGatewayFilterFactory.Config> {

    private final RetryPolicyStore store;
    private final RetryGatewayFilterFactory delegate;

    public DynamicRetryGatewayFilterFactory(final RetryPolicyStore store, final RetryGatewayFilterFactory delegate) {
        super(Config.class);
        this.store = store;
        this.delegate = delegate;
    }

    @Override
    public GatewayFilter apply(final Config config) {
        return (exchange, chain) -> {
            RetryPolicy policy = store.get(config.getName());

            int retries = policy.toGatewayRetries();
            if (retries <= 0) {
                return chain.filter(exchange);
            }

            HttpMethod[] methods = policy.methods().stream()
                .map(s -> s == null ? null : HttpMethod.valueOf(s.trim().toUpperCase()))
                .filter(Objects::nonNull)
                .toArray(HttpMethod[]::new);

            if (methods.length == 0) {
                methods = new HttpMethod[]{HttpMethod.GET};
            }

            HttpStatus[] statuses = policy.statuses().stream()
                .map(code -> {
                    try {
                        return HttpStatus.valueOf(code);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(HttpStatus[]::new);

            if (statuses.length == 0) {
                statuses = new HttpStatus[]{HttpStatus.BAD_GATEWAY, HttpStatus.GATEWAY_TIMEOUT};
            }

            RetryGatewayFilterFactory.RetryConfig rc = new RetryGatewayFilterFactory.RetryConfig();
            rc.setRetries(retries);
            rc.setMethods(methods);
            rc.setStatuses(statuses);
            rc.setBackoff(policy.firstBackoff(), policy.maxBackoff(), policy.factor(), policy.basedOnPreviousValue());

            return delegate.apply(rc).filter(exchange, chain);
        };
    }

    @Validated
    public static class Config {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}
