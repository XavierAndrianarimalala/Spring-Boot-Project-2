package com.finance.dto.category;

import com.finance.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
    @NotBlank(message = "Category name is required")
    String name,

    String description,

    @NotNull(message = "Category type is required")
    Category.CategoryType type,

    String icon,
    String color,
    Long parentId
) {}
