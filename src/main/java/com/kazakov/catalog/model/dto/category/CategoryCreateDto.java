package com.kazakov.catalog.model.dto.category;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateDto {

    @NotBlank
    @NotNull
    private String name;

    @Nullable
    private CategoryDto parent;


}
