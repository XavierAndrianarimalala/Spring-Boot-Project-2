package com.finance.service;

import com.finance.dto.account.AccountRequest;
import com.finance.dto.account.AccountResponse;
import com.finance.entity.Account;
import com.finance.entity.User;
import com.finance.exception.ResourceNotFoundException;
import com.finance.exception.UnauthorizedException;
import com.finance.mapper.AccountMapper;
import com.finance.repository.AccountRepository;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    @Transactional
    public AccountResponse createAccount(AccountRequest request, String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Account account = Account.builder()
            .name(request.name())
            .description(request.description())
            .type(request.type())
            .balance(request.balance() != null ? request.balance() : BigDecimal.ZERO)
            .currency(request.currency() != null ? request.currency() : "EUR")
            .active(true)
            .user(user)
            .build();

        Account saved = accountRepository.save(account);
        return accountMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id, String username) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        validateAccountOwnership(account, username);
        return accountMapper.toResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return accountRepository.findByUserId(user.getId())
            .stream()
            .map(accountMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getActiveAccounts(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return accountRepository.findByUserIdAndActive(user.getId(), true)
            .stream()
            .map(accountMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalBalance(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        BigDecimal total = accountRepository.getTotalBalanceByUserId(user.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional
    public AccountResponse updateAccount(Long id, AccountRequest request, String username) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        validateAccountOwnership(account, username);

        account.setName(request.name());
        account.setDescription(request.description());
        account.setType(request.type());
        account.setBalance(request.balance());
        if (request.currency() != null) {
            account.setCurrency(request.currency());
        }

        Account updated = accountRepository.save(account);
        return accountMapper.toResponse(updated);
    }

    @Transactional
    public void deleteAccount(Long id, String username) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        validateAccountOwnership(account, username);
        accountRepository.delete(account);
    }

    @Transactional
    public AccountResponse toggleAccountStatus(Long id, String username) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        validateAccountOwnership(account, username);
        account.setActive(!account.getActive());

        Account updated = accountRepository.save(account);
        return accountMapper.toResponse(updated);
    }

    private void validateAccountOwnership(Account account, String username) {
        if (!account.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You don't have permission to access this account");
        }
    }
}
