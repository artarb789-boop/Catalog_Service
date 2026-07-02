package com.kazakov.catalog.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private UUID productId;
    private int quantity;
    private BigDecimal price;
    private String name;
    private String sku;
}
