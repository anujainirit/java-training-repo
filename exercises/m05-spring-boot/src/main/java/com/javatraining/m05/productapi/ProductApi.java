package com.javatraining.m05.productapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// ─────────────────────────────────────────────────────────────────────────────
// DTO — DO NOT MODIFY
// ─────────────────────────────────────────────────────────────────────────────

record CreateProductRequest(
    @NotBlank(message = "name must not be blank")
    String name,

    @NotBlank(message = "category must not be blank")
    String category,

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.01", message = "price must be at least 0.01")
    Double price,

    @Min(value = 0, message = "stock must be non-negative")
    int stock
) {}

record UpdateStockRequest(
    @Min(value = 0, message = "stock must be non-negative")
    int stock
) {}

record ProductResponse(Long id, String name, String category, double price, int stock) {}

// ─────────────────────────────────────────────────────────────────────────────
// SERVICE — implement this
// ─────────────────────────────────────────────────────────────────────────────

/**
 * EXERCISE M05-T1: Product Catalogue REST API
 *
 * Implement the ProductService and ProductController below.
 *
 * API contract (verified by grading tests):
 *
 *   POST   /api/products              → 201 Created, body = ProductResponse
 *   GET    /api/products              → 200 OK, body = List<ProductResponse>
 *   GET    /api/products/{id}         → 200 OK or 404 Not Found
 *   GET    /api/products?category=X   → 200 OK, filtered list
 *   PUT    /api/products/{id}/stock   → 200 OK, updated ProductResponse
 *   DELETE /api/products/{id}         → 204 No Content or 404
 *
 * Validation errors must return 400 Bad Request with RFC 7807 ProblemDetail.
 * Unknown product IDs must return 404 with a meaningful message.
 *
 * IDs are auto-assigned (sequential starting from 1).
 * Store products in-memory (no DB required for this exercise).
 */
// @Service   ← uncomment after implementing
class ProductService {

    // TODO: choose in-memory store (e.g., Map<Long, ProductResponse>)

    public ProductResponse create(CreateProductRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public List<ProductResponse> findAll(Optional<String> category) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public ProductResponse findById(Long id) {
        // throw ProductNotFoundException if not found
        throw new UnsupportedOperationException("Not implemented");
    }

    public ProductResponse updateStock(Long id, int newStock) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void delete(Long id) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EXCEPTION — implement this
// ─────────────────────────────────────────────────────────────────────────────

// TODO: Create ProductNotFoundException (extends RuntimeException)
// It should store the product ID that was not found.

// ─────────────────────────────────────────────────────────────────────────────
// CONTROLLER — implement this
// ─────────────────────────────────────────────────────────────────────────────

/**
 * TODO: Annotate with @RestController and @RequestMapping("/api/products")
 * Inject ProductService via constructor injection.
 * Map each method to the correct HTTP verb and path.
 * Use @Valid on request bodies.
 */
class ProductController {

    // TODO: inject ProductService

    // POST /api/products
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // GET /api/products[?category=X]
    public List<ProductResponse> findAll(@RequestParam Optional<String> category) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // GET /api/products/{id}
    public ProductResponse findById(@PathVariable Long id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // PUT /api/products/{id}/stock
    public ProductResponse updateStock(@PathVariable Long id,
                                       @Valid @RequestBody UpdateStockRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // DELETE /api/products/{id}
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GLOBAL EXCEPTION HANDLER — implement this
// ─────────────────────────────────────────────────────────────────────────────

/**
 * TODO:
 *  - Annotate with @RestControllerAdvice
 *  - Handle ProductNotFoundException → 404 with ProblemDetail
 *  - Handle MethodArgumentNotValidException → 400 with ProblemDetail listing field errors
 */
class GlobalExceptionHandler {
    // TODO: implement handlers
}
