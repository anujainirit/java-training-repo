package com.javatraining.m02.concurrency;

import org.junit.jupiter.api.*;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 * ══════════════════════════════════════════════════════════════
 */
@DisplayName("M02-T4: AsyncPipeline — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AsyncPipelineTest {

    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        executor = Executors.newFixedThreadPool(4);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        executor.shutdownNow();
        executor.awaitTermination(2, TimeUnit.SECONDS);
    }

    @Test @Order(1)
    @DisplayName("Happy path: all inputs succeed, results mapped correctly")
    void allSucceed() {
        AsyncPipeline<String, Integer> pipeline = new AsyncPipeline<>(executor, 2000);

        ProcessingResult<String, Integer> result = pipeline.execute(
            List.of("a", "bb", "ccc"),
            input -> CompletableFuture.supplyAsync(() -> input.length(), executor),
            len -> len * 10
        );

        assertThat(result.successCount()).isEqualTo(3);
        assertThat(result.failureCount()).isEqualTo(0);
        assertThat(result.successes()).containsEntry("a", 10)
                                      .containsEntry("bb", 20)
                                      .containsEntry("ccc", 30);
    }

    @Test @Order(2)
    @DisplayName("Failed fetcher: failure captured, not thrown")
    void fetcherFailureCaptured() {
        AsyncPipeline<String, String> pipeline = new AsyncPipeline<>(executor, 2000);

        ProcessingResult<String, String> result = pipeline.execute(
            List.of("ok", "fail", "ok2"),
            input -> {
                if ("fail".equals(input)) {
                    return CompletableFuture.failedFuture(new RuntimeException("fetch error"));
                }
                return CompletableFuture.completedFuture(input.toUpperCase());
            },
            s -> s + "!"
        );

        assertThat(result.successCount()).isEqualTo(2);
        assertThat(result.failureCount()).isEqualTo(1);
        assertThat(result.failures()).containsKey("fail");
        assertThat(result.failures().get("fail")).contains("fetch error");
        assertThat(result.successes()).containsEntry("ok", "OK!").containsEntry("ok2", "OK2!");
    }

    @Test @Order(3)
    @DisplayName("Transform exception: failure captured, not thrown")
    void transformFailureCaptured() {
        AsyncPipeline<Integer, Integer> pipeline = new AsyncPipeline<>(executor, 2000);

        ProcessingResult<Integer, Integer> result = pipeline.execute(
            List.of(10, 0, 5),
            input -> CompletableFuture.completedFuture(input),
            divisor -> 100 / divisor  // throws ArithmeticException for 0
        );

        assertThat(result.successCount()).isEqualTo(2);
        assertThat(result.failureCount()).isEqualTo(1);
        assertThat(result.failures()).containsKey(0);
    }

    @Test @Order(4)
    @DisplayName("Timeout: slow tasks captured as failures")
    void timeoutCaptured() {
        AsyncPipeline<String, String> pipeline = new AsyncPipeline<>(executor, 100); // 100ms timeout

        ProcessingResult<String, String> result = pipeline.execute(
            List.of("fast", "slow"),
            input -> CompletableFuture.supplyAsync(() -> {
                if ("slow".equals(input)) {
                    try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
                return input;
            }, executor),
            s -> s
        );

        assertThat(result.successCount()).isEqualTo(1);
        assertThat(result.failureCount()).isEqualTo(1);
        assertThat(result.failures()).containsKey("slow");
    }

    @Test @Order(5)
    @DisplayName("Fetches execute in parallel — total time < sum of individual times")
    void executesInParallel() {
        AsyncPipeline<Integer, Integer> pipeline = new AsyncPipeline<>(executor, 5000);
        int delay = 200; // ms each

        long start = System.currentTimeMillis();
        ProcessingResult<Integer, Integer> result = pipeline.execute(
            List.of(1, 2, 3, 4),
            input -> CompletableFuture.supplyAsync(() -> {
                try { Thread.sleep(delay); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return input;
            }, executor),
            i -> i
        );
        long elapsed = System.currentTimeMillis() - start;

        assertThat(result.successCount()).isEqualTo(4);
        // Sequential would be 800ms+; parallel should be ~200-300ms
        assertThat(elapsed).isLessThan(600);
    }

    @Test @Order(6)
    @DisplayName("Empty input list returns empty result without throwing")
    void emptyInput() {
        AsyncPipeline<String, String> pipeline = new AsyncPipeline<>(executor, 1000);
        ProcessingResult<String, String> result = pipeline.execute(
            List.of(),
            input -> CompletableFuture.completedFuture(input),
            s -> s
        );
        assertThat(result.successCount()).isEqualTo(0);
        assertThat(result.failureCount()).isEqualTo(0);
        assertThat(result.hasFailures()).isFalse();
    }

    @Test @Order(7)
    @DisplayName("All failures: result has 0 successes")
    void allFailures() {
        AsyncPipeline<String, String> pipeline = new AsyncPipeline<>(executor, 1000);
        ProcessingResult<String, String> result = pipeline.execute(
            List.of("a", "b", "c"),
            input -> CompletableFuture.failedFuture(new RuntimeException("always fails")),
            s -> s
        );
        assertThat(result.successCount()).isEqualTo(0);
        assertThat(result.failureCount()).isEqualTo(3);
        assertThat(result.hasFailures()).isTrue();
    }

    @Test @Order(8)
    @DisplayName("Fetcher is called exactly once per input")
    void fetcherCalledOnce() {
        AtomicInteger callCount = new AtomicInteger(0);
        AsyncPipeline<String, String> pipeline = new AsyncPipeline<>(executor, 2000);

        pipeline.execute(
            List.of("x", "y", "z"),
            input -> {
                callCount.incrementAndGet();
                return CompletableFuture.completedFuture(input);
            },
            s -> s
        );

        assertThat(callCount.get()).isEqualTo(3);
    }
}
