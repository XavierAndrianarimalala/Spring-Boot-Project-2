package com.finance.dto.transaction;

import com.finance.dto.account.AccountResponse;
import com.finance.dto.category.CategoryResponse;
import com.finance.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
    Long id,
    BigDecimal amount,
    Transaction.TransactionType type,
    LocalDate transactionDate,
    String description,
    String payee,
    String reference,
    String notes,
    AccountResponse account,
    CategoryResponse category,
    AccountResponse transferAccount,
    Boolean reconciled,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
