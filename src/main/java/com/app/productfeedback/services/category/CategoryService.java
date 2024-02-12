package com.app.productfeedback.services.category;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.app.productfeedback.entities.Category;
import com.app.productfeedback.entities.User;
import com.app.productfeedback.enums.UserRole;
import com.app.productfeedback.exceptions.BadRequestException;
import com.app.productfeedback.exceptions.ConflictException;
import com.app.productfeedback.exceptions.NotFoundException;
import com.app.productfeedback.exceptions.UnauthorizedException;
import com.app.productfeedback.interfaces.CategoryRepository;
import com.app.productfeedback.interfaces.UserRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepositoryInterface;

    private final UserRepository userRepositoryInterface;

    public CategoryService(CategoryRepository categoryRepositoryInterface,
            UserRepository userRepositoryInterface) {
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

    public void delete(UUID categoryId, UUID userId) {
        if (categoryId == null) {
            throw new BadRequestException("Invalid category id.");
        }

        if (userId == null) {
            throw new BadRequestException("Invalid user id.");
        }

        Optional<User> doesUserExists = this.userRepositoryInterface.findById(userId);

        if (doesUserExists.isEmpty()) {
            throw new NotFoundException("User not found.");
        }

        if (!doesUserExists.get().getRole().equals(UserRole.ADMIN)) {
            throw new UnauthorizedException("Only admins can manage categories.");
        }

        Optional<Category> doesCategoryExists =
                this.categoryRepositoryInterface.findById(categoryId);

        System.out.println(doesCategoryExists);

        if (doesCategoryExists.isEmpty()) {
            throw new NotFoundException("Category not found.");
        }

        this.categoryRepositoryInterface.deleteById(categoryId);
    }

    public Page<Category> listAll(int pageNumber, int perPage) {
        if (pageNumber < 1) {
            pageNumber = 1;
        }

        if (perPage < 5) {
            perPage = 5;
        }

        Pageable pageable = PageRequest.of((pageNumber - 1), perPage);

        return this.categoryRepositoryInterface.findAll(pageable);
    }
}
