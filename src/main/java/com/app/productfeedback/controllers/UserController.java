package com.app.productfeedback.controllers;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.productfeedback.dto.RegisterNewUserDto;
import com.app.productfeedback.services.user.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<Map<String, String>> registerNewUser(
            @RequestBody @Valid RegisterNewUserDto registerNewUserDto) {
        this.userService.register(registerNewUserDto.toEntity());

        return ResponseEntity.ok().body(Collections.singletonMap("message", "Register success!"));
    }
}
