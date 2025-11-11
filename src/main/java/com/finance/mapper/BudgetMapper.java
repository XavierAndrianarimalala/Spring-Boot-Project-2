package com.finance.mapper;

import com.finance.dto.budget.BudgetResponse;
import com.finance.entity.Budget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BudgetMapper {

    private final CategoryMapper categoryMapper;

    public BudgetResponse toResponse(Budget budget) {
        return new BudgetResponse(
            budget.getId(),
            budget.getName(),
            budget.getAmount(),
            budget.getSpent(),
            budget.getRemainingAmount(),
            budget.getPercentageUsed(),
            budget.getPeriod(),
            budget.getStartDate(),
            budget.getEndDate(),
            budget.getDescription(),
            categoryMapper.toSimpleResponse(budget.getCategory()),
            budget.getActive(),
            budget.getAlertThreshold(),
            budget.getCreatedAt(),
            budget.getUpdatedAt()
        );
    }
}
