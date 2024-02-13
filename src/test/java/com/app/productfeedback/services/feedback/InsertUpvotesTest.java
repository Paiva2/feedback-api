package com.app.productfeedback.services.feedback;

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
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.repositories.CategoryRepositoryTest;
import com.app.productfeedback.repositories.FeedbackRepositoryTest;
import com.app.productfeedback.repositories.UserRepositoryTest;

@ActiveProfiles("test")
public class InsertUpvotesTest {
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
    @DisplayName("it should insert an upvote on feedback")
    public void caseOne() {
        User user = this.userGenerator();

        Category category = this.categoryGenerator();

        Feedback feedback = this.feedbackGenerator(user, category.getId());

        Feedback feedbackUpvote = this.sut.insertUpVotes(feedback.getId());

        Assertions.assertNotNull(feedbackUpvote);
        Assertions.assertEquals(feedbackUpvote.getId(), feedback.getId());
        Assertions.assertEquals(feedbackUpvote.getUpVotes(), 1);
    }

    @Test
    @DisplayName("it should not insert an upvote on feedback if feedback arg aren't provided")
    public void caseTwo() {
        Exception requiredArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.insertUpVotes(null);
        });

        Assertions.assertEquals(requiredArgEx.getMessage(), "Invalid feedback id.");
    }

    @Test
    @DisplayName("it should not insert an upvote on feedback if feedback doesn't exists")
    public void caseThree() {
        Exception requiredArgEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.insertUpVotes(UUID.randomUUID());
        });

        Assertions.assertEquals(requiredArgEx.getMessage(), "Feedback not found.");
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
