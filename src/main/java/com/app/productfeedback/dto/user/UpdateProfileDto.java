package com.app.productfeedback.dto.user;

import java.util.UUID;
import com.app.productfeedback.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateProfileDto {
    @Email(message = "email must be an e-mail type.")
    private String email;

    @Size(min = 6, message = "password must have at least 6 characters.")
    private String password;

    @Size(min = 3, message = "username must have at least 3 characters.")
    private String username;

    public UpdateProfileDto() {}

    public User toEntity(UUID id) {
        User user = new User();
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setUsername(this.username);
        user.setId(id);

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
