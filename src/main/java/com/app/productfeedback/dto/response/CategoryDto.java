package com.app.productfeedback.dto.response;

import java.util.UUID;

public class CategoryDto {
    public UUID id;
    public String name;

    public CategoryDto() {}

    public CategoryDto(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
