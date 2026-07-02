package com.kazakov.catalog.model.dto.stock;

import com.kazakov.catalog.model.dto.product.ProductDto;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StockResponseDto {
    private UUID id;
    private ProductDto product;
    private int quantity;
    private int reserved;
    @Builder.Default
    private Boolean success = false;
}
