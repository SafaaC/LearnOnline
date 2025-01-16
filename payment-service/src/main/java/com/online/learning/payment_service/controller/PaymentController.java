package com.online.learning.payment_service.controller;
import com.online.learning.payment_service.model.Payment;
import com.online.learning.payment_service.model.PaymentRequest;
import com.online.learning.payment_service.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    /**
     * Endpoint to create a payment intent and save payment details.
     *
     * @param paymentRequest The payment request details
     * @return The client secret for payment confirmation
     * @throws StripeException if there's an error creating the payment intent
     */
    @PostMapping("/create-payment-intent")
    public String createPaymentIntent(@RequestBody PaymentRequest paymentRequest) throws StripeException {
        // Set Stripe API key
        Stripe.apiKey = stripeApiKey;

        // Create payment intent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(paymentRequest.getAmount())
                .setCurrency("usd")
                .build();
        PaymentIntent intent = PaymentIntent.create(params);

        // Save payment details
        Payment payment = new Payment();
        payment.setPaymentId(intent.getId());
        payment.setStatus(intent.getStatus());
        payment.setAmount((double) paymentRequest.getAmount());
        payment.setCurrency("usd");
        payment.setUserId(paymentRequest.getUserId());
        paymentService.savePayment(payment);

        return intent.getClientSecret();
    }

    /**
     * Endpoint to retrieve payment details by ID.
     *
     * @param id The payment ID
     * @return Payment details
     */
    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }
}