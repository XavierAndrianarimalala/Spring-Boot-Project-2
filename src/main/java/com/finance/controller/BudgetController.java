package com.finance.controller;

import com.finance.dto.ApiResponse;
import com.finance.dto.budget.BudgetRequest;
import com.finance.dto.budget.BudgetResponse;
import com.finance.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Budgets", description = "Budget management endpoints")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @Operation(summary = "Create a new budget")
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication
    ) {
        BudgetResponse budget = budgetService.createBudget(request, authentication.getName());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Budget created successfully", budget));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudget(
            @PathVariable Long id,
            Authentication authentication
    ) {
        BudgetResponse budget = budgetService.getBudgetById(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Budget retrieved successfully", budget));
    }

    @GetMapping
    @Operation(summary = "Get all budgets")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getAllBudgets(Authentication authentication) {
        List<BudgetResponse> budgets = budgetService.getAllBudgets(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Budgets retrieved successfully", budgets));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active budgets")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getActiveBudgets(Authentication authentication) {
        List<BudgetResponse> budgets = budgetService.getActiveBudgets(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Active budgets retrieved successfully", budgets));
    }

    @GetMapping("/current")
    @Operation(summary = "Get current budgets (active in the current period)")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getCurrentBudgets(Authentication authentication) {
        List<BudgetResponse> budgets = budgetService.getCurrentBudgets(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Current budgets retrieved successfully", budgets));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a budget")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication
    ) {
        BudgetResponse budget = budgetService.updateBudget(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", budget));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @PathVariable Long id,
            Authentication authentication
    ) {
        budgetService.deleteBudget(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully", null));
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle budget active status")
    public ResponseEntity<ApiResponse<BudgetResponse>> toggleBudgetStatus(
            @PathVariable Long id,
            Authentication authentication
    ) {
        BudgetResponse budget = budgetService.toggleBudgetStatus(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Budget status toggled successfully", budget));
    }
}
