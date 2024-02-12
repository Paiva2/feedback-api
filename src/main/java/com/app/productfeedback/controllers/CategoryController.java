package com.app.productfeedback.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.productfeedback.dto.request.category.NewCategoryDto;
import com.app.productfeedback.dto.response.category.ListAllCategoriesDto;
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

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Object> deleteCategory(
            @PathVariable(name = "categoryId", required = true) UUID categoryId,
            @RequestHeader("Authorization") String authToken) {
        String userId = this.jwtService.verify(authToken.replaceAll("Bearer ", ""));

        this.categoryService.delete(categoryId, UUID.fromString(userId));

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<ListAllCategoriesDto> listAllCategories(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "5") int perPage) {
        Page<Category> categoriesList = this.categoryService.listAll(page, perPage);

        Pageable listInformations = categoriesList.getPageable();
        int currentPage = listInformations.getPageNumber() + 1;
        int currentItensPerPage = listInformations.getPageSize();

        ListAllCategoriesDto listAllCategoriesDto = new ListAllCategoriesDto(currentPage,
                listInformations.getPageSize(), categoriesList.getContent(), currentItensPerPage);

        return ResponseEntity.ok().body(listAllCategoriesDto);
    }
}
