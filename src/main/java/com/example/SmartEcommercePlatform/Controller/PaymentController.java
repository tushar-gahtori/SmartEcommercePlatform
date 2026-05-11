package com.example.SmartEcommercePlatform.Controller;

import com.example.SmartEcommercePlatform.Response.ApiResponse;

import com.example.SmartEcommercePlatform.Strategy.PaymentFactory;
import com.example.SmartEcommercePlatform.Strategy.PaymentStrategy;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFactory paymentFactory;

    @Operation(summary = "Process Payment", description = "Test the Strategy Pattern. Valid methods: CREDIT_CARD, GOOGLE_PAY")
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<String>> processPayment(
            @RequestParam String method,
            @RequestParam double amount) {

        PaymentStrategy strategy = paymentFactory.getStrategy(method);
        boolean success = strategy.processPayment(amount);
        if (success) {
            return ResponseEntity.ok(new ApiResponse<>("Payment processed successfully using " + method, null, 200));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Payment failed", null, 400));
        }
    }
}