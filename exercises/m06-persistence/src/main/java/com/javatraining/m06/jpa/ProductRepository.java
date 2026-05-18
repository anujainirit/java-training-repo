package com.javatraining.m06.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * EXERCISE M06-T2 & T3: JPA Entity Mapping + Spring Data Repository
 *
 * Complete the entity mappings and repository methods below.
 *
 * Schema (managed by Flyway — see src/main/resources/db/migration):
 *
 *   categories(id BIGSERIAL PK, name VARCHAR(100) UNIQUE NOT NULL)
 *
 *   products(id BIGSERIAL PK,
 *            name VARCHAR(200) NOT NULL,
 *            description TEXT,
 *            price NUMERIC(12,2) NOT NULL CHECK(price > 0),
 *            stock INT NOT NULL DEFAULT 0,
 *            category_id BIGINT REFERENCES categories(id),
 *            created_at TIMESTAMPTZ DEFAULT now())
 *
 *   reviews(id BIGSERIAL PK,
 *           product_id BIGINT REFERENCES products(id) ON DELETE CASCADE,
 *           reviewer VARCHAR(100) NOT NULL,
 *           rating SMALLINT CHECK(rating BETWEEN 1 AND 5),
 *           body TEXT)
 */

// ── Category entity — complete this ──────────────────────────────────────────
// TODO: annotate with @Entity, @Table(name="categories")
class Category {

    // TODO: @Id, @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: @Column(nullable = false, unique = true, length = 100)
    private String name;

    // TODO: bidirectional @OneToMany to Product (mappedBy="category", cascade=ALL, orphanRemoval=true)
    private List<Product> products;

    // TODO: constructors, getters, setters
}


// ── Product entity — complete this ───────────────────────────────────────────
// TODO: @Entity, @Table(name="products"), @EntityListeners(AuditingEntityListener.class)
class Product {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;

    // TODO: @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="category_id")
    private Category category;

    // TODO: @OneToMany(mappedBy="product", cascade=ALL, orphanRemoval=true, fetch=LAZY)
    private List<Review> reviews;

    // TODO: @CreationTimestamp or @CreatedDate
    // private Instant createdAt;

    // TODO: constructors, getters, setters, equals/hashCode on id
}


// ── Review entity — complete this ────────────────────────────────────────────
// TODO: @Entity, @Table(name="reviews")
class Review {

    private Long id;
    private String reviewer;

    // TODO: @Min(1) @Max(5)
    private int rating;

    private String body;

    // TODO: @ManyToOne(fetch=LAZY) @JoinColumn(name="product_id")
    private Product product;

    // TODO: constructors, getters, setters
}


// ── ProductRepository — implement the query methods ──────────────────────────

@Repository
interface ProductRepository extends JpaRepository<Product, Long>,
                                      JpaSpecificationExecutor<Product> {

    // TODO: derived query — find by category name (case-insensitive)
    List<Product> findByCategoryNameIgnoreCase(String categoryName);

    // TODO: derived query — find by price range
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    // TODO: derived query — find products with stock <= threshold
    List<Product> findByStockLessThanEqual(int threshold);

    // TODO: JPQL query — find products with average rating >= minRating
    // JOIN FETCH their reviews to avoid N+1
    @Query("""
        SELECT DISTINCT p FROM Product p
        JOIN FETCH p.reviews r
        WHERE (SELECT AVG(r2.rating) FROM Review r2 WHERE r2.product = p) >= :minRating
        """)
    List<Product> findByMinAverageRating(@Param("minRating") double minRating);

    // TODO: modifying query — bulk update stock for a category
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock + :delta WHERE p.category.id = :categoryId")
    int incrementStockByCategory(@Param("categoryId") Long categoryId, @Param("delta") int delta);

    // TODO: native query — top N products by review count
    @Query(value = """
        SELECT p.* FROM products p
        LEFT JOIN reviews r ON r.product_id = p.id
        GROUP BY p.id
        ORDER BY COUNT(r.id) DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Product> findTopByReviewCount(@Param("limit") int limit);
}
