package com.app.productfeedback.controllers;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.productfeedback.dto.request.user.AuthUserDto;
import com.app.productfeedback.dto.request.user.ForgotUserPasswordDto;
import com.app.productfeedback.dto.request.user.RegisterNewUserDto;
import com.app.productfeedback.dto.request.user.UpdateProfileDto;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.services.jwt.JwtService;
import com.app.productfeedback.services.user.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerNewUser(
            @RequestBody @Valid RegisterNewUserDto registerNewUserDto) {
        this.userService.register(registerNewUserDto.toEntity());

        return ResponseEntity.status(201)
                .body(Collections.singletonMap("message", "Register success!"));
    }

    @PatchMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotUserPassword(
            @RequestBody @Valid ForgotUserPasswordDto forgotUserPasswordDto) {
        this.userService.forgotPassword(forgotUserPasswordDto.toEntity());

        return ResponseEntity.status(201)
                .body(Collections.singletonMap("message", "Password updated successfully."));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> userAuth(
            @RequestBody @Valid AuthUserDto authUserDto) {

        User authenticate = this.userService.auth(authUserDto.toEntity());

        String tokenGenerate = this.jwtService.sign(authenticate.getId().toString());

        return ResponseEntity.ok().body(Collections.singletonMap("token", tokenGenerate));
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(
            @RequestHeader(name = "Authorization", required = true) String authorizationHeader) {
        String parseToken = this.jwtService.verify(authorizationHeader.replaceAll("Bearer", ""));

        Map<String, Object> profile = this.userService.profile(UUID.fromString(parseToken));

        return ResponseEntity.ok().body(profile);
    }

    @PatchMapping("/update")
    public ResponseEntity<Map<String, String>> updateUserProfile(
            @RequestHeader(name = "Authorization", required = true) String authorizationHeader,
            @RequestBody @Valid UpdateProfileDto updateProfileDto) {
        String parseToken = this.jwtService.verify(authorizationHeader.replaceAll("Bearer", ""));

        this.userService.updateProfile(updateProfileDto, UUID.fromString(parseToken));

        return ResponseEntity.status(201)
                .body(Collections.singletonMap("message", "Profile updated!"));
    }
}
