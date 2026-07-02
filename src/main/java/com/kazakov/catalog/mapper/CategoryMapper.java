package com.kazakov.catalog.mapper;

import com.kazakov.catalog.model.dto.category.CategoryCreateDto;
import com.kazakov.catalog.model.dto.category.CategoryResponseDto;
import com.kazakov.catalog.model.entity.Category;
import com.kazakov.catalog.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryCreateDto dto);

    @Mapping(target = "productIds", source = "products", qualifiedByName = "mapToProductIds")
    CategoryResponseDto toDto(Category category);

    @Named("mapToProductIds")
    default List<UUID> mapToProductIds(List<Product> products) {
        if(products==null){
            return new ArrayList<>();
        }
        return products.stream()
                .map(Product::getId)
                .toList();
    }
}
