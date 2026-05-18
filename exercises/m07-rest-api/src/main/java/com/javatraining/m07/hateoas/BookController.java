package com.javatraining.m07.hateoas;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * EXERCISE M07-T1: HATEOAS Book Library API (Richardson Maturity Level 3)
 *
 * Evolve a flat CRUD API to full Level 3 HATEOAS by adding hypermedia links.
 *
 * Requirements:
 *  - Every single-resource response (BookModel) must include:
 *      self   → GET /api/books/{id}
 *      update → PUT /api/books/{id}
 *      delete → DELETE /api/books/{id}
 *      books  → GET /api/books  (collection link)
 *
 *  - Collection responses (GET /api/books) must include:
 *      self       → GET /api/books
 *      next/prev  → pagination links when page > 0 or more pages exist
 *
 *  - POST response must include Location header pointing to the new resource
 *
 *  - Use Spring HATEOAS: WebMvcLinkBuilder.linkTo() / methodOn()
 *
 * DO NOT modify DTOs, BookStore, or method signatures.
 */

// ── DTOs (DO NOT MODIFY) ─────────────────────────────────────────────────────

record CreateBookRequest(
    @NotBlank String title,
    @NotBlank String author,
    @NotBlank @Pattern(regexp = "\\d{13}", message = "ISBN must be 13 digits") String isbn,
    @Min(1) int year
) {}

record BookResponse(Long id, String title, String author, String isbn, int year) {}

// ── In-memory store (DO NOT MODIFY) ─────────────────────────────────────────

class BookStore {
    private final Map<Long, BookResponse> store = new LinkedHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public BookResponse save(CreateBookRequest req) {
        Long id = idGen.getAndIncrement();
        BookResponse book = new BookResponse(id, req.title(), req.author(), req.isbn(), req.year());
        store.put(id, book);
        return book;
    }

    public Optional<BookResponse> findById(Long id)     { return Optional.ofNullable(store.get(id)); }
    public List<BookResponse>     findAll()             { return List.copyOf(store.values()); }
    public boolean                delete(Long id)       { return store.remove(id) != null; }
    public int                    count()               { return store.size(); }

    public Optional<BookResponse> update(Long id, CreateBookRequest req) {
        if (!store.containsKey(id)) return Optional.empty();
        BookResponse updated = new BookResponse(id, req.title(), req.author(), req.isbn(), req.year());
        store.put(id, updated);
        return Optional.of(updated);
    }
}

// ── BookModelAssembler — implement this ──────────────────────────────────────

/**
 * TODO:
 *  - Implement RepresentationModelAssembler<BookResponse, EntityModel<BookResponse>>
 *  - In toModel(BookResponse book):
 *      Add links: self, update, delete, books
 *  - Annotate with @Component
 */
class BookModelAssembler {
    // TODO: implement toModel(BookResponse book) → EntityModel<BookResponse>
}

// ── BookController — implement this ──────────────────────────────────────────

/**
 * TODO:
 *  - @RestController @RequestMapping("/api/books")
 *  - Constructor-inject BookStore and BookModelAssembler
 *
 * Endpoints:
 *   POST   /api/books               → 201 + Location header + EntityModel<BookResponse>
 *   GET    /api/books               → 200 + CollectionModel with self + pagination links
 *   GET    /api/books?page=N&size=M → paginated response (default page=0, size=10)
 *   GET    /api/books/{id}          → 200 EntityModel or 404
 *   PUT    /api/books/{id}          → 200 EntityModel or 404
 *   DELETE /api/books/{id}          → 204 or 404
 */
class BookController {

    // TODO: inject BookStore and BookModelAssembler

    public ResponseEntity<EntityModel<BookResponse>> create(
            @Valid @RequestBody CreateBookRequest req) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public CollectionModel<EntityModel<BookResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public ResponseEntity<EntityModel<BookResponse>> findById(@PathVariable Long id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public ResponseEntity<EntityModel<BookResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateBookRequest req) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public ResponseEntity<Void> delete(@PathVariable Long id) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
