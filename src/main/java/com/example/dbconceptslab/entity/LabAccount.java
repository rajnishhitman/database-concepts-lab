package com.example.dbconceptslab.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lab_accounts")
public class LabAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal balance;

    @Version
    @Column(nullable = false)
    private Long version;

    protected LabAccount() {
    }

    public Long getId() {
        return id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Long getVersion() {
        return version;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}