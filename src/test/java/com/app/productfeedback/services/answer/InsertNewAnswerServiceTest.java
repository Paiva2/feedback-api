package com.app.productfeedback.services.answer;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.dto.request.answer.AnswerCreationDto;
import com.app.productfeedback.entities.Answer;
import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.Comment;
import com.app.productfeedback.entities.Feedback;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.repositories.AnswerRepositoryTest;
import com.app.productfeedback.repositories.CategoryRepositoryTest;
import com.app.productfeedback.repositories.CommentRepositoryTest;
import com.app.productfeedback.repositories.FeedbackRepositoryTest;
import com.app.productfeedback.repositories.UserRepositoryTest;

@ActiveProfiles("test")
public class InsertNewAnswerServiceTest {
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
    @DisplayName("it should create a new answer to an comment from an feedback")
    public void caseOne() {
        User userCommenting = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(userCommenting.getId(), category.getId());
        Comment comment = this.commentGenerator(feedback.getId(), userCommenting.getId());

        User userAnswering = this.userGenerator();

        AnswerCreationDto newAnswer = new AnswerCreationDto();

        newAnswer.setAnswer("Answering to comment...");
        newAnswer.setAnsweringToId(userCommenting.getId().toString());
        newAnswer.setCommentId(comment.getId().toString());

        Answer answerCreated = this.sut.create(newAnswer, userAnswering.getId());

        Assertions.assertNotNull(answerCreated);
        Assertions.assertEquals("Answering to comment...", answerCreated.getAnswer());
        Assertions.assertEquals(userAnswering.getId(), answerCreated.getUserId());
        Assertions.assertEquals(userCommenting.getId(), answerCreated.getAnsweringToId());
        Assertions.assertEquals(comment.getId(), answerCreated.getCommentId());
    }

    @Test
    @DisplayName("it should not create a new answer without needed args provided")
    public void caseTwo() {
        AnswerCreationDto newAnswer = new AnswerCreationDto();

        Exception noDtoArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.create(null, null);
        });

        Exception noAnswerArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.create(newAnswer, null);
        });

        Assertions.assertEquals(noDtoArgEx.getMessage(), "Answer creation dto can't be null.");
        Assertions.assertEquals(noAnswerArgEx.getMessage(), "Answer can't be null.");
    }

    @Test
    @DisplayName("it should not create a new answer if user provided doesn't exists (only if isn't null)")
    public void caseThree() {
        AnswerCreationDto newAnswer = new AnswerCreationDto();

        newAnswer.setAnswer("Answering to comment...");

        Exception notFoundUserEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.create(newAnswer, UUID.randomUUID());
        });

        Assertions.assertEquals(notFoundUserEx.getMessage(), "User not found.");
    }

    @Test
    @DisplayName("it should not create a new answer if user beeing replied to doesn't exists (only if isn't null)")
    public void caseFour() {
        User userAnswering = this.userGenerator();

        AnswerCreationDto newAnswer = new AnswerCreationDto();

        newAnswer.setAnswer("Answering to comment...");
        newAnswer.setAnsweringToId(UUID.randomUUID().toString());

        Exception notFoundUserBeingRepliedToEx =
                Assertions.assertThrows(NotFoundException.class, () -> {
                    this.sut.create(newAnswer, userAnswering.getId());
                });

        Assertions.assertEquals(notFoundUserBeingRepliedToEx.getMessage(),
                "User being replied not found.");
    }

    @Test
    @DisplayName("it should not create a new answer if comment doesn't exists")
    public void caseFive() {
        User userCommenting = this.userGenerator();
        User userAnswering = this.userGenerator();

        AnswerCreationDto newAnswer = new AnswerCreationDto();

        newAnswer.setAnswer("Answering to comment...");
        newAnswer.setAnsweringToId(userCommenting.getId().toString());
        newAnswer.setCommentId(UUID.randomUUID().toString());

        Exception notFoundCommentEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.create(newAnswer, userAnswering.getId());
        });

        Assertions.assertEquals(notFoundCommentEx.getMessage(), "Comment not found.");
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
