package com.kazakov.catalog.mapper;

import com.kazakov.catalog.model.dto.product.ProductCreateDto;
import com.kazakov.catalog.model.dto.product.ProductDto;
import com.kazakov.catalog.model.dto.product.ProductListResponseDto;
import com.kazakov.catalog.model.dto.product.ProductResponseDto;
import com.kazakov.catalog.model.entity.Product;
import org.mapstruct.Mapper;



@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponseDto toDto(Product product);
    ProductDto toProductDto(Product product);
    Product toEntity(ProductCreateDto dto);

}
