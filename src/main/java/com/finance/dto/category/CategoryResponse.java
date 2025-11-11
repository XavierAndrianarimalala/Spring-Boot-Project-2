package com.finance.dto.category;

import com.finance.entity.Category;

import java.time.LocalDateTime;
import java.util.List;

public record CategoryResponse(
    Long id,
    String name,
    String description,
    Category.CategoryType type,
    String icon,
    String color,
    Long parentId,
    List<CategoryResponse> subCategories,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
