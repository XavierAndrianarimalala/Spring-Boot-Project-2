package com.finance.service;

import com.finance.dto.dashboard.*;
import com.finance.entity.Transaction;
import com.finance.entity.Transaction.TransactionType;
import com.finance.entity.User;
import com.finance.exception.ResourceNotFoundException;
import com.finance.repository.AccountRepository;
import com.finance.repository.TransactionRepository;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public DashboardSummaryResponse getDashboardSummary(String username, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Calculate total balance across all accounts
        BigDecimal totalBalance = accountRepository.findByUserIdAndActive(user.getId(), true)
            .stream()
            .map(account -> account.getBalance())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Get transactions for the period
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
            user.getId(), startDate, endDate);

        // Calculate income and expenses
        BigDecimal totalIncome = transactions.stream()
            .filter(t -> t.getType() == TransactionType.INCOME)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netSavings = totalIncome.subtract(totalExpense);

        // Get top expense categories
        List<CategoryStatisticsResponse> topExpenseCategories = getTopCategoriesByType(
            transactions, TransactionType.EXPENSE, totalExpense, 5);

        // Get top income categories
        List<CategoryStatisticsResponse> topIncomeCategories = getTopCategoriesByType(
            transactions, TransactionType.INCOME, totalIncome, 5);

        // Get monthly trends (last 6 months)
        List<MonthlyTrendResponse> monthlyTrends = getMonthlyTrends(user, 6);

        // Get period comparison (current vs previous period)
        PeriodComparisonResponse periodComparison = getPeriodComparison(user, startDate, endDate);

        return DashboardSummaryResponse.builder()
            .totalBalance(totalBalance)
            .totalIncome(totalIncome)
            .totalExpense(totalExpense)
            .netSavings(netSavings)
            .transactionCount(transactions.size())
            .topExpenseCategories(topExpenseCategories)
            .topIncomeCategories(topIncomeCategories)
            .monthlyTrends(monthlyTrends)
            .periodComparison(periodComparison)
            .build();
    }

    private List<CategoryStatisticsResponse> getTopCategoriesByType(
            List<Transaction> transactions,
            TransactionType type,
            BigDecimal total,
            int limit) {

        Map<Long, List<Transaction>> transactionsByCategory = transactions.stream()
            .filter(t -> t.getType() == type)
            .filter(t -> t.getCategory() != null)
            .collect(Collectors.groupingBy(t -> t.getCategory().getId()));

        return transactionsByCategory.entrySet().stream()
            .map(entry -> {
                List<Transaction> categoryTransactions = entry.getValue();
                Transaction firstTransaction = categoryTransactions.get(0);
                BigDecimal categoryTotal = categoryTransactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal percentage = total.compareTo(BigDecimal.ZERO) > 0
                    ? categoryTotal.divide(total, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;

                return CategoryStatisticsResponse.builder()
                    .categoryId(firstTransaction.getCategory().getId())
                    .categoryName(firstTransaction.getCategory().getName())
                    .categoryIcon(firstTransaction.getCategory().getIcon())
                    .categoryColor(firstTransaction.getCategory().getColor())
                    .totalAmount(categoryTotal)
                    .transactionCount(categoryTransactions.size())
                    .percentage(percentage)
                    .build();
            })
            .sorted(Comparator.comparing(CategoryStatisticsResponse::getTotalAmount).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    private List<MonthlyTrendResponse> getMonthlyTrends(User user, int months) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months - 1).withDayOfMonth(1);

        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
            user.getId(), startDate, endDate);

        Map<YearMonth, List<Transaction>> transactionsByMonth = transactions.stream()
            .collect(Collectors.groupingBy(t -> YearMonth.from(t.getTransactionDate())));

        List<MonthlyTrendResponse> trends = new ArrayList<>();
        YearMonth currentMonth = YearMonth.from(startDate);
        YearMonth lastMonth = YearMonth.from(endDate);

        BigDecimal runningBalance = BigDecimal.ZERO;

        while (!currentMonth.isAfter(lastMonth)) {
            List<Transaction> monthTransactions = transactionsByMonth.getOrDefault(currentMonth, Collections.emptyList());

            BigDecimal monthIncome = monthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal monthExpense = monthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal monthNetSavings = monthIncome.subtract(monthExpense);
            runningBalance = runningBalance.add(monthNetSavings);

            trends.add(MonthlyTrendResponse.builder()
                .month(currentMonth.getMonth().toString())
                .year(currentMonth.getYear())
                .income(monthIncome)
                .expense(monthExpense)
                .netSavings(monthNetSavings)
                .balance(runningBalance)
                .build());

            currentMonth = currentMonth.plusMonths(1);
        }

        return trends;
    }

    private PeriodComparisonResponse getPeriodComparison(User user, LocalDate currentStart, LocalDate currentEnd) {
        // Calculate previous period
        long daysBetween = currentEnd.toEpochDay() - currentStart.toEpochDay();
        LocalDate previousStart = currentStart.minusDays(daysBetween + 1);
        LocalDate previousEnd = currentStart.minusDays(1);

        // Current period transactions
        List<Transaction> currentTransactions = transactionRepository.findByUserIdAndTransactionDateBetween(
            user.getId(), currentStart, currentEnd);

        BigDecimal currentIncome = currentTransactions.stream()
            .filter(t -> t.getType() == TransactionType.INCOME)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal currentExpense = currentTransactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Previous period transactions
        List<Transaction> previousTransactions = transactionRepository.findByUserIdAndTransactionDateBetween(
            user.getId(), previousStart, previousEnd);

        BigDecimal previousIncome = previousTransactions.stream()
            .filter(t -> t.getType() == TransactionType.INCOME)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal previousExpense = previousTransactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate changes
        BigDecimal incomeChange = currentIncome.subtract(previousIncome);
        BigDecimal incomeChangePercentage = calculatePercentageChange(previousIncome, currentIncome);

        BigDecimal expenseChange = currentExpense.subtract(previousExpense);
        BigDecimal expenseChangePercentage = calculatePercentageChange(previousExpense, currentExpense);

        BigDecimal currentSavings = currentIncome.subtract(currentExpense);
        BigDecimal previousSavings = previousIncome.subtract(previousExpense);
        BigDecimal savingsChange = currentSavings.subtract(previousSavings);
        BigDecimal savingsChangePercentage = calculatePercentageChange(previousSavings, currentSavings);

        return PeriodComparisonResponse.builder()
            .currentPeriodIncome(currentIncome)
            .previousPeriodIncome(previousIncome)
            .incomeChange(incomeChange)
            .incomeChangePercentage(incomeChangePercentage)
            .currentPeriodExpense(currentExpense)
            .previousPeriodExpense(previousExpense)
            .expenseChange(expenseChange)
            .expenseChangePercentage(expenseChangePercentage)
            .currentPeriodSavings(currentSavings)
            .previousPeriodSavings(previousSavings)
            .savingsChange(savingsChange)
            .savingsChangePercentage(savingsChangePercentage)
            .build();
    }

    private BigDecimal calculatePercentageChange(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            return newValue.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(100);
        }
        return newValue.subtract(oldValue)
            .divide(oldValue, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    public List<CategoryStatisticsResponse> getCategoryStatistics(
            String username,
            LocalDate startDate,
            LocalDate endDate,
            TransactionType type) {

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
            user.getId(), startDate, endDate);

        BigDecimal total = transactions.stream()
            .filter(t -> type == null || t.getType() == type)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return getTopCategoriesByType(transactions, type, total, Integer.MAX_VALUE);
    }
}
