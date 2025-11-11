package com.finance.repository;

import com.finance.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);

    List<Category> findByUserIdAndType(Long userId, Category.CategoryType type);

    List<Category> findByUserIdAndParentIsNull(Long userId);

    List<Category> findByParentId(Long parentId);

    List<Category> findByUserIdAndNameContainingIgnoreCase(Long userId, String name);
}
