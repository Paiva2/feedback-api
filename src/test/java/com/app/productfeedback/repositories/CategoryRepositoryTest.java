package com.app.productfeedback.repositories;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.beans.PropertyDescriptor;

import com.app.productfeedback.entities.Category;
import com.app.productfeedback.interfaces.CategoryRepositoryInterface;

@SuppressWarnings("null")
public class CategoryRepositoryTest implements CategoryRepositoryInterface {
    protected List<Category> categories = new ArrayList<>();

    @Override
    public Optional<Category> findByName(String categoryName) {
        Optional<Category> findCategory = this.categories.stream()
                .filter(category -> category.getName().equals(categoryName)).findFirst();

        return findCategory;
    }

    @Override
    public Category save(Category category) {
        Category categoryReturn;

        if (category.getId() != null) {
            Optional<Category> categoryExistent = this.categories.stream()
                    .filter(categories -> categories.getId().equals(category.getId())).findFirst();

            BeanWrapper sourceCategory = new BeanWrapperImpl(categoryExistent.get());
            BeanWrapper updatedCategory = new BeanWrapperImpl(category);

            List<PropertyDescriptor> fields = List.of(updatedCategory.getPropertyDescriptors());

            fields.forEach(field -> {
                String fieldName = field.getName();
                Object value = updatedCategory.getPropertyValue(fieldName);

                boolean canUpdate = fieldName.hashCode() != "id".hashCode()
                        && fieldName.hashCode() != "class".hashCode() && value != null;

                if (canUpdate) {
                    sourceCategory.setPropertyValue(fieldName, value);
                }
            });

            categoryReturn = categoryExistent.get();
        } else {
            category.setId(UUID.randomUUID());

            this.categories.add(category);

            categoryReturn = category;
        }

        return categoryReturn;
    }

    @Override
    public Optional<Category> findById(UUID categoryId) {
        Optional<Category> findCategory = this.categories.stream()
                .filter(category -> category.getId().equals(categoryId)).findFirst();

        return findCategory;
    }

    @Override
    public void deleteById(UUID categoryId) {
        this.categories = this.categories.stream()
                .filter(category -> !category.getId().equals(categoryId)).toList();
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        int fromIndex = pageable.getPageNumber() * pageable.getPageSize();

        if (this.categories.size() <= fromIndex) {
            return new PageImpl<Category>(Collections.emptyList());
        } ;

        List<Category> createCategoriesPagination = this.categories.subList(fromIndex,
                Math.min(fromIndex + pageable.getPageSize(), this.categories.size()));

        return new PageImpl<Category>(createCategoriesPagination);
    }
}
