package com.app.productfeedback.services.answer;

import java.util.Optional;
import java.util.UUID;

import com.app.productfeedback.entities.Answer;
import com.app.productfeedback.entities.Comment;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.dto.request.answer.AnswerCreationDto;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.interfaces.AnswerRepository;
import com.app.productfeedback.interfaces.CommentRepository;
import com.app.productfeedback.interfaces.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class AnswerService {
    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final AnswerRepository answerRepository;

    public AnswerService(UserRepository userRepository, CommentRepository commentRepository,
            AnswerRepository answerRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.answerRepository = answerRepository;
    }

    public Answer create(AnswerCreationDto dto, UUID userId) {
        if (dto == null) {
            throw new BadRequestException("Answer creation dto can't be null.");
        }

        if (dto.getAnswer() == null) {
            throw new BadRequestException("Answer can't be null.");
        }

        Answer newAnswer = new Answer();

        if (userId != null) {
            Optional<User> doesUserExists = this.userRepository.findById(userId);

            if (doesUserExists.isEmpty()) {
                throw new NotFoundException("User not found.");
            }

            newAnswer.setUserId(doesUserExists.get().getId());
        }

        if (dto.getAnsweringToId() != null) {
            Optional<User> doesUserBeingRepliedExists =
                    this.userRepository.findById(UUID.fromString(dto.getAnsweringToId()));

            if (doesUserBeingRepliedExists.isEmpty()) {
                throw new NotFoundException("User being replied not found.");
            }

            newAnswer.setAnsweringToId(doesUserBeingRepliedExists.get().getId());
        }

        Optional<Comment> doesCommentExists =
                this.commentRepository.findById(UUID.fromString(dto.getCommentId()));

        if (doesCommentExists.isEmpty()) {
            throw new NotFoundException("Comment not found.");
        }

        newAnswer.setCommentId(doesCommentExists.get().getId());
        newAnswer.setAnswer(dto.getAnswer());

        return this.answerRepository.save(newAnswer);
    }
}
