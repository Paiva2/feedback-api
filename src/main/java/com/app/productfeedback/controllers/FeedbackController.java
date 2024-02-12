package com.app.productfeedback.controllers;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.productfeedback.dto.request.feedback.NewFeedbackDto;
import com.app.productfeedback.dto.response.category.CategoryDto;
import com.app.productfeedback.dto.response.feedback.FilterFeedbackDto;
import com.app.productfeedback.dto.response.feedback.ListAllFeedbacksDto;
import com.app.productfeedback.dto.response.user.UserDto;
import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.entities.User;
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
                        parseToken = UUID.fromString(this.jwtService
                                        .verify(authToken.replaceAll("Bearer ", "")));
                }

                this.feedbackService.create(dto, parseToken);

                return ResponseEntity.status(201).body(Collections.singletonMap("message",
                                "Feedback successfully created."));
        }

        @GetMapping
        public ResponseEntity<ListAllFeedbacksDto> listAllFeedbacks(
                        @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                        @RequestParam(name = "perPage", required = false,
                                        defaultValue = "5") int perPage,
                        @RequestParam(name = "status", required = false) FeedbackStatus status) {
                Page<Feedback> feedbackPaginable =
                                this.feedbackService.listAll(page, perPage, status);

                int currentPage = feedbackPaginable.getPageable().getPageNumber() + 1;
                int itensPerPage = feedbackPaginable.getPageable().getPageSize();
                long totalElements = feedbackPaginable.getTotalElements();

                return ResponseEntity.ok().body(new ListAllFeedbacksDto(currentPage, itensPerPage,
                                totalElements, feedbackPaginable.getContent()));
        }

        @GetMapping("/{feedbackId}")
        public ResponseEntity<FilterFeedbackDto> filterFeedback(
                        @PathVariable(name = "feedbackId", required = false) UUID feedbackId) {
                Feedback getFeedback = this.feedbackService.getById(feedbackId);

                return ResponseEntity.ok().body(this.filterFeedbackDtoMapper(getFeedback));
        }

        protected FilterFeedbackDto filterFeedbackDtoMapper(Feedback feedback) {
                User userFeedback = feedback.getUserId();
                Category feedbackCategory = feedback.getCategoryId();

                UserDto userDto = new UserDto(feedback.getUserId().getId(), userFeedback.getEmail(),
                                userFeedback.getUsername(), userFeedback.getProfilePictureUrl());

                CategoryDto categoryDto = new CategoryDto(feedbackCategory.getId(),
                                feedbackCategory.getName());

                return new FilterFeedbackDto(feedback.getId(), feedback.getTitle(),
                                feedback.getDetails(), feedback.getStatus(), categoryDto, userDto,
                                feedback.getUpVotes());
        }
}
