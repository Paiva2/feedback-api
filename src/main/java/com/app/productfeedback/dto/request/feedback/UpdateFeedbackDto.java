package com.app.productfeedback.dto.request.feedback;

import org.hibernate.validator.constraints.UUID;

import com.app.productfeedback.enums.FeedbackStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateFeedbackDto {
    @UUID
    @NotNull(message = "id can't be null.")
    @NotBlank(message = "id can't be blank.")
    private String id;

    private String title;

    private String details;

    private FeedbackStatus status;

    @UUID
    private String categoryId;

    public UpdateFeedbackDto() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public FeedbackStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
