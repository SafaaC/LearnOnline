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

    public PaymentService(PaymentRepository paymentRepository, RazorpayConfig razorpayConfig) throws Exception {
        this.paymentRepository = paymentRepository;
        this.razorpayClient = new RazorpayClient(razorpayConfig.getKey(), razorpayConfig.getSecret());
    }

    @Transactional
    public Payment createPaymentOrder(PaymentRequest paymentRequest) throws Exception {
        // Build the Razorpay order request
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", paymentRequest.getAmount() * 100); // convert rupees to paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receipt_" + paymentRequest.getUserId() + "_" + paymentRequest.getCourseId());

        // Store userId and courseId in Razorpay's notes field
        // These come back in the webhook so we know who paid for what
        JSONObject notes = new JSONObject();
        notes.put("user_id", paymentRequest.getUserId());
        notes.put("course_id", paymentRequest.getCourseId().toString());
        orderRequest.put("notes", notes);

        // Create the order in Razorpay
        Order order = razorpayClient.Orders.create(orderRequest);

        // Save to our database as Pending
        Payment payment = new Payment();
        payment.setPaymentId(order.get("id").toString());
        payment.setStatus("Pending");
        payment.setAmount(paymentRequest.getAmount().doubleValue());
        payment.setUserId(paymentRequest.getUserId());
        payment.setCourseId(paymentRequest.getCourseId());

        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
    }

    public Payment updatePaymentStatus(String paymentId, String status) {
        Payment payment = paymentRepository.findByPaymentId(paymentId);
        if (payment == null) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}