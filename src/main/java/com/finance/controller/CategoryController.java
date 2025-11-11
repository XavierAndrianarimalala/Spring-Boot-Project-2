package com.finance.controller;

import com.finance.dto.ApiResponse;
import com.finance.dto.category.CategoryRequest;
import com.finance.dto.category.CategoryResponse;
import com.finance.entity.Category;
import com.finance.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request,
            Authentication authentication
    ) {
        CategoryResponse category = categoryService.createCategory(request, authentication.getName());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Category created successfully", category));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(
            @PathVariable Long id,
            Authentication authentication
    ) {
        CategoryResponse category = categoryService.getCategoryById(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(Authentication authentication) {
        List<CategoryResponse> categories = categoryService.getAllCategories(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }

    @GetMapping("/root")
    @Operation(summary = "Get root categories (with subcategories)")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories(Authentication authentication) {
        List<CategoryResponse> categories = categoryService.getRootCategories(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Root categories retrieved successfully", categories));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get categories by type (INCOME or EXPENSE)")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoriesByType(
            @PathVariable Category.CategoryType type,
            Authentication authentication
    ) {
        List<CategoryResponse> categories = categoryService.getCategoriesByType(type, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            Authentication authentication
    ) {
        CategoryResponse category = categoryService.updateCategory(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long id,
            Authentication authentication
    ) {
        categoryService.deleteCategory(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
