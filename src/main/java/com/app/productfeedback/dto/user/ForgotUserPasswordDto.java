package com.app.productfeedback.dto.user;

import com.app.productfeedback.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ForgotUserPasswordDto {
    @NotBlank(message = "email can't be empty.")
    @NotNull(message = "email can't be null.")
    @Email(message = "email must be an valid e-mail type.")
    private String email;

    @NotBlank(message = "password can't be empty.")
    @NotNull(message = "password can't be null.")
    @Size(min = 6, message = "password must have at least 6 characters.")
    private String password;

    @NotBlank(message = "secretQuestion can't be empty.")
    @NotNull(message = "secretQuestion can't be null.")
    private String secretQuestion;

    @NotBlank(message = "secretAnswer can't be empty.")
    @NotNull(message = "secretAnswer can't be null.")
    private String secretAnswer;

    public ForgotUserPasswordDto() {}

    public User toEntity() {
        User user = new User();
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setSecretQuestion(this.secretQuestion);
        user.setSecretAnswer(this.secretAnswer);

        return user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecretQuestion() {
        return secretQuestion;
    }

    public void setSecretQuestion(String secretQuestion) {
        this.secretQuestion = secretQuestion;
    }

    public String getSecretAnswer() {
        return secretAnswer;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }
}
