package com.javatraining.m12.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * EXERCISE M12-T3: Custom Micrometer Metrics
 *
 * Implement OrderMetricsService that registers and updates:
 *
 *   1. Counter   — "orders.created"          tags: status=success|failure
 *   2. Timer     — "orders.processing.time"  measures how long processOrder() takes
 *   3. Gauge     — "orders.active"           current count of in-progress orders
 *   4. DistSummary — "orders.amount"         distribution of order amounts (₹)
 *
 * Rules:
 *  - Register all metrics in the constructor via MeterRegistry
 *  - Tags must use Micrometer's Tags.of() API
 *  - Timer must use Timer.Sample for accurate measurement
 *  - Gauge must reflect the live AtomicInteger value
 *
 * DO NOT modify method signatures or the interface.
 */
public interface OrderMetricsService {
    void recordOrderCreated(boolean success);
    void recordOrderAmount(double amount);
    Timer.Sample startOrderTimer();
    void stopOrderTimer(Timer.Sample sample);
    void incrementActiveOrders();
    void decrementActiveOrders();
    int getActiveOrderCount();
}

// ── Implement this class ──────────────────────────────────────────────────────

@Service
class OrderMetricsServiceImpl implements OrderMetricsService {

    private final MeterRegistry registry;
    private final AtomicInteger activeOrders = new AtomicInteger(0);

    // TODO: declare Counter, Timer, DistributionSummary fields

    public OrderMetricsServiceImpl(MeterRegistry registry) {
        this.registry = registry;
        // TODO: register all metrics

        // Counter example:
        // Counter.builder("orders.created").tag("status", "success").register(registry);

        // Timer example:
        // Timer.builder("orders.processing.time").register(registry);

        // Gauge example:
        // Gauge.builder("orders.active", activeOrders, AtomicInteger::get).register(registry);

        // DistributionSummary example:
        // DistributionSummary.builder("orders.amount").baseUnit("rupees").register(registry);
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void recordOrderCreated(boolean success) {
        // TODO: increment the right counter (success or failure tag)
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void recordOrderAmount(double amount) {
        // TODO: record in distribution summary
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Timer.Sample startOrderTimer() {
        // TODO: return Timer.start(registry)
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void stopOrderTimer(Timer.Sample sample) {
        // TODO: sample.stop(timer)
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void incrementActiveOrders() {
        activeOrders.incrementAndGet();
    }

    @Override
    public void decrementActiveOrders() {
        activeOrders.decrementAndGet();
    }

    @Override
    public int getActiveOrderCount() {
        return activeOrders.get();
    }
}
