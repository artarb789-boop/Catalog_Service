package com.kazakov.catalog.repository;

import com.kazakov.catalog.model.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<Stock, UUID>, JpaSpecificationExecutor<Stock> {

    @Query("from Stock s where s.id = :id")
    Optional<Stock> findByUUID(UUID id);

    @Query("from Stock s where s.product.id = :id")
    Optional<Stock> findByProductId(UUID id);

    @Query("from Stock s where s.product.sku = :sku")
    Optional<Stock> findByProductSku(String sku);
}
