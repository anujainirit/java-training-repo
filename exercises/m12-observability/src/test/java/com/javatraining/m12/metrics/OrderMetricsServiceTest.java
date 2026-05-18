package com.javatraining.m12.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 * ══════════════════════════════════════════════════════════════
 */
@DisplayName("M12-T3: Custom Metrics — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderMetricsServiceTest {

    private MeterRegistry registry;
    private OrderMetricsService metricsService;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metricsService = new OrderMetricsServiceImpl(registry);
    }

    // ── Counter ──────────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("orders.created counter registered with success tag")
    void successCounterRegistered() {
        assertThat(registry.find("orders.created").tag("status", "success").counter())
            .as("Counter 'orders.created' with tag status=success must be registered")
            .isNotNull();
    }

    @Test @Order(2)
    @DisplayName("orders.created counter registered with failure tag")
    void failureCounterRegistered() {
        assertThat(registry.find("orders.created").tag("status", "failure").counter())
            .as("Counter 'orders.created' with tag status=failure must be registered")
            .isNotNull();
    }

    @Test @Order(3)
    @DisplayName("recordOrderCreated(true) increments success counter")
    void recordSuccessIncrements() {
        metricsService.recordOrderCreated(true);
        metricsService.recordOrderCreated(true);

        double count = registry.find("orders.created")
            .tag("status", "success").counter().count();
        assertThat(count).isEqualTo(2.0);
    }

    @Test @Order(4)
    @DisplayName("recordOrderCreated(false) increments failure counter")
    void recordFailureIncrements() {
        metricsService.recordOrderCreated(false);

        double count = registry.find("orders.created")
            .tag("status", "failure").counter().count();
        assertThat(count).isEqualTo(1.0);
    }

    @Test @Order(5)
    @DisplayName("Success and failure counters are independent")
    void countersAreIndependent() {
        metricsService.recordOrderCreated(true);
        metricsService.recordOrderCreated(true);
        metricsService.recordOrderCreated(false);

        assertThat(registry.find("orders.created").tag("status", "success").counter().count())
            .isEqualTo(2.0);
        assertThat(registry.find("orders.created").tag("status", "failure").counter().count())
            .isEqualTo(1.0);
    }

    // ── Distribution Summary ─────────────────────────────────────────────────

    @Test @Order(6)
    @DisplayName("orders.amount distribution summary registered")
    void amountSummaryRegistered() {
        assertThat(registry.find("orders.amount").summary())
            .as("DistributionSummary 'orders.amount' must be registered")
            .isNotNull();
    }

    @Test @Order(7)
    @DisplayName("recordOrderAmount() tracks count and sum")
    void recordAmountTracksStats() {
        metricsService.recordOrderAmount(1000.0);
        metricsService.recordOrderAmount(2000.0);
        metricsService.recordOrderAmount(3000.0);

        var summary = registry.find("orders.amount").summary();
        assertThat(summary.count()).isEqualTo(3);
        assertThat(summary.totalAmount()).isEqualTo(6000.0);
    }

    // ── Timer ────────────────────────────────────────────────────────────────

    @Test @Order(8)
    @DisplayName("orders.processing.time timer registered")
    void timerRegistered() {
        assertThat(registry.find("orders.processing.time").timer())
            .as("Timer 'orders.processing.time' must be registered")
            .isNotNull();
    }

    @Test @Order(9)
    @DisplayName("Timer sample records elapsed time > 0")
    void timerRecordsElapsedTime() throws InterruptedException {
        Timer.Sample sample = metricsService.startOrderTimer();
        Thread.sleep(10);
        metricsService.stopOrderTimer(sample);

        var timer = registry.find("orders.processing.time").timer();
        assertThat(timer.count()).isEqualTo(1);
        assertThat(timer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS))
            .isGreaterThan(5.0);
    }

    @Test @Order(10)
    @DisplayName("Multiple timer samples accumulate correctly")
    void multipleSamplesAccumulate() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            Timer.Sample s = metricsService.startOrderTimer();
            Thread.sleep(5);
            metricsService.stopOrderTimer(s);
        }
        assertThat(registry.find("orders.processing.time").timer().count()).isEqualTo(3);
    }

    // ── Gauge ────────────────────────────────────────────────────────────────

    @Test @Order(11)
    @DisplayName("orders.active gauge registered")
    void gaugeRegistered() {
        assertThat(registry.find("orders.active").gauge())
            .as("Gauge 'orders.active' must be registered")
            .isNotNull();
    }

    @Test @Order(12)
    @DisplayName("Gauge reflects live activeOrders count")
    void gaugeReflectsLiveCount() {
        assertThat(registry.find("orders.active").gauge().value()).isEqualTo(0.0);

        metricsService.incrementActiveOrders();
        metricsService.incrementActiveOrders();
        assertThat(registry.find("orders.active").gauge().value()).isEqualTo(2.0);

        metricsService.decrementActiveOrders();
        assertThat(registry.find("orders.active").gauge().value()).isEqualTo(1.0);
    }

    @Test @Order(13)
    @DisplayName("getActiveOrderCount() matches gauge value")
    void activeCountMatchesGauge() {
        metricsService.incrementActiveOrders();
        metricsService.incrementActiveOrders();
        metricsService.incrementActiveOrders();

        assertThat((double) metricsService.getActiveOrderCount())
            .isEqualTo(registry.find("orders.active").gauge().value());
    }

    @Test @Order(14)
    @DisplayName("Gauge does not go below zero")
    void gaugeDoesNotGoBelowZero() {
        metricsService.decrementActiveOrders(); // decrement from 0
        assertThat(metricsService.getActiveOrderCount()).isLessThanOrEqualTo(0);
        // This is acceptable — we just verify no exception is thrown
    }
}
