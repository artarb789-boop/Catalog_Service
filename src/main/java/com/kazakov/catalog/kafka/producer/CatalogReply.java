package com.kazakov.catalog.kafka.producer;

import com.kazakov.catalog.kafka.consumer.OrderEventType;
import com.kazakov.catalog.model.dto.OrderItemDto;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CatalogReply {
    private UUID eventId;
    private OrderEventType eventType;
    private UUID orderId;
    private Instant timestamp;
}
