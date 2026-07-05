package com.kazakov.catalog.controller;

import com.kazakov.catalog.model.dto.product.*;
import com.kazakov.catalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ProductResponseDto create(@RequestBody @Valid ProductCreateDto dto) throws ExecutionException, InterruptedException {
        return productService.createProduct(dto);
    }

    @GetMapping("/sku/{sku}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponseDto findBySku(@PathVariable String sku){
        return productService.findBySku(sku);
    }

    @GetMapping("/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponseDto findByUUID(@PathVariable UUID id){
        return productService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ProductListResponseDto findAll(ProductFilter productFilter){
        return productService.findAll(productFilter);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteByUUID(@PathVariable UUID id) throws ExecutionException, InterruptedException {
        productService.deleteById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ProductResponseDto update(@PathVariable UUID id, @RequestBody @Valid ProductUpdateDto dto) throws ExecutionException, InterruptedException {
        return productService.updateProduct(id, dto);
    }

    @GetMapping("/info/{id}")
    public ProductDto getProductInfo(@PathVariable UUID id){
        return productService.getProductInfo(id);
    }

}
