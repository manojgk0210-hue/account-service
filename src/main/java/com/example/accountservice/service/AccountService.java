package com.example.accountservice.service;

import com.example.accountservice.dto.CreateAccountRequest;
import com.example.accountservice.exception.AccountNotFoundException;
import com.example.accountservice.exception.InsufficientFundsException;
import com.example.accountservice.model.Account;
import com.example.accountservice.repository.AccountRepository;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    private final AccountRepository repository;
    private final SecureRandom random = new SecureRandom();

    public AccountService(AccountRepository repository) { this.repository = repository; }

    @Transactional
    public Account createAccount(CreateAccountRequest request, String owner) {
        String number;
        do { number = Long.toString(random.nextLong(100_000_000_000L, 1_000_000_000_000L)); }
        while (repository.existsByAccountNumber(number));
        BigDecimal initial = request.initialDeposit() == null ? BigDecimal.ZERO : request.initialDeposit();
        return repository.save(new Account(number, owner, request.accountType(), initial));
    }

    @Transactional(readOnly = true)
    public List<Account> getAccounts(String owner) { return repository.findAllByOwnerUsername(owner); }

    @Transactional(readOnly = true)
    public Account getAccount(Long id, String owner) {
        return repository.findByIdAndOwnerUsername(id, owner).orElseThrow(AccountNotFoundException::new);
    }

    // These practice operations will be replaced by transaction-service.
    @Transactional
    public Account deposit(Long id, String owner, BigDecimal amount) {
        Account account = getAccount(id, owner);
        account.setBalance(account.getBalance().add(amount));
        return repository.save(account);
    }

    // These practice operations will be replaced by transaction-service.
    @Transactional
    public Account withdraw(Long id, String owner, BigDecimal amount) {
        Account account = getAccount(id, owner);
        if (amount.compareTo(account.getBalance()) > 0) throw new InsufficientFundsException();
        account.setBalance(account.getBalance().subtract(amount));
        return repository.save(account);
    }
}
