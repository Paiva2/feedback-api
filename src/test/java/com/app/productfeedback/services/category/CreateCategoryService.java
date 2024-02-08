package com.app.productfeedback.services.category;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ConflictException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.exceptions.UnauthorizedException;
import com.app.productfeedback.repositories.CategoryRepositoryTest;
import com.app.productfeedback.repositories.UserRepositoryTest;

@ActiveProfiles("test")
public class CreateCategoryService {
    private CategoryRepositoryTest categoryRepositoryTest;

    private UserRepositoryTest userRepositoryTest;

    private CategoryService categoryService;

    @BeforeEach
    public void setup() {
        this.categoryRepositoryTest = new CategoryRepositoryTest();
        this.userRepositoryTest = new UserRepositoryTest();

        this.categoryService = new CategoryService(categoryRepositoryTest, userRepositoryTest);
    }

    @Test
    @DisplayName("it should create a new category")
    public void caseOne() {
        User user = this.createUser();
        Category categoryDto = new Category();

        categoryDto.setName("Feature");

        Category categoryCreated = this.categoryService.create(categoryDto, user.getId());

        Assertions.assertNotNull(categoryCreated);
        Assertions.assertNotNull(categoryCreated.getId());
        Assertions.assertEquals(categoryCreated.getName(), "Feature");
    }

    @Test
    @DisplayName("it should not create a new category without correctly provided params")
    public void caseTwo() {
        User user = this.createUser();
        Category categoryDto = new Category();

        Exception thrownCategoryNull = Assertions.assertThrows(BadRequestException.class, () -> {
            this.categoryService.create(null, user.getId());
        });

        Exception thrownUserIdNull = Assertions.assertThrows(BadRequestException.class, () -> {
            this.categoryService.create(categoryDto, null);
        });

        Exception thrownCategoryNameBlank =
                Assertions.assertThrows(BadRequestException.class, () -> {
                    categoryDto.setName(null);

                    this.categoryService.create(categoryDto, user.getId());
                });

        Assertions.assertEquals(thrownCategoryNull.getMessage(), "Category can't be null.");
        Assertions.assertEquals(thrownUserIdNull.getMessage(), "User id can't be null.");
        Assertions.assertEquals(thrownCategoryNameBlank.getMessage(),
                "Category name can't be null.");
    }

    @Test
    @DisplayName("it should not create a new category if user doesn't exists")
    public void caseThree() {
        Category categoryDto = new Category();
        categoryDto.setName("Feature");

        Exception thrownEx = Assertions.assertThrows(NotFoundException.class, () -> {
            this.categoryService.create(categoryDto, UUID.randomUUID());
        });

        Assertions.assertEquals(thrownEx.getMessage(), "User not found.");
    }

    @Test
    @DisplayName("it should not create a new category if user doesn't have permissions")
    public void caseFour() {
        User user = this.createUser();

        Category categoryDto = new Category();
        categoryDto.setName("Feature");

        Exception thrownEx = Assertions.assertThrows(UnauthorizedException.class, () -> {
            user.setRole(UserRole.USER);

            this.categoryService.create(categoryDto, user.getId());
        });

        Assertions.assertEquals(thrownEx.getMessage(), "Only admins can create categories.");
    }

    @Test
    @DisplayName("it should not create a new category if category name already exists")
    public void caseFive() {
        User user = this.createUser();

        Category categoryDto = new Category();

        categoryDto.setName("Feature");
        this.categoryService.create(categoryDto, user.getId());

        Exception thrownEx = Assertions.assertThrows(ConflictException.class, () -> {
            this.categoryService.create(categoryDto, user.getId());
        });

        Assertions.assertEquals(thrownEx.getMessage(), "Category already exists.");
    }

    public User createUser() {
        User user = new User();
        user.setEmail("johndoe@email.com");
        user.setPassword("123456");
        user.setRole(UserRole.ADMIN);
        user.setUsername("John Doe");

        User creation = this.userRepositoryTest.save(user);

        return creation;
    }

}
