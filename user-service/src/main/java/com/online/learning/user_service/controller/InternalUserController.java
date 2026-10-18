package com.online.learning.user_service.controller;

import com.online.learning.user_service.model.User;
import com.online.learning.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{username}/email")
    public ResponseEntity<String> getEmail(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null || user.getEmail() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user.getEmail());
    }
}