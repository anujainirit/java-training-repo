package com.javatraining.m07.openapi;

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
@DisplayName("M07-T3/T4: OpenAPI + Content Negotiation + JsonView — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookCatalogueControllerTest {

    @Autowired MockMvc mockMvc;

    static final String BASE = "/api/v1/catalogue";
    static final String BOOK_JSON =
        "{\"title\":\"Clean Code\",\"author\":\"Robert Martin\"," +
        "\"year\":2008,\"price\":750.0,\"costPrice\":400.0}";

    // ── CRUD basics ──────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("POST → 201 Created with JSON response")
    void createReturns201() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(BOOK_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test @Order(2)
    @DisplayName("GET /api/v1/catalogue → returns all books as JSON")
    void getAllJson() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON));

        mockMvc.perform(get(BASE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test @Order(3)
    @DisplayName("GET /api/v1/catalogue/{id} → 200 found")
    void getByIdFound() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON));
        mockMvc.perform(get(BASE + "/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test @Order(4)
    @DisplayName("GET /api/v1/catalogue/{id} → 404 not found")
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get(BASE + "/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test @Order(5)
    @DisplayName("DELETE /api/v1/catalogue/{id} → 204")
    void delete() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON));
        mockMvc.perform(delete(BASE + "/1")).andExpect(status().isNoContent());
        mockMvc.perform(get(BASE + "/1")).andExpect(status().isNotFound());
    }

    // ── Content Negotiation ──────────────────────────────────────────────────

    @Test @Order(6)
    @DisplayName("Accept: application/xml → response is XML")
    void acceptXmlReturnsXml() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON));

        mockMvc.perform(get(BASE + "/1").accept(MediaType.APPLICATION_XML))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
    }

    @Test @Order(7)
    @DisplayName("XML response contains book data")
    void xmlResponseContainsData() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON));

        String xml = mockMvc.perform(get(BASE + "/1").accept(MediaType.APPLICATION_XML))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        org.assertj.core.api.Assertions.assertThat(xml)
            .contains("Clean Code")
            .contains("Robert Martin");
    }

    @Test @Order(8)
    @DisplayName("Accept: application/json (default) → response is JSON")
    void defaultAcceptIsJson() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON));

        mockMvc.perform(get(BASE + "/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    // ── @JsonView role-based field visibility ────────────────────────────────

    @Test @Order(9)
    @DisplayName("USER role: price and costPrice NOT in response")
    void userRoleHidesPrice() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Role", "USER")
                .content(BOOK_JSON));

        mockMvc.perform(get(BASE + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Role", "USER"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.price").doesNotExist())
            .andExpect(jsonPath("$.costPrice").doesNotExist());
    }

    @Test @Order(10)
    @DisplayName("USER role: id, title, author, year ARE in response")
    void userRoleSeesPublicFields() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON));

        mockMvc.perform(get(BASE + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Role", "USER"))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title").value("Clean Code"))
            .andExpect(jsonPath("$.author").value("Robert Martin"))
            .andExpect(jsonPath("$.year").value(2008));
    }

    @Test @Order(11)
    @DisplayName("ADMIN role: price AND costPrice ARE in response")
    void adminRoleSeesPrice() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON));

        mockMvc.perform(get(BASE + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Role", "ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.price").value(750.0))
            .andExpect(jsonPath("$.costPrice").value(400.0));
    }

    @Test @Order(12)
    @DisplayName("ADMIN role: also sees all public fields")
    void adminRoleSeesAllFields() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON));

        mockMvc.perform(get(BASE + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Role", "ADMIN"))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title").exists())
            .andExpect(jsonPath("$.author").exists())
            .andExpect(jsonPath("$.year").exists())
            .andExpect(jsonPath("$.price").exists())
            .andExpect(jsonPath("$.costPrice").exists());
    }

    @Test @Order(13)
    @DisplayName("Default role (no header) behaves as USER — no price exposed")
    void defaultRoleIsUser() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON));

        mockMvc.perform(get(BASE + "/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.price").doesNotExist());
    }

    @Test @Order(14)
    @DisplayName("GET /api/v1/catalogue list with ADMIN role shows price on each item")
    void listAdminShowsPrices() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(BOOK_JSON));
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\":\"Refactoring\",\"author\":\"Fowler\",\"year\":1999,\"price\":650.0,\"costPrice\":300.0}"));

        mockMvc.perform(get(BASE)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Role", "ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].price", hasItems(750.0, 650.0)));
    }

    // ── OpenAPI spec verification ─────────────────────────────────────────────

    @Test @Order(15)
    @DisplayName("GET /v3/api-docs → OpenAPI spec is generated (springdoc)")
    void openApiSpecGenerated() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.openapi").value(startsWith("3.")));
    }

    @Test @Order(16)
    @DisplayName("OpenAPI spec contains the Book Catalogue tag")
    void openApiContainsCatalogueTag() throws Exception {
        String spec = mockMvc.perform(get("/v3/api-docs"))
            .andReturn().getResponse().getContentAsString();
        org.assertj.core.api.Assertions.assertThat(spec)
            .contains("Book Catalogue");
    }

    @Test @Order(17)
    @DisplayName("OpenAPI spec contains /api/v1/catalogue path")
    void openApiContainsCataloguePath() throws Exception {
        String spec = mockMvc.perform(get("/v3/api-docs"))
            .andReturn().getResponse().getContentAsString();
        org.assertj.core.api.Assertions.assertThat(spec)
            .contains("/api/v1/catalogue");
    }

    // ── Validation ───────────────────────────────────────────────────────────

    @Test @Order(18)
    @DisplayName("POST with blank title → 400")
    void blankTitleReturns400() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\",\"author\":\"Author\",\"year\":2020,\"price\":100,\"costPrice\":50}"))
            .andExpect(status().isBadRequest());
    }

    @Test @Order(19)
    @DisplayName("POST with zero price → 400")
    void zeroPriceReturns400() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"X\",\"author\":\"Y\",\"year\":2020,\"price\":0,\"costPrice\":50}"))
            .andExpect(status().isBadRequest());
    }
}
