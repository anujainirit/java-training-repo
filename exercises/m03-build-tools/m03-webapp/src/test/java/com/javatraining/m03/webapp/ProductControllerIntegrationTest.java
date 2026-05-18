package com.javatraining.m03.webapp;

import com.javatraining.m03.api.ProductDto;
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
 *  Integration test: boots full Spring Boot context
 * ══════════════════════════════════════════════════════════════
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("M03-T1: Multi-Module Webapp — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerIntegrationTest {

    @Autowired MockMvc mockMvc;

    private static final String BASE = "/api/v1/products";

    private static final String LAPTOP_JSON =
        "{\"name\":\"Laptop Pro\",\"category\":\"Electronics\",\"price\":75000,\"stock\":10}";
    private static final String PHONE_JSON =
        "{\"name\":\"Smartphone X\",\"category\":\"Electronics\",\"price\":25000,\"stock\":5}";
    private static final String CHAIR_JSON =
        "{\"name\":\"Office Chair\",\"category\":\"Furniture\",\"price\":8000,\"stock\":3}";

    @Test @Order(1)
    @DisplayName("POST /api/v1/products → 201 with body")
    void createReturns201() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Laptop Pro"))
            .andExpect(jsonPath("$.price").value(75000.0));
    }

    @Test @Order(2)
    @DisplayName("GET /api/v1/products → returns all products")
    void getAllProducts() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON));
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CHAIR_JSON));

        mockMvc.perform(get(BASE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test @Order(3)
    @DisplayName("GET /api/v1/products?category=Electronics → filtered")
    void getByCategory() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON));
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(PHONE_JSON));
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CHAIR_JSON));

        mockMvc.perform(get(BASE + "?category=Electronics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].category", everyItem(is("Electronics"))));
    }

    @Test @Order(4)
    @DisplayName("GET /api/v1/products/{id} → 200 for existing")
    void getByIdFound() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON));
        mockMvc.perform(get(BASE + "/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Laptop Pro"));
    }

    @Test @Order(5)
    @DisplayName("GET /api/v1/products/{id} → 404 for unknown")
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get(BASE + "/999"))
            .andExpect(status().isNotFound());
    }

    @Test @Order(6)
    @DisplayName("PATCH /api/v1/products/{id}/stock → updates stock")
    void updateStock() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON));
        mockMvc.perform(patch(BASE + "/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stock\":99}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stock").value(99));
    }

    @Test @Order(7)
    @DisplayName("PATCH /api/v1/products/{id}/stock → 404 for unknown")
    void updateStockNotFound() throws Exception {
        mockMvc.perform(patch(BASE + "/999/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stock\":5}"))
            .andExpect(status().isNotFound());
    }

    @Test @Order(8)
    @DisplayName("DELETE /api/v1/products/{id} → 204 No Content")
    void deleteProduct() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(LAPTOP_JSON));
        mockMvc.perform(delete(BASE + "/1")).andExpect(status().isNoContent());
        mockMvc.perform(get(BASE + "/1")).andExpect(status().isNotFound());
    }

    @Test @Order(9)
    @DisplayName("DELETE /api/v1/products/{id} → 404 for unknown")
    void deleteNotFound() throws Exception {
        mockMvc.perform(delete(BASE + "/999"))
            .andExpect(status().isNotFound());
    }

    @Test @Order(10)
    @DisplayName("ProductService bean is wired (not null) in Spring context")
    void productServiceBeanExists(@Autowired com.javatraining.m03.api.ProductService svc) {
        org.assertj.core.api.Assertions.assertThat(svc).isNotNull();
        org.assertj.core.api.Assertions.assertThat(svc)
            .isInstanceOf(com.javatraining.m03.impl.InMemoryProductService.class);
    }
}
