package com.kazakov.catalog.model.dto.product;

import com.kazakov.catalog.model.dto.category.CategoryDto;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductCreateDto {

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9-]+$")
    private String sku;

    @NotBlank
    @Size(max = 255)
    private String name;

    private String description;

    @Nullable
    private CategoryDto category;

    @NotNull
    @Positive
    private BigDecimal price;

    private boolean available;
}
