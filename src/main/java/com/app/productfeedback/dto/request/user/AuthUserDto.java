package com.app.productfeedback.dto.request.user;

import com.app.productfeedback.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthUserDto {
    @NotBlank(message = "email can't be empty.")
    @NotNull(message = "email can't be null.")
    @Email(message = "email must be an e-mail type.")
    private String email;

    @NotBlank(message = "password can't be empty.")
    @NotNull(message = "password can't be null.")
    @Size(min = 1, message = "password must have at least 6 characters")
    private String password;

    public AuthUserDto() {}

    public User toEntity() {
        User user = new User();
        user.setEmail(this.email);
        user.setPassword(this.password);

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
}
