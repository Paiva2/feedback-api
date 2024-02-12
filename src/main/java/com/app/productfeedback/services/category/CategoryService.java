package com.app.productfeedback.services.category;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ConflictException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.exceptions.UnauthorizedException;
import com.app.productfeedback.interfaces.CategoryRepositoryInterface;
import com.app.productfeedback.interfaces.UserRepositoryInterface;

@Service
public class CategoryService {
    private CategoryRepositoryInterface categoryRepositoryInterface;

    private UserRepositoryInterface userRepositoryInterface;

    public CategoryService(CategoryRepositoryInterface categoryRepositoryInterface,
            UserRepositoryInterface userRepositoryInterface) {
        this.categoryRepositoryInterface = categoryRepositoryInterface;
        this.userRepositoryInterface = userRepositoryInterface;
    }

    public Category create(Category category, UUID userId) {
        if (category == null) {
            throw new BadRequestException("Category can't be null.");
        }

        if (userId == null) {
            throw new BadRequestException("User id can't be null.");
        }

        if (category.getName() == null) {
            throw new BadRequestException("Category name can't be null.");
        }

        Optional<User> doesUserExists = this.userRepositoryInterface.findById(userId);

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        if (doesUserExists.get().getRole().equals(UserRole.USER)) {
            throw new UnauthorizedException("Only admins can create categories.");
        }

        Optional<Category> doesCategoryExists =
                this.categoryRepositoryInterface.findByName(category.getName());

        if (doesCategoryExists.isPresent()) {
            throw new ConflictException("Category already exists.");
        }

        return this.categoryRepositoryInterface.save(category);
    }
}
