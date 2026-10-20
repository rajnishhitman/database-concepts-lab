package com.example.dbconceptslab.idempotency;

import java.math.BigDecimal;

public record IdempotencyRequest(
        String description,
        BigDecimal amount
) {}
