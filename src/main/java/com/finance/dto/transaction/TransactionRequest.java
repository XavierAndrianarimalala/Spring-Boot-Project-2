package com.finance.dto.transaction;

import com.finance.entity.Transaction;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
    @NotNull(message = "Amount is required")
    BigDecimal amount,

    @NotNull(message = "Transaction type is required")
    Transaction.TransactionType type,

    @NotNull(message = "Transaction date is required")
    LocalDate transactionDate,

    String description,
    String payee,
    String reference,
    String notes,

    @NotNull(message = "Account ID is required")
    Long accountId,

    @NotNull(message = "Category ID is required")
    Long categoryId,

    Long transferAccountId,
    Boolean reconciled
) {}
