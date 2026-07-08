package com.online.learning.payment_service.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.learning.payment_service.model.Payment;
import com.online.learning.payment_service.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.util.Map;

@RestController
@RequestMapping("/payment/webhook")
public class WebhookController {

    @Autowired
    PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(HttpServletRequest request) {
        try {
            // Read the raw request body
            StringBuilder payload = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                payload.append(line);
            }

            // Parse the JSON body into a Map
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> webhookPayload = objectMapper.readValue(
                payload.toString(),
                new TypeReference<Map<String, Object>>() {}
            );

            // What kind of event did Razorpay send?
            String eventType = (String) webhookPayload.get("event");
            System.out.println("Received Webhook Event: " + eventType);

            if ("order.paid".equals(eventType)) {
                handleOrderPaid(webhookPayload);
            } else if ("payment.failed".equals(eventType)) {
                handlePaymentFailed(webhookPayload);
            } else {
                System.out.println("Unhandled event: " + eventType);
            }

            return ResponseEntity.ok("Webhook processed successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook");
        }
    }

    @SuppressWarnings("unchecked")
    private void handleOrderPaid(Map<String, Object> webhookPayload) {
        try {
            // Razorpay's webhook structure:
            // webhookPayload -> payload -> payment -> entity -> { id, amount, notes }
            Map<String, Object> payload = (Map<String, Object>) webhookPayload.get("payload");
            Map<String, Object> paymentWrapper = (Map<String, Object>) payload.get("payment");
            Map<String, Object> entity = (Map<String, Object>) paymentWrapper.get("entity");

            if (entity == null) {
                System.out.println("No entity found in webhook payload");
                return;
            }

            // Extract basic payment details
            String paymentId = (String) entity.get("id");
            Integer amountPaise = (Integer) entity.get("amount");
            Double amountRupees = amountPaise / 100.0;

            // Extract userId and courseId from the notes field
            // These were stored when we created the order in PaymentService
            Map<String, Object> notes = (Map<String, Object>) entity.get("notes");
            String userId = notes != null ? (String) notes.get("user_id") : null;
            String courseIdStr = notes != null ? (String) notes.get("course_id") : null;
            Long courseId = courseIdStr != null ? Long.parseLong(courseIdStr) : null;

            System.out.println("Payment succeeded: " + paymentId
                + " | User: " + userId
                + " | Course: " + courseId
                + " | Amount: ₹" + amountRupees);

            // Save the successful payment to our database
            Payment payment = new Payment();
            payment.setPaymentId(paymentId);
            payment.setStatus("Success");
            payment.setAmount(amountRupees);
            payment.setUserId(userId);
            payment.setCourseId(courseId);
            paymentService.createPayment(payment);

            // TODO: call course-service to mark registration as paid
            // This is the next step we'll implement after confirming
            // the basic payment flow works
            System.out.println("TODO: mark registration paid for user "
                + userId + " course " + courseId);

        } catch (Exception e) {
            System.out.println("Error handling order.paid: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void handlePaymentFailed(Map<String, Object> webhookPayload) {
        try {
            Map<String, Object> payload = (Map<String, Object>) webhookPayload.get("payload");
            Map<String, Object> paymentWrapper = (Map<String, Object>) payload.get("payment");
            Map<String, Object> entity = (Map<String, Object>) paymentWrapper.get("entity");

            if (entity != null) {
                String paymentId = (String) entity.get("id");
                System.out.println("Payment failed: " + paymentId);
                // Optionally update the payment status in your DB here
            }
        } catch (Exception e) {
            System.out.println("Error handling payment.failed: " + e.getMessage());
        }
    }
}