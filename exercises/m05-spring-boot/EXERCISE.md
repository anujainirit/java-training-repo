# M05 — Topic 1: Product Catalogue REST API

## Objective
Build a fully functional, validated REST API for a product catalogue using Spring Boot.

## Problem Statement
An e-commerce platform needs a product management service. You will implement  
the service layer, controller, and global exception handler for a CRUD API.

## Files
| File | Your Action |
|---|---|
| `src/main/java/.../ProductApi.java` | ✅ Implement all TODO sections |
| `src/test/java/.../ProductApiTest.java` | 🚫 DO NOT MODIFY |

## Steps

### Step 1 — Implement `ProductService`
1. Add `@Service` annotation
2. Use `Map<Long, ProductResponse>` with `AtomicLong` counter for in-memory storage
3. Implement all 5 methods: `create`, `findAll`, `findById`, `updateStock`, `delete`
4. `findById` and `updateStock` must throw `ProductNotFoundException` when ID not found

### Step 2 — Create `ProductNotFoundException`
```java
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found: " + id);
    }
}
```

### Step 3 — Implement `ProductController`
- `@RestController` + `@RequestMapping("/api/products")`
- Constructor-inject `ProductService`
- Return `ResponseEntity.status(201).body(...)` for POST
- Return `ResponseEntity.noContent().build()` for DELETE

### Step 4 — Implement `GlobalExceptionHandler`
- `@RestControllerAdvice`
- Handle `ProductNotFoundException` → 404 `ProblemDetail`
- Handle `MethodArgumentNotValidException` → 400 `ProblemDetail` with field error details

```java
// ProblemDetail example
ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
pd.setTitle("Product Not Found");
return pd;
```

## API Contract

| Method | Path | Request | Response |
|---|---|---|---|
| POST | `/api/products` | `CreateProductRequest` | 201 `ProductResponse` |
| GET | `/api/products` | — | 200 `List<ProductResponse>` |
| GET | `/api/products?category=X` | — | 200 filtered list |
| GET | `/api/products/{id}` | — | 200 or 404 |
| PUT | `/api/products/{id}/stock` | `UpdateStockRequest` | 200 or 404 |
| DELETE | `/api/products/{id}` | — | 204 or 404 |

## Validation Rules
| Field | Rule |
|---|---|
| `name` | Not blank |
| `category` | Not blank |
| `price` | Not null, ≥ 0.01 |
| `stock` | ≥ 0 |

## Running Locally
```bash
cd exercises/m05-spring-boot
mvn test
mvn spring-boot:run   # test manually with curl or Postman

# Example:
curl -s -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","category":"Electronics","price":75000,"stock":10}'
```

## Acceptance Criteria
- [ ] All 19 test cases pass
- [ ] Coverage ≥ 85%
- [ ] Zero SonarQube issues
- [ ] Errors follow RFC 7807 ProblemDetail format
