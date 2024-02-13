package com.app.productfeedback.services.feedback;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.dto.request.feedback.UpdateFeedbackDto;
import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.FeedbackStatus;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ForbiddenException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.repositories.CategoryRepositoryTest;
import com.app.productfeedback.repositories.FeedbackRepositoryTest;
import com.app.productfeedback.repositories.UserRepositoryTest;

@ActiveProfiles("test")
public class UpdateFeedbackTest {
    private UserRepositoryTest userRepositoryTest;

    private CategoryRepositoryTest categoryRepositoryTest;

    private FeedbackRepositoryTest feedbackRepositoryTest;

    private FeedbackService sut;

    @BeforeEach
    public void setup() {
        userRepositoryTest = new UserRepositoryTest();
        categoryRepositoryTest = new CategoryRepositoryTest();
        feedbackRepositoryTest = new FeedbackRepositoryTest();

        sut = new FeedbackService(feedbackRepositoryTest, userRepositoryTest,
                categoryRepositoryTest);
    }

    @Test
    @DisplayName("it should update an feedback informations")
    public void caseOne() {
        User user = this.userGenerator();

        Category category = this.categoryGenerator();

        Category alternativeCategory = this.categoryGenerator();

        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        UpdateFeedbackDto updateFeedbackDto = new UpdateFeedbackDto();

        updateFeedbackDto.setCategoryId(alternativeCategory.getId().toString());
        updateFeedbackDto.setTitle("Update feedback title");
        updateFeedbackDto.setDetails("Update feedback details");
        updateFeedbackDto.setStatus(FeedbackStatus.PLANNED);
        updateFeedbackDto.setId(feedback.getId().toString());

        Feedback feedbackUpdated = this.sut.update(user.getId(), updateFeedbackDto);

        Assertions.assertAll("Assert all feedback updates",
                () -> Assertions.assertEquals("Update feedback title", feedbackUpdated.getTitle()),
                () -> Assertions.assertEquals(FeedbackStatus.PLANNED, feedbackUpdated.getStatus()),
                () -> Assertions.assertEquals("Update feedback details",
                        feedbackUpdated.getDetails()),
                () -> Assertions.assertEquals(alternativeCategory.getId(),
                        feedbackUpdated.getCategory().getId()));
    }

    @Test
    @DisplayName("it should not update an feedback informations without correctly provided args")
    public void caseTwo() {
        User user = this.userGenerator();

        Category category = this.categoryGenerator();

        UpdateFeedbackDto updateFeedbackDto = new UpdateFeedbackDto();

        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        Exception noFeedbackDtoArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.update(user.getId(), null);
        });

        Exception noFeedbackIdArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            updateFeedbackDto.setId(null);

            this.sut.update(user.getId(), updateFeedbackDto);
        });

        Exception noUserIdArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            updateFeedbackDto.setId(feedback.getId().toString());

            this.sut.update(null, updateFeedbackDto);
        });

        Assertions.assertAll("Assert arg errors",
                () -> Assertions.assertEquals("Invalid feedback.", noFeedbackDtoArgEx.getMessage()),
                () -> Assertions.assertEquals("Invalid feedback id.",
                        noFeedbackIdArgEx.getMessage()),
                () -> Assertions.assertEquals("Invalid user id.", noUserIdArgEx.getMessage()));
    }

    @Test
    @DisplayName("it should not update an feedback informations if user doesn't exists")
    public void caseThree() {
        User user = this.userGenerator();

        Category category = this.categoryGenerator();

        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        UpdateFeedbackDto updateFeedbackDto = new UpdateFeedbackDto();

        updateFeedbackDto.setId(feedback.getId().toString());

        Exception notFoundUserEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.update(UUID.randomUUID(), updateFeedbackDto);
        });

        Assertions.assertEquals("User not found.", notFoundUserEx.getMessage());
    }

    @Test
    @DisplayName("it should not update an feedback informations if feedback doesn't exists")
    public void caseFour() {
        User user = this.userGenerator();

        UpdateFeedbackDto updateFeedbackDto = new UpdateFeedbackDto();

        updateFeedbackDto.setId(UUID.randomUUID().toString());

        Exception notFoundFeedbackEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.update(user.getId(), updateFeedbackDto);
        });

        Assertions.assertEquals("Feedback not found.", notFoundFeedbackEx.getMessage());
    }

    @Test
    @DisplayName("it should not update an feedback informations if category doesn't exists")
    public void caseFive() {
        User user = this.userGenerator();

        Category category = this.categoryGenerator();

        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        UpdateFeedbackDto updateFeedbackDto = new UpdateFeedbackDto();

        updateFeedbackDto.setId(feedback.getId().toString());
        updateFeedbackDto.setCategoryId(UUID.randomUUID().toString());

        Exception notFoundCategoryEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.update(user.getId(), updateFeedbackDto);
        });

        Assertions.assertEquals("Category not found.", notFoundCategoryEx.getMessage());
    }

    @Test
    @DisplayName("it should not update an feedback informations if user isn't authorized")
    public void caseSix() {
        User user = this.userGenerator();
        User alternativeUser = this.userGenerator();

        Category category = this.categoryGenerator();

        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        UpdateFeedbackDto updateFeedbackDto = new UpdateFeedbackDto();

        updateFeedbackDto.setId(feedback.getId().toString());

        Exception notFoundCategoryEx = Assertions.assertThrows(ForbiddenException.class, () -> {
            this.sut.update(alternativeUser.getId(), updateFeedbackDto);
        });

        Assertions.assertEquals("Only feedback owners can manage their own feedbacks.",
                notFoundCategoryEx.getMessage());
    }

    protected User userGenerator() {
        User newUser = new User();
        newUser.setEmail("johndoe@test.com");
        newUser.setUsername("John Doe");
        newUser.setRole(UserRole.ADMIN);

        return this.userRepositoryTest.save(newUser);
    }

    protected Category categoryGenerator() {
        Category newCategory = new Category();
        newCategory.setName("Features");

        return this.categoryRepositoryTest.save(newCategory);
    }

    protected Feedback feedbackGenerator(UUID userId, UUID categoryId) {
        Feedback newFeedback = new Feedback();
        newFeedback.setTitle("Feedback 1");
        newFeedback.setDetails("Feedback 1 details");
        newFeedback.setFkUserId(userId);
        newFeedback.setFkCategory(categoryId);

        return this.feedbackRepositoryTest.save(newFeedback);
    }
}
