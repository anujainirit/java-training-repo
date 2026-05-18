package com.javatraining.m03.impl;

import com.javatraining.m03.api.CreateProductDto;
import com.javatraining.m03.api.ProductDto;
import com.javatraining.m03.api.ProductService;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * EXERCISE M03-T1 — impl module
 *
 * Implement ProductService using an in-memory store.
 * This class must NOT have any Spring annotations (pure Java, no framework).
 * The webapp module will instantiate and wire it via Spring @Bean.
 *
 * Validation rules:
 *  - name and category must not be null or blank
 *  - price must be > 0
 *  - stock must be >= 0
 *  Throw IllegalArgumentException with a descriptive message for any violation.
 */
public class InMemoryProductService implements ProductService {

    private final Map<Long, ProductDto> store = new LinkedHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public ProductDto create(CreateProductDto dto) {
        // TODO: validate dto fields, then store and return new ProductDto with auto id
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Optional<ProductDto> findById(Long id) {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<ProductDto> findAll(String category) {
        // TODO: if category is null, return all; otherwise filter case-insensitively
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Optional<ProductDto> updateStock(Long id, int newStock) {
        // TODO: validate newStock >= 0; update and return updated dto, or empty if not found
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean delete(Long id) {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int count() {
        // TODO
        throw new UnsupportedOperationException("Not implemented");
    }
}
