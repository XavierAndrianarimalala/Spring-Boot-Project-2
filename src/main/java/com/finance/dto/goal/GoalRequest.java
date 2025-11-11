package com.finance.dto.goal;

import com.finance.entity.Goal.GoalPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalRequest(
    @NotBlank(message = "Goal name is required")
    String name,

    String description,

    @NotNull(message = "Target amount is required")
    @Positive(message = "Target amount must be positive")
    BigDecimal targetAmount,

    BigDecimal currentAmount,

    @NotNull(message = "Target date is required")
    LocalDate targetDate,

    GoalPriority priority,

    String icon,

    String color,

    Long accountId
) {
}
