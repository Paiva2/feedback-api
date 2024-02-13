package com.app.productfeedback.dto.response.answer;

import java.util.Date;
import java.util.UUID;

import com.app.productfeedback.dto.response.user.UserDto;

public class AnswerDto {
    private UUID id;

    private String answer;

    private Date createdAt;

    public UserDto user;

    public UserDto answeringTo;

    public AnswerDto(UUID id, String answer, Date createdAt, UserDto user, UserDto answeringTo) {
        this.id = id;
        this.answer = answer;
        this.createdAt = createdAt;
        this.user = user;
        this.answeringTo = answeringTo;
    }

    public UUID getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public UserDto getUser() {
        return user;
    }

    public UserDto getAnsweringTo() {
        return answeringTo;
    }
}
