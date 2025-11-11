package com.finance.service;

import com.finance.dto.budget.BudgetRequest;
import com.finance.dto.budget.BudgetResponse;
import com.finance.entity.Budget;
import com.finance.entity.Category;
import com.finance.entity.User;
import com.finance.exception.ResourceNotFoundException;
import com.finance.exception.UnauthorizedException;
import com.finance.mapper.BudgetMapper;
import com.finance.repository.BudgetRepository;
import com.finance.repository.CategoryRepository;
import com.finance.repository.TransactionRepository;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetMapper budgetMapper;

    @Transactional
    public BudgetResponse createBudget(BudgetRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.categoryId()));
        validateCategoryOwnership(category, username);

        Budget budget = Budget.builder()
            .name(request.name())
            .amount(request.amount())
            .spent(BigDecimal.ZERO)
            .period(request.period())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .description(request.description())
            .category(category)
            .user(user)
            .active(true)
            .alertThreshold(request.alertThreshold() != null ? request.alertThreshold() : new BigDecimal("80.00"))
            .build();

        // Calculate spent amount
        updateBudgetSpent(budget);

        Budget saved = budgetRepository.save(budget);
        return budgetMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(Long id, String username) {
        Budget budget = budgetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", id));

        validateBudgetOwnership(budget, username);
        return budgetMapper.toResponse(budget);
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgets(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return budgetRepository.findByUserId(user.getId())
            .stream()
            .map(budgetMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getActiveBudgets(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return budgetRepository.findByUserIdAndActive(user.getId(), true)
            .stream()
            .map(budgetMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getCurrentBudgets(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return budgetRepository.findActiveBudgetsByUserIdAndDate(user.getId(), LocalDate.now())
            .stream()
            .map(budgetMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public BudgetResponse updateBudget(Long id, BudgetRequest request, String username) {
        Budget budget = budgetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", id));

        validateBudgetOwnership(budget, username);

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.categoryId()));
        validateCategoryOwnership(category, username);

        budget.setName(request.name());
        budget.setAmount(request.amount());
        budget.setPeriod(request.period());
        budget.setStartDate(request.startDate());
        budget.setEndDate(request.endDate());
        budget.setDescription(request.description());
        budget.setCategory(category);

        if (request.alertThreshold() != null) {
            budget.setAlertThreshold(request.alertThreshold());
        }

        // Recalculate spent amount
        updateBudgetSpent(budget);

        Budget updated = budgetRepository.save(budget);
        return budgetMapper.toResponse(updated);
    }

    @Transactional
    public void deleteBudget(Long id, String username) {
        Budget budget = budgetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", id));

        validateBudgetOwnership(budget, username);
        budgetRepository.delete(budget);
    }

    @Transactional
    public BudgetResponse toggleBudgetStatus(Long id, String username) {
        Budget budget = budgetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", id));

        validateBudgetOwnership(budget, username);
        budget.setActive(!budget.getActive());

        Budget updated = budgetRepository.save(budget);
        return budgetMapper.toResponse(updated);
    }

    @Transactional
    public void updateBudgetSpent(Budget budget) {
        BigDecimal spent = transactionRepository.sumAmountByCategoryIdAndDateRange(
            budget.getCategory().getId(),
            budget.getStartDate(),
            budget.getEndDate()
        );

        budget.setSpent(spent != null ? spent : BigDecimal.ZERO);
    }

    private void validateBudgetOwnership(Budget budget, String username) {
        if (!budget.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to access this budget");
        }
    }

    private void validateCategoryOwnership(Category category, String username) {
        if (!category.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to access this category");
        }
    }
}
