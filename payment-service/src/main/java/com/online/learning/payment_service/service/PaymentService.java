package com.online.learning.payment_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.online.learning.payment_service.repository.PaymentRepository;
import com.online.learning.payment_service.model.Payment;


@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Save a payment record to the database
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Retrieve a payment by ID
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
    }
}