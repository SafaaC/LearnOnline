package com.online.learning.payment_service.model;

public class PaymentRequest {

    private Long amount;  
    private String userId; // username of the person paying
    private Long courseId; // which course they're paying for

    public PaymentRequest() {}

    public PaymentRequest(Long amount, String userId, Long courseId) {
        this.amount = amount;
        this.userId = userId;
        this.courseId = courseId;
    }

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
}