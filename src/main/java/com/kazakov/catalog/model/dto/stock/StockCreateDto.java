package com.kazakov.catalog.model.dto.stock;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StockCreateDto {
    private UUID productId;
    @Positive
    private int quantity;
    @Positive
    private int reserved;
}
