package com.app.productfeedback.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateProfileDto {
    @Email(message = "email must be an e-mail type.")
    private String email;

    @Size(min = 6, message = "password must have at least 6 characters.")
    private String password;

    @Size(min = 3, message = "username must have at least 3 characters.")
    private String username;

    @Size(min = 3, message = "username must have at least 3 characters.")
    private String profilePictureUrl;

    public UpdateProfileDto() {}

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

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
