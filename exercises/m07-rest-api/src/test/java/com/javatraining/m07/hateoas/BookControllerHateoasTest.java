package com.javatraining.m07.hateoas;

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
@DisplayName("M07-T1: HATEOAS Book Library — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerHateoasTest {

    @Autowired MockMvc mockMvc;

    static final String BASE = "/api/books";

    static final String CLEAN_CODE =
        "{\"title\":\"Clean Code\",\"author\":\"Robert Martin\",\"isbn\":\"9780132350884\",\"year\":2008}";
    static final String EFFECTIVE_JAVA =
        "{\"title\":\"Effective Java\",\"author\":\"Joshua Bloch\",\"isbn\":\"9780134685991\",\"year\":2018}";
    static final String REFACTORING =
        "{\"title\":\"Refactoring\",\"author\":\"Martin Fowler\",\"isbn\":\"9780201485677\",\"year\":1999}";

    // ── POST ─────────────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("POST /api/books → 201 Created")
    void createReturns201() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CLEAN_CODE))
            .andExpect(status().isCreated());
    }

    @Test @Order(2)
    @DisplayName("POST response body contains self link")
    void createResponseHasSelfLink() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CLEAN_CODE))
            .andExpect(jsonPath("$._links.self.href").exists())
            .andExpect(jsonPath("$._links.self.href", containsString("/api/books/1")));
    }

    @Test @Order(3)
    @DisplayName("POST response contains Location header pointing to new resource")
    void createResponseHasLocationHeader() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CLEAN_CODE))
            .andExpect(header().string("Location", containsString("/api/books/1")));
    }

    @Test @Order(4)
    @DisplayName("POST response body contains update link")
    void createResponseHasUpdateLink() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CLEAN_CODE))
            .andExpect(jsonPath("$._links.update.href", containsString("/api/books/1")));
    }

    @Test @Order(5)
    @DisplayName("POST response body contains delete link")
    void createResponseHasDeleteLink() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CLEAN_CODE))
            .andExpect(jsonPath("$._links.delete.href", containsString("/api/books/1")));
    }

    @Test @Order(6)
    @DisplayName("POST response body contains books (collection) link")
    void createResponseHasBooksLink() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CLEAN_CODE))
            .andExpect(jsonPath("$._links.books.href", containsString("/api/books")));
    }

    // ── GET single ───────────────────────────────────────────────────────────

    @Test @Order(7)
    @DisplayName("GET /api/books/{id} → 200 with all 4 links")
    void getByIdHasAllLinks() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CLEAN_CODE));

        mockMvc.perform(get(BASE + "/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.self.href").exists())
            .andExpect(jsonPath("$._links.update.href").exists())
            .andExpect(jsonPath("$._links.delete.href").exists())
            .andExpect(jsonPath("$._links.books.href").exists());
    }

    @Test @Order(8)
    @DisplayName("GET /api/books/{id} → 404 for unknown id")
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get(BASE + "/999"))
            .andExpect(status().isNotFound());
    }

    @Test @Order(9)
    @DisplayName("GET /api/books/{id} → response contains book data")
    void getByIdContainsData() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CLEAN_CODE));

        mockMvc.perform(get(BASE + "/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.title").value("Clean Code"))
            .andExpect(jsonPath("$.author").value("Robert Martin"))
            .andExpect(jsonPath("$.isbn").value("9780132350884"))
            .andExpect(jsonPath("$.year").value(2008));
    }

    // ── GET collection ───────────────────────────────────────────────────────

    @Test @Order(10)
    @DisplayName("GET /api/books → 200 with self link on collection")
    void collectionHasSelfLink() throws Exception {
        mockMvc.perform(get(BASE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.self.href", containsString("/api/books")));
    }

    @Test @Order(11)
    @DisplayName("GET /api/books → embedded books list present")
    void collectionHasEmbeddedBooks() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CLEAN_CODE));
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(EFFECTIVE_JAVA));

        mockMvc.perform(get(BASE).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded").exists())
            .andExpect(jsonPath("$._embedded..title",
                hasItems("Clean Code", "Effective Java")));
    }

    @Test @Order(12)
    @DisplayName("GET /api/books → each embedded book has its own self link")
    void embeddedBooksHaveSelfLinks() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CLEAN_CODE));

        mockMvc.perform(get(BASE).accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$._embedded..[?(@.title=='Clean Code')]._links.self.href")
                .value(hasItem(containsString("/api/books/1"))));
    }

    @Test @Order(13)
    @DisplayName("GET /api/books?page=1&size=1 → response has next and prev links")
    void paginationLinksPresent() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CLEAN_CODE));
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(EFFECTIVE_JAVA));
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(REFACTORING));

        // page=1 of 3 books with size=1 → should have both next and prev
        mockMvc.perform(get(BASE + "?page=1&size=1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.next.href").exists())
            .andExpect(jsonPath("$._links.prev.href").exists());
    }

    @Test @Order(14)
    @DisplayName("GET /api/books?page=0&size=1 → first page has next but NO prev")
    void firstPageHasNextNoPrev() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CLEAN_CODE));
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(EFFECTIVE_JAVA));

        mockMvc.perform(get(BASE + "?page=0&size=1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.next.href").exists())
            .andExpect(jsonPath("$._links.prev").doesNotExist());
    }

    @Test @Order(15)
    @DisplayName("GET /api/books?page=1&size=2 → last page has prev but NO next")
    void lastPageHasPrevNoNext() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CLEAN_CODE));
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(EFFECTIVE_JAVA));
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(REFACTORING));

        // 3 books, size=2: page=0 has [1,2], page=1 has [3] — last page
        mockMvc.perform(get(BASE + "?page=1&size=2").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._links.prev.href").exists())
            .andExpect(jsonPath("$._links.next").doesNotExist());
    }

    // ── PUT ──────────────────────────────────────────────────────────────────

    @Test @Order(16)
    @DisplayName("PUT /api/books/{id} → 200 with updated data and self link")
    void updateBook() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CLEAN_CODE));

        mockMvc.perform(put(BASE + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Clean Code 2nd Ed\",\"author\":\"Robert Martin\",\"isbn\":\"9780132350884\",\"year\":2024}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Clean Code 2nd Ed"))
            .andExpect(jsonPath("$.year").value(2024))
            .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test @Order(17)
    @DisplayName("PUT /api/books/{id} → 404 for unknown id")
    void updateNotFound() throws Exception {
        mockMvc.perform(put(BASE + "/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(CLEAN_CODE))
            .andExpect(status().isNotFound());
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @Test @Order(18)
    @DisplayName("DELETE /api/books/{id} → 204 No Content")
    void deleteBook() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(CLEAN_CODE));
        mockMvc.perform(delete(BASE + "/1")).andExpect(status().isNoContent());
        mockMvc.perform(get(BASE + "/1")).andExpect(status().isNotFound());
    }

    @Test @Order(19)
    @DisplayName("DELETE /api/books/{id} → 404 for unknown id")
    void deleteNotFound() throws Exception {
        mockMvc.perform(delete(BASE + "/999"))
            .andExpect(status().isNotFound());
    }

    // ── Validation ───────────────────────────────────────────────────────────

    @Test @Order(20)
    @DisplayName("POST with invalid ISBN (not 13 digits) → 400")
    void invalidIsbnReturns400() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Book\",\"author\":\"Author\",\"isbn\":\"123\",\"year\":2020}"))
            .andExpect(status().isBadRequest());
    }

    @Test @Order(21)
    @DisplayName("POST with blank title → 400")
    void blankTitleReturns400() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\",\"author\":\"Author\",\"isbn\":\"9780132350884\",\"year\":2020}"))
            .andExpect(status().isBadRequest());
    }

    @Test @Order(22)
    @DisplayName("POST with year < 1 → 400")
    void invalidYearReturns400() throws Exception {
        mockMvc.perform(post(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Book\",\"author\":\"Author\",\"isbn\":\"9780132350884\",\"year\":0}"))
            .andExpect(status().isBadRequest());
    }
}
