package com.app.productfeedback.services.feedback;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

import com.app.productfeedback.dto.request.feedback.NewFeedbackDto;
import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.interfaces.CategoryRepository;
import com.app.productfeedback.interfaces.FeedbackRepository;
import com.app.productfeedback.interfaces.UserRepository;

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

        newFeedback.setFkCategoryId(doesCategoryExists.get().getId());
        newFeedback.setTitle(dto.getTitle());
        newFeedback.setDetails(dto.getDetails());

        return this.feedbackRepository.save(newFeedback);
    }
}
