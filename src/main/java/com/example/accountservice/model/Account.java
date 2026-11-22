package com.example.accountservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "accounts", indexes = @Index(name = "idx_accounts_owner_username", columnList = "owner_username"))
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_number", nullable = false, unique = true, length = 12)
    private String accountNumber;
    @Column(name = "owner_username", nullable = false)
    private String ownerUsername;
    @Enumerated(EnumType.STRING) @Column(name = "account_type", nullable = false)
    private AccountType accountType;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    @Column(nullable = false, length = 3)
    private String currency = "INR";
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Version
    private Long version;

    protected Account() { }
    public Account(String accountNumber, String ownerUsername, AccountType accountType, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.ownerUsername = ownerUsername;
        this.accountType = accountType;
        this.balance = balance;
    }
    @PrePersist void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerUsername() { return ownerUsername; }
    public AccountType getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public String getCurrency() { return currency; }
    public AccountStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
