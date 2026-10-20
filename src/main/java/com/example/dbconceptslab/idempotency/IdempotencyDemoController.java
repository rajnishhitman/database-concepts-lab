package com.example.dbconceptslab.idempotency;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/idempotency-demo")
public class IdempotencyDemoController {

    private final IdempotencyDemoService service;

    public IdempotencyDemoController(
            IdempotencyDemoService service) {
        this.service = service;
    }

    @PostMapping("/operations")
    public ResponseEntity<IdempotencyResponse> execute(
            @RequestHeader("Idempotency-Key") String key,
            @RequestBody IdempotencyRequest request) {

        IdempotencyResponse response =
                service.execute(key, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}