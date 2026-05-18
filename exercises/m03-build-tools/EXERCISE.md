# M03 — Topic 1: Multi-Module Maven Project

## Objective
Structure a real-world Java application as a **multi-module Maven project** with clear layer separation, shared BOM-style dependency management, and a Spring Boot web layer that wires the modules together.

## Module Structure

```
m03-build-tools/          ← Parent POM (packaging=pom)
├── m03-api/              ← Interfaces + DTOs only (no Spring, no impl)
├── m03-impl/             ← InMemoryProductService implements ProductService
└── m03-webapp/           ← Spring Boot app, wires impl, exposes REST endpoints
```

## Files & Tasks

| File | Your Action |
|---|---|
| `m03-api/.../ProductService.java` | 🚫 DO NOT MODIFY (interface is given) |
| `m03-impl/.../InMemoryProductService.java` | ✅ Implement all 6 methods |
| `m03-webapp/.../Application.java` | ✅ Complete `AppConfig` + `ProductController` |
| `m03-impl/src/test/.../InMemoryProductServiceTest.java` | 🚫 DO NOT MODIFY (22 tests) |
| `m03-webapp/src/test/.../ProductControllerIntegrationTest.java` | 🚫 DO NOT MODIFY (10 tests) |

## Step-by-Step

### Step 1 — Implement `InMemoryProductService` (m03-impl)
- Use `LinkedHashMap<Long, ProductDto>` to preserve insertion order
- Use `AtomicLong` for ID generation (starts at 1, never reused)
- Validate all inputs — throw `IllegalArgumentException` with clear message
- `findAll(null)` returns everything; `findAll("Electronics")` filters case-insensitively

### Step 2 — Wire into Spring Boot (m03-webapp)

**AppConfig.java:**
```java
@Configuration
class AppConfig {
    @Bean
    public ProductService productService() {
        return new InMemoryProductService();
    }
}
```

**ProductController.java:**
```java
@RestController
@RequestMapping("/api/v1/products")
class ProductController {
    private final ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody CreateProductDto dto) {
        return ResponseEntity.status(201).body(productService.create(dto));
    }
    // ... other endpoints
}
```

### Step 3 — PATCH /{id}/stock endpoint
Use `PATCH` (not `PUT`) since we're updating a single field:
```java
@PatchMapping("/{id}/stock")
public ResponseEntity<ProductDto> updateStock(@PathVariable Long id,
                                               @RequestBody UpdateStockRequest req) {
    return productService.updateStock(id, req.stock())
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}
```

## Verifying the Module Structure

```bash
cd exercises/m03-build-tools

# Build all modules in dependency order
mvn clean install -DskipTests

# Check m03-api produces a plain JAR (no Spring Boot fat jar)
ls m03-api/target/*.jar    # should be ~5KB

# Check m03-webapp produces an executable fat jar
ls m03-webapp/target/*.jar  # should be ~20MB

# Run the app
java -jar m03-webapp/target/m03-webapp-1.0.0-SNAPSHOT.jar

# Test manually
curl -s -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","category":"Electronics","price":75000,"stock":10}' | jq .
```

## Running the Grading Tests

```bash
# Test impl module (22 unit tests)
mvn test -pl m03-impl

# Test webapp module (10 integration tests)
mvn test -pl m03-webapp

# Full build with coverage
mvn verify
```

## Acceptance Criteria
- [ ] `mvn clean install` succeeds on the parent POM
- [ ] All 22 impl unit tests pass
- [ ] All 10 webapp integration tests pass
- [ ] m03-api JAR has no transitive Spring dependencies
- [ ] m03-webapp uses constructor injection (no `@Autowired` on fields)
- [ ] Coverage ≥ 85% across all modules
- [ ] Zero SonarQube issues
