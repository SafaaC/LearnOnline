package com.online.learning.payment_service.service;

import com.online.learning.payment_service.config.RazorpayConfig;
import com.online.learning.payment_service.model.Payment;
import com.online.learning.payment_service.model.PaymentRequest;
import com.online.learning.payment_service.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

import jakarta.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RazorpayClient razorpayClient;

    // Constructor injection
    public PaymentService(PaymentRepository paymentRepository, RazorpayConfig razorpayConfig) throws Exception {
        this.paymentRepository = paymentRepository;

        // Initialize RazorpayClient with the key and secret from RazorpayConfig
        this.razorpayClient = new RazorpayClient(razorpayConfig.getKey(), razorpayConfig.getSecret());
    }

    // Method to create a payment order
    @Transactional
    public Payment createPaymentOrder(PaymentRequest paymentRequest) throws Exception {
        // Constructing Razorpay order request
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", paymentRequest.getAmount() * 100); // Razorpay expects amount in paise
        orderRequest.put("receipt", "receipt#001");

        // Creating order in Razorpay
        Order order = razorpayClient.Orders.create(orderRequest);

        // Storing order details in the database
        Payment payment = new Payment();
        payment.setPaymentId(order.get("id").toString());
        payment.setStatus("Pending");
        payment.setAmount(paymentRequest.getAmount().doubleValue());
        payment.setUserId(paymentRequest.getUserId());
        
        // Save payment in the database
        return paymentRepository.save(payment);
    }

    // Fetch payment details by payment ID
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
    }

    // Update payment status after successful payment
    public Payment updatePaymentStatus(String paymentId, String status) {
        // Fetch payment details by payment ID
        Payment payment = paymentRepository.findByPaymentId(paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }

        // Update status and save changes
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment createPayment(Payment payment) {
    return paymentRepository.save(payment);
}
}