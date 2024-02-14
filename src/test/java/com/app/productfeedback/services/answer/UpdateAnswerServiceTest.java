package com.app.productfeedback.services.answer;

import java.util.UUID;

import org.junit.jupiter.api.*;

import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.dto.request.answer.AnswerCreationDto;
import com.app.productfeedback.dto.request.answer.UpdateAnswerDto;
import com.app.productfeedback.entities.*;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.*;
import com.app.productfeedback.repositories.*;

@ActiveProfiles("test")
public class UpdateAnswerServiceTest {
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
    @DisplayName("it should update an answer")
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

        UpdateAnswerDto updateAnswerDto = new UpdateAnswerDto();

        updateAnswerDto.setAnswer("Updating my answer!");
        updateAnswerDto.setId(answerCreated.getId().toString());

        Answer performUpdate = this.sut.update(userAnswering.getId(), updateAnswerDto);

        Assertions.assertNotNull(performUpdate);
        Assertions.assertEquals(answerCreated.getId(), performUpdate.getId());
        Assertions.assertEquals("Updating my answer!", performUpdate.getAnswer());
        Assertions.assertEquals(userAnswering.getId(), performUpdate.getUserId());
    }

    @Test
    @DisplayName("it should throw Exception if required arg is not provided")
    public void caseTwo() {
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

        Exception nullUserIdArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            UpdateAnswerDto nullUserIdDto = new UpdateAnswerDto();

            nullUserIdDto.setAnswer("Updating my answer!");
            nullUserIdDto.setId(answerCreated.getId().toString());

            this.sut.update(userAnswering.getId(), nullUserIdDto);

            this.sut.update(null, nullUserIdDto);
        });

        Assertions.assertEquals("Invalid user id.", nullUserIdArgEx.getMessage());

        Exception nullAnswerIdArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            UpdateAnswerDto nullAnswerIdDto = new UpdateAnswerDto();
            nullAnswerIdDto.setAnswer("Updating my answer!");

            this.sut.update(userAnswering.getId(), nullAnswerIdDto);
        });

        Assertions.assertEquals("Invalid answer id.", nullAnswerIdArgEx.getMessage());

        Exception emptyAnswerArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            UpdateAnswerDto emptyAnswerDto = new UpdateAnswerDto();
            emptyAnswerDto.setAnswer(null);
            emptyAnswerDto.setId(answerCreated.getId().toString());

            this.sut.update(userAnswering.getId(), emptyAnswerDto);
        });

        Assertions.assertEquals("New answer can't be empty.", emptyAnswerArgEx.getMessage());
    }

    @Test
    @DisplayName("it throw Exception if user isn't found")
    public void caseThree() {
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

        UpdateAnswerDto updateAnswerDto = new UpdateAnswerDto();

        updateAnswerDto.setAnswer("Updating my answer!");
        updateAnswerDto.setId(answerCreated.getId().toString());

        Exception notFoundUserEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.update(UUID.randomUUID(), updateAnswerDto);
        });

        Assertions.assertEquals("User not found.", notFoundUserEx.getMessage());
    }

    @Test
    @DisplayName("it throw Exception if answer isn't found")
    public void caseFour() {
        User userAnswering = this.userGenerator();

        Exception notFoundUserEx = Assertions.assertThrows(NotFoundException.class, () -> {
            UpdateAnswerDto updateAnswerDto = new UpdateAnswerDto();

            updateAnswerDto.setAnswer("Updating my answer!");
            updateAnswerDto.setId(UUID.randomUUID().toString());

            this.sut.update(userAnswering.getId(), updateAnswerDto);
        });

        Assertions.assertEquals("Answer not found.", notFoundUserEx.getMessage());
    }

    @Test
    @DisplayName("it throw an Exception if provided id doesn't match answer owner")
    public void caseFive() {
        User userCommenting = this.userGenerator();
        Category category = this.categoryGenerator();
        Feedback feedback = this.feedbackGenerator(userCommenting.getId(), category.getId());
        Comment comment = this.commentGenerator(feedback.getId(), userCommenting.getId());

        User userAnswering = this.userGenerator();
        User notAnswerOwner = this.userGenerator();

        AnswerCreationDto newAnswer = new AnswerCreationDto();

        newAnswer.setAnswer("Answering to comment...");
        newAnswer.setAnsweringToId(userCommenting.getId().toString());
        newAnswer.setCommentId(comment.getId().toString());

        Answer answerCreated = this.sut.create(newAnswer, userAnswering.getId());

        UpdateAnswerDto updateAnswerDto = new UpdateAnswerDto();

        updateAnswerDto.setAnswer("Updating my answer!");
        updateAnswerDto.setId(answerCreated.getId().toString());

        Exception notOwnerEx = Assertions.assertThrows(ForbiddenException.class, () -> {
            this.sut.update(notAnswerOwner.getId(), updateAnswerDto);
        });

        Assertions.assertEquals("Only answer owner can edit their own answer.",
                notOwnerEx.getMessage());
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
