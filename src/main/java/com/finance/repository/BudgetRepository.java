package com.finance.repository;

import com.finance.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserId(Long userId);

    List<Budget> findByUserIdAndActive(Long userId, Boolean active);

    List<Budget> findByCategoryId(Long categoryId);

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND b.startDate <= :date AND b.endDate >= :date")
    List<Budget> findActiveBudgetsByUserIdAndDate(
        @Param("userId") Long userId,
        @Param("date") LocalDate date
    );

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND b.category.id = :categoryId " +
           "AND b.startDate <= :date AND b.endDate >= :date " +
           "AND b.active = true")
    List<Budget> findByUserIdAndCategoryIdAndDate(
        @Param("userId") Long userId,
        @Param("categoryId") Long categoryId,
        @Param("date") LocalDate date
    );
}
