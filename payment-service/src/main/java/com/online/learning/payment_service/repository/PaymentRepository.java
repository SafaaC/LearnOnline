package com.online.learning.payment_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.online.learning.payment_service.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByPaymentId(String paymentId);
    
    @Override
    public Payment save(Payment payment);
}