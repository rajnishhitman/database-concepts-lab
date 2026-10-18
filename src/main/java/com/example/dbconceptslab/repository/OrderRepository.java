package com.example.dbconceptslab.repository;

import com.example.dbconceptslab.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository
        extends JpaRepository<OrderEntity, Long> {
}