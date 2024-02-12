package com.app.productfeedback.dto.response.feedback;

import java.util.UUID;

import com.app.productfeedback.dto.response.category.CategoryDto;
import com.app.productfeedback.enums.FeedbackStatus;

public class FeedbackDto {
    private final UUID id;

    private final String title;

    private final String details;

    private final FeedbackStatus status;

    private final int upVotes;

    private final CategoryDto category;

    public FeedbackDto(UUID id, String title, String details, FeedbackStatus status,
            CategoryDto category, int upVotes) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.status = status;
        this.category = category;
        this.upVotes = upVotes;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public FeedbackStatus getStatus() {
        return status;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public int getUpVotes() {
        return upVotes;
    }
}
