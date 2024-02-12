package com.app.productfeedback.interfaces;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.app.productfeedback.entities.Category;

public interface CategoryRepository {
    Optional<Category> findByName(String categoryName);

    Optional<Category> findById(UUID categoryId);

    Category save(Category category);

    void deleteById(UUID categoryId);

    Page<Category> findAll(Pageable pageable);
}
