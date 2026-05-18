package com.javatraining.m09.saga;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 * ══════════════════════════════════════════════════════════════
 */
@DisplayName("M09-T5: Checkout Saga — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CheckoutSagaTest {

    private EventBus eventBus;
    private InventoryService inventoryService;
    private PaymentService paymentService;
    private OrderService orderService;
    private List<SagaEvent> publishedEvents;

    @BeforeEach
    void setUp() {
        eventBus = new EventBus();
        publishedEvents = new ArrayList<>();

        // Spy on all events
        for (Class<? extends SagaEvent> type : List.of(
                CheckoutStarted.class, InventoryReserved.class, InventoryFailed.class,
                PaymentCharged.class, PaymentFailed.class, OrderConfirmed.class, OrderFailed.class)) {
            eventBus.subscribe(type, publishedEvents::add);
        }

        inventoryService = new InventoryService(eventBus,
            Map.of("LAPTOP", 10, "PHONE", 5));

        paymentService = new PaymentService(eventBus,
            Map.of("customer1", 100_000.0, "customer2", 1000.0));  // customer2 is poor

        orderService = new OrderService(eventBus);
    }

    // ── Happy path ──────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("Successful checkout: order CONFIRMED, inventory deducted, payment charged")
    void happyPath() {
        Order order = new Order(OrderId.generate(), "customer1", "LAPTOP", 2, 75_000.0);
        orderService.checkout(order);

        assertThat(orderService.getStatus(order.id())).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(inventoryService.availableStock("LAPTOP")).isEqualTo(8);  // 10 - 2
        assertThat(paymentService.balanceOf("customer1")).isEqualTo(100_000.0 - 150_000.0);

        // Event sequence must include InventoryReserved → PaymentCharged → OrderConfirmed
        assertThat(publishedEvents)
            .extracting(e -> e.getClass().getSimpleName())
            .containsSubsequence("CheckoutStarted", "InventoryReserved", "PaymentCharged", "OrderConfirmed");
    }

    // ── Inventory failure ───────────────────────────────────────────────────

    @Test @Order(2)
    @DisplayName("Inventory insufficient: order FAILED, InventoryFailed event published")
    void inventoryFailed() {
        Order order = new Order(OrderId.generate(), "customer1", "LAPTOP", 100, 75_000.0); // only 10 in stock
        orderService.checkout(order);

        assertThat(orderService.getStatus(order.id())).isEqualTo(OrderStatus.FAILED);
        assertThat(inventoryService.availableStock("LAPTOP")).isEqualTo(10); // unchanged

        assertThat(publishedEvents)
            .extracting(e -> e.getClass().getSimpleName())
            .contains("InventoryFailed", "OrderFailed")
            .doesNotContain("PaymentCharged", "OrderConfirmed");
    }

    @Test @Order(3)
    @DisplayName("Inventory failure: payment is NOT attempted")
    void paymentNotAttemptedWhenInventoryFails() {
        Order order = new Order(OrderId.generate(), "customer1", "LAPTOP", 999, 75_000.0);
        orderService.checkout(order);

        long paymentEvents = publishedEvents.stream()
            .filter(e -> e instanceof PaymentCharged || e instanceof PaymentFailed)
            .count();
        assertThat(paymentEvents).isZero();
    }

    // ── Payment failure + compensation ──────────────────────────────────────

    @Test @Order(4)
    @DisplayName("Payment failure: order FAILED, inventory is released (compensation)")
    void paymentFailedInventoryReleased() {
        paymentService.setFailNextPayment(true);

        Order order = new Order(OrderId.generate(), "customer1", "LAPTOP", 3, 75_000.0);
        orderService.checkout(order);

        assertThat(orderService.getStatus(order.id())).isEqualTo(OrderStatus.FAILED);
        // Compensation: stock must be back to 10
        assertThat(inventoryService.availableStock("LAPTOP")).isEqualTo(10);

        assertThat(publishedEvents)
            .extracting(e -> e.getClass().getSimpleName())
            .containsSubsequence("InventoryReserved", "PaymentFailed", "OrderFailed")
            .doesNotContain("OrderConfirmed");
    }

    @Test @Order(5)
    @DisplayName("Payment failure: customer balance is NOT deducted")
    void customerBalanceUnchangedOnPaymentFailure() {
        paymentService.setFailNextPayment(true);
        double balanceBefore = paymentService.balanceOf("customer1");

        Order order = new Order(OrderId.generate(), "customer1", "LAPTOP", 1, 75_000.0);
        orderService.checkout(order);

        assertThat(paymentService.balanceOf("customer1")).isEqualTo(balanceBefore);
    }

    // ── Insufficient balance ─────────────────────────────────────────────────

    @Test @Order(6)
    @DisplayName("Insufficient customer balance: order FAILED, inventory released")
    void insufficientBalance() {
        // customer2 has ₹1000 balance, order costs ₹2000
        Order order = new Order(OrderId.generate(), "customer2", "PHONE", 1, 2000.0);
        orderService.checkout(order);

        assertThat(orderService.getStatus(order.id())).isEqualTo(OrderStatus.FAILED);
        assertThat(inventoryService.availableStock("PHONE")).isEqualTo(5); // fully restored
    }

    // ── Multiple concurrent orders ───────────────────────────────────────────

    @Test @Order(7)
    @DisplayName("Two concurrent orders: stock correctly reduced for both")
    void twoSuccessfulOrders() {
        Order order1 = new Order(OrderId.generate(), "customer1", "LAPTOP", 3, 10_000.0);
        Order order2 = new Order(OrderId.generate(), "customer1", "LAPTOP", 2, 10_000.0);

        orderService.checkout(order1);
        orderService.checkout(order2);

        assertThat(orderService.getStatus(order1.id())).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(orderService.getStatus(order2.id())).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(inventoryService.availableStock("LAPTOP")).isEqualTo(5); // 10 - 3 - 2
    }

    @Test @Order(8)
    @DisplayName("Second order fails when first order depletes stock")
    void secondOrderFailsWhenStockDepleted() {
        Order order1 = new Order(OrderId.generate(), "customer1", "PHONE", 5, 1000.0); // all 5
        Order order2 = new Order(OrderId.generate(), "customer1", "PHONE", 1, 1000.0); // none left

        orderService.checkout(order1);
        orderService.checkout(order2);

        assertThat(orderService.getStatus(order1.id())).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(orderService.getStatus(order2.id())).isEqualTo(OrderStatus.FAILED);
    }

    // ── Event sequence ───────────────────────────────────────────────────────

    @Test @Order(9)
    @DisplayName("Each checkout emits exactly one CheckoutStarted event")
    void exactlyOneCheckoutStarted() {
        Order order = new Order(OrderId.generate(), "customer1", "LAPTOP", 1, 10_000.0);
        orderService.checkout(order);

        long count = publishedEvents.stream()
            .filter(e -> e instanceof CheckoutStarted cs && cs.orderId().equals(order.id()))
            .count();
        assertThat(count).isEqualTo(1);
    }

    @Test @Order(10)
    @DisplayName("OrderConfirmed event contains the correct orderId")
    void confirmedEventHasCorrectId() {
        Order order = new Order(OrderId.generate(), "customer1", "LAPTOP", 1, 10_000.0);
        orderService.checkout(order);

        assertThat(publishedEvents)
            .filteredOn(e -> e instanceof OrderConfirmed)
            .hasSize(1)
            .first()
            .extracting(e -> ((OrderConfirmed) e).orderId())
            .isEqualTo(order.id());
    }
}
