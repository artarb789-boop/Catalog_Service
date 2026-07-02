package com.kazakov.catalog.service;

import com.kazakov.catalog.model.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ProductSpecification {

    public Specification<Product> nameSpec(String name){
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%"+name.toLowerCase()+"%");
    }

    public Specification<Product> priceSpec(BigDecimal price){
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("price"), price);
    }

    public Specification<Product> availabilitySpec(boolean available) {
        return (root, query, cb) ->
                available ? cb.isTrue(root.get("available")) : cb.isFalse(root.get("available"));
    }

    public Specification<Product> priceRangeSpec(BigDecimal priceFrom, BigDecimal priceTo) {
        return (root, query, cb) ->
                cb.between(root.get("price"), priceFrom, priceTo);
    }

    public Specification<Product> descriptionSpec(String description) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("description")), "%"+description+"%");
    }

    public Specification<Product> categorySpec(UUID categoryId) {
        return (root, query, cb) ->
                cb.equal(root.get("category").get("id"), categoryId);
    }
}
