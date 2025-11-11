package com.finance.dto.goal;

import com.finance.entity.Goal.GoalPriority;
import com.finance.entity.Goal.GoalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private BigDecimal remainingAmount;
    private BigDecimal percentageCompleted;
    private LocalDate targetDate;
    private Long daysRemaining;
    private GoalStatus status;
    private GoalPriority priority;
    private String icon;
    private String color;
    private Long accountId;
    private String accountName;
    private BigDecimal suggestedMonthlySavings;
    private Boolean isOverdue;
    private Boolean isCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
