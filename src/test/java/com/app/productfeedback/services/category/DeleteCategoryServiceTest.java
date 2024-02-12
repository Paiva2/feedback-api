package com.app.productfeedback.services.category;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.exceptions.UnauthorizedException;
import com.app.productfeedback.repositories.CategoryRepositoryTest;
import com.app.productfeedback.repositories.UserRepositoryTest;

@ActiveProfiles("test")
public class DeleteCategoryServiceTest {

    private UserRepositoryTest userRepositoryTest;
    private CategoryRepositoryTest categoryRepositoryTest;

    private CategoryService sut;

    @BeforeEach
    public void setup() {
        this.userRepositoryTest = new UserRepositoryTest();
        this.categoryRepositoryTest = new CategoryRepositoryTest();

        sut = new CategoryService(categoryRepositoryTest, userRepositoryTest);
    }

    @Test
    @DisplayName("it should delete an existent category")
    public void caseOne() {
        User user = this.userCreation();
        Category category = new Category();
        category.setName("Features");

        Category categoryCreation = this.sut.create(category, user.getId());

        this.sut.delete(categoryCreation.getId(), user.getId());

        Optional<Category> searchRemovedCategory =
                categoryRepositoryTest.findById(categoryCreation.getId());

        Assertions.assertEquals(searchRemovedCategory, Optional.empty());
    }

    @Test
    @DisplayName("it should not delete an existent category without correctly provided parameters")
    public void caseTwo() {
        User user = this.userCreation();
        Category category = new Category();
        category.setName("Features");
        category.setId(UUID.randomUUID());

        Exception firstArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.delete(null, user.getId());
        });

        Exception secondArgEx = Assertions.assertThrows(BadRequestException.class, () -> {
            this.sut.delete(category.getId(), null);
        });

        Assertions.assertEquals(firstArgEx.getMessage(), "Invalid category id.");
        Assertions.assertEquals(secondArgEx.getMessage(), "Invalid user id.");
    }

    @Test
    @DisplayName("it should not delete an existent category if user doesn't exists")
    public void caseThree() {
        Category category = new Category();
        category.setName("Features");
        category.setId(UUID.randomUUID());

        UUID nonRegisteredUserId = UUID.randomUUID();

        Exception notFoundUserEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.delete(category.getId(), nonRegisteredUserId);
        });

        Assertions.assertEquals(notFoundUserEx.getMessage(), "User not found.");
    }

    @Test
    @DisplayName("it should not delete an existent category if user doesn't have suficient permissions")
    public void caseFour() {
        User user = this.userCreation();

        Category category = new Category();
        category.setName("Features");

        Category categoryCreation = this.sut.create(category, user.getId());

        user.setRole(UserRole.USER);

        Exception notFoundUserEx = Assertions.assertThrows(UnauthorizedException.class, () -> {
            this.sut.delete(categoryCreation.getId(), user.getId());
        });

        Assertions.assertEquals(notFoundUserEx.getMessage(), "Only admins can manage categories.");
    }

    @Test
    @DisplayName("it should not delete an existent category if category doesn't exists")
    public void caseFive() {
        User user = this.userCreation();

        UUID categoryIdNonCreated = UUID.randomUUID();

        Exception notFoundUserEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.sut.delete(categoryIdNonCreated, user.getId());
        });

        Assertions.assertEquals(notFoundUserEx.getMessage(), "Category not found.");
    }

    public User userCreation() {
        User newUser = new User();
        newUser.setEmail("johndoe@email.com");
        newUser.setPassword("123456");
        newUser.setRole(UserRole.ADMIN);

        User userCreation = this.userRepositoryTest.save(newUser);

        return userCreation;
    }
}
