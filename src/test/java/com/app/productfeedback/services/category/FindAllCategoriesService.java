package com.app.productfeedback.services.category;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.repositories.CategoryRepositoryTest;
import com.app.productfeedback.repositories.UserRepositoryTest;

@ActiveProfiles("test")
public class FindAllCategoriesService {
    private CategoryRepositoryTest categoryRepositoryTest;

    private UserRepositoryTest userRepositoryTest;

    private CategoryService sut;

    @BeforeEach
    public void setup() {
        categoryRepositoryTest = new CategoryRepositoryTest();
        userRepositoryTest = new UserRepositoryTest();

        sut = new CategoryService(categoryRepositoryTest, userRepositoryTest);
    }

    @Test
    @DisplayName("it should return all categories paginated")
    public void caseOne() {
        this.categoriesGenerator(23);

        Page<Category> categories = this.sut.listAll(3, 10);

        Category firstItem = categories.getContent().get(0);
        Category secondtItem = categories.getContent().get(1);
        Category thirdItem = categories.getContent().get(2);

        Assertions.assertEquals(categories.getTotalElements(), 3);
        Assertions.assertAll("Assert pagination return of page 3",
                () -> Assertions.assertEquals(firstItem.getName(), "Category num 21"),
                () -> Assertions.assertEquals(secondtItem.getName(), "Category num 22"),
                () -> Assertions.assertEquals(thirdItem.getName(), "Category num 23"));
    }

    @Test
    @DisplayName("it should return page 1 if provided page is less than 1")
    public void caseTwo() {
        this.categoriesGenerator(20);

        Page<Category> categories = this.sut.listAll(0, 10);

        Category firstItem = categories.getContent().get(0);
        Category secondtItem = categories.getContent().get(1);
        Category thirdItem = categories.getContent().get(2);

        Assertions.assertEquals(categories.getTotalElements(), 10);

        Assertions.assertAll("Assert pagination return of page 1",
                () -> Assertions.assertEquals(firstItem.getName(), "Category num 1"),
                () -> Assertions.assertEquals(secondtItem.getName(), "Category num 2"),
                () -> Assertions.assertEquals(thirdItem.getName(), "Category num 3"));
    }

    @Test
    @DisplayName("it should return 5 itens per page if provided itensPerPage is less than 5")
    public void caseThree() {
        this.categoriesGenerator(20);

        Page<Category> categories = this.sut.listAll(1, 4);

        Assertions.assertEquals(categories.getTotalElements(), 5);
    }

    public void categoriesGenerator(int quantityToCreate) {
        User user = new User();

        user.setEmail("johndoe@test.com");
        user.setUsername("John Doe");
        user.setRole(UserRole.ADMIN);

        User userCreation = this.userRepositoryTest.save(user);

        for (int i = 1; i <= quantityToCreate; i++) {
            Category category = new Category();
            category.setName("Category num " + i);

            this.sut.create(category, userCreation.getId());
        }
    }
}
