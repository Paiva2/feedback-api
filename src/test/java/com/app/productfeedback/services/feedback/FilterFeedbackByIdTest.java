package com.app.productfeedback.services.feedback;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.repositories.CategoryRepositoryTest;
import com.app.productfeedback.repositories.FeedbackRepositoryTest;

@ActiveProfiles("test")
public class FilterFeedbackByIdTest {
    private CategoryRepositoryTest categoryRepositoryTest;

    private FeedbackRepositoryTest feedbackRepositoryTest;

    private FeedbackService sut;

    @BeforeEach
    public void setup() {
        categoryRepositoryTest = new CategoryRepositoryTest();
        feedbackRepositoryTest = new FeedbackRepositoryTest();

        sut = new FeedbackService(feedbackRepositoryTest, null, categoryRepositoryTest);
    }

    @Test
    @DisplayName("it should filter an feedback by id")
    public void caseOne() {
        Feedback feedback = this.generateFeedback();

        Feedback filterFeedback = this.sut.getById(feedback.getId());

        Assertions.assertNotNull(filterFeedback);
        Assertions.assertEquals(filterFeedback.getId(), feedback.getId());
        Assertions.assertEquals(filterFeedback.getTitle(), feedback.getTitle());
        Assertions.assertEquals(filterFeedback.getDetails(), feedback.getDetails());
    }

    @Test
    @DisplayName("it should not filter an feedback by id without feedback id")
    public void caseTwo() {
        Exception missingArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.getById(null);
        });

        Assertions.assertEquals(missingArgEx.getMessage(), "Invalid feedback id.");
    }

    @Test
    @DisplayName("it should not filter an feedback by id if feedback doesn't exists")
    public void caseThree() {
        Exception missingArgEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.getById(UUID.randomUUID());
        });

        Assertions.assertEquals(missingArgEx.getMessage(), "Feedback not found.");
    }

    public Feedback generateFeedback() {
        Category newCategory = new Category();
        newCategory.setName("Features");

        Category categoryCreation = this.categoryRepositoryTest.save(newCategory);

        Feedback newFeedback = new Feedback();
        newFeedback.setTitle("Feedback Test Title");
        newFeedback.setDetails("Feedback Test");
        newFeedback.setFkCategoryId(categoryCreation.getId());

        return this.feedbackRepositoryTest.save(newFeedback);
    }
}
