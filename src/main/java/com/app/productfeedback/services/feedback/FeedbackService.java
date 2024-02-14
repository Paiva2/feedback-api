package com.app.productfeedback.services.feedback;

import java.beans.PropertyDescriptor;
import java.util.*;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.FeedbackStatus;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ForbiddenException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.interfaces.CategoryRepository;
import com.app.productfeedback.interfaces.FeedbackRepository;
import com.app.productfeedback.interfaces.UserRepository;
import com.app.productfeedback.dto.request.feedback.NewFeedbackDto;
import com.app.productfeedback.dto.request.feedback.UpdateFeedbackDto;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, UserRepository userRepository,
            CategoryRepository categoryRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public Feedback create(NewFeedbackDto dto, UUID userId) {
        if (dto == null) {
            throw new BadRequestException("New feedback dto can't be null.");
        }

        Feedback newFeedback = new Feedback();

        if (userId != null) {
            Optional<User> doesUserExists = this.userRepository.findById(userId);

            if (doesUserExists.isEmpty()) {
                throw new NotFoundException("User not found.");
            }

            newFeedback.setFkUserId(doesUserExists.get().getId());
        }

        Optional<Category> doesCategoryExists =
                this.categoryRepository.findById(UUID.fromString(dto.getCategoryId().trim()));

        if (doesCategoryExists.isEmpty()) {
            throw new NotFoundException("Category not found.");
        }

        newFeedback.setFkCategory(doesCategoryExists.get().getId());
        newFeedback.setTitle(dto.getTitle());
        newFeedback.setDetails(dto.getDetails());

        return this.feedbackRepository.save(newFeedback);
    }

    public Page<Feedback> listAll(int page, int perPage, FeedbackStatus filterBy) {
        if (page < 1) {
            page = 1;
        }

        if (perPage < 5) {
            perPage = 5;
        }

        Pageable pageable = PageRequest.of((page - 1), perPage);

        Page<Feedback> list = null;

        if (filterBy != null) {
            list = this.feedbackRepository.findAllByStatusOrderByCreatedAtDesc(filterBy, pageable);
        } else {
            list = this.feedbackRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return list;
    }

    public Feedback getById(UUID feedbackId) {
        if (feedbackId == null) {
            throw new BadRequestException("Invalid feedback id.");
        }

        Optional<Feedback> getFeedback = this.feedbackRepository.findById(feedbackId);

        if (getFeedback.isEmpty()) {
            throw new NotFoundException("Feedback not found.");
        }

        return getFeedback.get();
    }

    public Feedback update(UUID userId, UpdateFeedbackDto dto) {
        if (dto == null) {
            throw new BadRequestException("Invalid feedback.");
        }

        if (dto.getId() == null) {
            throw new BadRequestException("Invalid feedback id.");
        }

        if (userId == null) {
            throw new BadRequestException("Invalid user id.");
        }

        Optional<User> doesUserExists = this.userRepository.findById(userId);

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        Optional<Feedback> doesFeedbackExists =
                this.feedbackRepository.findById(UUID.fromString(dto.getId()));

        if (doesFeedbackExists.isEmpty()) {
            throw new NotFoundException("Feedback not found.");
        }

        Feedback getFeedback = doesFeedbackExists.get();

        if (!getFeedback.getFkUserId().equals(userId)) {
            throw new ForbiddenException("Only feedback owners can manage their own feedbacks.");
        }

        BeanWrapper sourceFeedback = new BeanWrapperImpl(getFeedback);
        BeanWrapper updatedFeedback = new BeanWrapperImpl(dto);

        List<PropertyDescriptor> fields = List.of(updatedFeedback.getPropertyDescriptors());

        fields.forEach(field -> {
            String fieldName = field.getName();
            Object fieldValue = updatedFeedback.getPropertyValue(fieldName);

            boolean canUpdate = fieldName.hashCode() != "class".hashCode()
                    && fieldName.hashCode() != "id".hashCode()
                    && fieldName.hashCode() != "categoryId".hashCode();

            if (fieldValue != null && canUpdate) {
                sourceFeedback.setPropertyValue(fieldName, fieldValue);
            }
        });

        if (dto.getCategoryId() != null) {
            Optional<Category> doesCategoryExists =
                    this.categoryRepository.findById(UUID.fromString(dto.getCategoryId()));

            if (doesCategoryExists.isEmpty()) {
                throw new NotFoundException("Category not found.");
            }

            getFeedback.setCategory(doesCategoryExists.get());
        }

        return this.feedbackRepository.save(getFeedback);
    }

    public void delete(UUID userId, UUID feedbackId) {
        if (userId == null) {
            throw new BadRequestException("Invalid user id.");
        }

        if (feedbackId == null) {
            throw new BadRequestException("Invalid feedback id.");
        }

        Optional<User> doesUserExists = this.userRepository.findById(userId);

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        Optional<Feedback> doesFeedbackExists = this.feedbackRepository.findById(feedbackId);

        if (doesFeedbackExists.isEmpty()) {
            throw new NotFoundException("Feedback not found.");
        }

        Feedback getFeedback = doesFeedbackExists.get();

        User getUser = doesUserExists.get();

        if (getFeedback.getUser().getId() != getUser.getId()) {
            throw new ForbiddenException("Invalid permissions.");
        }

        this.feedbackRepository.deleteById(getFeedback.getId());
    }

    // TODO: ONLY ONE UPVOTE FOR USER
    public Feedback insertUpVotes(UUID feedbackId) {
        if (feedbackId == null) {
            throw new BadRequestException("Invalid feedback id.");
        }

        Optional<Feedback> doesFeedbackExists = this.feedbackRepository.findById(feedbackId);

        if (doesFeedbackExists.isEmpty()) {
            throw new NotFoundException("Feedback not found.");
        }

        doesFeedbackExists.get().insertUpVote();

        return this.feedbackRepository.save(doesFeedbackExists.get());
    }
}
