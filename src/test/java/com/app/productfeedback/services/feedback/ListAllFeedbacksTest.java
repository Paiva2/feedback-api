package com.app.productfeedback.services.feedback;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.enums.FeedbackStatus;
import com.app.productfeedback.interfaces.CategoryRepository;
import com.app.productfeedback.interfaces.FeedbackRepository;
import com.app.productfeedback.repositories.CategoryRepositoryTest;
import com.app.productfeedback.repositories.FeedbackRepositoryTest;

@ActiveProfiles("test")
public class ListAllFeedbacksTest {
    private FeedbackService sut;

    private CategoryRepository categoryRepositoryTest;

    private FeedbackRepository feedbackRepositoryTest;

    @BeforeEach
    public void setup() {
        this.categoryRepositoryTest = new CategoryRepositoryTest();
        this.feedbackRepositoryTest = new FeedbackRepositoryTest();

        this.sut = new FeedbackService(feedbackRepositoryTest, null, categoryRepositoryTest);
    }

    @Test
    @DisplayName("it should list all feedbacks if no filter arg is provided")
    public void caseOne() {
        this.feedbacksGenerator();

        Page<Feedback> feedbacksPagination = this.sut.listAll(1, 20, null);

        List<Feedback> feedbackList = feedbacksPagination.getContent();

        Assertions.assertAll("Assert returned feedbacks",
                () -> Assertions.assertEquals(FeedbackStatus.SUGGESTION,
                        feedbackList.get(0).getStatus()),

                () -> Assertions.assertEquals(FeedbackStatus.SUGGESTION,
                        feedbackList.get(1).getStatus()),

                () -> Assertions.assertEquals(FeedbackStatus.IN_PROGRESS,
                        feedbackList.get(11).getStatus()),

                () -> Assertions.assertEquals(FeedbackStatus.IN_PROGRESS,
                        feedbackList.get(12).getStatus()));
    }

    @Test
    @DisplayName("it should list all feedbacks in progress")
    public void caseTwo() {
        this.feedbacksGenerator();

        Page<Feedback> feedbacksPagination = this.sut.listAll(1, 20, FeedbackStatus.IN_PROGRESS);

        List<Feedback> feedbackList = feedbacksPagination.getContent();

        Assertions.assertAll("Assert returned feedbacks",
                () -> Assertions.assertEquals(FeedbackStatus.IN_PROGRESS,
                        feedbackList.get(0).getStatus()),

                () -> Assertions.assertEquals(FeedbackStatus.IN_PROGRESS,
                        feedbackList.get(1).getStatus()));
    }

    @Test
    @DisplayName("it should list all feedbacks in suggestion status")
    public void caseThree() {
        this.feedbacksGenerator();

        Page<Feedback> feedbacksPagination = this.sut.listAll(1, 20, FeedbackStatus.SUGGESTION);

        List<Feedback> feedbackList = feedbacksPagination.getContent();

        Assertions.assertAll("Assert returned feedbacks",
                () -> Assertions.assertEquals(FeedbackStatus.SUGGESTION,
                        feedbackList.get(0).getStatus()),

                () -> Assertions.assertEquals(FeedbackStatus.SUGGESTION,
                        feedbackList.get(1).getStatus()));
    }

    @Test
    @DisplayName("it should return the first page if page is less than 0")
    public void caseFour() {
        this.feedbacksGenerator();

        Page<Feedback> feedbacksPagination = this.sut.listAll(0, 20, FeedbackStatus.SUGGESTION);

        Assertions.assertEquals(feedbacksPagination.getNumber(), 0); // pageable is 0 based index so
                                                                     // page 0 is page 1
    }

    @Test
    @DisplayName("it should return the five itens perPage if itensPerPage is less than 5")
    public void caseFive() {
        this.feedbacksGenerator();

        Page<Feedback> feedbacksPagination = this.sut.listAll(1, 4, FeedbackStatus.SUGGESTION);

        Assertions.assertEquals(feedbacksPagination.getSize(), 5);
    }

    public Category categoryGenerator() {
        Category newCategory = new Category();
        newCategory.setName("Features");

        Category categoryCreated = this.categoryRepositoryTest.save(newCategory);

        return categoryCreated;
    }

    protected void feedbacksGenerator() {
        Category category = this.categoryGenerator();

        for (int i = 1; i <= 20; i++) {
            Feedback newFeedback = new Feedback();

            newFeedback.setFkCategory(category.getId());
            newFeedback.setDetails("Feedback Detail n " + i);
            newFeedback.setTitle("Feedback n " + i);

            if (i == 12) {
                newFeedback.setStatus(FeedbackStatus.IN_PROGRESS);
            }

            if (i > 10) {
                newFeedback.setStatus(FeedbackStatus.IN_PROGRESS);
            }

            this.feedbackRepositoryTest.save(newFeedback);
        }
    }
}
