--liquibase formatted sql

--changeset kazakov:4 labels:catalog context:initial
--comment: Create table processed_events for Kafka idempotency check
CREATE TABLE processed_events (
                                  event_id     UUID PRIMARY KEY,
                                  processed_at TIMESTAMP WITH TIME ZONE NOT NULL
);

--rollback DROP TABLE processed_events;