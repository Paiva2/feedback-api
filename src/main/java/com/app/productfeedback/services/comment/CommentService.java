package com.app.productfeedback.services.comment;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.app.productfeedback.entities.Comment;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.dto.request.comment.NewCommentDto;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.interfaces.CommentRepository;
import com.app.productfeedback.interfaces.FeedbackRepository;
import com.app.productfeedback.interfaces.UserRepository;

@Service
public class CommentService {
    private final UserRepository UserRepository;

    private final FeedbackRepository feedbackRepository;

    private final CommentRepository commentRepository;

    public CommentService(UserRepository UserRepository, FeedbackRepository feedbackRepository,
            CommentRepository commentRepository) {
        this.UserRepository = UserRepository;
        this.feedbackRepository = feedbackRepository;
        this.commentRepository = commentRepository;
    }

    public Comment create(UUID userId, NewCommentDto dto) {
        if (dto == null) {
            throw new BadRequestException("New comment dto can't be null.");
        }

        if (dto.getFeedbackId() == null) {
            throw new BadRequestException("Feedback id can't be null.");
        }

        if (dto.getComment() == null) {
            throw new BadRequestException("Comment can't be null.");
        }

        Comment newComment = new Comment();

        Optional<Feedback> doesFeedbackExists =
                this.feedbackRepository.findById(UUID.fromString(dto.getFeedbackId()));

        if (doesFeedbackExists.isEmpty()) {
            throw new NotFoundException("Feedback not found.");
        }

        if (userId != null) {
            Optional<User> doesUserExists = this.UserRepository.findById(userId);

            if (doesUserExists.isEmpty()) {
                throw new NotFoundException("User not found.");
            }

            newComment.setUserId(userId);
        }

        newComment.setFeedbackId(doesFeedbackExists.get().getId());
        newComment.setComment(dto.getComment());

        return this.commentRepository.save(newComment);
    }
}
