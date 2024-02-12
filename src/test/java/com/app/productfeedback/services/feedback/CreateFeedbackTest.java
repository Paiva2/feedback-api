package com.app.productfeedback.services.feedback;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.dto.request.feedback.NewFeedbackDto;
import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.interfaces.CategoryRepository;
import com.app.productfeedback.interfaces.FeedbackRepository;
import com.app.productfeedback.interfaces.UserRepository;
import com.app.productfeedback.repositories.CategoryRepositoryTest;
import com.app.productfeedback.repositories.FeedbackRepositoryTest;
import com.app.productfeedback.repositories.UserRepositoryTest;

@ActiveProfiles("test")
public class CreateFeedbackTest {
    private UserRepository userRepositoryTest;

    private CategoryRepository categoryRepositoryTest;

    private FeedbackRepository feedbackRepositoryTest;

    private FeedbackService sut;

    @BeforeEach
    public void setup() {
        this.userRepositoryTest = new UserRepositoryTest();
        this.categoryRepositoryTest = new CategoryRepositoryTest();
        this.feedbackRepositoryTest = new FeedbackRepositoryTest();

        this.sut = new FeedbackService(feedbackRepositoryTest, userRepositoryTest,
                categoryRepositoryTest);
    }

    @Test
    @DisplayName("it should create an feedback for an auth user")
    public void caseOne() {
        User user = this.userGenerator();
        Category category = this.categoryGenerator();

        NewFeedbackDto newFeedbackDto = new NewFeedbackDto();

        newFeedbackDto.setCategoryId(category.getId().toString());
        newFeedbackDto.setTitle("First feedback");
        newFeedbackDto.setDetails("Testing my feedback creation");

        Feedback feedback = this.sut.create(newFeedbackDto, user.getId());

        Assertions.assertAll("Feedback creation",
                () -> Assertions.assertEquals(feedback.getTitle(), newFeedbackDto.getTitle()),
                () -> Assertions.assertEquals(feedback.getDetails(), newFeedbackDto.getDetails()),
                () -> Assertions.assertEquals(feedback.getFkCategoryId(),
                        UUID.fromString(newFeedbackDto.getCategoryId())),
                () -> Assertions.assertEquals(feedback.getFkUserId(), user.getId()));
    }

    @Test
    @DisplayName("it should create an feedback for an non auth user")
    public void caseTwo() {
        Category category = this.categoryGenerator();

        NewFeedbackDto newFeedbackDto = new NewFeedbackDto();

        newFeedbackDto.setCategoryId(category.getId().toString());
        newFeedbackDto.setTitle("First feedback");
        newFeedbackDto.setDetails("Testing my feedback creation");

        Feedback feedback = this.sut.create(newFeedbackDto, null);

        Assertions.assertAll("Feedback creation",
                () -> Assertions.assertEquals(feedback.getTitle(), newFeedbackDto.getTitle()),
                () -> Assertions.assertEquals(feedback.getDetails(), newFeedbackDto.getDetails()),
                () -> Assertions.assertEquals(feedback.getFkCategoryId(),
                        UUID.fromString(newFeedbackDto.getCategoryId())),
                () -> Assertions.assertEquals(feedback.getFkUserId(), null));
    }

    @Test
    @DisplayName("it should not create an feedback if dto is null")
    public void caseThree() {

        Exception nullArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.create(null, UUID.randomUUID());
        });

        Assertions.assertEquals(nullArgEx.getMessage(), "New feedback dto can't be null.");
    }

    @Test
    @DisplayName("it should not create an feedback for an auth user if user doesn't exists")
    public void caseFour() {
        Category category = this.categoryGenerator();

        NewFeedbackDto newFeedbackDto = new NewFeedbackDto();

        newFeedbackDto.setCategoryId(category.getId().toString());
        newFeedbackDto.setTitle("First feedback");
        newFeedbackDto.setDetails("Testing my feedback creation");

        Exception notFoundEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.create(newFeedbackDto, UUID.randomUUID());
        });

        Assertions.assertEquals(notFoundEx.getMessage(), "User not found.");
    }

    @Test
    @DisplayName("it should not create an feedback if category doesn't exists")
    public void caseFive() {
        User user = this.userGenerator();

        NewFeedbackDto newFeedbackDto = new NewFeedbackDto();

        newFeedbackDto.setCategoryId(UUID.randomUUID().toString());
        newFeedbackDto.setTitle("First feedback");
        newFeedbackDto.setDetails("Testing my feedback creation");

        Exception notFoundEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.create(newFeedbackDto, user.getId());
        });

        Assertions.assertEquals(notFoundEx.getMessage(), "Category not found.");
    }

    protected User userGenerator() {
        User newUser = new User();
        newUser.setEmail("johndoe@test.com");
        newUser.setPassword("123456");
        newUser.setUsername("John Doe");
        newUser.setRole(UserRole.USER);

        User userCreation = this.userRepositoryTest.save(newUser);

        return userCreation;
    }

    protected Category categoryGenerator() {
        Category newCategory = new Category();
        newCategory.setName("Features");

        Category categoryCreation = this.categoryRepositoryTest.save(newCategory);

        return categoryCreation;
    }
}
