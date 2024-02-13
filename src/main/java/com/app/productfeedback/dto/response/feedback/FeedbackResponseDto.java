package com.app.productfeedback.dto.response.feedback;

import java.util.UUID;
import java.util.Date;
import java.util.List;

import com.app.productfeedback.dto.response.category.CategoryDto;
import com.app.productfeedback.dto.response.comment.CommentDto;
import com.app.productfeedback.dto.response.user.UserDto;
import com.app.productfeedback.entities.Comment;
import com.app.productfeedback.enums.FeedbackStatus;

public class FeedbackResponseDto extends FeedbackDto {
    private UserDto feedbackOwner;

    private List<CommentDto> comments;

    public FeedbackResponseDto(UUID id, String title, String details, FeedbackStatus status,
            CategoryDto category, UserDto feedbackOwner, int upVotes, List<Comment> comments,
            Date createdAt) {

        super(id, title, details, status, category, upVotes, createdAt);

        this.feedbackOwner = feedbackOwner;
        this.comments = this.formatComments(comments);
    }

    public UserDto getFeedbackOwner() {
        return feedbackOwner;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public List<CommentDto> formatComments(List<Comment> comments) {
        return comments.stream().map(comment -> {
            return new CommentDto(comment.getId(), comment.getComment(), comment.getUserId(),
                    comment.getFeedbackId(), comment.getCreatedAt());
        }).toList();
    }
}
