package com.finance.dto.budget;

import com.finance.entity.Budget;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetRequest(
    @NotBlank(message = "Budget name is required")
    String name,

    @NotNull(message = "Amount is required")
    BigDecimal amount,

    @NotNull(message = "Budget period is required")
    Budget.BudgetPeriod period,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    LocalDate endDate,

    String description,

    @NotNull(message = "Category ID is required")
    Long categoryId,

    BigDecimal alertThreshold
) {}
