package com.example.dbconceptslab.locking;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LockingAccountRepository
        extends JpaRepository<LockingAccount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           SELECT a
           FROM LockingAccount a
           WHERE a.id = :id
           """)
    Optional<LockingAccount> findByIdForUpdate(
            @Param("id") Long id
    );

//    @Query defines which records to retrieve.
//    @Lock defines what locking behavior to request.
//    Hibernate combines these instructions when generating SQL.

//    SELECT *
//    FROM locking_demo_accounts
//    WHERE id = 1
//    FOR UPDATE; -> this is the reason why another transaction have to wait
}