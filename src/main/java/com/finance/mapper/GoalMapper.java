package com.finance.mapper;

import com.finance.dto.goal.GoalResponse;
import com.finance.entity.Goal;
import org.springframework.stereotype.Component;

@Component
public class GoalMapper {

    public GoalResponse toResponse(Goal goal) {
        return GoalResponse.builder()
            .id(goal.getId())
            .name(goal.getName())
            .description(goal.getDescription())
            .targetAmount(goal.getTargetAmount())
            .currentAmount(goal.getCurrentAmount())
            .remainingAmount(goal.getRemainingAmount())
            .percentageCompleted(goal.getPercentageCompleted())
            .targetDate(goal.getTargetDate())
            .daysRemaining(goal.getDaysRemaining())
            .status(goal.getStatus())
            .priority(goal.getPriority())
            .icon(goal.getIcon())
            .color(goal.getColor())
            .accountId(goal.getAccount() != null ? goal.getAccount().getId() : null)
            .accountName(goal.getAccount() != null ? goal.getAccount().getName() : null)
            .suggestedMonthlySavings(goal.getSuggestedMonthlySavings())
            .isOverdue(goal.isOverdue())
            .isCompleted(goal.isCompleted())
            .createdAt(goal.getCreatedAt())
            .updatedAt(goal.getUpdatedAt())
            .build();
    }
}
