package com.example.accountservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.accountservice.dto.CreateAccountRequest;
import com.example.accountservice.exception.AccountNotFoundException;
import com.example.accountservice.exception.InsufficientFundsException;
import com.example.accountservice.model.Account;
import com.example.accountservice.model.AccountType;
import com.example.accountservice.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock AccountRepository repository;
    AccountService service;
    @BeforeEach void setUp() { service = new AccountService(repository); }

    @Test void createAccount_success_returnsAccountWithOwnerFromToken() {
        // Arrange
        when(repository.existsByAccountNumber(any())).thenReturn(false);
        when(repository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // Act
        Account account = service.createAccount(new CreateAccountRequest(AccountType.SAVINGS, new BigDecimal("1000.00")), "john");
        // Assert
        assertEquals("john", account.getOwnerUsername());
        assertEquals(new BigDecimal("1000.00"), account.getBalance());
        assertEquals(12, account.getAccountNumber().length());
        verify(repository).save(any(Account.class));
    }

    @Test void getAccount_ownedByAnotherUser_throwsNotFound() {
        // Arrange
        when(repository.findByIdAndOwnerUsername(1L, "john")).thenReturn(Optional.empty());
        // Act and assert
        assertThrows(AccountNotFoundException.class, () -> service.getAccount(1L, "john"));
    }

    @Test void deposit_validAmount_increasesBalance() {
        // Arrange
        Account account = new Account("482910573621", "john", AccountType.SAVINGS, new BigDecimal("100.00"));
        when(repository.findByIdAndOwnerUsername(1L, "john")).thenReturn(Optional.of(account));
        when(repository.save(account)).thenReturn(account);
        // Act
        Account updated = service.deposit(1L, "john", new BigDecimal("25.50"));
        // Assert
        assertEquals(new BigDecimal("125.50"), updated.getBalance());
    }

    @Test void withdraw_amountGreaterThanBalance_throwsInsufficientFunds() {
        // Arrange
        Account account = new Account("482910573621", "john", AccountType.SAVINGS, new BigDecimal("10.00"));
        when(repository.findByIdAndOwnerUsername(1L, "john")).thenReturn(Optional.of(account));
        // Act and assert
        assertThrows(InsufficientFundsException.class, () -> service.withdraw(1L, "john", new BigDecimal("11.00")));
    }
}
