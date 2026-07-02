--liquibase formatted sql

--changeset kazakov:2 labels:catalog context:initial
--comment: Create table products with foreign key to categories
CREATE TABLE products (
                          id          UUID PRIMARY KEY,
                          sku         VARCHAR(255) NOT NULL UNIQUE,
                          name        VARCHAR(255) NOT NULL,
                          description TEXT,
                          price       NUMERIC(10, 2) NOT NULL,
                          currency    VARCHAR(10) DEFAULT 'EUR',
                          category_id UUID,
                          created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
                          updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
                          available   BOOLEAN DEFAULT TRUE NOT NULL
);

ALTER TABLE products
    ADD CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
            ON DELETE SET NULL;

--rollback ALTER TABLE products DROP CONSTRAINT fk_products_category;
--rollback DROP TABLE products;

