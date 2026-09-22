package com.mavis.mock;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "payment-api")
public interface PaymentClient {
    
    @PostMapping("/api/payments/process")
    String processPayment();
}