package com.example.dbconceptslab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
//@Table(
//        name = "customers",
//        indexes = {
//                @Index(name = "idx_customers_city", columnList = "city")
//        }
//)
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false, length = 30)
    private String city;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected CustomerEntity() {}

    public CustomerEntity(String fullName, String email, String city, String status, LocalDateTime createdAt) {
        this.fullName = fullName;
        this.email = email;
        this.city = city;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getCity() { return city; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
