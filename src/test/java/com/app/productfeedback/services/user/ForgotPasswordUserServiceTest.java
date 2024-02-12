package com.app.productfeedback.services.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.app.productfeedback.entities.User;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ForbiddenException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.interfaces.UserRepositoryInterface;
import com.app.productfeedback.repositories.UserRepositoryTest;

import org.springframework.security.crypto.bcrypt.BCrypt;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class ForgotPasswordUserServiceTest {
    protected BCrypt bcrypt = new BCrypt();

    private UserService userService;

    private UserRepositoryInterface userRepository;

    @BeforeEach
    void setup() {
        this.userRepository = new UserRepositoryTest();
        this.userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("it should update an user password")
    public void caseOne() {
        User user = this.userCreation();

        user.setPassword("changepassword");

        User userPasswordUpdated = this.userService.forgotPassword(user);

        boolean DoesNewPasswordMatches =
                BCrypt.checkpw("changepassword", userPasswordUpdated.getPassword());

        Assertions.assertTrue(DoesNewPasswordMatches);
        Assertions.assertNotNull(userPasswordUpdated);
        Assertions.assertEquals(userPasswordUpdated.getEmail(), "johndoe@test.com");
    }

    @Test
    @DisplayName("it should not update an user password without User provided")
    public void caseTwo() {
        User newUser = null;

        Exception thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            this.userService.forgotPassword(newUser);
        });

        Assertions.assertEquals("User can't be null.", thrown.getMessage());
    }

    @Test
    @DisplayName("it should not update an user password if user isn't registered")
    public void caseThree() {
        User nonExistentUser = new User();

        Exception thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            this.userService.forgotPassword(nonExistentUser);
        });

        Assertions.assertEquals("User not found.", thrown.getMessage());
    }


    @Test
    @DisplayName("it should not update an user password if password has less than 6 characters")
    public void caseFour() {
        User userToUpdate = this.userCreation();

        userToUpdate.setPassword("12345");

        Exception thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            this.userService.forgotPassword(userToUpdate);
        });

        Assertions.assertEquals("Password must have at least 6 characters.", thrown.getMessage());
    }

    @Test
    @DisplayName("it should not update an user password if secret answer is wrong")
    public void caseFive() {
        User userCreation = this.userCreation();
        User userToUpdate = new User();

        userToUpdate.setEmail(userCreation.getEmail());
        userToUpdate.setSecretQuestion("Fav Band");
        userToUpdate.setSecretAnswer("Wrong Answer");

        Exception thrown = Assertions.assertThrows(ForbiddenException.class, () -> {
            this.userService.forgotPassword(userToUpdate);
        });

        Assertions.assertEquals("Secret answer doesn't match.", thrown.getMessage());
    }

    @Test
    @DisplayName("it should not update an user password if secret question is wrong")
    public void caseSix() {
        User userCreation = this.userCreation();
        User userToUpdate = new User();

        userToUpdate.setEmail(userCreation.getEmail());
        userToUpdate.setSecretQuestion("Wrong question");
        userToUpdate.setSecretAnswer("The Beatles");

        Exception thrown = Assertions.assertThrows(ForbiddenException.class, () -> {
            this.userService.forgotPassword(userToUpdate);
        });

        Assertions.assertEquals("Secret question doesn't match.", thrown.getMessage());
    }

    protected User userCreation() {
        User newUser = new User();

        newUser.setEmail("johndoe@test.com");
        newUser.setPassword("123456");
        newUser.setUsername("John Doe");
        newUser.setSecretQuestion("Fav Band");
        newUser.setSecretAnswer("The Beatles");

        User createdUser = this.userService.register(newUser);

        return createdUser;
    }
}
