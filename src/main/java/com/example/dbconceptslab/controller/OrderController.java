package com.example.dbconceptslab.controller;

import com.example.dbconceptslab.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public record CreateOrderRequest(
            Long customerId,
            BigDecimal amount,
            boolean simulateFailure) {

    }

    @PostMapping
    public Map<String, Object> createOrder(
            @RequestBody CreateOrderRequest request){

        if(request.customerId() == null
                || request.customerId() <= 0
                || request.amount().compareTo(BigDecimal.ZERO) <= 0){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer ID and amount must be positive");
        }

        return orderService.createOrderAndPayment(
                request.customerId(),
                request.amount(),
                request.simulateFailure());
    }
}
