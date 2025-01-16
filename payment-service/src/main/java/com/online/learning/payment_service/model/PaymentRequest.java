package com.online.learning.payment_service.model;

public class PaymentRequest {

    private Long amount; // Amount in cents (e.g., $10.00 = 1000 cents)
    private String userId; // ID of the user making the payment

    // Getters and Setters
    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
