package com.finance.mapper;

import com.finance.dto.transaction.TransactionResponse;
import com.finance.entity.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final AccountMapper accountMapper;
    private final CategoryMapper categoryMapper;

    public TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getType(),
            transaction.getTransactionDate(),
            transaction.getDescription(),
            transaction.getPayee(),
            transaction.getReference(),
            transaction.getNotes(),
            accountMapper.toResponse(transaction.getAccount()),
            categoryMapper.toSimpleResponse(transaction.getCategory()),
            transaction.getTransferAccount() != null ?
                accountMapper.toResponse(transaction.getTransferAccount()) : null,
            transaction.getReconciled(),
            transaction.getCreatedAt(),
            transaction.getUpdatedAt()
        );
    }
}
