package com.kazakov.catalog.integration.controller;


import com.kazakov.catalog.integration.BaseIntegrationTest;
import com.kazakov.catalog.model.dto.product.ProductCreateDto;
import com.kazakov.catalog.model.entity.Product;
import com.kazakov.catalog.repository.ProductRepository;
import com.kazakov.catalog.repository.StockRepository;
import com.kazakov.catalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        stockRepository.deleteAll();
        productRepository.deleteAll(); // Гарантируем чистоту базы перед каждым тестом
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProductByAdmin() throws Exception {
        ProductCreateDto requestDto = ProductCreateDto.builder()
                .sku("SKU-123")
                .name("Name-123")
                .price(BigDecimal.valueOf(1500))
                .build();


        mockMvc.perform(post("/api/v1/products")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SKU-123"))
                .andExpect(jsonPath("$.name").value("Name-123"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProductByUser() throws Exception {

        ProductCreateDto createDto = ProductCreateDto.builder()
                .sku("SKU-123")
                .name("Name-123")
                .price(BigDecimal.valueOf(1500))
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isForbidden()); // Ждем 403 Error
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProductButBadRequest() throws Exception {

        ProductCreateDto invalidDto = ProductCreateDto.builder()
                .sku("")
                .name("")
                .price(BigDecimal.valueOf(1500))
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()); // Ждем 400 из-за @Valid
    }

    @Test
    void findBySku_ShouldReturnProduct_WhenProductExists() throws Exception {

        Product product = Product.builder()
                .sku("SKU-EXIST")
                .name("Existing Product")
                .price(BigDecimal.valueOf(1500))
                .build();
        productRepository.save(product);


        mockMvc.perform(get("/api/v1/products/sku/{sku}", "SKU-EXIST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-EXIST"))
                .andExpect(jsonPath("$.name").value("Existing Product"));
    }


}
