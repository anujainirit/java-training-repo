# M09 — Topic 5: Checkout Saga (Choreography Pattern)

## Objective
Implement a distributed transaction using the **Saga Choreography** pattern —  
all in-memory, no real Kafka. The focus is on the _pattern_, not the infrastructure.

## Problem Statement
An e-commerce checkout involves three steps that must be coordinated:
1. **Reserve inventory** — deduct stock for the ordered product
2. **Charge payment** — debit the customer's account  
3. **Confirm order** — mark order as CONFIRMED

If any step fails, previously completed steps must be **compensated** (undone).

## Files
| File | Your Action |
|---|---|
| `src/main/java/.../CheckoutSaga.java` | ✅ Implement all TODO sections |
| `src/test/java/.../CheckoutSagaTest.java` | 🚫 DO NOT MODIFY |

## The Event Flow

```
checkout(order)
    │
    └─► EventBus.publish(CheckoutStarted)
              │
              ▼ InventoryService listens
         [stock OK?]
          ├─ YES → publish(InventoryReserved)
          │             │
          │             ▼ PaymentService listens
          │        [balance OK?]
          │         ├─ YES → publish(PaymentCharged)
          │         │             │
          │         │             ▼ OrderService listens
          │         │          publish(OrderConfirmed) → status=CONFIRMED
          │         │
          │         └─ NO → publish(PaymentFailed)
          │                       │
          │                       ▼ OrderService listens
          │                   COMPENSATION: release inventory
          │                   publish(OrderFailed) → status=FAILED
          │
          └─ NO → publish(InventoryFailed)
                        │
                        ▼ OrderService listens
                    publish(OrderFailed) → status=FAILED
```

## Implementation Steps

### 1. `InventoryService` constructor
```java
// Subscribe to CheckoutStarted
eventBus.subscribe(CheckoutStarted.class, event -> {
    Order order = event.order();
    int currentStock = stock.getOrDefault(order.productId(), 0);
    if (currentStock >= order.quantity()) {
        stock.put(order.productId(), currentStock - order.quantity());
        eventBus.publish(new InventoryReserved(event.orderId(), order));
    } else {
        eventBus.publish(new InventoryFailed(event.orderId(), "Insufficient stock"));
    }
});
```

### 2. `PaymentService` constructor
Subscribe to `InventoryReserved` — charge customer if `failNextPayment` is false  
and customer balance is sufficient.

### 3. `OrderService` constructor
Subscribe to:
- `PaymentCharged` → mark CONFIRMED, emit `OrderConfirmed`
- `PaymentFailed` → call `inventoryService.releaseReservation()`, emit `OrderFailed`  
- `InventoryFailed` → emit `OrderFailed`

### 4. `InventoryService.releaseReservation()`
Add the quantity back to stock (compensation transaction).

## Key Rules
- Every handler must be **idempotent** where possible
- Status transitions: `PENDING → CONFIRMED` or `PENDING → FAILED` only
- The `EventBus` is synchronous — handlers fire immediately when `publish()` is called

## Running Locally
```bash
cd exercises/m09-microservices
mvn test -Dtest=CheckoutSagaTest -pl .
```

## Acceptance Criteria
- [ ] All 10 test cases pass
- [ ] Compensation fires correctly on payment failure
- [ ] Coverage ≥ 85%
- [ ] Zero SonarQube issues
