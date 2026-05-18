package com.javatraining.m03.api;

import java.util.List;
import java.util.Optional;

/**
 * EXERCISE M03-T1: Multi-Module Maven Project
 *
 * This module (m03-api) contains ONLY interfaces and DTOs.
 * It has zero dependencies beyond the JDK.
 *
 * Student tasks:
 *  1. Create m03-impl module that implements these interfaces
 *  2. Create m03-webapp module (Spring Boot) that wires impl via DI
 *  3. Parent POM must declare shared dependency versions via <dependencyManagement>
 *  4. Each module must produce a JAR (not a fat jar) except m03-webapp
 *
 * DO NOT add any implementation code in this module.
 */

// ── DTOs (no-arg + all-arg constructor, equals/hashCode, toString required) ──

public record ProductDto(Long id, String name, String category, double price, int stock) {}

public record CreateProductDto(String name, String category, double price, int stock) {}

// ── Service interface ─────────────────────────────────────────────────────────

public interface ProductService {

    /** Create a new product. id is auto-assigned. */
    ProductDto create(CreateProductDto dto);

    /** Find by id — empty if not found. */
    Optional<ProductDto> findById(Long id);

    /** Find all products, optionally filtered by category (null = all). */
    List<ProductDto> findAll(String category);

    /** Update stock level. Returns updated product or empty if not found. */
    Optional<ProductDto> updateStock(Long id, int newStock);

    /** Delete by id. Returns true if existed and was deleted. */
    boolean delete(Long id);

    /** Total number of tracked products. */
    int count();
}
