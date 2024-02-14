package com.app.productfeedback.controllers;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.productfeedback.dto.request.comment.NewCommentDto;
import com.app.productfeedback.services.comment.CommentService;
import com.app.productfeedback.services.jwt.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/new")
    public ResponseEntity<?> newComment(
            @RequestHeader(name = "Authorization", required = false) String jwtToken,
            @RequestBody @Valid NewCommentDto newCommentDto) {
        UUID parsedToken = null;

        if (jwtToken != null) {
            String getJwt = this.jwtService.verify(jwtToken.replaceAll("Bearer ", ""));

            parsedToken = UUID.fromString(getJwt);
        }

        this.commentService.create(parsedToken, newCommentDto);

        return ResponseEntity.status(201)
                .body(Collections.singletonMap("message", "Comment created!"));
    }

    @DeleteMapping("remove/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @RequestHeader(name = "Authorization", required = true) String jwtToken,
            @PathVariable(name = "commentId", required = true) UUID commentId) {
        String parseToken = this.jwtService.verify(jwtToken.replaceAll("Bearer ", ""));

        this.commentService.delete(UUID.fromString(parseToken), commentId);

        return ResponseEntity.ok().body(Collections.singletonMap("message", "Comment removed."));
    }
}
