package com.example.accountservice.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() { super("Account not found"); }
}
