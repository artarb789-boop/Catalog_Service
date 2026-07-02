package com.kazakov.catalog.controller;

import com.kazakov.catalog.model.dto.category.CategoryResponseDto;
import com.kazakov.catalog.model.dto.category.CategoryUpdateDto;
import com.kazakov.catalog.model.dto.stock.StockCreateDto;
import com.kazakov.catalog.model.dto.stock.StockResponseDto;
import com.kazakov.catalog.model.dto.stock.StockUpdateDto;
import com.kazakov.catalog.service.StockService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final StockService service;

    @Autowired
    public StockController(StockService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockResponseDto create(@RequestBody StockCreateDto dto) {
        return service.createStock(dto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StockResponseDto findStockById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StockResponseDto> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "quantity") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), sortBy);
        return service.findAll(pageable);
    }

    @GetMapping("/product/sku/{sku}")
    @ResponseStatus(HttpStatus.OK)
    public StockResponseDto findStockByProductSku(@PathVariable String sku) {
        return service.findByProductSku(sku);
    }

    @GetMapping("/product/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StockResponseDto findStockByProductId(@PathVariable UUID id) {
        return service.findByProductId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StockResponseDto update(@PathVariable UUID id, @RequestBody @Valid StockUpdateDto dto) {
        return service.updateStock(id, dto);
    }

//    @PostMapping("/reserve/{productId}")
//    public StockResponseDto reserveStock(@PathVariable UUID productId, @RequestParam int quantity) {
//        return service.reserveStock(productId, quantity);
//    }

//    @PutMapping("/{productId}/cancel")
//    public void cancelReserved(@PathVariable UUID productId, @RequestParam int reservedQuantity) {
//       service.cancelReserved(productId, reservedQuantity);
//    }
//    @PutMapping("/{productId}/confirm-payment")
//    public void confirmReserved(@PathVariable UUID productId, @RequestParam int reservedQuantity) {
//        service.confirmReserved(productId, reservedQuantity);
//    }
}
