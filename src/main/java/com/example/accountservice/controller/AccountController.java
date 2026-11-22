package com.example.accountservice.controller;

import com.example.accountservice.dto.*;
import com.example.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService service;
    public AccountController(AccountService service) { this.service = service; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) @SecurityRequirement(name = "bearerAuth")
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request, Authentication auth) {
        return AccountResponse.from(service.createAccount(request, auth.getName()));
    }
    @GetMapping @SecurityRequirement(name = "bearerAuth") public List<AccountResponse> list(Authentication auth) {
        return service.getAccounts(auth.getName()).stream().map(AccountResponse::from).toList();
    }
    @GetMapping("/{id}") @SecurityRequirement(name = "bearerAuth") public AccountResponse get(@PathVariable Long id, Authentication auth) {
        return AccountResponse.from(service.getAccount(id, auth.getName()));
    }
    @GetMapping("/{id}/balance") @SecurityRequirement(name = "bearerAuth") public BalanceResponse balance(@PathVariable Long id, Authentication auth) {
        return BalanceResponse.from(service.getAccount(id, auth.getName()));
    }
    @PostMapping("/{id}/deposit") @SecurityRequirement(name = "bearerAuth") public AccountResponse deposit(@PathVariable Long id, @Valid @RequestBody AmountRequest request, Authentication auth) {
        return AccountResponse.from(service.deposit(id, auth.getName(), request.amount()));
    }
    @PostMapping("/{id}/withdraw") @SecurityRequirement(name = "bearerAuth") public AccountResponse withdraw(@PathVariable Long id, @Valid @RequestBody AmountRequest request, Authentication auth) {
        return AccountResponse.from(service.withdraw(id, auth.getName(), request.amount()));
    }
    @GetMapping("/health")
    public java.util.Map<String, String> health() { return java.util.Map.of("status", "UP"); }
}
