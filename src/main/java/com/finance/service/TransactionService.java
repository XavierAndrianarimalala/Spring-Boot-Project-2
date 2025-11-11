package com.finance.service;

import com.finance.dto.transaction.TransactionRequest;
import com.finance.dto.transaction.TransactionResponse;
import com.finance.entity.Account;
import com.finance.entity.Category;
import com.finance.entity.Transaction;
import com.finance.entity.User;
import com.finance.exception.ResourceNotFoundException;
import com.finance.exception.UnauthorizedException;
import com.finance.mapper.TransactionMapper;
import com.finance.repository.AccountRepository;
import com.finance.repository.CategoryRepository;
import com.finance.repository.TransactionRepository;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Account account = accountRepository.findById(request.accountId())
            .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.accountId()));
        validateAccountOwnership(account, username);

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.categoryId()));
        validateCategoryOwnership(category, username);

        Transaction transaction = Transaction.builder()
            .amount(request.amount())
            .type(request.type())
            .transactionDate(request.transactionDate())
            .description(request.description())
            .payee(request.payee())
            .reference(request.reference())
            .notes(request.notes())
            .account(account)
            .category(category)
            .user(user)
            .reconciled(request.reconciled() != null ? request.reconciled() : false)
            .build();

        if (request.transferAccountId() != null) {
            Account transferAccount = accountRepository.findById(request.transferAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.transferAccountId()));
            validateAccountOwnership(transferAccount, username);
            transaction.setTransferAccount(transferAccount);
        }

        // Update account balance
        updateAccountBalance(account, request.amount(), request.type());

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id, String username) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

        validateTransactionOwnership(transaction, username);
        return transactionMapper.toResponse(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllTransactions(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return transactionRepository.findByUserId(user.getId(), pageable)
            .map(transactionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionsByAccount(Long accountId, String username, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        validateAccountOwnership(account, username);

        return transactionRepository.findByAccountId(accountId, pageable)
            .map(transactionMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByDateRange(
        String username, LocalDate startDate, LocalDate endDate
    ) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return transactionRepository.findByUserIdAndTransactionDateBetween(user.getId(), startDate, endDate)
            .stream()
            .map(transactionMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> searchTransactions(String username, String keyword, Pageable pageable) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return transactionRepository.searchByUserIdAndKeyword(user.getId(), keyword, pageable)
            .map(transactionMapper::toResponse);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request, String username) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

        validateTransactionOwnership(transaction, username);

        // Restore old balance
        updateAccountBalance(transaction.getAccount(), transaction.getAmount().negate(), transaction.getType());

        Account account = accountRepository.findById(request.accountId())
            .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.accountId()));
        validateAccountOwnership(account, username);

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.categoryId()));
        validateCategoryOwnership(category, username);

        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setDescription(request.description());
        transaction.setPayee(request.payee());
        transaction.setReference(request.reference());
        transaction.setNotes(request.notes());
        transaction.setAccount(account);
        transaction.setCategory(category);

        if (request.transferAccountId() != null) {
            Account transferAccount = accountRepository.findById(request.transferAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.transferAccountId()));
            validateAccountOwnership(transferAccount, username);
            transaction.setTransferAccount(transferAccount);
        } else {
            transaction.setTransferAccount(null);
        }

        if (request.reconciled() != null) {
            transaction.setReconciled(request.reconciled());
        }

        // Apply new balance
        updateAccountBalance(account, request.amount(), request.type());

        Transaction updated = transactionRepository.save(transaction);
        return transactionMapper.toResponse(updated);
    }

    @Transactional
    public void deleteTransaction(Long id, String username) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

        validateTransactionOwnership(transaction, username);

        // Restore account balance
        updateAccountBalance(transaction.getAccount(), transaction.getAmount().negate(), transaction.getType());

        transactionRepository.delete(transaction);
    }

    private void updateAccountBalance(Account account, BigDecimal amount, Transaction.TransactionType type) {
        BigDecimal currentBalance = account.getBalance();
        BigDecimal newBalance;

        switch (type) {
            case INCOME -> newBalance = currentBalance.add(amount);
            case EXPENSE -> newBalance = currentBalance.subtract(amount);
            case TRANSFER -> newBalance = currentBalance.subtract(amount);
            default -> newBalance = currentBalance;
        }

        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    private void validateTransactionOwnership(Transaction transaction, String username) {
        if (!transaction.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to access this transaction");
        }
    }

    private void validateAccountOwnership(Account account, String username) {
        if (!account.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to access this account");
        }
    }

    private void validateCategoryOwnership(Category category, String username) {
        if (!category.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to access this category");
        }
    }
}
