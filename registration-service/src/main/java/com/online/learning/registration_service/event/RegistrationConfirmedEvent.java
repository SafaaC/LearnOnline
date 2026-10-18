package com.online.learning.registration_service.event;

public record RegistrationConfirmedEvent(String username, Long courseId) {}