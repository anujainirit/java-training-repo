# M12 — Observability & Reliability Exercises

---

## Topic 1: Structured Logging with MDC Trace ID

### Objective
Implement a servlet filter that propagates a `traceId` through the SLF4J MDC so every log line emitted during a request automatically contains the trace identifier — enabling easy log correlation across services.

### Files
| File | Your Action |
|---|---|
| `src/main/java/.../TraceIdFilter.java` | ✅ Implement `TraceIdFilter` + `OrderProcessingService` |
| `src/main/resources/logback-spring.xml` | ✅ Complete the JSON logging config (TODOs) |
| `src/test/java/.../TraceIdFilterTest.java` | 🚫 DO NOT MODIFY (12 tests) |

### Implementation Guide

**TraceIdFilter:**
```java
@Component
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpRes = (HttpServletResponse) res;

        String traceId = Optional.ofNullable(httpReq.getHeader("X-Trace-Id"))
            .filter(s -> !s.isBlank())
            .orElse(UUID.randomUUID().toString());

        try {
            MDC.put(MdcKeys.TRACE_ID,   traceId);
            MDC.put(MdcKeys.REQUEST_ID, UUID.randomUUID().toString());
            httpRes.setHeader("X-Trace-Id", traceId);
            chain.doFilter(req, res);
        } finally {
            MDC.clear();   // CRITICAL — prevents context leak to next request
        }
    }
}
```

**OrderProcessingService — structured logging:**
```java
// Use net.logstash.logback.argument.StructuredArguments.kv()
log.info("Processing order",
    kv("orderId",    orderId),
    kv("customerId", customerId),
    kv("amount",     amount));
// traceId appears automatically from MDC — do NOT add it manually here
```

**logback-spring.xml — complete the TODOs:**
- Add `<customFields>` with application name and env
- Add `<timeZone>UTC</timeZone>`
- Uncomment the `includeMdcKeyName` lines

### Verifying Locally
```bash
# Run the app with staging profile to get JSON logs
mvn spring-boot:run -Dspring-boot.run.profiles=staging

# In another terminal, send a request with trace ID
curl -H "X-Trace-Id: my-test-trace-001" http://localhost:8080/actuator/health

# Observe the log line — it should contain:
# {"traceId":"my-test-trace-001","requestId":"...","message":"..."}
```

### Running Tests
```bash
mvn test -Dtest=TraceIdFilterTest
```

---

## Topic 3: Custom Micrometer Metrics

### Objective
Register four types of Micrometer meters in a Spring service and verify they record data correctly.

### Files
| File | Your Action |
|---|---|
| `src/main/java/.../OrderMetricsService.java` | ✅ Implement `OrderMetricsServiceImpl` |
| `src/test/java/.../OrderMetricsServiceTest.java` | 🚫 DO NOT MODIFY (14 tests) |

### Implementation Guide

```java
@Service
class OrderMetricsServiceImpl implements OrderMetricsService {

    private final Counter successCounter;
    private final Counter failureCounter;
    private final Timer processingTimer;
    private final DistributionSummary amountSummary;
    private final AtomicInteger activeOrders = new AtomicInteger(0);

    public OrderMetricsServiceImpl(MeterRegistry registry) {
        this.registry = registry;

        successCounter = Counter.builder("orders.created")
            .tag("status", "success")
            .description("Number of successfully created orders")
            .register(registry);

        failureCounter = Counter.builder("orders.created")
            .tag("status", "failure")
            .register(registry);

        processingTimer = Timer.builder("orders.processing.time")
            .description("Time to process an order")
            .register(registry);

        amountSummary = DistributionSummary.builder("orders.amount")
            .baseUnit("rupees")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(registry);

        Gauge.builder("orders.active", activeOrders, AtomicInteger::get)
            .description("Current number of active orders")
            .register(registry);
    }
}
```

**Usage in a service:**
```java
// Time an operation
Timer.Sample sample = metricsService.startOrderTimer();
try {
    metricsService.incrementActiveOrders();
    // ... do work ...
    metricsService.recordOrderCreated(true);
    metricsService.recordOrderAmount(totalAmount);
} finally {
    metricsService.decrementActiveOrders();
    metricsService.stopOrderTimer(sample);
}
```

### View in Prometheus Format
```bash
mvn spring-boot:run
curl http://localhost:8080/actuator/prometheus | grep orders
# Expected output:
# orders_created_total{status="success"} 5.0
# orders_created_total{status="failure"} 1.0
# orders_processing_time_seconds_count 6.0
# orders_active 0.0
```

---

## Acceptance Criteria (All M12 Topics)
- [ ] All 12 TraceIdFilterTest tests pass
- [ ] All 14 OrderMetricsServiceTest tests pass
- [ ] logback-spring.xml produces valid JSON with `traceId` field
- [ ] `/actuator/prometheus` exposes `orders_created_total` metric
- [ ] Coverage ≥ 85% on Java source files
- [ ] Zero SonarQube issues
