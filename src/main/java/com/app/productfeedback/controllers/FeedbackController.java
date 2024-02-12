package com.app.productfeedback.controllers;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.productfeedback.dto.request.feedback.NewFeedbackDto;
import com.app.productfeedback.dto.response.feedback.ListAllFeedbacksDto;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.enums.FeedbackStatus;
import com.app.productfeedback.services.feedback.FeedbackService;
import com.app.productfeedback.services.jwt.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/new")
    public ResponseEntity<Map<String, String>> createNewFeedback(
            @RequestHeader(name = "Authorization", required = false) String authToken,
            @RequestBody @Valid NewFeedbackDto dto) {
        UUID parseToken = null;

        if (authToken != null) {
            parseToken =
                    UUID.fromString(this.jwtService.verify(authToken.replaceAll("Bearer ", "")));
        }

        this.feedbackService.create(dto, parseToken);

        return ResponseEntity.status(201)
                .body(Collections.singletonMap("message", "Feedback successfully created."));
    }

    @GetMapping
    public ResponseEntity<ListAllFeedbacksDto> listAllFeedbacks(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "5") int perPage,
            @RequestParam(name = "status", required = false) FeedbackStatus status) {
        Page<Feedback> feedbackPaginable = this.feedbackService.listAll(page, perPage, status);

        int currentPage = feedbackPaginable.getPageable().getPageNumber() + 1;
        int itensPerPage = feedbackPaginable.getPageable().getPageSize();
        long totalElements = feedbackPaginable.getTotalElements();

        return ResponseEntity.ok().body(new ListAllFeedbacksDto(currentPage, itensPerPage,
                totalElements, feedbackPaginable.getContent()));
    }
}
