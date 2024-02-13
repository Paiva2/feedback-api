package com.app.productfeedback.controllers;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.productfeedback.dto.request.feedback.NewFeedbackDto;
import com.app.productfeedback.dto.request.feedback.UpdateFeedbackDto;
import com.app.productfeedback.dto.response.category.CategoryDto;
import com.app.productfeedback.dto.response.feedback.FeedbackResponseDto;
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

        @PatchMapping("/update")
        public ResponseEntity<FeedbackResponseDto> updateFeedback(
                        @RequestBody @Valid UpdateFeedbackDto updateFeedbackDto,
                        @RequestHeader(name = "Authorization", required = true) String jwtToken) {
                String parseToken = this.jwtService.verify(jwtToken.replaceAll("Bearer ", ""));

                Feedback feedback = this.feedbackService.update(UUID.fromString(parseToken),
                                updateFeedbackDto);

                return ResponseEntity.status(201).body(this.feedbackResponseDto(feedback));
        }

        @GetMapping("/{feedbackId}")
        public ResponseEntity<FeedbackResponseDto> filterFeedback(
                        @PathVariable(name = "feedbackId", required = false) UUID feedbackId) {
                Feedback getFeedback = this.feedbackService.getById(feedbackId);

                return ResponseEntity.ok().body(this.feedbackResponseDto(getFeedback));
        }

        @DeleteMapping("/{feedbackId}")
        public ResponseEntity<Map<String, String>> deleteFeedback(
                        @PathVariable(name = "feedbackId") UUID feedbackId,
                        @RequestHeader(name = "Authorization", required = true) String authToken) {
                String parseToken = this.jwtService.verify(authToken.replaceAll("Bearer ", ""));

                this.feedbackService.delete(UUID.fromString(parseToken), feedbackId);

                return ResponseEntity.ok()
                                .body(Collections.singletonMap("message", "Deleted successfully."));
        }

        protected FeedbackResponseDto feedbackResponseDto(Feedback feedback) {
                User userFeedback = feedback.getUser();
                Category feedbackCategory = feedback.getCategory();

                UserDto userDto = new UserDto(feedback.getUser().getId(), userFeedback.getEmail(),
                                userFeedback.getUsername(), userFeedback.getProfilePictureUrl());

                CategoryDto categoryDto = new CategoryDto(feedbackCategory.getId(),
                                feedbackCategory.getName());

                return new FeedbackResponseDto(feedback.getId(), feedback.getTitle(),
                                feedback.getDetails(), feedback.getStatus(), categoryDto, userDto,
                                feedback.getUpVotes());
        }
}
