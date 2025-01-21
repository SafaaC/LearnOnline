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

    private static final String WEBHOOK_SECRET = "WebHookSecrete"; // Replace with your Razorpay webhook secret

    @PostMapping
    public ResponseEntity<String> handleWebhook(HttpServletRequest request) {
        try {
            // Read the request payload
            StringBuilder payload = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                payload.append(line);
            }
    
            // Retrieve the Razorpay signature from headers
            String razorpaySignature = request.getHeader("X-Razorpay-Signature");
    
            // For testing, skip signature validation
            boolean isValid = true; // Skip signature validation for testing (instead of actual signature check)
    
            // If signature validation is enabled, you can do it like this:
            // boolean isValid = Utils.verifyWebhookSignature(payload.toString(), razorpaySignature, WEBHOOK_SECRET);
    
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid webhook signature");
            }
    
            // Convert the payload into a Map with type safety
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> webhookPayload = objectMapper.readValue(payload.toString(),
                    new TypeReference<Map<String, Object>>() {});
    
            // Log the event or process it based on event type
            String eventType = (String) webhookPayload.get("event");
            System.out.println("Received Webhook Event: " + eventType);
    
            // Handle different event types
            switch (eventType) {
                case "order.paid":
                    handleOrderPaid(webhookPayload);
                    break;
                case "payment.failed":
                    handlePaymentFailed(webhookPayload);
                    break;
                default:
                    System.out.println("Unhandled Webhook Event: " + eventType);
            }
    
            return ResponseEntity.ok("Webhook processed successfully");
    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");
        }
    }
    @SuppressWarnings("unchecked")
    private void handleOrderPaid(Map<String, Object> webhookPayload) {
        // Example: Extract and log details
        Map<String, Object> payload = (Map<String, Object>) webhookPayload.get("payload");

        if (payload != null) {
            Map<String, Object> paymentDetails = (Map<String, Object>) payload.get("payment");

            if (paymentDetails != null) {
                if (paymentDetails != null) {
                    String paymentId = (String) paymentDetails.get("id");
                    Double amount = Double.valueOf((Integer) paymentDetails.get("amount")) / 100.0; // Convert paise to rupees
                    String userId = (String) paymentDetails.get("notes.user_id"); // Use Razorpay's "notes" field for custom data
        
                    // Save payment details in the database
                    Payment payment = new Payment();
                    payment.setPaymentId(paymentId);
                    payment.setStatus("Success");
                    payment.setAmount(amount);
                    payment.setUserId(userId);
        
                    paymentService.createPayment(payment);
        
                    System.out.println("Payment details saved successfully: " + paymentId);
                } else {
                    System.out.println("Payment details are missing in the payload.");
                }
            } else {
                System.out.println("Payload is missing in the webhook data.");
            }}
}
    

    @SuppressWarnings("unchecked")
    private void handlePaymentFailed(Map<String, Object> webhookPayload) {
        // Example: Extract and log details
        Map<String, Object> payload = (Map<String, Object>) webhookPayload.get("payload");

        if (payload != null) {
            Map<String, Object> paymentDetails = (Map<String, Object>) payload.get("payment");

            if (paymentDetails != null) {
                System.out.println("Payment Failed: " + paymentDetails.get("id"));
            } else {
                System.out.println("Payment details are missing in the payload.");
            }
        } else {
            System.out.println("Payload is missing in the webhook data.");
        }
    }
}
