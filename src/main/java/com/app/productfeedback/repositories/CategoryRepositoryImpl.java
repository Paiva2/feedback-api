package com.app.productfeedback.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.productfeedback.entities.Category;
import com.app.productfeedback.interfaces.CategoryRepositoryInterface;


public interface CategoryRepositoryImpl
        extends CategoryRepositoryInterface, JpaRepository<Category, UUID> {
}
