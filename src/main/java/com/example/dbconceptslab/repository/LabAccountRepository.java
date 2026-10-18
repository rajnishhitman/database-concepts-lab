package com.example.dbconceptslab.repository;

import com.example.dbconceptslab.entity.LabAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface LabAccountRepository
        extends JpaRepository<LabAccount, Long> {

    List<LabAccount> findByBalanceGreaterThanEqualOrderById(
            BigDecimal balance);
}