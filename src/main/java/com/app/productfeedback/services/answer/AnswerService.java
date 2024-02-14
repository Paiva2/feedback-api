package com.app.productfeedback.services.answer;

import java.util.Optional;
import java.util.UUID;

import com.app.productfeedback.entities.Answer;
import com.app.productfeedback.entities.Comment;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.dto.request.answer.AnswerCreationDto;
import com.app.productfeedback.dto.request.answer.UpdateAnswerDto;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ForbiddenException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.interfaces.AnswerRepository;
import com.app.productfeedback.interfaces.CommentRepository;
import com.app.productfeedback.interfaces.UserRepository;

import org.springframework.beans.BeanUtils;
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

    public void delete(UUID userId, UUID answerId) {
        if (userId == null) {
            throw new BadRequestException("User id can't be null.");
        }

        if (answerId == null) {
            throw new BadRequestException("Answer id can't be null.");
        }

        Optional<User> doesUserExists = this.userRepository.findById(userId);

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        Optional<Answer> doesAnswerExists = this.answerRepository.findById(answerId);

        if (doesAnswerExists.isEmpty()) {
            throw new NotFoundException("Answer not found.");
        }

        User user = doesUserExists.get();
        Answer answer = doesAnswerExists.get();

        if (!answer.getUserId().equals(user.getId())) {
            throw new ForbiddenException("Only answer owner can delete their own answers.");
        }

        this.answerRepository.deleteById(answerId);
    }

    public Answer update(UUID userId, UpdateAnswerDto dto) {
        if (userId == null) {
            throw new BadRequestException("Invalid user id.");
        }

        if (dto.getId() == null) {
            throw new BadRequestException("Invalid answer id.");
        }

        if (dto.getAnswer() == null) {
            throw new BadRequestException("New answer can't be empty.");
        }

        Optional<User> doesUserExists = this.userRepository.findById(userId);

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        Optional<Answer> doesAnswerExists =
                this.answerRepository.findById(UUID.fromString(dto.getId()));

        if (doesAnswerExists.isEmpty()) {
            throw new NotFoundException("Answer not found.");
        }

        Answer answer = doesAnswerExists.get();

        if (!answer.getUserId().equals(userId)) {
            throw new ForbiddenException("Only answer owner can edit their own answer.");
        }

        BeanUtils.copyProperties(dto, answer);

        return this.answerRepository.save(answer);
    }
}
