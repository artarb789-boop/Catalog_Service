--liquibase formatted sql

--changeset kazakov:3 labels:catalog context:initial
--comment: Create table stock with one-to-one relationship to products
CREATE TABLE stock (
                       id         UUID PRIMARY KEY,
                       product_id UUID NOT NULL UNIQUE,
                       quantity   INTEGER NOT NULL,
                       reserved   INTEGER NOT NULL,

    -- Проверка на уровне БД, что количество не может быть отрицательным (@PositiveOrZero)
                       CONSTRAINT chk_stock_quantity_positive CHECK (quantity >= 0),
                       CONSTRAINT chk_stock_reserved_positive CHECK (reserved >= 0)
);

-- Добавляем внешний ключ на таблицу продуктов
-- ON DELETE CASCADE означает: если продукт удален, его складская запись удалится автоматически
ALTER TABLE stock
    ADD CONSTRAINT fk_stock_product
        FOREIGN KEY (product_id) REFERENCES products(id)
            ON DELETE CASCADE;

--rollback ALTER TABLE stock DROP CONSTRAINT fk_stock_product;
--rollback DROP TABLE stock;