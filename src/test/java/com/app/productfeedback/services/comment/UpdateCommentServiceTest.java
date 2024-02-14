package com.app.productfeedback.services.comment;

import java.util.UUID;

import org.junit.jupiter.api.*;

import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.dto.request.comment.UpdateCommentDto;
import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.Comment;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ForbiddenException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.repositories.CategoryRepositoryTest;
import com.app.productfeedback.repositories.CommentRepositoryTest;
import com.app.productfeedback.repositories.FeedbackRepositoryTest;
import com.app.productfeedback.repositories.UserRepositoryTest;

@ActiveProfiles("test")
public class UpdateCommentServiceTest {
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
    @DisplayName("it should update an comment")
    public void caseOne() {
        User user = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());
        Comment comment = this.commentGenerator(user.getId(), feedback.getId());

        UpdateCommentDto commentToUpdate = new UpdateCommentDto();

        commentToUpdate.setComment("Updating my comment...");
        commentToUpdate.setId(comment.getId().toString());

        Comment updatedComment = this.sut.update(user.getId(), commentToUpdate);

        Assertions.assertNotNull(updatedComment);
        Assertions.assertEquals(updatedComment.getId(), comment.getId());
        Assertions.assertEquals(updatedComment.getComment(), commentToUpdate.getComment());
    }

    @Test
    @DisplayName("it should throw exceptions if args aren't provided correctly")
    public void caseTwo() {
        User user = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());
        Comment comment = this.commentGenerator(user.getId(), feedback.getId());

        Exception noUserIdArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            UpdateCommentDto noUserIdDto = new UpdateCommentDto();

            noUserIdDto.setComment("Updating my comment...");
            noUserIdDto.setId(comment.getId().toString());

            this.sut.update(null, noUserIdDto);
        });

        Exception noValidDtoArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.update(user.getId(), null);
        });

        Exception noCommentIdArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            UpdateCommentDto noCommentDto = new UpdateCommentDto();

            noCommentDto.setComment("Updating commment...");
            noCommentDto.setId(null);

            this.sut.update(user.getId(), noCommentDto);
        });

        Exception emptyCommentArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            UpdateCommentDto noCommentDto = new UpdateCommentDto();

            noCommentDto.setComment(null);
            noCommentDto.setId(comment.getId().toString());

            this.sut.update(user.getId(), noCommentDto);
        });

        Assertions.assertAll("Assert arg exceptions",
                () -> Assertions.assertEquals(noUserIdArgEx.getMessage(), "Invalid user id."),

                () -> Assertions.assertEquals(emptyCommentArgEx.getMessage(),
                        "New comment can't be empty."),

                () -> Assertions.assertEquals(noValidDtoArgEx.getMessage(),
                        "Invalid update comment dto."),

                () -> Assertions.assertEquals(noCommentIdArgEx.getMessage(),
                        "Invalid comment id."));
    }


    @Test
    @DisplayName("it should throw exceptions if user isn't found")
    public void caseThree() {
        User user = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());
        Comment comment = this.commentGenerator(user.getId(), feedback.getId());

        Exception notFoundUserEx = Assertions.assertThrows(NotFoundException.class, () -> {
            UpdateCommentDto userNotFoundDto = new UpdateCommentDto();

            userNotFoundDto.setComment("Updating my comment...");
            userNotFoundDto.setId(comment.getId().toString());

            this.sut.update(UUID.randomUUID(), userNotFoundDto);
        });

        Assertions.assertEquals(notFoundUserEx.getMessage(), "User not found.");
    }

    @Test
    @DisplayName("it should throw exceptions if user editing is not the same who created the comment")
    public void caseFour() {
        User user = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());
        Comment comment = this.commentGenerator(user.getId(), feedback.getId());

        User alternativeUser = this.userGenerator();

        Exception notFoundUserEx = Assertions.assertThrows(ForbiddenException.class, () -> {
            UpdateCommentDto userNotFoundDto = new UpdateCommentDto();

            userNotFoundDto.setComment("Updating my comment...");
            userNotFoundDto.setId(comment.getId().toString());

            this.sut.update(alternativeUser.getId(), userNotFoundDto);
        });

        Assertions.assertEquals(notFoundUserEx.getMessage(),
                "Only comment owners can edit their own comments.");
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

    protected Comment commentGenerator(UUID userId, UUID feedbackId) {
        Comment newComment = new Comment();
        newComment.setComment("My Comment Test");
        newComment.setUserId(userId);
        newComment.setFeedbackId(feedbackId);

        return this.commentRepositoryTest.save(newComment);
    }
}
