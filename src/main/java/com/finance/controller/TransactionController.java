package com.finance.controller;

import com.finance.dto.ApiResponse;
import com.finance.dto.transaction.TransactionRequest;
import com.finance.dto.transaction.TransactionResponse;
import com.finance.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Transactions", description = "Transaction management endpoints")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Create a new transaction")
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication
    ) {
        TransactionResponse transaction = transactionService.createTransaction(request, authentication.getName());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Transaction created successfully", transaction));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(
            @PathVariable Long id,
            Authentication authentication
    ) {
        TransactionResponse transaction = transactionService.getTransactionById(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", transaction));
    }

    @GetMapping
    @Operation(summary = "Get all transactions (paginated)")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getAllTransactions(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<TransactionResponse> transactions = transactionService.getAllTransactions(authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get transactions by account (paginated)")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactionsByAccount(
            @PathVariable Long accountId,
            Authentication authentication,
            @PageableDefault(size = 20, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<TransactionResponse> transactions = transactionService.getTransactionsByAccount(
            accountId, authentication.getName(), pageable
        );
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get transactions by date range")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication
    ) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByDateRange(
            authentication.getName(), startDate, endDate
        );
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }

    @GetMapping("/search")
    @Operation(summary = "Search transactions by keyword")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> searchTransactions(
            @RequestParam String keyword,
            Authentication authentication,
            @PageableDefault(size = 20, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<TransactionResponse> transactions = transactionService.searchTransactions(
            authentication.getName(), keyword, pageable
        );
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a transaction")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request,
            Authentication authentication
    ) {
        TransactionResponse transaction = transactionService.updateTransaction(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Transaction updated successfully", transaction));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a transaction")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(
            @PathVariable Long id,
            Authentication authentication
    ) {
        transactionService.deleteTransaction(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
    }
}
