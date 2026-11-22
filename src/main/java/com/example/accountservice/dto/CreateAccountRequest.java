package com.example.accountservice.dto;

import com.example.accountservice.model.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateAccountRequest(@NotNull AccountType accountType,
    @DecimalMin(value = "0.00", inclusive = true) @Digits(integer = 17, fraction = 2) BigDecimal initialDeposit) { }
