package com.app.productfeedback.dto.request.category;

import com.app.productfeedback.entities.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NewCategoryDto {
    @NotBlank(message = "name can't be empty.")
    @NotNull(message = "name can't be null.")
    private String name;

    public NewCategoryDto() {}

    public Category toEntity() {
        Category category = new Category();
        category.setName(name);

        return category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
