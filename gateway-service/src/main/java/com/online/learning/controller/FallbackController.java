package com.online.learning.gateway_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {

    // @RequestMapping (not @GetMapping) so it answers GET, POST, PUT, DELETE alike.
    // A failed POST /courses/{id}/register is forwarded here with the SAME method.
    @RequestMapping("/fallback/courses")
    public ResponseEntity<Map<String, String>> courseFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Course service is temporarily unavailable. Try again shortly."));
    }

    @RequestMapping("/fallback/payment")
    public ResponseEntity<Map<String, String>> paymentFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "Payment service is temporarily unavailable. Your card was not charged."));
    }
}