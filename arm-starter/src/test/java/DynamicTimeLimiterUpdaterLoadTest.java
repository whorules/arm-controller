import com.korovko.starter.timeout.DynamicTimeLimiterUpdater;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DynamicTimeLimiterUpdaterLoadTest {

    private TimeLimiterRegistry timeLimiterRegistry;
    private DynamicTimeLimiterUpdater updater;

    @BeforeEach
    void setUp() {
        timeLimiterRegistry = TimeLimiterRegistry.ofDefaults();
        updater = new DynamicTimeLimiterUpdater(timeLimiterRegistry);
    }

    @Test
    void load_singleThreaded_updates() {
        int routes = 1000;
        int iterations = 100_000;
        Random random = new Random(42);

        long startNanos = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            String routeId = "route-" + (i % routes);
            long timeoutMs = 100 + random.nextInt(2000);
            boolean cancelRunningFuture = (i % 2 == 0);

            updater.updateTimeout(routeId, timeoutMs, cancelRunningFuture);
        }

        long endNanos = System.nanoTime();
        double totalMillis = (endNanos - startNanos) / 1_000_000.0;
        double avgMicros = (endNanos - startNanos) / (double) iterations / 1000.0;

        System.out.println("Single-threaded:");
        System.out.println("  iterations = " + iterations);
        System.out.println("  total time = " + totalMillis + " ms");
        System.out.println("  avg per call = " + avgMicros + " µs");
        System.out.println("  registry size = " + timeLimiterRegistry.getAllTimeLimiters().size());

        Assertions.assertTrue(timeLimiterRegistry.getAllTimeLimiters().size() <= routes);
    }

    @Test
    void load_multiThreaded_updates() throws InterruptedException {
        int routes = 1000;
        int iterationsPerThread = 50_000;
        int threads = 8;
        Random random = new Random(42);

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        long startNanos = System.nanoTime();

        for (int t = 0; t < threads; t++) {
            executor.submit(() -> {
                for (int i = 0; i < iterationsPerThread; i++) {
                    String routeId = "route-" + (i % routes);
                    long timeoutMs = 100 + random.nextInt(2000);
                    boolean cancelRunningFuture = (i % 2 == 0);

                    updater.updateTimeout(routeId, timeoutMs, cancelRunningFuture);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

        long endNanos = System.nanoTime();
        double totalMillis = (endNanos - startNanos) / 1_000_000.0;
        long totalIterations = (long) iterationsPerThread * threads;
        double avgMicros = (endNanos - startNanos) / (double) totalIterations / 1000.0;

        System.out.println("Multi-threaded:");
        System.out.println("  threads = " + threads);
        System.out.println("  total iterations = " + totalIterations);
        System.out.println("  total time = " + totalMillis + " ms");
        System.out.println("  avg per call = " + avgMicros + " µs");
        System.out.println("  registry size = " + timeLimiterRegistry.getAllTimeLimiters().size());

        Assertions.assertTrue(timeLimiterRegistry.getAllTimeLimiters().size() <= routes);
    }
}
