package com.online.learning.gateway_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import java.util.List;

@Configuration
public class WebConfig {

    // CorsWebFilter is the REACTIVE equivalent of WebMvcConfigurer's CORS setup.
    // Spring Cloud Gateway runs on WebFlux (reactive), so the old servlet-style
    // WebMvcConfigurer.addCorsMappings() never actually runs here — this is the
    // version that works for a reactive app.
    @Bean
    public CorsWebFilter corsWebFilter() {

        // Holds all the CORS rules: who can call this API, with what methods, etc.
        CorsConfiguration config = new CorsConfiguration();

        // Only allow requests coming from the React frontend (localhost:3000).
        // Without this, the browser blocks the request before it even reaches us.
        config.setAllowedOrigins(List.of("http://localhost:3000"));

        // Which HTTP methods the frontend is allowed to use against this gateway.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

        // "*" means accept any request headers (e.g. Content-Type, Authorization).
        config.setAllowedHeaders(List.of("*"));

        // Allows cookies / Authorization headers to be sent cross-origin.
        // Needed since we send a JWT in the Authorization header for protected routes.
        config.setAllowCredentials(true);

        // Maps the CORS rules above to every path ("/**") this gateway handles.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // Wrap it in a CorsWebFilter bean — Spring picks this up automatically
        // and applies it to every incoming request before routing happens.
        return new CorsWebFilter(source);
    }
}