package com.finance.controller;

import com.finance.dto.ApiResponse;
import com.finance.dto.dashboard.CategoryStatisticsResponse;
import com.finance.dto.dashboard.DashboardSummaryResponse;
import com.finance.entity.Transaction.TransactionType;
import com.finance.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Dashboard", description = "Dashboard and analytics endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary with statistics")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Start date for the period (defaults to first day of current month)")
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "End date for the period (defaults to today)")
            LocalDate endDate,

            Authentication authentication
    ) {
        // Default to current month if dates not provided
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        DashboardSummaryResponse summary = dashboardService.getDashboardSummary(
            authentication.getName(), startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("Dashboard summary retrieved successfully", summary));
    }

    @GetMapping("/category-statistics")
    @Operation(summary = "Get detailed category statistics")
    public ResponseEntity<ApiResponse<List<CategoryStatisticsResponse>>> getCategoryStatistics(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Start date for the period (defaults to first day of current month)")
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "End date for the period (defaults to today)")
            LocalDate endDate,

            @RequestParam(required = false)
            @Parameter(description = "Transaction type filter (INCOME or EXPENSE)")
            TransactionType type,

            Authentication authentication
    ) {
        // Default to current month if dates not provided
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<CategoryStatisticsResponse> statistics = dashboardService.getCategoryStatistics(
            authentication.getName(), startDate, endDate, type);

        return ResponseEntity.ok(ApiResponse.success("Category statistics retrieved successfully", statistics));
    }
}
