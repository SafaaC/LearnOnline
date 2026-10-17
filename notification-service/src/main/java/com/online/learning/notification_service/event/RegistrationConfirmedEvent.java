package com.online.learning.notification_service.event;

public record RegistrationConfirmedEvent(String username, Long courseId) {}