package com.kazakov.catalog.kafka.consumer;

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
public class KafkaOrderEvent {
    private UUID eventId;
    private OrderEventType eventType;
    private UUID orderId;
    private List<OrderItemDto> items;
    private Instant timestamp;
}
