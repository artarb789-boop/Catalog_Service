package com.kazakov.catalog.model.dto.product;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProductFilter {
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private UUID categoryId;
    private Boolean available;
    private int page = 0;
    private int pageSize = 2;
}
