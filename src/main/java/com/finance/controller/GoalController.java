package com.finance.controller;

import com.finance.dto.ApiResponse;
import com.finance.dto.goal.GoalRequest;
import com.finance.dto.goal.GoalResponse;
import com.finance.entity.Goal.GoalStatus;
import com.finance.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Goals", description = "Savings goal management endpoints")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @Operation(summary = "Create a new savings goal")
    public ResponseEntity<ApiResponse<GoalResponse>> createGoal(
            @Valid @RequestBody GoalRequest request,
            Authentication authentication
    ) {
        GoalResponse goal = goalService.createGoal(request, authentication.getName());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Goal created successfully", goal));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by ID")
    public ResponseEntity<ApiResponse<GoalResponse>> getGoal(
            @PathVariable Long id,
            Authentication authentication
    ) {
        GoalResponse goal = goalService.getGoalById(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Goal retrieved successfully", goal));
    }

    @GetMapping
    @Operation(summary = "Get all goals")
    public ResponseEntity<ApiResponse<List<GoalResponse>>> getAllGoals(
            Authentication authentication
    ) {
        List<GoalResponse> goals = goalService.getAllGoals(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Goals retrieved successfully", goals));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get goals by status")
    public ResponseEntity<ApiResponse<List<GoalResponse>>> getGoalsByStatus(
            @PathVariable
            @Parameter(description = "Goal status (IN_PROGRESS, COMPLETED, ABANDONED, PAUSED)")
            GoalStatus status,
            Authentication authentication
    ) {
        List<GoalResponse> goals = goalService.getGoalsByStatus(authentication.getName(), status);
        return ResponseEntity.ok(ApiResponse.success("Goals retrieved successfully", goals));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active goals ordered by priority")
    public ResponseEntity<ApiResponse<List<GoalResponse>>> getActiveGoals(
            Authentication authentication
    ) {
        List<GoalResponse> goals = goalService.getActiveGoals(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Active goals retrieved successfully", goals));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue goals")
    public ResponseEntity<ApiResponse<List<GoalResponse>>> getOverdueGoals(
            Authentication authentication
    ) {
        List<GoalResponse> goals = goalService.getOverdueGoals(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Overdue goals retrieved successfully", goals));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goal")
    public ResponseEntity<ApiResponse<GoalResponse>> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody GoalRequest request,
            Authentication authentication
    ) {
        GoalResponse goal = goalService.updateGoal(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Goal updated successfully", goal));
    }

    @PatchMapping("/{id}/progress")
    @Operation(summary = "Update goal progress (set current amount)")
    public ResponseEntity<ApiResponse<GoalResponse>> updateProgress(
            @PathVariable Long id,
            @RequestParam
            @Parameter(description = "New current amount")
            BigDecimal amount,
            Authentication authentication
    ) {
        GoalResponse goal = goalService.updateProgress(id, amount, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Goal progress updated successfully", goal));
    }

    @PatchMapping("/{id}/add-progress")
    @Operation(summary = "Add to goal progress (increment current amount)")
    public ResponseEntity<ApiResponse<GoalResponse>> addProgress(
            @PathVariable Long id,
            @RequestParam
            @Parameter(description = "Amount to add to current progress")
            BigDecimal amount,
            Authentication authentication
    ) {
        GoalResponse goal = goalService.addProgress(id, amount, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Goal progress added successfully", goal));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update goal status")
    public ResponseEntity<ApiResponse<GoalResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam
            @Parameter(description = "New status (IN_PROGRESS, COMPLETED, ABANDONED, PAUSED)")
            GoalStatus status,
            Authentication authentication
    ) {
        GoalResponse goal = goalService.updateStatus(id, status, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Goal status updated successfully", goal));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete goal")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(
            @PathVariable Long id,
            Authentication authentication
    ) {
        goalService.deleteGoal(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Goal deleted successfully", null));
    }
}
