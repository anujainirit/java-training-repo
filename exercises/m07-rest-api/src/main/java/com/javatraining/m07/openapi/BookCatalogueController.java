package com.javatraining.m07.openapi;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * EXERCISE M07-T3 & T4: OpenAPI Annotations + Content Negotiation + @JsonView
 *
 * This exercise combines two topics:
 *
 *  PART A — OpenAPI Annotations:
 *    Annotate every endpoint with @Operation, @ApiResponse, @Tag.
 *    Run the app and verify Swagger UI at http://localhost:8080/swagger-ui.html
 *    shows all endpoints with descriptions and response schemas.
 *
 *  PART B — Content Negotiation + @JsonView:
 *    The API must return:
 *      - JSON when Accept: application/json  (default)
 *      - XML  when Accept: application/xml
 *
 *    @JsonView must expose different fields per role:
 *      Views.Public  → id, title, author, year  (no price, no cost)
 *      Views.Admin   → all fields including price and costPrice
 *
 *    The role is simulated via X-Role header: "ADMIN" or "USER" (default USER)
 *
 * DO NOT modify the Views interface or DTOs — only add annotations and complete
 * the controller methods.
 */

// ── JsonView markers (DO NOT MODIFY) ─────────────────────────────────────────
interface Views {
    interface Public {}
    interface Admin extends Public {}
}

// ── DTOs (DO NOT MODIFY) ─────────────────────────────────────────────────────

class BookCatalogueEntry {

    @JsonView(Views.Public.class)
    private Long id;

    @JsonView(Views.Public.class)
    @NotBlank
    private String title;

    @JsonView(Views.Public.class)
    @NotBlank
    private String author;

    @JsonView(Views.Public.class)
    @Min(1)
    private int year;

    // Only visible to ADMIN role
    @JsonView(Views.Admin.class)
    @DecimalMin("0.01")
    private double price;

    // Only visible to ADMIN role
    @JsonView(Views.Admin.class)
    @DecimalMin("0.01")
    private double costPrice;

    public BookCatalogueEntry() {}
    public BookCatalogueEntry(Long id, String title, String author,
                              int year, double price, double costPrice) {
        this.id = id; this.title = title; this.author = author;
        this.year = year; this.price = price; this.costPrice = costPrice;
    }

    // getters & setters
    public Long getId()            { return id; }
    public String getTitle()       { return title; }
    public String getAuthor()      { return author; }
    public int getYear()           { return year; }
    public double getPrice()       { return price; }
    public double getCostPrice()   { return costPrice; }
    public void setId(Long id)     { this.id = id; }
    public void setTitle(String t) { this.title = t; }
    public void setAuthor(String a){ this.author = a; }
    public void setYear(int y)     { this.year = y; }
    public void setPrice(double p) { this.price = p; }
    public void setCostPrice(double c){ this.costPrice = c; }
}

record CreateBookCatalogueRequest(
    @NotBlank String title,
    @NotBlank String author,
    @Min(1) int year,
    @DecimalMin("0.01") double price,
    @DecimalMin("0.01") double costPrice
) {}

// ── Controller — complete this ────────────────────────────────────────────────

/**
 * TODO:
 *  1. Add @RestController, @RequestMapping("/api/v1/catalogue")
 *  2. Add @Tag(name="Book Catalogue", description="Manage the book catalogue")
 *  3. Add produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
 *     to @RequestMapping so Spring serves both formats
 *  4. Annotate each method with @Operation and @ApiResponse
 *  5. Implement role-based @JsonView selection based on X-Role header
 *  6. Implement CRUD using in-memory LinkedHashMap
 */
// @Tag(name = "Book Catalogue", description = "TODO")
// @RestController
// @RequestMapping(value = "/api/v1/catalogue", produces = { ... })
class BookCatalogueController {

    private final Map<Long, BookCatalogueEntry> store = new LinkedHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * POST /api/v1/catalogue
     * TODO: @Operation(summary="Create book", description="Adds a new book to the catalogue")
     * TODO: @ApiResponse(responseCode="201", description="Book created")
     * TODO: @ApiResponse(responseCode="400", description="Validation error")
     */
    public ResponseEntity<?> create(
            @Valid @RequestBody CreateBookCatalogueRequest req,
            @RequestHeader(value = "X-Role", defaultValue = "USER") String role) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * GET /api/v1/catalogue
     * TODO: return @JsonView(Views.Admin) if role=ADMIN, else @JsonView(Views.Public)
     * TODO: Swagger: @Operation(summary="List all books")
     */
    public ResponseEntity<?> findAll(
            @RequestHeader(value = "X-Role", defaultValue = "USER") String role) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * GET /api/v1/catalogue/{id}
     * TODO: role-based JsonView, 404 if not found
     */
    public ResponseEntity<?> findById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Role", defaultValue = "USER") String role) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * DELETE /api/v1/catalogue/{id}
     * TODO: 204 or 404
     */
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
