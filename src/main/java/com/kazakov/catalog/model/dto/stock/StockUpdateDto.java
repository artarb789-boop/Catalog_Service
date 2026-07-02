package com.kazakov.catalog.model.dto.stock;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StockUpdateDto {
    @PositiveOrZero
    private int quantity;
    @PositiveOrZero
    private int reserved;
}
