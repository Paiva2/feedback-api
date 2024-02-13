package com.app.productfeedback.services.comment;

import java.util.UUID;

import org.junit.jupiter.api.*;

import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.repositories.*;
import com.app.productfeedback.dto.request.comment.NewCommentDto;
import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.Comment;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.NotFoundException;

@ActiveProfiles("test")
public class NewCommentServiceTest {
    private UserRepositoryTest userRepositoryTest;

    private CommentRepositoryTest commentRepositoryTest;

    private FeedbackRepositoryTest feedbackRepositoryTest;

    private CategoryRepositoryTest categoryRepositoryTest;

    private CommentService sut;

    @BeforeEach
    public void setup() {
        userRepositoryTest = new UserRepositoryTest();
        commentRepositoryTest = new CommentRepositoryTest();
        feedbackRepositoryTest = new FeedbackRepositoryTest();
        categoryRepositoryTest = new CategoryRepositoryTest();

        sut = new CommentService(userRepositoryTest, feedbackRepositoryTest, commentRepositoryTest);
    }

    @Test
    @DisplayName("it should insert a new comment on an feedback for an auth user")
    public void caseOne() {
        User user = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        NewCommentDto newCommentDto = new NewCommentDto();

        newCommentDto.setComment("This is my first comment!");
        newCommentDto.setFeedbackId(feedback.getId().toString());

        Comment commentCreated = this.sut.create(user.getId(), newCommentDto);

        Assertions.assertNotNull(commentCreated);
        Assertions.assertEquals(commentCreated.getComment(), newCommentDto.getComment());
        Assertions.assertEquals(commentCreated.getUserId(), user.getId());
        Assertions.assertEquals(commentCreated.getFeedbackId(), feedback.getId());
    }

    @Test
    @DisplayName("it should insert a new comment on an feedback for an non-auth user")
    public void caseTwo() {
        User user = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        NewCommentDto newCommentDto = new NewCommentDto();

        newCommentDto.setComment("This is my first comment!");
        newCommentDto.setFeedbackId(feedback.getId().toString());

        Comment commentCreated = this.sut.create(null, newCommentDto);

        Assertions.assertNotNull(commentCreated);
        Assertions.assertEquals(commentCreated.getComment(), newCommentDto.getComment());
        Assertions.assertEquals(null, commentCreated.getUserId());
        Assertions.assertEquals(commentCreated.getFeedbackId(), feedback.getId());
    }

    @Test
    @DisplayName("it should throw an exception if any needed arg is null")
    public void caseThree() {
        User user = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setFeedbackId(feedback.getId().toString());

        Exception dtoArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.create(user.getId(), null);
        });

        Exception feedbackIdArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            newCommentDto.setFeedbackId(null);
            this.sut.create(user.getId(), newCommentDto);
        });

        Exception commentArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            newCommentDto.setComment(null);
            newCommentDto.setFeedbackId(feedback.getId().toString());

            this.sut.create(user.getId(), newCommentDto);
        });

        Assertions.assertAll("Args Exceptions",
                () -> Assertions.assertEquals(dtoArgEx.getMessage(),
                        "New comment dto can't be null."),
                () -> Assertions.assertEquals(feedbackIdArgEx.getMessage(),
                        "Feedback id can't be null."),
                () -> Assertions.assertEquals(commentArgEx.getMessage(), "Comment can't be null."));
    }

    @Test
    @DisplayName("it should throw an exception if feedback is not found")
    public void caseFour() {
        User user = this.userGenerator();

        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setFeedbackId(UUID.randomUUID().toString());
        newCommentDto.setComment("Test comment");

        Exception feedbackNotFoundEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.create(user.getId(), newCommentDto);
        });

        Assertions.assertEquals("Feedback not found.", feedbackNotFoundEx.getMessage());
    }

    @Test
    @DisplayName("it should throw an exception if provided user isn't found")
    public void caseFive() {
        User user = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        NewCommentDto newCommentDto = new NewCommentDto();
        newCommentDto.setFeedbackId(feedback.getId().toString());
        newCommentDto.setComment("Test comment");

        Exception feedbackNotFoundEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.create(UUID.randomUUID(), newCommentDto);
        });

        Assertions.assertEquals("User not found.", feedbackNotFoundEx.getMessage());
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
