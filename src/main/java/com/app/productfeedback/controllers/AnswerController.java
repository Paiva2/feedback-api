package com.app.productfeedback.controllers;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import com.app.productfeedback.dto.request.answer.AnswerCreationDto;
import com.app.productfeedback.services.answer.AnswerService;
import com.app.productfeedback.services.jwt.JwtService;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

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

}
