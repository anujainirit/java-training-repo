package com.javatraining.m05.productapi;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 * ══════════════════════════════════════════════════════════════
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("M05-T1: Product REST API — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductApiTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE = "/api/products";

    private static final String LAPTOP_JSON = """
        {
          "name": "Laptop Pro",
          "category": "Electronics",
          "price": 75000.00,
          "stock": 50
        }
        """;

    private static final String PHONE_JSON = """
        {
          "name": "Smartphone X",
          "category": "Electronics",
          "price": 25000.00,
          "stock": 100
        }
        """;

    private static final String CHAIR_JSON = """
        {
          "name": "Office Chair",
          "category": "Furniture",
          "price": 8000.00,
          "stock": 20
        }
        """;

    // ── Create ──────────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("POST /api/products → 201 with product body")
    void createProductReturns201() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(LAPTOP_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value("Laptop Pro"))
            .andExpect(jsonPath("$.category").value("Electronics"))
            .andExpect(jsonPath("$.price").value(75000.0))
            .andExpect(jsonPath("$.stock").value(50));
    }

    @Test @Order(2)
    @DisplayName("POST /api/products → IDs auto-increment from 1")
    void idsAutoIncrement() throws Exception {
        var result1 = mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON))
            .andReturn();
        var result2 = mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON).content(PHONE_JSON))
            .andReturn();

        String body1 = result1.getResponse().getContentAsString();
        String body2 = result2.getResponse().getContentAsString();
        // ids should differ by 1
        // (simplistic check — relies on field order)
        org.assertj.core.api.Assertions.assertThat(body1).contains("\"id\":1");
        org.assertj.core.api.Assertions.assertThat(body2).contains("\"id\":2");
    }

    // ── Validation ──────────────────────────────────────────────────────────

    @Test @Order(3)
    @DisplayName("POST with blank name → 400 Bad Request")
    void createWithBlankNameReturns400() throws Exception {
        String bad = """
            {"name":"","category":"Electronics","price":1000,"stock":10}
            """;
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON).content(bad))
            .andExpect(status().isBadRequest());
    }

    @Test @Order(4)
    @DisplayName("POST with negative price → 400 Bad Request")
    void createWithNegativePriceReturns400() throws Exception {
        String bad = """
            {"name":"Test","category":"Cat","price":-1,"stock":10}
            """;
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON).content(bad))
            .andExpect(status().isBadRequest());
    }

    @Test @Order(5)
    @DisplayName("POST with negative stock → 400 Bad Request")
    void createWithNegativeStockReturns400() throws Exception {
        String bad = """
            {"name":"Test","category":"Cat","price":100,"stock":-5}
            """;
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON).content(bad))
            .andExpect(status().isBadRequest());
    }

    @Test @Order(6)
    @DisplayName("POST with missing price → 400 Bad Request")
    void createWithMissingPriceReturns400() throws Exception {
        String bad = """
            {"name":"Test","category":"Cat","stock":10}
            """;
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON).content(bad))
            .andExpect(status().isBadRequest());
    }

    // ── Read ────────────────────────────────────────────────────────────────

    @Test @Order(7)
    @DisplayName("GET /api/products → empty list when no products")
    void getAllEmptyList() throws Exception {
        mockMvc.perform(get(BASE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test @Order(8)
    @DisplayName("GET /api/products → returns all created products")
    void getAllReturnsList() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON));
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(PHONE_JSON));

        mockMvc.perform(get(BASE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test @Order(9)
    @DisplayName("GET /api/products?category=Electronics → filtered results")
    void getByCategory() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON));
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(PHONE_JSON));
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CHAIR_JSON));

        mockMvc.perform(get(BASE + "?category=Electronics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].category", everyItem(is("Electronics"))));
    }

    @Test @Order(10)
    @DisplayName("GET /api/products?category=Unknown → empty list")
    void getByCategoryNoMatch() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON));

        mockMvc.perform(get(BASE + "?category=Unknown"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test @Order(11)
    @DisplayName("GET /api/products/{id} → returns product")
    void getByIdFound() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON));

        mockMvc.perform(get(BASE + "/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Laptop Pro"));
    }

    @Test @Order(12)
    @DisplayName("GET /api/products/{id} → 404 for unknown ID")
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get(BASE + "/999"))
            .andExpect(status().isNotFound());
    }

    // ── Update stock ────────────────────────────────────────────────────────

    @Test @Order(13)
    @DisplayName("PUT /api/products/{id}/stock → updates stock")
    void updateStock() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON));

        mockMvc.perform(put(BASE + "/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stock\": 200}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stock").value(200));
    }

    @Test @Order(14)
    @DisplayName("PUT /api/products/{id}/stock → 404 for unknown ID")
    void updateStockNotFound() throws Exception {
        mockMvc.perform(put(BASE + "/999/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stock\": 10}"))
            .andExpect(status().isNotFound());
    }

    @Test @Order(15)
    @DisplayName("PUT /api/products/{id}/stock → 400 for negative stock")
    void updateStockNegative() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON));

        mockMvc.perform(put(BASE + "/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stock\": -5}"))
            .andExpect(status().isBadRequest());
    }

    // ── Delete ──────────────────────────────────────────────────────────────

    @Test @Order(16)
    @DisplayName("DELETE /api/products/{id} → 204 No Content")
    void deleteProduct() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON));

        mockMvc.perform(delete(BASE + "/1"))
            .andExpect(status().isNoContent());

        // verify it's gone
        mockMvc.perform(get(BASE + "/1"))
            .andExpect(status().isNotFound());
    }

    @Test @Order(17)
    @DisplayName("DELETE /api/products/{id} → 404 for unknown ID")
    void deleteNotFound() throws Exception {
        mockMvc.perform(delete(BASE + "/999"))
            .andExpect(status().isNotFound());
    }

    // ── Error format ────────────────────────────────────────────────────────

    @Test @Order(18)
    @DisplayName("404 response body follows RFC 7807 ProblemDetail format")
    void notFoundIsRfc7807() throws Exception {
        mockMvc.perform(get(BASE + "/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.title").exists())
            .andExpect(jsonPath("$.detail").exists());
    }

    @Test @Order(19)
    @DisplayName("400 validation error body follows RFC 7807 ProblemDetail format")
    void badRequestIsRfc7807() throws Exception {
        String bad = "{\"name\":\"\",\"category\":\"X\",\"price\":10,\"stock\":5}";
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(bad))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }
}
