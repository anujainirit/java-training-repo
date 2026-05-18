package com.javatraining.m13.mockito;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 *
 *  These tests verify your understanding of Mockito.
 *  Unlike other modules, this test FILE IS the exercise.
 *  All TODO methods below must be fully implemented by the student.
 * ══════════════════════════════════════════════════════════════
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("M13-T2: OrderService Mockito Tests — Student Implementation")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderServiceTest {

    @Mock
    private PaymentGateway paymentGateway;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private EmailNotifier emailNotifier;

    @InjectMocks
    private OrderService orderService;

    // ── Tests that must be implemented by the student ─────────────────────────

    @Test @Order(1)
    @DisplayName("TODO: Happy path — order CONFIRMED when stock available and payment succeeds")
    void happyPath() {
        // TODO: Use given() to stub:
        //   - inventoryRepository.findByProductId("P001") → item with stock=10
        //   - paymentGateway.charge("C001", 200.0) → PaymentResult(SUCCESS, "TXN123")
        //
        // When: placeOrder(new OrderRequest("C001","P001",2))
        //
        // Then: order.getStatus() == "CONFIRMED"
        //   AND verify: inventoryRepository.decrementStock("P001", 2) was called once
        //   AND verify: emailNotifier.sendOrderConfirmation("C001", order.getId()) was called

        throw new UnsupportedOperationException("Student must implement this test");
    }

    @Test @Order(2)
    @DisplayName("TODO: Payment failure — order FAILED, failure email sent")
    void paymentFailed() {
        // TODO: stub inventory OK but payment returns FAILED
        // verify sendOrderFailure is called with reason "FAILED"
        // verify decrementStock is NEVER called

        throw new UnsupportedOperationException("Student must implement this test");
    }

    @Test @Order(3)
    @DisplayName("TODO: Insufficient funds — order FAILED with INSUFFICIENT_FUNDS reason")
    void insufficientFunds() {
        // TODO: stub payment to return INSUFFICIENT_FUNDS
        // verify failure email contains "INSUFFICIENT_FUNDS" as reason

        throw new UnsupportedOperationException("Student must implement this test");
    }

    @Test @Order(4)
    @DisplayName("TODO: Product not found in inventory — order FAILED")
    void productNotInInventory() {
        // TODO: stub inventoryRepository.findByProductId() to return Optional.empty()
        // verify: paymentGateway.charge() is NEVER called
        // verify: failure email IS sent

        throw new UnsupportedOperationException("Student must implement this test");
    }

    @Test @Order(5)
    @DisplayName("TODO: Insufficient stock — order FAILED, payment NOT charged")
    void insufficientStock() {
        // TODO: stub inventory with availableStock=1, request quantity=5
        // verify payment is not attempted

        throw new UnsupportedOperationException("Student must implement this test");
    }

    @Test @Order(6)
    @DisplayName("TODO: Null request — IllegalArgumentException thrown")
    void nullRequestThrows() {
        // TODO: assert that placeOrder(null) throws IllegalArgumentException
        // No mocks needed here

        throw new UnsupportedOperationException("Student must implement this test");
    }

    @Test @Order(7)
    @DisplayName("TODO: Zero/negative quantity — IllegalArgumentException thrown")
    void nonPositiveQuantityThrows() {
        // TODO: test quantity=0 and quantity=-1

        throw new UnsupportedOperationException("Student must implement this test");
    }

    @Test @Order(8)
    @DisplayName("TODO: ArgumentCaptor — verify exact amount charged to payment gateway")
    void verifyChargedAmountWithCaptor() {
        // TODO: Use ArgumentCaptor<Double> to capture the amount argument
        // For quantity=3 at ₹100 each, verify amount == 300.0

        throw new UnsupportedOperationException("Student must implement this test");
    }

    @Test @Order(9)
    @DisplayName("TODO: Verify no more interactions after failure")
    void noMoreInteractionsAfterInventoryFailure() {
        // TODO: After inventory failure, use verifyNoInteractions(paymentGateway)
        // to assert payment was never touched

        throw new UnsupportedOperationException("Student must implement this test");
    }

    @Test @Order(10)
    @DisplayName("TODO: Multiple orders — each gets a unique ID")
    void multipleOrdersHaveUniqueIds() {
        // TODO: place 3 orders, collect IDs, assert all distinct
        // Stub mocks for all 3 calls

        throw new UnsupportedOperationException("Student must implement this test");
    }
}
