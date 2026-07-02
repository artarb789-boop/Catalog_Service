package com.kazakov.catalog.repository;

import com.kazakov.catalog.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    @Query("from Product p where p.sku = :sku")
    Optional<Product> findBySku(String sku);

    @Query("from Product p where p.id = :id")
    Optional<Product> findByUUID(UUID id);
}
