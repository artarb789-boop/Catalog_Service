--liquibase formatted sql

--changeset kazakov:1 labels:catalog context:initial
--comment: Create table categories with self-reference
CREATE TABLE categories (
                            id        UUID PRIMARY KEY,
                            name      VARCHAR(255) NOT NULL UNIQUE,
                            parent_id UUID
);

ALTER TABLE categories
    ADD CONSTRAINT fk_categories_parent
        FOREIGN KEY (parent_id) REFERENCES categories(id)
            ON DELETE SET NULL;

--rollback ALTER TABLE categories DROP CONSTRAINT fk_categories_parent;
--rollback DROP TABLE categories;