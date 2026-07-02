package com.kazakov.catalog.kafka;


import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KafkaProductEvent {
    private ProductEventType eventType;
    private UUID productId;
    private String sku;
    private BigDecimal price;
    private Boolean available;
    private Instant timestamp;
}
