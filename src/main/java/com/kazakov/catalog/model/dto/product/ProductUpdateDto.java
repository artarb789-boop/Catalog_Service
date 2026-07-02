package com.kazakov.catalog.model.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateDto {
    @NotBlank
    @Size(max = 255)
    private String name;

    private String description;

    @NotNull
    @Positive
    private BigDecimal price;

    private String currency;


}
