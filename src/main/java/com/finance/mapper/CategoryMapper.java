package com.finance.mapper;

import com.finance.dto.category.CategoryResponse;
import com.finance.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getType(),
            category.getIcon(),
            category.getColor(),
            category.getParent() != null ? category.getParent().getId() : null,
            category.getSubCategories() != null
                ? category.getSubCategories().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList())
                : List.of(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }

    public CategoryResponse toSimpleResponse(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getType(),
            category.getIcon(),
            category.getColor(),
            category.getParent() != null ? category.getParent().getId() : null,
            List.of(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
}
