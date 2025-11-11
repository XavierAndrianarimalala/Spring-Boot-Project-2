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
public class MonthlyTrendResponse {
    private String month;
    private Integer year;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal netSavings;
    private BigDecimal balance;
}
