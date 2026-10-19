package com.example.dbconceptslab.sharding;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("sharding")
public class ShardingService {
    private final ShardResolver resolver;
    private final List<JdbcTemplate> shards;

    public ShardingService(ShardResolver resolver,
            @Qualifier("shard0JdbcTemplate") JdbcTemplate shard0,
            @Qualifier("shard1JdbcTemplate") JdbcTemplate shard1,
            @Qualifier("shard2JdbcTemplate") JdbcTemplate shard2) {
        this.resolver = resolver;
        this.shards = List.of(shard0, shard1, shard2);
    }

    @PostConstruct
    void createSchemaOnEveryShard() {
        for (JdbcTemplate shard : shards) {
            shard.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "id BIGINT PRIMARY KEY, " +
                    "full_name VARCHAR(200) NOT NULL, " +
                    "email VARCHAR(320) NOT NULL, " +
                    "city VARCHAR(120) NOT NULL)");
        }
    }

    public ShardedCustomer create(ShardedCustomer c) {
        jdbcFor(c.id()).update("INSERT INTO customers (id, full_name, email, city) VALUES (?, ?, ?, ?)",
                c.id(), c.fullName(), c.email(), c.city());
        return c;
    }

    public ShardedCustomer findById(long id) {
        List<ShardedCustomer> rows = jdbcFor(id).query(
                "SELECT id, full_name, email, city FROM customers WHERE id = ?",
                (rs, rowNum) -> new ShardedCustomer(rs.getLong("id"),
                        rs.getString("full_name"), rs.getString("email"), rs.getString("city")), id);
        if (rows.isEmpty()) throw new CustomerNotFoundException(id);
        return rows.get(0);
    }

    public int shardFor(long id) {
        return resolver.resolve(id);
    }

    public long countOnShard(int shardId) {
        if (shardId < 0 || shardId >= shards.size()) throw new IllegalArgumentException("Invalid shard id");
        Long count = shards.get(shardId).queryForObject("SELECT COUNT(*) FROM customers", Long.class);
        return count == null ? 0 : count;
    }

    private JdbcTemplate jdbcFor(long id) {
        return shards.get(resolver.resolve(id));
    }
}
