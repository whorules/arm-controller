import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.korovko.starter.timeout.DynamicTimeoutUpdater;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.apache.commons.lang3.StringUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DynamicTimeoutUpdaterFuzzTest {

    @FuzzTest(maxDuration = "30s")
    void updateTimeout_should_follow_contract(FuzzedDataProvider data) {
        TimeLimiterRegistry registry = TimeLimiterRegistry.ofDefaults();
        DynamicTimeoutUpdater updater = new DynamicTimeoutUpdater(registry);

        String routeId = data.consumeBoolean()
            ? null
            : data.consumeString(0);

        long timeoutMs = data.consumeLong(-5, 5000);
        boolean cancel = data.consumeBoolean();

        if (timeoutMs <= 0 || StringUtils.isEmpty(routeId)) {
            assertThrows(IllegalArgumentException.class, () -> updater.updateTimeout(routeId, timeoutMs, cancel));
        } else {
            assertDoesNotThrow(() -> updater.updateTimeout(routeId, timeoutMs, cancel));
        }
    }

}
