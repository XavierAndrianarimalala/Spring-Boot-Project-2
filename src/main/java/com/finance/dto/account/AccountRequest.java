package com.finance.dto.account;

import com.finance.entity.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountRequest(
    @NotBlank(message = "Account name is required")
    String name,

    String description,

    @NotNull(message = "Account type is required")
    Account.AccountType type,

    @NotNull(message = "Initial balance is required")
    BigDecimal balance,

    String currency
) {}
