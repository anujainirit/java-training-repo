-- V2__create_products_and_reviews.sql
CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    price       NUMERIC(12, 2) NOT NULL CHECK (price > 0),
    stock       INT NOT NULL DEFAULT 0,
    category_id BIGINT REFERENCES categories (id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE reviews (
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    reviewer   VARCHAR(100) NOT NULL,
    rating     SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    body       TEXT
);

CREATE INDEX idx_products_category ON products (category_id);
CREATE INDEX idx_reviews_product   ON reviews (product_id);
