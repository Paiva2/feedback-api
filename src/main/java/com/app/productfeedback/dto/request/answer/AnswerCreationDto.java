package com.app.productfeedback.dto.request.answer;

import org.hibernate.validator.constraints.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AnswerCreationDto {
    @NotNull(message = "answer can't be null.")
    @NotBlank(message = "answer can't be empty.")
    @Size(max = 500, message = "answer can't be more than 500 characters")
    private String answer;

    @NotNull(message = "answeringToId can't be null.")
    @NotBlank(message = "answeringToId can't be empty.")
    @UUID
    private String answeringToId;

    @NotNull(message = "commentId can't be null.")
    @NotBlank(message = "commentId can't be empty.")
    @UUID
    private String commentId;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnsweringToId() {
        return answeringToId;
    }

    public void setAnsweringToId(String answeringToId) {
        this.answeringToId = answeringToId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
