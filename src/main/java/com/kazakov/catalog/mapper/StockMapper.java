package com.kazakov.catalog.mapper;

import com.kazakov.catalog.model.dto.product.ProductResponseDto;
import com.kazakov.catalog.model.dto.stock.StockCreateDto;
import com.kazakov.catalog.model.dto.stock.StockResponseDto;
import com.kazakov.catalog.model.entity.Product;
import com.kazakov.catalog.model.entity.Stock;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockMapper {

    StockResponseDto toDto(Stock stock);
    Stock toEntity(StockCreateDto stockDto);
}
