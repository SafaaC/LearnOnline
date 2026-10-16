package com.online.learning.registration_service.controller;

import com.online.learning.registration_service.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/{courseId}")
    public ResponseEntity<?> register(@PathVariable Long courseId, Authentication authentication) {
        String username = authentication.getName();
        try {
            return ResponseEntity.ok(registrationService.registerUserForCourse(username, courseId));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
}