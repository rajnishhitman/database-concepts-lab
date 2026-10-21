package com.example.dbconceptslab.locking;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class LockingDemoService {

    private final LockingAccountRepository repository;

    public LockingDemoService(LockingAccountRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public LockingAccount createAccount(
            String ownerName,
            BigDecimal openingBalance) {

        if (openingBalance.signum() < 0) {
            throw new IllegalArgumentException(
                    "Opening balance cannot be negative");
        }

        return repository.save(
                new LockingAccount(ownerName, openingBalance));
    }

    // OPTIMISTIC LOCKING
    @Transactional
    public LockingAccount updateOptimistically(
            Long id, BigDecimal amount) {

        LockingAccount account = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Account not found: " + id));

        BigDecimal newBalance = account.getBalance().add(amount);

        if (newBalance.signum() < 0) {
            throw new IllegalArgumentException(
                    "Insufficient balance");
        }

        account.setBalance(newBalance);

        // JPA dirty checking updates the managed entity at flush/commit.
        // Hibernate checks and increments @Version automatically.
        return account;

//        setBalance() — changes the managed Java object.
//        Automatic flush — executes below SQL and performs the optimistic version check.
//                    UPDATE locking_demo_accounts
//                    SET balance = 1200,
//                          version = 1
//                    WHERE id = 1 AND version = 0;
//        Commit — completes the transaction, making the changes durable if successful.
    }

    // PESSIMISTIC LOCKING
    @Transactional
    public LockingAccount updatePessimistically(
            Long id, BigDecimal amount) {

        LockingAccount account = repository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Account not found: " + id));

        BigDecimal newBalance = account.getBalance().add(amount);

        if (newBalance.signum() < 0) {
            throw new IllegalArgumentException(
                    "Insufficient balance");
        }

        account.setBalance(newBalance);

        // The row remains locked until this transaction completes.
        return account;
    }

    @Transactional(readOnly = true)
    public LockingAccount getAccount(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Account not found: " + id));
    }
}