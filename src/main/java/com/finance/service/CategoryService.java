package com.finance.service;

import com.finance.dto.category.CategoryRequest;
import com.finance.dto.category.CategoryResponse;
import com.finance.entity.Category;
import com.finance.entity.User;
import com.finance.exception.ResourceNotFoundException;
import com.finance.exception.UnauthorizedException;
import com.finance.mapper.CategoryMapper;
import com.finance.repository.CategoryRepository;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Category category = Category.builder()
            .name(request.name())
            .description(request.description())
            .type(request.type())
            .icon(request.icon())
            .color(request.color())
            .user(user)
            .build();

        if (request.parentId() != null) {
            Category parent = categoryRepository.findById(request.parentId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.parentId()));
            validateCategoryOwnership(parent, username);
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id, String username) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        validateCategoryOwnership(category, username);
        return categoryMapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return categoryRepository.findByUserId(user.getId())
            .stream()
            .map(categoryMapper::toSimpleResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getRootCategories(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return categoryRepository.findByUserIdAndParentIsNull(user.getId())
            .stream()
            .map(categoryMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByType(Category.CategoryType type, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return categoryRepository.findByUserIdAndType(user.getId(), type)
            .stream()
            .map(categoryMapper::toSimpleResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request, String username) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        validateCategoryOwnership(category, username);

        category.setName(request.name());
        category.setDescription(request.description());
        category.setType(request.type());
        category.setIcon(request.icon());
        category.setColor(request.color());

        if (request.parentId() != null) {
            Category parent = categoryRepository.findById(request.parentId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.parentId()));
            validateCategoryOwnership(parent, username);
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        Category updated = categoryRepository.save(category);
        return categoryMapper.toResponse(updated);
    }

    @Transactional
    public void deleteCategory(Long id, String username) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        validateCategoryOwnership(category, username);
        categoryRepository.delete(category);
    }

    private void validateCategoryOwnership(Category category, String username) {
        if (!category.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to access this category");
        }
    }
}
