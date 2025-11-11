package com.finance.dto.budget;

import com.finance.dto.category.CategoryResponse;
import com.finance.entity.Budget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BudgetResponse(
    Long id,
    String name,
    BigDecimal amount,
    BigDecimal spent,
    BigDecimal remaining,
    BigDecimal percentageUsed,
    Budget.BudgetPeriod period,
    LocalDate startDate,
    LocalDate endDate,
    String description,
    CategoryResponse category,
    Boolean active,
    BigDecimal alertThreshold,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
