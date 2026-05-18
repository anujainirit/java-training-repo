package com.javatraining.m09.saga;

import java.util.UUID;

/**
 * EXERCISE M09-T5: Order Checkout Saga (Choreography Pattern)
 *
 * Implement a simplified in-memory checkout saga:
 *
 *  Step 1: ReserveInventory   — deduct stock; emit InventoryReserved or InventoryFailed
 *  Step 2: ChargePayment      — charge customer; emit PaymentCharged or PaymentFailed
 *  Step 3: ConfirmOrder       — mark order as CONFIRMED; emit OrderConfirmed
 *
 * Compensations (if a later step fails, undo earlier steps):
 *  - PaymentFailed  → release inventory (ReleaseInventory)
 *  - Any failure    → OrderFailed event emitted
 *
 * All services communicate via the EventBus (in-memory pub/sub).
 * DO NOT add real Kafka/messaging — this exercise is about the pattern.
 *
 * Grading tests will:
 *  1. Inject faults at each step and verify compensations fire
 *  2. Verify the happy path produces OrderConfirmed
 *  3. Check that duplicate event handling is idempotent
 */

// ─── Domain ──────────────────────────────────────────────────────────────────

enum OrderStatus { PENDING, CONFIRMED, FAILED }

record OrderId(String value) {
    public static OrderId generate() { return new OrderId(UUID.randomUUID().toString()); }
}

record Order(OrderId id, String customerId, String productId, int quantity, double unitPrice) {
    public double totalAmount() { return quantity * unitPrice; }
}

// ─── Events (sealed hierarchy — DO NOT MODIFY) ───────────────────────────────

sealed interface SagaEvent permits
    CheckoutStarted,
    InventoryReserved, InventoryFailed,
    PaymentCharged, PaymentFailed,
    OrderConfirmed, OrderFailed {}

record CheckoutStarted(OrderId orderId, Order order) implements SagaEvent {}
record InventoryReserved(OrderId orderId, Order order) implements SagaEvent {}
record InventoryFailed(OrderId orderId, String reason) implements SagaEvent {}
record PaymentCharged(OrderId orderId, double amount) implements SagaEvent {}
record PaymentFailed(OrderId orderId, String reason) implements SagaEvent {}
record OrderConfirmed(OrderId orderId) implements SagaEvent {}
record OrderFailed(OrderId orderId, String reason) implements SagaEvent {}

// ─── Event Bus (DO NOT MODIFY) ───────────────────────────────────────────────

interface EventHandler<T extends SagaEvent> {
    void handle(T event);
}

class EventBus {
    private final java.util.Map<Class<?>, java.util.List<EventHandler<?>>> handlers =
        new java.util.HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends SagaEvent> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new java.util.ArrayList<>()).add(handler);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void publish(SagaEvent event) {
        var list = handlers.getOrDefault(event.getClass(), java.util.List.of());
        for (EventHandler h : list) {
            h.handle(event);
        }
    }
}

// ─── Services — IMPLEMENT THESE ──────────────────────────────────────────────

/**
 * Manages product stock in-memory.
 * Must handle reservation and release atomically.
 */
class InventoryService {
    private final EventBus eventBus;
    private final java.util.Map<String, Integer> stock;  // productId → available stock

    public InventoryService(EventBus eventBus, java.util.Map<String, Integer> initialStock) {
        this.eventBus = eventBus;
        this.stock = new java.util.HashMap<>(initialStock);
        // TODO: subscribe to CheckoutStarted events
        //       → try to reserve, emit InventoryReserved or InventoryFailed
    }

    /** Called as compensation when payment fails. */
    public void releaseReservation(OrderId orderId, String productId, int quantity) {
        // TODO: add stock back and emit a ReleaseInventoryAck (or just update stock)
        throw new UnsupportedOperationException("Not implemented");
    }

    /** For test assertions — return current stock level for a product. */
    public int availableStock(String productId) {
        return stock.getOrDefault(productId, 0);
    }
}

/**
 * Simulates payment charging.
 * Supports injecting failures via setFailNextPayment(true).
 */
class PaymentService {
    private final EventBus eventBus;
    private boolean failNextPayment = false;
    private final java.util.Map<String, Double> customerBalance;  // customerId → balance

    public PaymentService(EventBus eventBus, java.util.Map<String, Double> initialBalances) {
        this.eventBus = eventBus;
        this.customerBalance = new java.util.HashMap<>(initialBalances);
        // TODO: subscribe to InventoryReserved events
        //       → try to charge, emit PaymentCharged or PaymentFailed
    }

    public void setFailNextPayment(boolean fail) { this.failNextPayment = fail; }

    public double balanceOf(String customerId) {
        return customerBalance.getOrDefault(customerId, 0.0);
    }
}

/**
 * Tracks order statuses and handles final confirmation/failure.
 */
class OrderService {
    private final EventBus eventBus;
    private final java.util.Map<OrderId, OrderStatus> orderStatuses = new java.util.HashMap<>();

    public OrderService(EventBus eventBus) {
        this.eventBus = eventBus;
        // TODO: subscribe to:
        //   PaymentCharged  → mark CONFIRMED, emit OrderConfirmed
        //   PaymentFailed   → trigger inventory compensation, emit OrderFailed
        //   InventoryFailed → emit OrderFailed
    }

    /** Starts the saga. */
    public void checkout(Order order) {
        orderStatuses.put(order.id(), OrderStatus.PENDING);
        eventBus.publish(new CheckoutStarted(order.id(), order));
    }

    public OrderStatus getStatus(OrderId orderId) {
        return orderStatuses.getOrDefault(orderId, null);
    }
}
