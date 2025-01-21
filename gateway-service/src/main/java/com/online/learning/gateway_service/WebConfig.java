package com.online.learning.gateway_service;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // Allow cross-origin requests from React app running on http://localhost:3000
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Your React app URL
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true); // Allow cookies and other credentials
    }
}