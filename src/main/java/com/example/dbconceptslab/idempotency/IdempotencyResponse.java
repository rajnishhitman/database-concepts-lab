package com.example.dbconceptslab.idempotency;

import java.math.BigDecimal;

public record IdempotencyResponse(
        Long id,
        String description,
        BigDecimal amount
) {}
