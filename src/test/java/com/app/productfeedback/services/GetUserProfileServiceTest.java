package com.app.productfeedback.services;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.entities.User;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.repositories.UserRepositoryTest;
import com.app.productfeedback.services.user.UserService;

@ActiveProfiles("test")
public class GetUserProfileServiceTest {
    private UserService userService;

    private UserRepositoryTest userRepositoryTest;

    @BeforeEach
    public void setup() {
        this.userRepositoryTest = new UserRepositoryTest();
        this.userService = new UserService(userRepositoryTest);
    }

    @Test
    @DisplayName("it should get an user profile")
    public void caseOne() {
        User userCreation = new User();
        userCreation.setEmail("johndoe@test.com");
        userCreation.setUsername("John Doe");
        userCreation.setPassword("123456");
        userCreation.setSecretQuestion("Fav Band");
        userCreation.setSecretAnswer("The Beatles");

        this.userService.register(userCreation);

        Map<String, Object> userProfile = this.userService.profile(userCreation.getId());

        Assertions.assertNotNull(userProfile);
        Assertions.assertEquals(userProfile.get("email"), "johndoe@test.com");
        Assertions.assertEquals(userProfile.get("username"), "John Doe");
        Assertions.assertEquals(userProfile.get("id"), userCreation.getId());
    }

    @Test
    @DisplayName("it should not get an user profile without an user id")
    public void caseTwo() {

        Exception thrownError = Assertions.assertThrows(BadRequestException.class, () -> {
            this.userService.profile(null);
        });

        Assertions.assertEquals("User can't be null.", thrownError.getMessage());
    }

    @Test
    @DisplayName("it should not get an user profile if user doesn't exists")
    public void caseThree() {
        Exception thrownError = Assertions.assertThrows(NotFoundException.class, () -> {
            this.userService.profile(UUID.randomUUID());
        });

        Assertions.assertEquals("User not found.", thrownError.getMessage());
    }

}
