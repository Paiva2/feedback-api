package com.app.productfeedback.services.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.entities.User;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ForbiddenException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.interfaces.UserRepository;
import com.app.productfeedback.repositories.UserRepositoryTest;

@ActiveProfiles("test")
public class AuthUserServiceTest {
    private UserService sut;

    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        this.userRepository = new UserRepositoryTest();
        this.sut = new UserService(userRepository);
    }

    @Test
    @DisplayName("it should auth an user if everything is ok")
    public void caseOne() {
        this.userCreation();
        User userToAuth = new User();

        userToAuth.setEmail("johndoe@test.com");
        userToAuth.setPassword("123456");

        User userAuth = this.sut.auth(userToAuth);

        Assertions.assertNotNull(userAuth);
        Assertions.assertEquals(userToAuth.getEmail(), "johndoe@test.com");
        Assertions.assertInstanceOf(User.class, userToAuth);
    }

    @Test
    @DisplayName("it should not auth user without user parameter")
    public void caseTwo() {
        User newUser = null;

        Exception thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.auth(newUser);
        });

        Assertions.assertEquals("User can't be null.", thrown.getMessage());
    }

    @Test
    @DisplayName("it should not auth user if user doesn't exists")
    public void caseThree() {
        User nonRegisteredUser = new User();

        nonRegisteredUser.setEmail("nonregistereduser@email.com");

        Exception thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.auth(nonRegisteredUser);
        });

        Assertions.assertEquals("User not found.", thrown.getMessage());
    }

    @Test
    @DisplayName("it should not auth user if credentials are wrong")
    public void caseFour() {
        User user = this.userCreation();

        user.setPassword("wrongpassword");

        Exception thrown = Assertions.assertThrows(ForbiddenException.class, () -> {
            this.sut.auth(user);
        });

        Assertions.assertEquals("Wrong credentials.", thrown.getMessage());
    }


    protected User userCreation() {
        User newUser = new User();

        newUser.setEmail("johndoe@test.com");
        newUser.setPassword("123456");
        newUser.setUsername("John Doe");
        newUser.setSecretQuestion("Fav Band");
        newUser.setSecretAnswer("The Beatles");

        this.sut.register(newUser);

        return newUser;
    }
}
