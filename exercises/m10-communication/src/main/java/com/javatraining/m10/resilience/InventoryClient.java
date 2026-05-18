package com.javatraining.m10.resilience;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * EXERCISE M10-T5: Resilience4j — Circuit Breaker + Retry
 *
 * Implement InventoryClient that calls a (simulated) downstream inventory service.
 *
 * Requirements:
 *  1. @Retry(name = "inventoryRetry")   — retry up to 3 times on IOException
 *  2. @CircuitBreaker(name = "inventoryCircuitBreaker", fallbackMethod = "fallbackStock")
 *     — circuit opens after 50% failure rate (min 5 calls)
 *     — fallback returns -1 when circuit is open
 *  3. Configure via application.yml (see src/main/resources/application.yml stub)
 *  4. getStock() must call the downstream URL: GET /inventory/{productId}/stock
 *     and return the stock as an Integer
 *
 * The test uses a WireMock stub to simulate the downstream service.
 *
 * DO NOT modify the method signatures.
 */
@Service
public class InventoryClient {

    private final RestClient restClient;
    private final AtomicInteger callCount = new AtomicInteger(0);

    public InventoryClient(RestClient.Builder builder, String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    /**
     * Fetch current stock for a product.
     * Decorated with @Retry and @CircuitBreaker.
     *
     * @param productId product identifier
     * @return stock level, or -1 if circuit is open (fallback)
     */
    // TODO: add @Retry(name="inventoryRetry")
    // TODO: add @CircuitBreaker(name="inventoryCircuitBreaker", fallbackMethod="fallbackStock")
    public Integer getStock(String productId) {
        callCount.incrementAndGet();
        // TODO: call GET /inventory/{productId}/stock using restClient
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Fallback: returns -1 when circuit is open or all retries exhausted.
     * Signature must match getStock() + Throwable.
     */
    public Integer fallbackStock(String productId, Throwable t) {
        return -1;
    }

    /** Exposed for testing — how many actual calls reached the downstream */
    public int getCallCount() { return callCount.get(); }
    public void resetCallCount() { callCount.set(0); }
}
