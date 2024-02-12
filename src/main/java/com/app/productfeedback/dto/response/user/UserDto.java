package com.app.productfeedback.dto.response.user;

import java.util.UUID;

public class UserDto {
    private UUID id;

    private String email;

    private String username;

    private String profilePictureUrl;

    public UserDto(UUID id, String email, String username, String profilePictureUrl) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
