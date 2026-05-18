package com.javatraining.m03.impl;

import com.javatraining.m03.api.CreateProductDto;
import com.javatraining.m03.api.ProductDto;
import com.javatraining.m03.api.ProductService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 * ══════════════════════════════════════════════════════════════
 */
@DisplayName("M03-T1: InMemoryProductService — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InMemoryProductServiceTest {

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new InMemoryProductService();
    }

    // ── Create ──────────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("create() assigns auto-incrementing IDs starting from 1")
    void createAssignsId() {
        ProductDto p1 = service.create(new CreateProductDto("Laptop", "Electronics", 75000, 10));
        ProductDto p2 = service.create(new CreateProductDto("Phone",  "Electronics", 25000, 5));
        assertThat(p1.id()).isEqualTo(1L);
        assertThat(p2.id()).isEqualTo(2L);
    }

    @Test @Order(2)
    @DisplayName("create() returns dto with all fields set correctly")
    void createFieldsCorrect() {
        ProductDto p = service.create(new CreateProductDto("Chair", "Furniture", 8000, 3));
        assertThat(p.name()).isEqualTo("Chair");
        assertThat(p.category()).isEqualTo("Furniture");
        assertThat(p.price()).isEqualTo(8000.0);
        assertThat(p.stock()).isEqualTo(3);
    }

    @Test @Order(3)
    @DisplayName("count() increments after each create")
    void countIncrements() {
        assertThat(service.count()).isZero();
        service.create(new CreateProductDto("A", "Cat", 100, 1));
        assertThat(service.count()).isEqualTo(1);
        service.create(new CreateProductDto("B", "Cat", 200, 2));
        assertThat(service.count()).isEqualTo(2);
    }

    // ── Validation ──────────────────────────────────────────────────────────

    @ParameterizedTest @Order(4)
    @DisplayName("create() throws for blank name")
    @ValueSource(strings = {"", "  ", "\t"})
    void createBlankNameThrows(String blank) {
        assertThatThrownBy(() -> service.create(new CreateProductDto(blank, "Cat", 100, 1)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @Order(5)
    @DisplayName("create() throws for null name")
    void createNullNameThrows() {
        assertThatThrownBy(() -> service.create(new CreateProductDto(null, "Cat", 100, 1)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest @Order(6)
    @DisplayName("create() throws for non-positive price")
    @ValueSource(doubles = {0.0, -1.0, -999.9})
    void createNonPositivePriceThrows(double badPrice) {
        assertThatThrownBy(() -> service.create(new CreateProductDto("X", "Cat", badPrice, 1)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @Order(7)
    @DisplayName("create() throws for negative stock")
    void createNegativeStockThrows() {
        assertThatThrownBy(() -> service.create(new CreateProductDto("X", "Cat", 100, -1)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @Order(8)
    @DisplayName("create() accepts zero stock")
    void createZeroStockAllowed() {
        assertThatNoException().isThrownBy(
            () -> service.create(new CreateProductDto("X", "Cat", 100, 0)));
    }

    // ── FindById ────────────────────────────────────────────────────────────

    @Test @Order(9)
    @DisplayName("findById() returns present Optional for existing product")
    void findByIdFound() {
        ProductDto created = service.create(new CreateProductDto("Laptop", "Electronics", 75000, 10));
        Optional<ProductDto> found = service.findById(created.id());
        assertThat(found).isPresent().contains(created);
    }

    @Test @Order(10)
    @DisplayName("findById() returns empty Optional for unknown id")
    void findByIdNotFound() {
        assertThat(service.findById(999L)).isEmpty();
    }

    // ── FindAll ─────────────────────────────────────────────────────────────

    @Test @Order(11)
    @DisplayName("findAll(null) returns all products")
    void findAllNoFilter() {
        service.create(new CreateProductDto("Laptop", "Electronics", 75000, 10));
        service.create(new CreateProductDto("Chair",  "Furniture",    8000,  3));
        assertThat(service.findAll(null)).hasSize(2);
    }

    @Test @Order(12)
    @DisplayName("findAll(category) filters case-insensitively")
    void findAllCategoryFilter() {
        service.create(new CreateProductDto("Laptop", "Electronics", 75000, 10));
        service.create(new CreateProductDto("Phone",  "Electronics", 25000,  5));
        service.create(new CreateProductDto("Chair",  "Furniture",    8000,  3));

        assertThat(service.findAll("electronics")).hasSize(2);
        assertThat(service.findAll("ELECTRONICS")).hasSize(2);
        assertThat(service.findAll("Furniture")).hasSize(1);
        assertThat(service.findAll("Unknown")).isEmpty();
    }

    @Test @Order(13)
    @DisplayName("findAll() on empty store returns empty list, not null")
    void findAllEmptyStore() {
        assertThat(service.findAll(null)).isNotNull().isEmpty();
    }

    // ── UpdateStock ─────────────────────────────────────────────────────────

    @Test @Order(14)
    @DisplayName("updateStock() returns updated product with new stock")
    void updateStockSuccess() {
        ProductDto p = service.create(new CreateProductDto("Laptop", "Electronics", 75000, 10));
        Optional<ProductDto> updated = service.updateStock(p.id(), 25);
        assertThat(updated).isPresent();
        assertThat(updated.get().stock()).isEqualTo(25);
        assertThat(updated.get().id()).isEqualTo(p.id());
        assertThat(updated.get().name()).isEqualTo("Laptop");
    }

    @Test @Order(15)
    @DisplayName("updateStock() returns empty for unknown id")
    void updateStockNotFound() {
        assertThat(service.updateStock(999L, 10)).isEmpty();
    }

    @Test @Order(16)
    @DisplayName("updateStock() throws for negative stock")
    void updateStockNegativeThrows() {
        ProductDto p = service.create(new CreateProductDto("Laptop", "Electronics", 75000, 10));
        assertThatThrownBy(() -> service.updateStock(p.id(), -1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @Order(17)
    @DisplayName("updateStock() to zero is valid")
    void updateStockToZero() {
        ProductDto p = service.create(new CreateProductDto("Laptop", "Electronics", 75000, 10));
        Optional<ProductDto> updated = service.updateStock(p.id(), 0);
        assertThat(updated).isPresent();
        assertThat(updated.get().stock()).isZero();
    }

    // ── Delete ──────────────────────────────────────────────────────────────

    @Test @Order(18)
    @DisplayName("delete() returns true and removes product")
    void deleteExisting() {
        ProductDto p = service.create(new CreateProductDto("Laptop", "Electronics", 75000, 10));
        assertThat(service.delete(p.id())).isTrue();
        assertThat(service.findById(p.id())).isEmpty();
        assertThat(service.count()).isZero();
    }

    @Test @Order(19)
    @DisplayName("delete() returns false for unknown id")
    void deleteNotFound() {
        assertThat(service.delete(999L)).isFalse();
    }

    @Test @Order(20)
    @DisplayName("Deleted product does not appear in findAll()")
    void deletedProductNotInFindAll() {
        ProductDto p = service.create(new CreateProductDto("Laptop", "Electronics", 75000, 10));
        service.delete(p.id());
        assertThat(service.findAll(null)).isEmpty();
    }

    // ── Ordering ────────────────────────────────────────────────────────────

    @Test @Order(21)
    @DisplayName("findAll() preserves insertion order")
    void findAllPreservesOrder() {
        service.create(new CreateProductDto("A", "Cat", 100, 1));
        service.create(new CreateProductDto("B", "Cat", 200, 2));
        service.create(new CreateProductDto("C", "Cat", 300, 3));

        List<String> names = service.findAll(null).stream()
            .map(ProductDto::name).toList();
        assertThat(names).containsExactly("A", "B", "C");
    }

    @Test @Order(22)
    @DisplayName("IDs remain stable after deletions (no re-use)")
    void idsNotReused() {
        ProductDto p1 = service.create(new CreateProductDto("A", "Cat", 100, 1));
        service.delete(p1.id());
        ProductDto p2 = service.create(new CreateProductDto("B", "Cat", 200, 2));
        assertThat(p2.id()).isGreaterThan(p1.id());
    }
}
