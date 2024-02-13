package com.app.productfeedback.dto.response.comment;

import java.util.Date;
import java.util.UUID;

public class CommentDto {
    private UUID id;

    private String comment;

    private UUID userId;

    private UUID feedbackId;

    private Date createdAt;

    public CommentDto(UUID id, String comment, UUID userId, UUID feedbackId, Date createdAt) {
        this.id = id;
        this.comment = comment;
        this.userId = userId;
        this.feedbackId = feedbackId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getFeedbackId() {
        return feedbackId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
