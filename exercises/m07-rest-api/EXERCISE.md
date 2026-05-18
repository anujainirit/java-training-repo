# M07 — REST API Design Exercises

---

## Topic 1: HATEOAS Book Library (Richardson Maturity Level 3)

### Objective
Evolve a plain CRUD API into a fully hypermedia-driven Level 3 REST API using Spring HATEOAS. Every response must include navigable links so clients can discover the API without out-of-band documentation.

### Files
| File | Your Action |
|---|---|
| `src/main/java/.../BookController.java` | ✅ Implement `BookModelAssembler` + `BookController` |
| `src/test/java/.../BookControllerHateoasTest.java` | 🚫 DO NOT MODIFY (22 tests) |

### What Is HATEOAS?
> "Hypermedia As The Engine Of Application State" — responses contain links that tell the client what it can do next, instead of the client having to hardcode URLs.

**Example response** for `GET /api/books/1`:
```json
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert Martin",
  "_links": {
    "self":   { "href": "http://localhost/api/books/1" },
    "update": { "href": "http://localhost/api/books/1" },
    "delete": { "href": "http://localhost/api/books/1" },
    "books":  { "href": "http://localhost/api/books"  }
  }
}
```

### Implementation Guide

**Step 1 — BookModelAssembler:**
```java
@Component
class BookModelAssembler
        implements RepresentationModelAssembler<BookResponse, EntityModel<BookResponse>> {

    @Override
    public EntityModel<BookResponse> toModel(BookResponse book) {
        return EntityModel.of(book,
            linkTo(methodOn(BookController.class).findById(book.id())).withSelfRel(),
            linkTo(methodOn(BookController.class).update(book.id(), null)).withRel("update"),
            linkTo(methodOn(BookController.class).delete(book.id())).withRel("delete"),
            linkTo(methodOn(BookController.class).findAll(0, 10)).withRel("books")
        );
    }
}
```

**Step 2 — POST with Location header:**
```java
@PostMapping
public ResponseEntity<EntityModel<BookResponse>> create(@Valid @RequestBody CreateBookRequest req) {
    BookResponse saved = bookStore.save(req);
    EntityModel<BookResponse> model = assembler.toModel(saved);
    return ResponseEntity
        .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(model);
}
```

**Step 3 — Paginated collection with prev/next links:**
```java
@GetMapping
public CollectionModel<EntityModel<BookResponse>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    List<BookResponse> all = bookStore.findAll();
    int totalPages = (int) Math.ceil((double) all.size() / size);
    List<EntityModel<BookResponse>> models = all.stream()
        .skip((long) page * size).limit(size)
        .map(assembler::toModel).toList();

    List<Link> links = new ArrayList<>();
    links.add(linkTo(methodOn(BookController.class).findAll(page, size)).withSelfRel());
    if (page > 0)
        links.add(linkTo(methodOn(BookController.class).findAll(page - 1, size)).withRel("prev"));
    if (page < totalPages - 1)
        links.add(linkTo(methodOn(BookController.class).findAll(page + 1, size)).withRel("next"));

    return CollectionModel.of(models, links);
}
```

### Running Tests
```bash
mvn test -Dtest=BookControllerHateoasTest
```

---

## Topic 3 & 4: OpenAPI Annotations + Content Negotiation + @JsonView

### Objective
Build an API that speaks both JSON and XML, documents itself via OpenAPI 3, and exposes different fields to different roles using Jackson's `@JsonView`.

### Files
| File | Your Action |
|---|---|
| `src/main/java/.../BookCatalogueController.java` | ✅ Complete all TODOs |
| `src/test/java/.../BookCatalogueControllerTest.java` | 🚫 DO NOT MODIFY (19 tests) |

### Part A — Content Negotiation
Add `produces` to `@RequestMapping` to support both formats:
```java
@RequestMapping(
    value = "/api/v1/catalogue",
    produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
)
```
Spring will automatically use Jackson XML when the client sends `Accept: application/xml`.

### Part B — @JsonView Role-Based Fields
```java
// In controller method:
public ResponseEntity<?> findById(@PathVariable Long id,
        @RequestHeader(value = "X-Role", defaultValue = "USER") String role) {

    BookCatalogueEntry book = store.get(id); // ...
    if ("ADMIN".equalsIgnoreCase(role)) {
        return ResponseEntity.ok(
            objectMapper.writerWithView(Views.Admin.class).writeValueAsString(book)
        );
    }
    return ResponseEntity.ok(
        objectMapper.writerWithView(Views.Public.class).writeValueAsString(book)
    );
}
```
*Alternatively, return `MappingJacksonValue` which is simpler with Spring MVC:*
```java
MappingJacksonValue wrapper = new MappingJacksonValue(book);
wrapper.setSerializationView("ADMIN".equals(role) ? Views.Admin.class : Views.Public.class);
return ResponseEntity.ok(wrapper);
```

### Part C — OpenAPI Annotations
```java
@Tag(name = "Book Catalogue", description = "Manage the book catalogue")
@RestController
// ...
class BookCatalogueController {

    @Operation(summary = "Create a new book",
               description = "Adds a book to the catalogue. Admin fields (price, costPrice) are required.")
    @ApiResponse(responseCode = "201", description = "Book created successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @PostMapping
    public ResponseEntity<?> create(...) { ... }
}
```

### Verifying Swagger UI
```bash
mvn spring-boot:run
open http://localhost:8080/swagger-ui.html
```
You should see: "Book Catalogue" tag with all endpoints, schemas, and response codes documented.

### Running Tests
```bash
mvn test -Dtest=BookCatalogueControllerTest
```

---

## Acceptance Criteria (All M07 Topics)
- [ ] All 22 HATEOAS tests pass
- [ ] All 19 OpenAPI/ContentNeg tests pass
- [ ] Swagger UI renders at `/swagger-ui.html` with "Book Catalogue" tag
- [ ] GET with `Accept: application/xml` returns valid XML
- [ ] USER role never sees `price` or `costPrice` fields
- [ ] ADMIN role sees all fields
- [ ] Coverage ≥ 85%
- [ ] Zero SonarQube issues
