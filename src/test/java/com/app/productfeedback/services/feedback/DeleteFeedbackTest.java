package com.app.productfeedback.services.feedback;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ForbiddenException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.repositories.CategoryRepositoryTest;
import com.app.productfeedback.repositories.FeedbackRepositoryTest;
import com.app.productfeedback.repositories.UserRepositoryTest;

@ActiveProfiles("test")
public class DeleteFeedbackTest {
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
    @DisplayName("it should delete an category")
    public void caseOne() {
        User user = this.userGenerator();

        Category category = this.categoryGenerator();

        Feedback feedback = this.feedbackGenerator(user, category.getId());

        this.sut.delete(user.getId(), feedback.getId());

        Optional<Feedback> findDeletedFeedback =
                this.feedbackRepositoryTest.findById(feedback.getId());

        Assertions.assertEquals(Optional.empty(), findDeletedFeedback);
    }

    @Test
    @DisplayName("it should not delete an category without all args provided")
    public void caseTwo() {
        User user = this.userGenerator();

        Category category = this.categoryGenerator();

        Feedback feedback = this.feedbackGenerator(user, category.getId());

        Exception noFirstArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.delete(null, feedback.getId());
        });

        Exception noSecondArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.delete(user.getId(), null);
        });

        Assertions.assertEquals("Invalid user id.", noFirstArgEx.getMessage());
        Assertions.assertEquals("Invalid feedback id.", noSecondArgEx.getMessage());
    }

    @Test
    @DisplayName("it should not delete an category if user doesn't exists")
    public void caseThree() {
        User user = this.userGenerator();

        Category category = this.categoryGenerator();

        Feedback feedback = this.feedbackGenerator(user, category.getId());

        Exception noFirstArgEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.delete(UUID.randomUUID(), feedback.getId());
        });

        Assertions.assertEquals("User not found.", noFirstArgEx.getMessage());
    }

    @Test
    @DisplayName("it should not delete an category if feedback doesn't exists")
    public void caseFour() {
        User user = this.userGenerator();

        Exception noFirstArgEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.delete(user.getId(), UUID.randomUUID());
        });

        Assertions.assertEquals("Feedback not found.", noFirstArgEx.getMessage());
    }

    @Test
    @DisplayName("it should not delete an category if feedback doesn't belongs to me")
    public void caseFive() {
        User user = this.userGenerator();

        User alternativeUser = this.userGenerator();

        Category category = this.categoryGenerator();

        Feedback feedback = this.feedbackGenerator(user, category.getId());

        Exception noFirstArgEx = Assertions.assertThrows(ForbiddenException.class, () -> {
            this.sut.delete(alternativeUser.getId(), feedback.getId());
        });

        Assertions.assertEquals("Invalid permissions.", noFirstArgEx.getMessage());
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

    protected Feedback feedbackGenerator(User user, UUID categoryId) {
        Feedback newFeedback = new Feedback();
        newFeedback.setTitle("Feedback 1");
        newFeedback.setDetails("Feedback 1 details");
        newFeedback.setUser(user);
        newFeedback.setFkCategory(categoryId);

        return this.feedbackRepositoryTest.save(newFeedback);
    }
}
