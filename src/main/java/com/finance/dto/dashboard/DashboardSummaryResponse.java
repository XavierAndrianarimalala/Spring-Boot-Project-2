package com.finance.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private BigDecimal totalBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;
    private Integer transactionCount;
    private List<CategoryStatisticsResponse> topExpenseCategories;
    private List<CategoryStatisticsResponse> topIncomeCategories;
    private List<MonthlyTrendResponse> monthlyTrends;
    private PeriodComparisonResponse periodComparison;
}
