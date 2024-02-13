package com.app.productfeedback.dto.request.comment;

import org.hibernate.validator.constraints.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NewCommentDto {
    @NotNull(message = "comment can't be null")
    @NotBlank(message = "comment can't be empty")
    @Size(max = 500, message = "comment can't be more than 500 characters")
    private String comment;

    @NotNull(message = "feedbackId can't be null")
    @NotBlank(message = "feedbackId can't be empty")
    @UUID
    private String feedbackId;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }
}
