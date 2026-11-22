package com.example.accountservice.repository;

import com.example.accountservice.model.Account;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByOwnerUsername(String ownerUsername);
    Optional<Account> findByIdAndOwnerUsername(Long id, String ownerUsername);
    boolean existsByAccountNumber(String accountNumber);
}
