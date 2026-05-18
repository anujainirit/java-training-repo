package com.javatraining.m12.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

/**
 * EXERCISE M12-T1: Structured Logging with MDC Trace ID
 *
 * Implement a request tracing filter that:
 *  1. Extracts (or generates) a trace-id from the X-Trace-Id request header
 *  2. Stores it in MDC so every log line in the request thread contains it
 *  3. Adds the trace-id to the response header X-Trace-Id
 *  4. Clears MDC after the request completes (prevents leakage to next request)
 *
 * Also implement OrderProcessingService which:
 *  - Logs at INFO level with structured fields (amount, customerId)
 *  - Uses the trace-id already in MDC — do NOT pass it as a parameter
 *
 * The grading tests verify:
 *  - MDC is populated before service code runs
 *  - MDC is cleared after response
 *  - Response contains X-Trace-Id header
 *  - Logs contain the traceId field when captured
 *
 * DO NOT modify method signatures.
 */

// ── MDC constants (DO NOT MODIFY) ────────────────────────────────────────────
final class MdcKeys {
    static final String TRACE_ID   = "traceId";
    static final String REQUEST_ID = "requestId";
    static final String USER_ID    = "userId";
    private MdcKeys() {}
}

// ── TraceIdFilter — implement this ───────────────────────────────────────────

/**
 * TODO:
 *  - Annotate with @Component so Spring registers it automatically
 *  - Implement jakarta.servlet.Filter
 *  - In doFilter():
 *      a. Read X-Trace-Id header; if absent, generate UUID
 *      b. MDC.put(MdcKeys.TRACE_ID, traceId)
 *      c. MDC.put(MdcKeys.REQUEST_ID, UUID.randomUUID().toString())
 *      d. Add X-Trace-Id to response header
 *      e. Call chain.doFilter(request, response)
 *      f. MDC.clear() in a finally block
 */
class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented");
    }
}

// ── OrderProcessingService — implement this ───────────────────────────────────

/**
 * TODO:
 *  - Add @Service
 *  - processOrder() must log at INFO: "Processing order" with fields orderId, customerId, amount
 *  - completeOrder() must log at INFO: "Order completed" with field orderId
 *  - failOrder() must log at ERROR: "Order failed" with fields orderId and errorReason
 *  - Do NOT put traceId in method calls — it comes from MDC automatically
 */
class OrderProcessingService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessingService.class);

    public void processOrder(String orderId, String customerId, double amount) {
        // TODO: log with structured fields using parameterised logging
        // log.info("Processing order", kv("orderId", orderId), ...);
        // Or use Logstash markers: StructuredArguments.kv()
        throw new UnsupportedOperationException("Not implemented");
    }

    public void completeOrder(String orderId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void failOrder(String orderId, String errorReason) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
