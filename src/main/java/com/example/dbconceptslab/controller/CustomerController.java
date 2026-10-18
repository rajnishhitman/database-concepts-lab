package com.example.dbconceptslab.controller;

import com.example.dbconceptslab.entity.CustomerEntity;
import com.example.dbconceptslab.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerRepository repository;

    public CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public CustomerEntity getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }

    @GetMapping
    public Page<CustomerEntity> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        if (page < 0 || size < 1 || size > 200) {
            throw new IllegalArgumentException("page must be >= 0 and size must be between 1 and 200");
        }
        return repository.findAll(PageRequest.of(page, size, Sort.by(sortBy).ascending()));
    }

    @GetMapping("/by-city")
    public Page<CustomerEntity> getByCity(
            @RequestParam String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return repository.findByCity(city, PageRequest.of(page, size, Sort.by("id").ascending()));
    }

    @GetMapping("/by-email")
    public CustomerEntity getByEmail(@RequestParam String email) {
        return repository.findByEmail(email).orElseThrow();
    }

    @GetMapping("/sample-cities")
    public List<String> sampleCities() {
        return List.of("Bengaluru", "Kolkata", "Hyderabad", "Pune", "Chennai", "Delhi");
    }
}
