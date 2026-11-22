package com.example.accountservice.dto;

import com.example.accountservice.model.Account;
import com.example.accountservice.model.AccountStatus;
import com.example.accountservice.model.AccountType;
import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(Long id, String accountNumber, AccountType accountType,
    BigDecimal balance, String currency, AccountStatus status, String ownerUsername, Instant createdAt) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getAccountNumber(), account.getAccountType(),
            account.getBalance(), account.getCurrency(), account.getStatus(), account.getOwnerUsername(), account.getCreatedAt());
    }
}
