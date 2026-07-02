package com.kazakov.catalog.service;

import com.kazakov.catalog.kafka.KafkaProductEvent;
import com.kazakov.catalog.kafka.ProductEventType;
import com.kazakov.catalog.kafka.producer.KafkaProducer;
import com.kazakov.catalog.mapper.ProductMapper;
import com.kazakov.catalog.model.dto.product.*;
import com.kazakov.catalog.model.entity.Product;
import com.kazakov.catalog.model.entity.Stock;
import com.kazakov.catalog.repository.ProductRepository;
import com.kazakov.catalog.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Getter
@Service
public class ProductService {

    private final StockRepository stockRepository;
    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final ProductSpecification specification;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public ProductService(StockRepository stockRepository, ProductRepository productRepository, ProductMapper mapper, ProductSpecification specification, KafkaProducer kafkaProducer) {
        this.stockRepository = stockRepository;
        this.repository = productRepository;
        this.mapper = mapper;
        this.specification = specification;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    @CacheEvict(value = "products-lists", allEntries = true)
    public ProductResponseDto createProduct(ProductCreateDto createDto) throws ExecutionException, InterruptedException {
        Product product = mapper.toEntity(createDto);
        Product savedProduct = repository.save(product);
        Stock stock = Stock.builder()
                .product(savedProduct)
                .quantity(0)
                .reserved(0)
                .build();
        stockRepository.save(stock);
        KafkaProductEvent event = KafkaProductEvent.builder()
                .eventType(ProductEventType.PRODUCT_CREATED)
                .productId(savedProduct.getId())
                .sku(savedProduct.getSku())
                .price(savedProduct.getPrice())
                .available(true)
                .timestamp(Instant.now())
                .build();
        kafkaProducer.send("product-create", savedProduct.getId().toString(), event);
        return mapper.toDto(savedProduct);
    }

    @Cacheable(value = "products", key = "#sku")
    public ProductResponseDto findBySku(String sku) {
        Optional<Product> product = repository.findBySku(sku);
        if (product.isEmpty()) {
            throw new EntityNotFoundException("Product not found with sku: " + sku);
        }
        return mapper.toDto(product.get());
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponseDto findById(UUID id) {
        Optional<Product> product = repository.findById(id);
        if (product.isEmpty()) {
            throw new EntityNotFoundException("Product not found with UUID: " + id);
        }
        return mapper.toDto(product.get());
    }

    @Cacheable(value = "products-lists", key = "#productFilter")
    public ProductListResponseDto findAll(ProductFilter productFilter) {
        Specification<Product> spec = Specification.where(null);
        if (productFilter.getName() != null) {
            spec = spec.and(specification.nameSpec(productFilter.getName()));
        }
        if (productFilter.getPrice() != null) {
            spec = spec.and(specification.priceSpec(productFilter.getPrice()));
        }
        if (productFilter.getAvailable() != null) {
            spec = spec.and(specification.availabilitySpec(productFilter.getAvailable()));
        }
        if (productFilter.getPriceFrom() != null && productFilter.getPriceTo() != null) {
            spec = spec.and(specification.priceRangeSpec(productFilter.getPriceFrom(), productFilter.getPriceTo()));
        }
        if (productFilter.getDescription() != null) {
            spec = spec.and(specification.descriptionSpec(productFilter.getDescription()));
        }
        if (productFilter.getCategoryId() != null) {
            spec = spec.and(specification.categorySpec(productFilter.getCategoryId()));
        }

        Page<Product> page = repository.findAll(spec, PageRequest.of(productFilter.getPage(), productFilter.getPageSize()));
        List<ProductResponseDto> products = page.getContent().stream().map(mapper::toDto)
                .toList();
        return new ProductListResponseDto(products);
    }

    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "products-lists", allEntries = true)
    })
    @Transactional
    public void deleteById(UUID id) throws ExecutionException, InterruptedException {
        Stock stock = stockRepository.findByProductId(id).orElseThrow(
                () -> new EntityNotFoundException("Product not found with UUID: " + id));
        stockRepository.delete(stock);
        Product  product = repository.findById(id).orElse(null);
        repository.deleteById(id);
        KafkaProductEvent event = KafkaProductEvent.builder()
                .eventType(ProductEventType.PRODUCT_DELETED)
                .productId(id)
                .sku(product.getSku())
                .price(product.getPrice())
                .available(false)
                .timestamp(Instant.now())
                .build();
        kafkaProducer.send("product-delete", id.toString(), event);
    }

    @Transactional
    @PutMapping("/products")
    @Caching(evict = {
            @CacheEvict(value = "products", key = "#id"),
            @CacheEvict(value = "products-lists", allEntries = true)
    })
    public ProductResponseDto updateProduct(UUID id, ProductUpdateDto updateDto) throws ExecutionException, InterruptedException {
        Optional<Product> product = repository.findByUUID(id);
        if (product.isEmpty()) {
            throw new EntityNotFoundException("Product not found");
        }
        Product productToUpdate = product.get();

        productToUpdate.setName(updateDto.getName());
        productToUpdate.setDescription(updateDto.getDescription());
        productToUpdate.setPrice(updateDto.getPrice());
        productToUpdate.setCurrency(updateDto.getCurrency());

        repository.save(product.get());

        KafkaProductEvent event = KafkaProductEvent.builder()
                .eventType(ProductEventType.PRODUCT_UPDATED)
                .productId(id)
                .sku(productToUpdate.getSku())
                .price(productToUpdate.getPrice())
                .available(productToUpdate.isAvailable())
                .timestamp(Instant.now())
                .build();
        kafkaProducer.send("product-update", id.toString(), event);
        return mapper.toDto(productToUpdate);
    }

    @Transactional
    public ProductDto getProductInfo(UUID productId) {
        Optional<Product> product = repository.findByUUID(productId);
        if (product.isEmpty()) {
            throw new EntityNotFoundException("Product not found with UUID: " + productId);
        }
        return mapper.toProductDto(product.get());
    }

}
