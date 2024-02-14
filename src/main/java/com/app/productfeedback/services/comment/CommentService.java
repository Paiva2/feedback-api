package com.app.productfeedback.services.comment;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.app.productfeedback.entities.Comment;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.dto.request.comment.NewCommentDto;
import com.app.productfeedback.dto.request.comment.UpdateCommentDto;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ForbiddenException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.exceptions.UnauthorizedException;
import com.app.productfeedback.interfaces.CommentRepository;
import com.app.productfeedback.interfaces.FeedbackRepository;
import com.app.productfeedback.interfaces.UserRepository;

@Service
public class CommentService {
    private final UserRepository userRepository;

    private final FeedbackRepository feedbackRepository;

    private final CommentRepository commentRepository;

    public CommentService(UserRepository userRepository, FeedbackRepository feedbackRepository,
            CommentRepository commentRepository) {
        this.userRepository = userRepository;
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
            Optional<User> doesUserExists = this.userRepository.findById(userId);

            if (doesUserExists.isEmpty()) {
                throw new NotFoundException("User not found.");
            }

            newComment.setUserId(userId);
        }

        newComment.setFeedbackId(doesFeedbackExists.get().getId());
        newComment.setComment(dto.getComment());

        return this.commentRepository.save(newComment);
    }

    public void delete(UUID userId, UUID commentId) {
        if (commentId == null) {
            throw new BadRequestException("Invalid comment id.");
        }

        if (userId == null) {
            throw new BadRequestException("Only admins can manage comments made by guests.");
        }

        Optional<User> doesUserExists = this.userRepository.findById(userId);

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        Optional<Comment> doesCommentExists = this.commentRepository.findById(commentId);

        if (doesCommentExists.isEmpty()) {
            throw new NotFoundException("Comment not found.");
        }

        if (!doesCommentExists.get().getUserId().equals(userId)) {
            throw new UnauthorizedException(
                    "Only comment owners or admins can manage their own comments.");
        }

        this.commentRepository.deleteById(doesCommentExists.get().getId());
    }

    public Comment update(UUID userId, UpdateCommentDto updateCommentDto) {
        if (userId == null) {
            throw new BadRequestException("Invalid user id.");
        }

        if (updateCommentDto == null) {
            throw new BadRequestException("Invalid update comment dto.");
        }

        if (updateCommentDto.getId() == null) {
            throw new BadRequestException("Invalid comment id.");
        }

        if (updateCommentDto.getComment() == null) {
            throw new BadRequestException("New comment can't be empty.");
        }

        Optional<User> doesUserExists = this.userRepository.findById(userId);

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        Optional<Comment> doesCommentExists =
                this.commentRepository.findById(UUID.fromString(updateCommentDto.getId()));

        if (doesCommentExists.isEmpty()) {
            throw new NotFoundException("Comment not found.");
        }

        if (!doesCommentExists.get().getUserId().equals(userId)) {
            throw new ForbiddenException("Only comment owners can edit their own comments.");
        }

        BeanUtils.copyProperties(updateCommentDto, doesCommentExists.get());

        return this.commentRepository.save(doesCommentExists.get());
    }
}
