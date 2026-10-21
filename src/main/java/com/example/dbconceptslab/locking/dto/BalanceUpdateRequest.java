package com.example.dbconceptslab.locking.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BalanceUpdateRequest(
        @NotNull BigDecimal amount
) {
}