package com.javatraining.m13.mockito;

import java.util.Optional;

/**
 * EXERCISE M13-T2: Mockito — Unit Test OrderService
 *
 * The production code (OrderService, PaymentGateway, InventoryRepository, EmailNotifier)
 * is provided here. Your task is to write comprehensive unit tests in:
 *   src/test/java/.../OrderServiceTest.java
 *
 * Requirements:
 *  - Mock all dependencies (PaymentGateway, InventoryRepository, EmailNotifier)
 *  - Use @ExtendWith(MockitoExtension.class) — NO Spring context
 *  - Use ArgumentCaptor to verify arguments passed to mocks
 *  - Cover: happy path, payment failure, inventory unavailable, null inputs
 *  - Use BDDMockito (given/when/then style)
 *  - Achieve ≥ 85% coverage on OrderService
 *
 * DO NOT modify the production code below.
 */

// ── Domain objects ────────────────────────────────────────────────────────────

record OrderRequest(String customerId, String productId, int quantity) {}

enum PaymentStatus { SUCCESS, FAILED, INSUFFICIENT_FUNDS }

record PaymentResult(PaymentStatus status, String transactionId) {}

record InventoryItem(String productId, int availableStock) {}

class Order {
    private final String id;
    private final String customerId;
    private final String productId;
    private final int quantity;
    private String status;  // PENDING, CONFIRMED, FAILED

    public Order(String id, String customerId, String productId, int quantity) {
        this.id = id; this.customerId = customerId;
        this.productId = productId; this.quantity = quantity;
        this.status = "PENDING";
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

// ── Interfaces to mock ────────────────────────────────────────────────────────

interface PaymentGateway {
    PaymentResult charge(String customerId, double amount);
}

interface InventoryRepository {
    Optional<InventoryItem> findByProductId(String productId);
    void decrementStock(String productId, int quantity);
}

interface EmailNotifier {
    void sendOrderConfirmation(String customerId, String orderId);
    void sendOrderFailure(String customerId, String orderId, String reason);
}

// ── Service under test ────────────────────────────────────────────────────────

class OrderService {

    private static final double PRICE_PER_UNIT = 100.0;

    private final PaymentGateway paymentGateway;
    private final InventoryRepository inventoryRepository;
    private final EmailNotifier emailNotifier;

    public OrderService(PaymentGateway paymentGateway,
                        InventoryRepository inventoryRepository,
                        EmailNotifier emailNotifier) {
        this.paymentGateway = paymentGateway;
        this.inventoryRepository = inventoryRepository;
        this.emailNotifier = emailNotifier;
    }

    public Order placeOrder(OrderRequest request) {
        if (request == null) throw new IllegalArgumentException("Request must not be null");
        if (request.quantity() <= 0) throw new IllegalArgumentException("Quantity must be positive");

        Order order = new Order(
            java.util.UUID.randomUUID().toString(),
            request.customerId(), request.productId(), request.quantity()
        );

        // Check inventory
        Optional<InventoryItem> item = inventoryRepository.findByProductId(request.productId());
        if (item.isEmpty() || item.get().availableStock() < request.quantity()) {
            order.setStatus("FAILED");
            emailNotifier.sendOrderFailure(request.customerId(), order.getId(), "INSUFFICIENT_STOCK");
            return order;
        }

        // Charge payment
        double amount = request.quantity() * PRICE_PER_UNIT;
        PaymentResult payment = paymentGateway.charge(request.customerId(), amount);

        if (payment.status() != PaymentStatus.SUCCESS) {
            order.setStatus("FAILED");
            emailNotifier.sendOrderFailure(request.customerId(), order.getId(),
                                           payment.status().name());
            return order;
        }

        // Confirm
        inventoryRepository.decrementStock(request.productId(), request.quantity());
        order.setStatus("CONFIRMED");
        emailNotifier.sendOrderConfirmation(request.customerId(), order.getId());
        return order;
    }
}
