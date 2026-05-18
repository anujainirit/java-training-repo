package com.javatraining.m06.jpa;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 *  Uses real PostgreSQL via Testcontainers
 * ══════════════════════════════════════════════════════════════
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("M06-T2/T3: JPA Entities & Repository — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("training_test")
        .withUsername("training")
        .withPassword("training123");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager em;

    private Category electronics;
    private Category furniture;
    private Product laptop;
    private Product phone;
    private Product chair;

    @BeforeEach
    void seed() {
        electronics = new Category(); electronics.setName("Electronics");
        furniture   = new Category(); furniture.setName("Furniture");
        em.persist(electronics);
        em.persist(furniture);

        laptop = new Product();
        laptop.setName("Laptop Pro"); laptop.setPrice(new BigDecimal("75000"));
        laptop.setStock(10); laptop.setCategory(electronics);
        em.persist(laptop);

        phone = new Product();
        phone.setName("Smartphone X"); phone.setPrice(new BigDecimal("25000"));
        phone.setStock(0); phone.setCategory(electronics);
        em.persist(phone);

        chair = new Product();
        chair.setName("Office Chair"); chair.setPrice(new BigDecimal("8000"));
        chair.setStock(5); chair.setCategory(furniture);
        em.persist(chair);

        // Reviews for laptop
        Review r1 = new Review(); r1.setProduct(laptop); r1.setReviewer("Alice"); r1.setRating(5); r1.setBody("Excellent");
        Review r2 = new Review(); r2.setProduct(laptop); r2.setReviewer("Bob");   r2.setRating(4); r2.setBody("Good");
        Review r3 = new Review(); r3.setProduct(phone);  r3.setReviewer("Carol"); r3.setRating(3); r3.setBody("OK");
        em.persist(r1); em.persist(r2); em.persist(r3);
        em.flush();
    }

    @Test @Order(1)
    @DisplayName("findByCategoryNameIgnoreCase() returns products in given category")
    void findByCategoryName() {
        List<Product> results = productRepository.findByCategoryNameIgnoreCase("electronics");
        assertThat(results).hasSize(2)
            .extracting(Product::getName)
            .containsExactlyInAnyOrder("Laptop Pro", "Smartphone X");
    }

    @Test @Order(2)
    @DisplayName("findByCategoryNameIgnoreCase() is truly case-insensitive")
    void findByCategoryNameCaseInsensitive() {
        assertThat(productRepository.findByCategoryNameIgnoreCase("ELECTRONICS")).hasSize(2);
        assertThat(productRepository.findByCategoryNameIgnoreCase("Electronics")).hasSize(2);
        assertThat(productRepository.findByCategoryNameIgnoreCase("electronICS")).hasSize(2);
    }

    @Test @Order(3)
    @DisplayName("findByPriceBetween() returns products within range")
    void findByPriceBetween() {
        List<Product> results = productRepository.findByPriceBetween(
            new BigDecimal("10000"), new BigDecimal("80000"));
        assertThat(results).hasSize(2)
            .extracting(Product::getName)
            .containsExactlyInAnyOrder("Laptop Pro", "Smartphone X");
    }

    @Test @Order(4)
    @DisplayName("findByStockLessThanEqual() finds out-of-stock and low-stock products")
    void findByStockLessThanEqual() {
        List<Product> outOfStock = productRepository.findByStockLessThanEqual(0);
        assertThat(outOfStock).hasSize(1)
            .extracting(Product::getName).containsExactly("Smartphone X");

        List<Product> lowStock = productRepository.findByStockLessThanEqual(5);
        assertThat(lowStock).hasSize(2);
    }

    @Test @Order(5)
    @DisplayName("findByMinAverageRating() returns products with avg rating >= threshold")
    void findByMinAverageRating() {
        // laptop avg = (5+4)/2 = 4.5; phone avg = 3.0
        List<Product> highRated = productRepository.findByMinAverageRating(4.0);
        assertThat(highRated).hasSize(1)
            .extracting(Product::getName).containsExactly("Laptop Pro");
    }

    @Test @Order(6)
    @DisplayName("findByMinAverageRating() excludes products below threshold")
    void findByMinAverageRatingExcludes() {
        List<Product> results = productRepository.findByMinAverageRating(3.5);
        assertThat(results).extracting(Product::getName).doesNotContain("Smartphone X");
    }

    @Test @Order(7)
    @DisplayName("incrementStockByCategory() updates all products in category")
    void incrementStockByCategory() {
        int updated = productRepository.incrementStockByCategory(electronics.getId(), 5);
        assertThat(updated).isEqualTo(2);

        em.clear();  // evict cache
        Product updatedLaptop = productRepository.findById(laptop.getId()).orElseThrow();
        assertThat(updatedLaptop.getStock()).isEqualTo(15);  // 10 + 5

        Product updatedPhone = productRepository.findById(phone.getId()).orElseThrow();
        assertThat(updatedPhone.getStock()).isEqualTo(5);  // 0 + 5
    }

    @Test @Order(8)
    @DisplayName("incrementStockByCategory() does NOT affect other categories")
    void incrementStockCategoryIsolation() {
        productRepository.incrementStockByCategory(electronics.getId(), 100);
        em.clear();

        Product updatedChair = productRepository.findById(chair.getId()).orElseThrow();
        assertThat(updatedChair.getStock()).isEqualTo(5);  // unchanged
    }

    @Test @Order(9)
    @DisplayName("findTopByReviewCount() returns products ordered by review count desc")
    void findTopByReviewCount() {
        List<Product> top2 = productRepository.findTopByReviewCount(2);
        assertThat(top2).isNotEmpty();
        assertThat(top2.get(0).getName()).isEqualTo("Laptop Pro");  // 2 reviews
    }

    @Test @Order(10)
    @DisplayName("Cascade delete: deleting product removes its reviews")
    void cascadeDeleteReviews() {
        productRepository.delete(laptop);
        em.flush(); em.clear();
        assertThat(productRepository.findById(laptop.getId())).isEmpty();
        // reviews should also be gone — verified by absence of orphan rows
        long reviewCount = (Long) em.getEntityManager()
            .createQuery("SELECT COUNT(r) FROM Review r WHERE r.product.id = :id")
            .setParameter("id", laptop.getId())
            .getSingleResult();
        assertThat(reviewCount).isZero();
    }

    @Test @Order(11)
    @DisplayName("Product entity has correct equals/hashCode on id")
    void equalsHashCode() {
        Product a = productRepository.findById(laptop.getId()).orElseThrow();
        Product b = productRepository.findById(laptop.getId()).orElseThrow();
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test @Order(12)
    @DisplayName("createdAt field is auto-populated on persist")
    void createdAtAutoPopulated() {
        Product p = productRepository.findById(laptop.getId()).orElseThrow();
        assertThat(p.getCreatedAt()).isNotNull();
    }
}
