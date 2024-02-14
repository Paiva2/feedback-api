package com.app.productfeedback.services.comment;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.app.productfeedback.dto.request.comment.NewCommentDto;
import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.Comment;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.exceptions.UnauthorizedException;
import com.app.productfeedback.repositories.CategoryRepositoryTest;
import com.app.productfeedback.repositories.CommentRepositoryTest;
import com.app.productfeedback.repositories.FeedbackRepositoryTest;
import com.app.productfeedback.repositories.UserRepositoryTest;

public class DeleteComentServiceTest {
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
    @DisplayName("it should delete an comment")
    public void caseOne() {
        User user = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        NewCommentDto newCommentDto = new NewCommentDto();

        newCommentDto.setComment("This is my first comment!");
        newCommentDto.setFeedbackId(feedback.getId().toString());

        Comment commentCreated = this.sut.create(user.getId(), newCommentDto);

        this.sut.delete(user.getId(), commentCreated.getId());

        Optional<Comment> checkRemovedComment =
                this.commentRepositoryTest.findById(commentCreated.getId());

        Assertions.assertEquals(Optional.empty(), checkRemovedComment);
    }

    @Test
    @DisplayName("it should not delete an comment without correctly provided args")
    public void caseTwo() {
        User user = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        NewCommentDto newCommentDto = new NewCommentDto();

        newCommentDto.setComment("This is my first comment!");
        newCommentDto.setFeedbackId(feedback.getId().toString());

        Comment commentCreated = this.sut.create(user.getId(), newCommentDto);

        Exception noCommentIdArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.delete(user.getId(), null);
        });

        Exception noUserIdArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.delete(null, commentCreated.getId());
        });

        Assertions.assertEquals("Invalid comment id.", noCommentIdArgEx.getMessage());
        Assertions.assertEquals("Only admins can manage comments made by guests.",
                noUserIdArgEx.getMessage());
    }


    @Test
    @DisplayName("it should not delete an comment if user doesn't exists")
    public void caseThree() {
        User user = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        NewCommentDto newCommentDto = new NewCommentDto();

        newCommentDto.setComment("This is my first comment!");
        newCommentDto.setFeedbackId(feedback.getId().toString());

        Comment commentCreated = this.sut.create(user.getId(), newCommentDto);

        Exception noFoundUserEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.delete(UUID.randomUUID(), commentCreated.getId());
        });

        Assertions.assertEquals("User not found.", noFoundUserEx.getMessage());
    }

    @Test
    @DisplayName("it should not delete an comment if comment doesn't exists")
    public void caseFour() {
        User user = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        NewCommentDto newCommentDto = new NewCommentDto();

        newCommentDto.setComment("This is my first comment!");
        newCommentDto.setFeedbackId(feedback.getId().toString());

        Exception noFoundUserEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.delete(user.getId(), UUID.randomUUID());
        });

        Assertions.assertEquals("Comment not found.", noFoundUserEx.getMessage());
    }


    @Test
    @DisplayName("it should not delete an comment if user provided doesn't match with comment owner")
    public void caseFive() {
        User user = this.userGenerator();
        User alternativeUser = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(user.getId(), category.getId());

        NewCommentDto newCommentDto = new NewCommentDto();

        newCommentDto.setComment("This is my first comment!");
        newCommentDto.setFeedbackId(feedback.getId().toString());

        Comment commentCreated = this.sut.create(user.getId(), newCommentDto);

        Exception noAuthEx = Assertions.assertThrows(UnauthorizedException.class, () -> {
            this.sut.delete(alternativeUser.getId(), commentCreated.getId());
        });

        Assertions.assertEquals("Only comment owners or admins can manage their own comments.",
                noAuthEx.getMessage());
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
