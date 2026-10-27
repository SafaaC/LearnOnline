package com.online.learning.registration_service.client;
;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service", path = "/internal/payments")
public interface PaymentClient {

    @GetMapping("/verify")
    boolean verify(@RequestParam("userId") String userId,
                   @RequestParam("courseId") Long courseId);
}