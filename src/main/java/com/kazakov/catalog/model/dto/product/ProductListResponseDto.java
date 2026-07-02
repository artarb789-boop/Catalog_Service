package com.kazakov.catalog.model.dto.product;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponseDto implements Serializable {
    private List<ProductResponseDto> products;
}
