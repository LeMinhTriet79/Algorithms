package com.mavis.mock;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @GetMapping("/{id}")
    public String getOrder(@PathVariable String id) {
        return "Order Data";
    }
}