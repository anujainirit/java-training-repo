package com.javatraining.m04.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * EXERCISE M04-T3: Audit Logging Aspect
 *
 * Implement:
 *  1. @Auditable annotation — marks methods that must be audited
 *  2. AuditAspect — @Around advice that:
 *       a. Records an AuditEntry BEFORE the method runs (with timestamp, class, method, args)
 *       b. Records execution duration (ms)
 *       c. Records whether the method succeeded or threw an exception
 *       d. Stores all entries in AuditLog
 *  3. @LogExecutionTime annotation — marks methods whose execution time should be logged
 *  4. ExecutionTimeAspect — @Around advice that:
 *       a. Measures wall-clock time in ms
 *       b. Throws PerformanceException if execution exceeds the annotation's thresholdMs
 *
 * Rules:
 *  - Use constructor injection everywhere (no @Autowired on fields)
 *  - AuditLog must be a Spring bean (@Component)
 *  - Aspects must be Spring beans (@Aspect + @Component)
 */

// ── @Auditable annotation ────────────────────────────────────────────────────

// TODO: define @Auditable
// @Target(ElementType.METHOD)
// @Retention(RetentionPolicy.RUNTIME)
// public @interface Auditable {
//     String action() default "";  // optional description of the action
// }


// ── @LogExecutionTime annotation ─────────────────────────────────────────────

// TODO: define @LogExecutionTime
// @Target(ElementType.METHOD)
// @Retention(RetentionPolicy.RUNTIME)
// public @interface LogExecutionTime {
//     long thresholdMs() default Long.MAX_VALUE;  // throw if exceeded
// }


// ── AuditEntry record (DO NOT MODIFY) ────────────────────────────────────────

record AuditEntry(
    Instant timestamp,
    String className,
    String methodName,
    Object[] args,
    long durationMs,
    boolean success,
    String errorMessage   // null if success
) {}


// ── AuditLog bean — implement this ───────────────────────────────────────────

@Component
class AuditLog {
    private final List<AuditEntry> entries = new ArrayList<>();

    public void record(AuditEntry entry) {
        // TODO: thread-safely add entry
        throw new UnsupportedOperationException("Not implemented");
    }

    public List<AuditEntry> getEntries() {
        // TODO: return unmodifiable snapshot
        throw new UnsupportedOperationException("Not implemented");
    }

    public void clear() {
        entries.clear();
    }
}


// ── AuditAspect — implement this ─────────────────────────────────────────────

@Aspect
@Component
class AuditAspect {

    // TODO: inject AuditLog via constructor

    // TODO: @Around("@annotation(auditable)")
    // public Object audit(ProceedingJoinPoint pjp, Auditable auditable) throws Throwable { ... }
}


// ── PerformanceException (DO NOT MODIFY) ─────────────────────────────────────

class PerformanceException extends RuntimeException {
    private final long actualMs;
    private final long thresholdMs;

    public PerformanceException(String method, long actualMs, long thresholdMs) {
        super(String.format("Method '%s' took %dms, exceeded threshold of %dms",
                            method, actualMs, thresholdMs));
        this.actualMs = actualMs;
        this.thresholdMs = thresholdMs;
    }

    public long getActualMs() { return actualMs; }
    public long getThresholdMs() { return thresholdMs; }
}


// ── ExecutionTimeAspect — implement this ─────────────────────────────────────

@Aspect
@Component
class ExecutionTimeAspect {
    // TODO: @Around("@annotation(logExecutionTime)")
    // public Object measureTime(ProceedingJoinPoint pjp, LogExecutionTime logExecutionTime) throws Throwable { ... }
    // Throw PerformanceException if actualMs > logExecutionTime.thresholdMs()
}


// ── Sample service (DO NOT MODIFY — used by tests) ───────────────────────────

@Service
class OrderService {

    private int processedCount = 0;

    // TODO: annotate with @Auditable(action = "PROCESS_ORDER")
    public String processOrder(String orderId, double amount) {
        processedCount++;
        return "ORDER_PROCESSED:" + orderId;
    }

    // TODO: annotate with @Auditable(action = "CANCEL_ORDER")
    public void cancelOrder(String orderId) {
        if (orderId.startsWith("INVALID")) {
            throw new IllegalArgumentException("Invalid order ID: " + orderId);
        }
    }

    // TODO: annotate with @LogExecutionTime(thresholdMs = 50)
    public String slowOperation() throws InterruptedException {
        Thread.sleep(10); // fast — should NOT trigger PerformanceException
        return "done";
    }

    // TODO: annotate with @LogExecutionTime(thresholdMs = 5)
    public String verySlowOperation() throws InterruptedException {
        Thread.sleep(100); // slow — SHOULD trigger PerformanceException
        return "done";
    }

    public int getProcessedCount() { return processedCount; }
}
