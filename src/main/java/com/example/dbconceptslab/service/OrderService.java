package com.example.dbconceptslab.service;

import com.example.dbconceptslab.entity.OrderEntity;
import com.example.dbconceptslab.entity.PaymentEntity;
import com.example.dbconceptslab.repository.OrderRepository;
import com.example.dbconceptslab.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;


    public OrderService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Map<String, Object> createOrderAndPayment(
            Long customerId,
            BigDecimal amount,
            boolean simulateFailure) {

//        1. Save the order
        OrderEntity order = orderRepository.save(new OrderEntity(customerId, amount));

//        Force the order INSERT to execute in the database.
        orderRepository.flush();

//        2. Simulate failure after saving the order
        if(simulateFailure){
            throw new RuntimeException("Simulated payment failure");
        }

//        3. Save the payment
        PaymentEntity payment = paymentRepository.save(new PaymentEntity(order.getId(), amount));

//        4. Return successfully; Spring commits on method exit.
        return Map.of(
                "orderId", order.getId(),
                "paymentId", payment.getId(),
                "orderStatus", order.getStatus(),
                "paymentStatus", payment.getStatus(),
                "amount", amount
        );
    }
}
