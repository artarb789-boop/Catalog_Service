package com.kazakov.catalog.model.dto.product;

import com.kazakov.catalog.model.dto.category.CategoryDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private CategoryDto category;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean available;
}
