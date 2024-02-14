package com.app.productfeedback.dto.response.comment;

import java.util.Date;
import java.util.UUID;
import java.util.List;

import com.app.productfeedback.dto.response.answer.AnswerDto;
import com.app.productfeedback.dto.response.user.UserDto;
import com.app.productfeedback.entities.Answer;
import com.app.productfeedback.entities.User;

public class CommentDto {
    private UUID id;

    private String comment;

    private UserDto commentOwner;

    private UUID feedbackId;

    private Date createdAt;

    private List<AnswerDto> answers;

    public CommentDto(UUID id, String comment, UserDto commentOwner, UUID feedbackId,
            Date createdAt, List<Answer> answers) {
        this.id = id;
        this.comment = comment;
        this.commentOwner = commentOwner;
        this.feedbackId = feedbackId;
        this.createdAt = createdAt;
        this.answers = this.formatAnswers(answers);
    }

    public UUID getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public UUID getFeedbackId() {
        return feedbackId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public List<AnswerDto> getAnswers() {
        return answers;
    }

    public UserDto getCommentOwner() {
        return commentOwner;
    }

    public List<AnswerDto> formatAnswers(List<Answer> answers) {
        return answers.stream().map(answer -> {
            User answerUser = answer.getUser();
            User answeringToUser = answer.getAnsweringTo();

            UserDto userDto = null;

            UserDto answeringToDto = null;

            if (answerUser != null) {
                userDto = new UserDto(answerUser.getId(), answerUser.getEmail(),
                        answerUser.getUsername(), answerUser.getProfilePictureUrl());
            }

            if (answeringToUser != null) {
                answeringToDto = new UserDto(answeringToUser.getId(), answeringToUser.getEmail(),
                        answeringToUser.getUsername(), answeringToUser.getProfilePictureUrl());
            }

            return new AnswerDto(answer.getId(), answer.getAnswer(), answer.getCreatedAt(), userDto,
                    answeringToDto);
        }).toList();
    }
}
