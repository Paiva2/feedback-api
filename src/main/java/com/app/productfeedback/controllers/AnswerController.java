package com.app.productfeedback.controllers;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.app.productfeedback.dto.request.answer.AnswerCreationDto;
import com.app.productfeedback.dto.request.answer.UpdateAnswerDto;
import com.app.productfeedback.dto.response.answer.AnswerDto;
import com.app.productfeedback.dto.response.user.UserDto;
import com.app.productfeedback.entities.Answer;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.services.answer.AnswerService;
import com.app.productfeedback.services.jwt.JwtService;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/answer")
public class AnswerController {
    @Autowired
    private AnswerService answerService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/new")
    public ResponseEntity<Map<String, String>> insertANewAnswer(@RequestBody AnswerCreationDto dto,
            @RequestHeader(name = "Authorization", required = false) String jwtToken) {
        UUID parseToken = null;

        if (jwtToken != null) {
            String getToken = this.jwtService.verify(jwtToken.replaceAll("Bearer ", ""));

            parseToken = UUID.fromString(getToken);
        }

        this.answerService.create(dto, parseToken);

        return ResponseEntity.status(201)
                .body(Collections.singletonMap("message", "Answer created successfully!"));
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<Map<String, String>> deleteAnswer(
            @RequestHeader(name = "Authorization", required = true) String jwtToken,
            @PathVariable(name = "answerId", required = true) UUID answerId) {
        String parseToken = this.jwtService.verify(jwtToken.replaceAll("Bearer ", ""));

        this.answerService.delete(UUID.fromString(parseToken), answerId);

        return ResponseEntity.ok().body(Collections.singletonMap("message", "Answer deleted."));
    }

    @PatchMapping("/update")
    public ResponseEntity<AnswerDto> updateAnswer(@RequestBody @Valid UpdateAnswerDto dto,
            @RequestHeader(name = "Authorization", required = true) String jwtToken) {
        String parseToken = this.jwtService.verify(jwtToken.replaceAll("Bearer ", ""));

        Answer answerUpdated = this.answerService.update(UUID.fromString(parseToken), dto);

        return ResponseEntity.status(201).body(this.formatAnswerResponse(answerUpdated));
    }

    protected AnswerDto formatAnswerResponse(Answer answer) {
        User user = answer.getUser();
        User answeringTo = answer.getAnsweringTo();

        UserDto userDto = null;
        UserDto answeringToUserDto = null;

        if (user != null) {
            userDto = new UserDto(user.getId(), user.getEmail(), user.getUsername(),
                    user.getProfilePictureUrl());
        }

        if (answeringTo != null) {
            answeringToUserDto = new UserDto(answeringTo.getId(), answeringTo.getEmail(),
                    answeringTo.getUsername(), answeringTo.getProfilePictureUrl());
        }

        return new AnswerDto(answer.getId(), answer.getAnswer(), answer.getCreatedAt(), userDto,
                answeringToUserDto);
    }
}
