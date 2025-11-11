package com.finance.controller;

import com.finance.dto.ApiResponse;
import com.finance.dto.account.AccountRequest;
import com.finance.dto.account.AccountResponse;
import com.finance.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Accounts", description = "Account management endpoints")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create a new account")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody AccountRequest request,
            Authentication authentication
    ) {
        AccountResponse account = accountService.createAccount(request, authentication.getName());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Account created successfully", account));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
            @PathVariable Long id,
            Authentication authentication
    ) {
        AccountResponse account = accountService.getAccountById(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", account));
    }

    @GetMapping
    @Operation(summary = "Get all accounts")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAllAccounts(Authentication authentication) {
        List<AccountResponse> accounts = accountService.getAllAccounts(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", accounts));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active accounts")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getActiveAccounts(Authentication authentication) {
        List<AccountResponse> accounts = accountService.getActiveAccounts(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Active accounts retrieved successfully", accounts));
    }

    @GetMapping("/total-balance")
    @Operation(summary = "Get total balance across all accounts")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalBalance(Authentication authentication) {
        BigDecimal totalBalance = accountService.getTotalBalance(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Total balance calculated successfully", totalBalance));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an account")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequest request,
            Authentication authentication
    ) {
        AccountResponse account = accountService.updateAccount(id, request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Account updated successfully", account));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @PathVariable Long id,
            Authentication authentication
    ) {
        accountService.deleteAccount(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle account active status")
    public ResponseEntity<ApiResponse<AccountResponse>> toggleAccountStatus(
            @PathVariable Long id,
            Authentication authentication
    ) {
        AccountResponse account = accountService.toggleAccountStatus(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Account status toggled successfully", account));
    }
}
