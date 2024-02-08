package com.app.productfeedback.interfaces;

import java.util.Optional;
import com.app.productfeedback.entities.Category;

public interface CategoryRepositoryInterface {
    Optional<Category> findByName(String categoryName);

    Category save(Category category);
}
