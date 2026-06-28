package com.online.learning.course_service.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

@Service
public class JwtService {

    // MUST match user-service's JwtService key exactly
    private final Key signingKey = Keys.hmacShaKeyFor(
            "your_secret_key_must_be_at_least_32_bytes_long!!".getBytes(StandardCharsets.UTF_8)
    );

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return (List<String>) Jwts.parserBuilder().setSigningKey(signingKey).build()
                .parseClaimsJws(token).getBody().get("roles");
    }
}