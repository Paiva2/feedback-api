package com.app.productfeedback.dto.response.feedback;

import java.util.UUID;

import com.app.productfeedback.dto.response.category.CategoryDto;
import com.app.productfeedback.dto.response.user.UserDto;
import com.app.productfeedback.enums.FeedbackStatus;

public class FeedbackResponseDto extends FeedbackDto {
    private UserDto feedbackOwner;

    public FeedbackResponseDto(UUID id, String title, String details, FeedbackStatus status,
            CategoryDto category, UserDto feedbackOwner, int upVotes) {
        super(id, title, details, status, category, upVotes);

        this.feedbackOwner = feedbackOwner;
    }


    public UserDto getFeedbackOwner() {
        return feedbackOwner;
    }
}
