package com.example.dbconceptslab.locking;

import com.example.dbconceptslab.locking.dto.BalanceUpdateRequest;
import com.example.dbconceptslab.locking.dto.CreateAccountRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locking-demo/accounts")
public class LockingDemoController {

    private final LockingDemoService service;

    public LockingDemoController(LockingDemoService service) {
        this.service = service;
    }

    @PostMapping
    public LockingAccount create(
            @Valid @RequestBody CreateAccountRequest request) {

        return service.createAccount(
                request.ownerName(),
                request.openingBalance());
    }

    @GetMapping("/{id}")
    public LockingAccount get(@PathVariable Long id) {
        return service.getAccount(id);
    }

    @PatchMapping("/{id}/optimistic")
    public LockingAccount optimisticUpdate(
            @PathVariable Long id,
            @Valid @RequestBody BalanceUpdateRequest request) {

        return service.updateOptimistically(id, request.amount());
    }

    @PatchMapping("/{id}/pessimistic")
    public LockingAccount pessimisticUpdate(
            @PathVariable Long id,
            @Valid @RequestBody BalanceUpdateRequest request) {

        return service.updatePessimistically(id, request.amount());
    }
}