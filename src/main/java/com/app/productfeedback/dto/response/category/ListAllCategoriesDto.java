package com.app.productfeedback.dto.response.category;

import java.util.ArrayList;
import java.util.List;

import com.app.productfeedback.entities.Category;

public class ListAllCategoriesDto {
    public int currentPage;

    public int itensPerPage;

    public long totalElements;

    public List<CategoryDto> categories;

    public ListAllCategoriesDto() {}

    public ListAllCategoriesDto(int currentPage, int itensPerPage, List<Category> categories,
            long totalElements) {
        this.currentPage = currentPage;
        this.itensPerPage = itensPerPage;
        this.categories = this.formatCategoriesList(categories);
        this.totalElements = totalElements;
    }

    public List<CategoryDto> formatCategoriesList(List<Category> categoriesList) {
        List<CategoryDto> categoryDtoList = new ArrayList<>();

        categoriesList.forEach(category -> {
            categoryDtoList.add(new CategoryDto(category.getId(), category.getName()));
        });

        return categoryDtoList;
    }
}
