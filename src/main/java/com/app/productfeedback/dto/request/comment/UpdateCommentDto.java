package com.app.productfeedback.dto.request.comment;

import org.hibernate.validator.constraints.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateCommentDto {
    @NotBlank(message = "id can't be empty")
    @NotNull(message = "id can't be null")
    @UUID
    private String id;

    @NotNull(message = "comment can't be null")
    @NotBlank(message = "comment can't be empty")
    @Size(max = 500, message = "comment can't be more than 500 characters")
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
