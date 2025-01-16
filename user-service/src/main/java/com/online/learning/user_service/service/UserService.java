package com.online.learning.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.online.learning.user_service.model.User;
import com.online.learning.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

     // Save a new user with hashed password
     public User saveUser(User user) {
        user.setPassword(hashPassword(user.getPassword())); // Hash the password before saving
        return userRepository.save(user);
    }

    // Hash password using BCrypt
    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Authenticate user by email and password
    public boolean authenticateUser(String email, String password) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Compare raw password with hashed password
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
}
