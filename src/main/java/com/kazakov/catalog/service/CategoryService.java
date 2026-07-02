package com.kazakov.catalog.service;

import com.kazakov.catalog.mapper.CategoryMapper;
import com.kazakov.catalog.model.dto.category.CategoryCreateDto;
import com.kazakov.catalog.model.dto.category.CategoryResponseDto;
import com.kazakov.catalog.model.dto.category.CategoryUpdateDto;
import com.kazakov.catalog.model.entity.Category;
import com.kazakov.catalog.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Getter
@Service
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public CategoryService(CategoryRepository repository, CategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public CategoryResponseDto createCategory(CategoryCreateDto dto) {
        Category category = mapper.toEntity(dto);
        repository.save(category);
        return mapper.toDto(category);
    }

    public CategoryResponseDto findById(UUID id) {
        Optional<Category> category = repository.findById(id);
        if(category.isEmpty()){
            throw new EntityNotFoundException("Category not found with UUID: " + id);
        }
        return mapper.toDto(category.get());
    }

    public CategoryResponseDto findByName(String name) {
        Optional<Category> category = repository.findByName(name);
        if(category.isEmpty()){
            throw new EntityNotFoundException("Category not found with name: " + name);
        }
        return mapper.toDto(category.get());
    }

    public List<CategoryResponseDto> findAll(Pageable pageable) {
        Page<Category> page = repository.findAll(pageable);
        return page.stream().map(mapper::toDto).toList();
    }

    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Transactional
    public CategoryResponseDto updateCategory(UUID id, CategoryUpdateDto dto) {
        Optional<Category> category = repository.findById(id);
        if(category.isEmpty()){
            throw new EntityNotFoundException("Category not found with UUID: " + id);
        }
        Category categoryToUpdate = category.get();
        categoryToUpdate.setName(dto.getName());
        repository.save(categoryToUpdate);

        return mapper.toDto(categoryToUpdate);
    }
}
