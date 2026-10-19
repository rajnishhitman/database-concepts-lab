package com.example.dbconceptslab.idempotency;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class IdempotencyDemoService {

    private final JdbcTemplate jdbcTemplate;

    public IdempotencyDemoService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public IdempotencyResponse execute(
            String key,
            IdempotencyRequest request) {

        validate(key, request);

        String hash = fingerprint(request);

        // Atomically claim the key.
        // A duplicate key does not insert another record.
        int claimed = jdbcTemplate.update("""
            INSERT INTO idempotency_records
                (idempotency_key, request_hash, operation_id)
            VALUES (?, ?, NULL)
            ON CONFLICT (idempotency_key) DO NOTHING
            """, key, hash);

        if (claimed == 0) {
            // A previous transaction owns this key.
            String existingHash = jdbcTemplate.queryForObject(
                    """
                    SELECT request_hash
                    FROM idempotency_records
                    WHERE idempotency_key = ?
                    """,
                    String.class,
                    key
            );

            if (!hash.equals(existingHash)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Idempotency key reused with a different payload"
                );
            }

            // Return the original business result.
            return jdbcTemplate.queryForObject(
                    """
                    SELECT o.id, o.description, o.amount
                    FROM idempotency_records i
                    JOIN idempotency_demo_operations o
                      ON o.id = i.operation_id
                    WHERE i.idempotency_key = ?
                    """,
                    (rs, rowNum) -> new IdempotencyResponse(
                            rs.getLong("id"),
                            rs.getString("description"),
                            rs.getBigDecimal("amount")
                    ),
                    key
            );
        }

        // First request: perform the business operation once.
        Long operationId = jdbcTemplate.queryForObject(
                """
                INSERT INTO idempotency_demo_operations
                    (description, amount)
                VALUES (?, ?)
                RETURNING id
                """,
                Long.class,
                request.description().trim(),
                request.amount()
        );

        // Save the result reference before committing.
        jdbcTemplate.update("""
            UPDATE idempotency_records
            SET operation_id = ?
            WHERE idempotency_key = ?
            """, operationId, key);

        return new IdempotencyResponse(
                operationId,
                request.description().trim(),
                request.amount()
        );
    }

    private void validate(
            String key,
            IdempotencyRequest request) {

        if (key == null || key.isBlank() || key.length() > 200) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Idempotency-Key must contain 1-200 characters"
            );
        }

        if (request == null
                || request.description() == null
                || request.description().isBlank()
                || request.description().trim().length() > 200
                || request.amount() == null
                || request.amount().compareTo(BigDecimal.ZERO) <= 0
                || request.amount().scale() > 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide a description and a positive amount "
                            + "with at most two decimal places"
            );
        }
    }

    private String fingerprint(IdempotencyRequest request) {
        String canonicalPayload =
                request.description().trim()
                        + "|"
                        + request.amount()
                        .setScale(2)
                        .toPlainString();

        try {
            byte[] digest = MessageDigest
                    .getInstance("SHA-256")
                    .digest(canonicalPayload.getBytes(
                            StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 is unavailable", e);
        }
    }
}
