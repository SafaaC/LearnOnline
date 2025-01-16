package com.online.learning.payment_service.controller;

import com.online.learning.payment_service.service.PaymentService;
import com.online.learning.payment_service.service.PaytmService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.online.learning.payment_service.model.Payment;
import com.online.learning.payment_service.model.PaymentRequest;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaytmService paytmService;

    /**
     * Initiates payment by creating a new payment request.
     */
    @PostMapping("/initiate")
    public ResponseEntity<String> initiatePayment(@RequestBody PaymentRequest paymentRequest) {
        String orderId = "ORDER_" + System.currentTimeMillis(); // Create a unique order ID
        String amount = String.valueOf(paymentRequest.getAmount()); // Convert to string to match Paytm's expected format

        // Create payment object and save it to the database
        Payment payment = new Payment(orderId, "PENDING", paymentRequest.getAmount().doubleValue(), paymentRequest.getUserId());
        paymentService.savePayment(payment);

        // Call Paytm to initiate payment process
        String paymentUrl = paytmService.createPayment(orderId, amount);

        return ResponseEntity.ok("Payment initiated. Redirect to: " + paymentUrl);
    }

    /**
     * Callback from Paytm to update payment status
     */
    @PostMapping("/callback")
    public ResponseEntity<String> paymentCallback(@RequestBody String paymentStatus) {
        // Assuming the callback from Paytm will contain the order ID and status
        String orderId = paymentStatus;  // Extract this from Paytm's response in real implementation
        Payment payment = paymentService.getPaymentById(Long.valueOf(orderId));

        // For simplicity, assume "SUCCESS" is the status received from Paytm
        payment.setStatus("SUCCESS");  // Update payment status
        paymentService.savePayment(payment); // Save updated payment status in the database

        return ResponseEntity.ok("Payment status updated successfully");
    }
}