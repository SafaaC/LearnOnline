package com.online.learning.payment_service.controller;

import com.online.learning.payment_service.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/payments")
public class InternalPaymentController {

    private final PaymentService paymentService;

    public InternalPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/verify")
    public boolean verify(@RequestParam String userId, @RequestParam Long courseId) {
        return paymentService.hasPaid(userId, courseId);
    }
}