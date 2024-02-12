package com.app.productfeedback.dto.request.feedback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NewFeedbackDto {
    @NotNull(message = "title can't be null.")
    @NotBlank(message = "title can't be empty.")
    private String title;

    @NotNull(message = "details can't be null.")
    @NotBlank(message = "details can't be empty.")
    private String details;

    @NotNull(message = "categoryId can't be null.")
    @org.hibernate.validator.constraints.UUID(message = "categoryId must be an valid uuid.")
    private String categoryId;

    public NewFeedbackDto() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
