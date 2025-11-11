package com.finance.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodComparisonResponse {
    private BigDecimal currentPeriodIncome;
    private BigDecimal previousPeriodIncome;
    private BigDecimal incomeChange;
    private BigDecimal incomeChangePercentage;

    private BigDecimal currentPeriodExpense;
    private BigDecimal previousPeriodExpense;
    private BigDecimal expenseChange;
    private BigDecimal expenseChangePercentage;

    private BigDecimal currentPeriodSavings;
    private BigDecimal previousPeriodSavings;
    private BigDecimal savingsChange;
    private BigDecimal savingsChangePercentage;
}
