package com.app.productfeedback.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.entities.User;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ConflictException;
import com.app.productfeedback.interfaces.user.UserRepositoryInterface;
import com.app.productfeedback.repositories.UserRepositoryTest;
import com.app.productfeedback.services.user.UserService;

@ActiveProfiles("test")
public class UpdateProfileServiceTest {
    private BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    private UserService userService;

    private UserRepositoryInterface userRepository;

    @BeforeEach
    void setup() {
        this.userRepository = new UserRepositoryTest();
        this.userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("should update user profile dinamically")
    public void caseOne() {
        User userCreation = this.userCreation();
        User userToUpdate = new User();

        userToUpdate.setId(userCreation.getId());
        userToUpdate.setUsername("Username change");
        userToUpdate.setProfilePictureUrl("fake profile pic url");
        userToUpdate.setEmail("updatedjohndoe@email.com");

        User updatedUser = this.userService.updateProfile(userToUpdate);

        Assertions.assertEquals(updatedUser.getUsername(), "Username change");
        Assertions.assertEquals(updatedUser.getProfilePictureUrl(), "fake profile pic url");
        Assertions.assertEquals(updatedUser.getEmail(), "updatedjohndoe@email.com");
        Assertions.assertEquals(userCreation.getId(), userToUpdate.getId());
        Assertions.assertNotNull(updatedUser);
    }


    @Test
    @DisplayName("should update password and hash the new one")
    public void caseTwo() {
        User userCreation = this.userCreation();
        User userToUpdate = new User();

        userToUpdate.setId(userCreation.getId());
        userToUpdate.setPassword("newpass");

        User updatedUser = this.userService.updateProfile(userToUpdate);

        boolean newPasswordMatches = bcrypt.matches("newpass", updatedUser.getPassword());

        Assertions.assertTrue(newPasswordMatches);
        Assertions.assertNotNull(updatedUser);
    }

    @Test
    @DisplayName("should not update password and hash the new one if new password has less than 6 characters")
    public void caseThree() {
        User userCreation = this.userCreation();
        User userToUpdate = new User();

        userToUpdate.setId(userCreation.getId());
        userToUpdate.setPassword("12345");

        Exception thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            this.userService.updateProfile(userToUpdate);
        });

        Assertions.assertEquals("Password must have at least 6 characters.", thrown.getMessage());
    }

    @Test
    @DisplayName("should not update user e-mail if new e-mail provided already exists")
    public void caseFour() {
        User user = this.userCreation();

        User existentEmail = new User();

        existentEmail.setEmail("existentuser@test.com");
        existentEmail.setPassword("123456");
        existentEmail.setUsername("Existent user");

        this.userService.register(existentEmail);

        user.setEmail("existentuser@test.com");

        Exception thrown = Assertions.assertThrows(ConflictException.class, () -> {
            this.userService.updateProfile(user);
        });

        Assertions.assertEquals("E-mail already exists.", thrown.getMessage());
    }

    @Test
    @DisplayName("should not update an user if necessary parameters are not provided correctly")
    public void caseFive() {
        Exception missingFirstArg = Assertions.assertThrows(BadRequestException.class, () -> {
            this.userService.updateProfile(null);
        });

        Exception missingSecondArg = Assertions.assertThrows(BadRequestException.class, () -> {
            this.userService.updateProfile(new User());
        });


        Assertions.assertEquals("User can't be null.", missingFirstArg.getMessage());
        Assertions.assertEquals("User id can't be null.", missingSecondArg.getMessage());
    }

    protected User userCreation() {
        User newUser = new User();

        newUser.setEmail("johndoe@test.com");
        newUser.setPassword("123456");
        newUser.setUsername("John Doe");

        User creation = this.userService.register(newUser);

        return creation;
    }
}
