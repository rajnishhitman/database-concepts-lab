package com.example.dbconceptslab.sharding;

import org.springframework.stereotype.Component;

@Component
public class ShardResolver {
    public static final int SHARD_COUNT = 3;

    public int resolve(long customerId) {
        return Math.floorMod(Long.hashCode(customerId), SHARD_COUNT);
    }
}
