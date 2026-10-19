package com.example.dbconceptslab.sharding;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Profile("sharding")
@RequestMapping("/api/sharded-customers")
public class ShardedCustomerController {
    private final ShardingService service;

    public ShardedCustomerController(ShardingService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShardedCustomer create(@RequestBody CreateCustomerRequest request) {
        return service.create(new ShardedCustomer(request.id(), request.fullName(), request.email(), request.city()));
    }

    @GetMapping("/{id}")
    public ShardedCustomer find(@PathVariable long id) {
        return service.findById(id);
    }

    @GetMapping("/{id}/shard")
    public Map<String, Object> shard(@PathVariable long id) {
        return Map.of("customerId", id, "shardId", service.shardFor(id));
    }

    @GetMapping("/shard-stats")
    public Map<String, Long> stats() {
        return Map.of("shard0", service.countOnShard(0),
                      "shard1", service.countOnShard(1),
                      "shard2", service.countOnShard(2));
    }

    public record CreateCustomerRequest(Long id, String fullName, String email, String city) {}
}
