package com.app.productfeedback.services.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import com.app.productfeedback.dto.request.user.UpdateProfileDto;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ConflictException;
import com.app.productfeedback.interfaces.UserRepositoryInterface;
import com.app.productfeedback.repositories.UserRepositoryTest;

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
        UpdateProfileDto userToUpdate = new UpdateProfileDto();

        userToUpdate.setUsername("Username change");
        userToUpdate.setProfilePictureUrl("fake profile pic url");
        userToUpdate.setEmail("updatedjohndoe@email.com");

        User updatedUser = this.userService.updateProfile(userToUpdate, userCreation.getId());

        Assertions.assertEquals(updatedUser.getUsername(), "Username change");
        Assertions.assertEquals(updatedUser.getProfilePictureUrl(), "fake profile pic url");
        Assertions.assertEquals(updatedUser.getEmail(), "updatedjohndoe@email.com");
        Assertions.assertEquals(userCreation.getId(), updatedUser.getId());
        Assertions.assertNotNull(updatedUser);
    }


    @Test
    @DisplayName("should update password and hash the new one")
    public void caseTwo() {
        User userCreation = this.userCreation();
        UpdateProfileDto userToUpdate = new UpdateProfileDto();

        userToUpdate.setPassword("newpass");

        User updatedUser = this.userService.updateProfile(userToUpdate, userCreation.getId());

        boolean newPasswordMatches = bcrypt.matches("newpass", updatedUser.getPassword());

        Assertions.assertTrue(newPasswordMatches);
        Assertions.assertNotNull(updatedUser);
    }

    @Test
    @DisplayName("should not update password and hash the new one if new password has less than 6 characters")
    public void caseThree() {
        User userCreation = this.userCreation();
        UpdateProfileDto userToUpdate = new UpdateProfileDto();

        userToUpdate.setPassword("12345");

        Exception thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            this.userService.updateProfile(userToUpdate, userCreation.getId());
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
        existentEmail.setSecretQuestion("Fav Band");
        existentEmail.setSecretAnswer("The Beatles");

        this.userService.register(existentEmail);

        UpdateProfileDto userToUpdate = new UpdateProfileDto();

        userToUpdate.setEmail("existentuser@test.com");

        Exception thrown = Assertions.assertThrows(ConflictException.class, () -> {
            this.userService.updateProfile(userToUpdate, user.getId());
        });

        Assertions.assertEquals("E-mail already exists.", thrown.getMessage());
    }

    @Test
    @DisplayName("should not update an user if necessary parameters are not provided correctly")
    public void caseFive() {
        Exception missingFirstArg = Assertions.assertThrows(BadRequestException.class, () -> {
            this.userService.updateProfile(null, null);
        });

        Exception missingSecondArg = Assertions.assertThrows(BadRequestException.class, () -> {
            this.userService.updateProfile(new UpdateProfileDto(), null);
        });


        Assertions.assertEquals("User can't be null.", missingFirstArg.getMessage());
        Assertions.assertEquals("User id can't be null.", missingSecondArg.getMessage());
    }

    protected User userCreation() {
        User newUser = new User();

        newUser.setEmail("johndoe@test.com");
        newUser.setPassword("123456");
        newUser.setUsername("John Doe");
        newUser.setSecretQuestion("Fav Band");
        newUser.setSecretAnswer("The Beatles");

        User creation = this.userService.register(newUser);

        return creation;
    }
}
