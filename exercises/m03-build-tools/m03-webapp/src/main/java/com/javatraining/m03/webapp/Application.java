package com.javatraining.m03.webapp;

import com.javatraining.m03.api.CreateProductDto;
import com.javatraining.m03.api.ProductDto;
import com.javatraining.m03.api.ProductService;
import com.javatraining.m03.impl.InMemoryProductService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * EXERCISE M03-T1 — webapp module
 *
 * Wire the impl module into a Spring Boot REST API.
 *
 * Tasks:
 *  1. Complete AppConfig: expose InMemoryProductService as a Spring @Bean
 *  2. Complete ProductController: map each method to the correct HTTP endpoint
 *  3. Run `mvn spring-boot:run` from m03-webapp and test with curl
 *
 * The grading tests boot the full Spring context and call real HTTP endpoints.
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// ── Configuration ─────────────────────────────────────────────────────────────

@Configuration
class AppConfig {

    // TODO: declare InMemoryProductService as a @Bean
    // @Bean
    // public ProductService productService() { ... }
}

// ── Controller ────────────────────────────────────────────────────────────────

/**
 * TODO:
 *  - Add @RestController and @RequestMapping("/api/v1/products")
 *  - Inject ProductService via constructor
 *  - Map endpoints:
 *      POST   /               → 201 Created
 *      GET    /               → 200 OK (optional ?category= filter)
 *      GET    /{id}           → 200 OK or 404
 *      PATCH  /{id}/stock     → 200 OK or 404
 *      DELETE /{id}           → 204 No Content or 404
 */
class ProductController {

    // TODO: inject ProductService

    public ResponseEntity<ProductDto> create(@RequestBody CreateProductDto dto) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public List<ProductDto> findAll(@RequestParam(required = false) String category) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public ResponseEntity<ProductDto> findById(@PathVariable Long id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public ResponseEntity<ProductDto> updateStock(@PathVariable Long id,
                                                   @RequestBody UpdateStockRequest req) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public ResponseEntity<Void> delete(@PathVariable Long id) {
        throw new UnsupportedOperationException("Not implemented");
    }
}

record UpdateStockRequest(int stock) {}
