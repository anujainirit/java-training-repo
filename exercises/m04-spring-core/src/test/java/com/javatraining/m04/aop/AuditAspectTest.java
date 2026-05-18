package com.javatraining.m04.aop;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 * ══════════════════════════════════════════════════════════════
 */
@SpringBootTest
@DisplayName("M04-T3: Audit AOP Aspect — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuditAspectTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuditLog auditLog;

    @BeforeEach
    void clearLog() {
        auditLog.clear();
    }

    // ── @Auditable — happy path ──────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("@Auditable method: one entry recorded in AuditLog")
    void auditableMethodRecordsEntry() {
        orderService.processOrder("ORD001", 5000.0);
        assertThat(auditLog.getEntries()).hasSize(1);
    }

    @Test @Order(2)
    @DisplayName("AuditEntry has correct class and method name")
    void auditEntryClassAndMethod() {
        orderService.processOrder("ORD002", 1000.0);
        AuditEntry entry = auditLog.getEntries().get(0);

        assertThat(entry.className()).contains("OrderService");
        assertThat(entry.methodName()).isEqualTo("processOrder");
    }

    @Test @Order(3)
    @DisplayName("AuditEntry captures arguments")
    void auditEntryArgs() {
        orderService.processOrder("ORD003", 2500.0);
        AuditEntry entry = auditLog.getEntries().get(0);

        assertThat(entry.args()).contains("ORD003", 2500.0);
    }

    @Test @Order(4)
    @DisplayName("AuditEntry has non-null timestamp")
    void auditEntryTimestamp() {
        orderService.processOrder("ORD004", 100.0);
        assertThat(auditLog.getEntries().get(0).timestamp()).isNotNull();
    }

    @Test @Order(5)
    @DisplayName("AuditEntry records durationMs >= 0")
    void auditEntryDuration() {
        orderService.processOrder("ORD005", 100.0);
        assertThat(auditLog.getEntries().get(0).durationMs()).isGreaterThanOrEqualTo(0);
    }

    @Test @Order(6)
    @DisplayName("Successful method: success=true, errorMessage=null")
    void auditEntrySuccessTrue() {
        orderService.processOrder("ORD006", 100.0);
        AuditEntry entry = auditLog.getEntries().get(0);
        assertThat(entry.success()).isTrue();
        assertThat(entry.errorMessage()).isNull();
    }

    @Test @Order(7)
    @DisplayName("Multiple @Auditable calls: multiple entries recorded")
    void multipleAuditableCalls() {
        orderService.processOrder("ORD010", 100.0);
        orderService.processOrder("ORD011", 200.0);
        orderService.processOrder("ORD012", 300.0);
        assertThat(auditLog.getEntries()).hasSize(3);
    }

    // ── @Auditable — exception path ──────────────────────────────────────────

    @Test @Order(8)
    @DisplayName("Exception in @Auditable method: entry recorded with success=false")
    void auditableMethodExceptionRecorded() {
        assertThatThrownBy(() -> orderService.cancelOrder("INVALID-ORD"))
            .isInstanceOf(IllegalArgumentException.class);

        assertThat(auditLog.getEntries()).hasSize(1);
        AuditEntry entry = auditLog.getEntries().get(0);
        assertThat(entry.success()).isFalse();
        assertThat(entry.errorMessage()).isNotBlank();
    }

    @Test @Order(9)
    @DisplayName("Exception is re-thrown after audit logging")
    void exceptionRethrown() {
        assertThatThrownBy(() -> orderService.cancelOrder("INVALID-XYZ"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("INVALID-XYZ");
    }

    @Test @Order(10)
    @DisplayName("AuditLog.getEntries() returns unmodifiable list")
    void auditLogImmutable() {
        orderService.processOrder("ORD020", 100.0);
        List<AuditEntry> entries = auditLog.getEntries();
        assertThatThrownBy(() -> entries.add(null))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    // ── @LogExecutionTime ────────────────────────────────────────────────────

    @Test @Order(11)
    @DisplayName("@LogExecutionTime: fast method does NOT throw PerformanceException")
    void fastMethodDoesNotThrow() throws Exception {
        assertThatNoException().isThrownBy(() -> orderService.slowOperation());
    }

    @Test @Order(12)
    @DisplayName("@LogExecutionTime: slow method THROWS PerformanceException when threshold exceeded")
    void slowMethodThrowsPerformanceException() {
        assertThatThrownBy(() -> orderService.verySlowOperation())
            .isInstanceOf(PerformanceException.class);
    }

    @Test @Order(13)
    @DisplayName("PerformanceException contains actual and threshold values")
    void performanceExceptionDetails() {
        PerformanceException ex = catchThrowableOfType(
            () -> orderService.verySlowOperation(), PerformanceException.class);

        assertThat(ex.getThresholdMs()).isEqualTo(5);
        assertThat(ex.getActualMs()).isGreaterThan(5);
    }

    @Test @Order(14)
    @DisplayName("@LogExecutionTime does NOT record in AuditLog (different annotation)")
    void executionTimeNotAudited() throws Exception {
        orderService.slowOperation();
        assertThat(auditLog.getEntries()).isEmpty();
    }

    @Test @Order(15)
    @DisplayName("Non-annotated methods are NOT intercepted by AuditAspect")
    void nonAnnotatedMethodNotAudited() {
        orderService.getProcessedCount();
        assertThat(auditLog.getEntries()).isEmpty();
    }
}
