package com.finance.mapper;

import com.finance.dto.account.AccountResponse;
import com.finance.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getName(),
            account.getDescription(),
            account.getType(),
            account.getBalance(),
            account.getCurrency(),
            account.getActive(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }
}
