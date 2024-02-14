package com.app.productfeedback.services.answer;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.*;

import org.springframework.test.context.ActiveProfiles;
import com.app.productfeedback.dto.request.answer.AnswerCreationDto;
import com.app.productfeedback.entities.Answer;
import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.Comment;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ForbiddenException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.repositories.AnswerRepositoryTest;
import com.app.productfeedback.repositories.CategoryRepositoryTest;
import com.app.productfeedback.repositories.CommentRepositoryTest;
import com.app.productfeedback.repositories.FeedbackRepositoryTest;
import com.app.productfeedback.repositories.UserRepositoryTest;

@ActiveProfiles("test")
public class DeleteAnswerServiceTest {
    private UserRepositoryTest userRepositoryTest;

    private CommentRepositoryTest commentRepositoryTest;

    private FeedbackRepositoryTest feedbackRepositoryTest;

    private CategoryRepositoryTest categoryRepositoryTest;

    private AnswerRepositoryTest answerRepositoryTest;

    private AnswerService sut;

    @BeforeEach
    public void setup() {
        userRepositoryTest = new UserRepositoryTest();
        commentRepositoryTest = new CommentRepositoryTest();
        feedbackRepositoryTest = new FeedbackRepositoryTest();
        categoryRepositoryTest = new CategoryRepositoryTest();
        answerRepositoryTest = new AnswerRepositoryTest();

        sut = new AnswerService(userRepositoryTest, commentRepositoryTest, answerRepositoryTest);
    }

    @Test
    @DisplayName("it should delete an answer from an comment")
    public void caseOne() {
        User commentOwner = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(commentOwner.getId(), category.getId());
        Comment comment = this.commentGenerator(feedback.getId(), commentOwner.getId());

        User answerOwner = this.userGenerator();

        AnswerCreationDto newAnswer = new AnswerCreationDto();

        newAnswer.setAnswer("Answering to comment...");
        newAnswer.setAnsweringToId(commentOwner.getId().toString());
        newAnswer.setCommentId(comment.getId().toString());

        Answer answerCreated = this.sut.create(newAnswer, answerOwner.getId());

        this.sut.delete(answerOwner.getId(), answerCreated.getId());

        Optional<Answer> checkDeletedAnswer =
                this.answerRepositoryTest.findById(answerCreated.getId());

        Assertions.assertEquals(Optional.empty(), checkDeletedAnswer);
    }

    @Test
    @DisplayName("it should throw Exception if necessary args are not provided")
    public void caseTwo() {
        Exception noUserIdArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.delete(null, UUID.randomUUID());
        });

        Exception noAnswerIdArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.delete(UUID.randomUUID(), null);
        });

        Assertions.assertEquals("User id can't be null.", noUserIdArgEx.getMessage());
        Assertions.assertEquals("Answer id can't be null.", noAnswerIdArgEx.getMessage());
    }

    @Test
    @DisplayName("it should throw Exception if user isn't found")
    public void caseThree() {

        Exception userNotFoundEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.delete(UUID.randomUUID(), UUID.randomUUID());
        });

        Assertions.assertEquals("User not found.", userNotFoundEx.getMessage());
    }

    @Test
    @DisplayName("it should throw Exception if answer isn't found")
    public void caseFour() {
        User answerOwner = this.userGenerator();

        Exception answerNotFoundEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.delete(answerOwner.getId(), UUID.randomUUID());
        });

        Assertions.assertEquals("Answer not found.", answerNotFoundEx.getMessage());
    }

    @Test
    @DisplayName("it should not delete an answer from an comment if answer owner doesn't matches with provided user id")
    public void caseFive() {
        User commentOwner = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(commentOwner.getId(), category.getId());
        Comment comment = this.commentGenerator(feedback.getId(), commentOwner.getId());

        User answerOwner = this.userGenerator();
        User alternativeAnswerOwner = this.userGenerator();

        AnswerCreationDto newAnswer = new AnswerCreationDto();

        newAnswer.setAnswer("Answering to comment...");
        newAnswer.setAnsweringToId(commentOwner.getId().toString());
        newAnswer.setCommentId(comment.getId().toString());

        Answer answerCreated = this.sut.create(newAnswer, answerOwner.getId());

        Exception wrongAnswerOwnerEx = Assertions.assertThrows(ForbiddenException.class, () -> {
            this.sut.delete(alternativeAnswerOwner.getId(), answerCreated.getId());
        });

        Assertions.assertEquals("Only answer owner can delete their own answers.",
                wrongAnswerOwnerEx.getMessage());
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

    protected Comment commentGenerator(UUID feedbackId, UUID userId) {
        Comment newComment = new Comment();
        newComment.setComment("Some comment.");
        newComment.setFeedbackId(feedbackId);
        newComment.setUserId(userId);

        return this.commentRepositoryTest.save(newComment);
    }
}
