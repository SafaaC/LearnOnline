package com.online.learning.payment_service.controller;

// import com.online.learning.payment_service.model.PaymentRequest;
import com.online.learning.payment_service.model.Payment;
import com.online.learning.payment_service.model.PaymentRequest;
import com.online.learning.payment_service.service.PaymentService;

import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Endpoint to create a payment order
    @PostMapping("/create")
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            Payment payment = paymentService.createPaymentOrder(paymentRequest);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Endpoint to fetch payment details
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    // Endpoint to update the payment status after a successful payment
    @PostMapping("/update-status/{paymentId}")
    public ResponseEntity<Payment> updatePaymentStatus(@PathVariable String paymentId, @RequestParam String status) {
        Payment updatedPayment = paymentService.updatePaymentStatus(paymentId, status);
        return ResponseEntity.ok(updatedPayment);
    }
}