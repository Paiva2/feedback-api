package com.app.productfeedback.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.productfeedback.dto.request.category.NewCategoryDto;
import com.app.productfeedback.entities.Category;
import com.app.productfeedback.services.category.CategoryService;
import com.app.productfeedback.services.jwt.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private JwtService jwtService;

    @PostMapping
    public ResponseEntity<Category> newCategory(@RequestHeader("Authorization") String authToken,
            @RequestBody @Valid NewCategoryDto newCategoryDto) {
        String userId = this.jwtService.verify(authToken.replaceAll("Bearer", ""));

        Category categoryCreated =
                this.categoryService.create(newCategoryDto.toEntity(), UUID.fromString(userId));

        return ResponseEntity.status(201).body(categoryCreated);
    }
}
