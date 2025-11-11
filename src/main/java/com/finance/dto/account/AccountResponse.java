package com.finance.dto.account;

import com.finance.entity.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
    Long id,
    String name,
    String description,
    Account.AccountType type,
    BigDecimal balance,
    String currency,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
