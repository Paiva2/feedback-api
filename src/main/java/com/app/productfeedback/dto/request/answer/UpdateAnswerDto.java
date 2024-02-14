package com.app.productfeedback.dto.request.answer;

import org.hibernate.validator.constraints.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateAnswerDto {
    @NotNull(message = "id can't be null.")
    @NotBlank(message = "id can't be empty.")
    @UUID
    private String id;

    @NotNull(message = "answer can't be null.")
    @NotBlank(message = "answer can't be empty.")
    @Size(max = 500, message = "answer can't be more than 500 characters")
    private String answer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
