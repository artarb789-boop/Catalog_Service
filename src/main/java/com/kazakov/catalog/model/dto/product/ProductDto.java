package com.kazakov.catalog.model.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private UUID id;
    private String sku;
    private String name;
    private BigDecimal price;
    private String currency;
}
