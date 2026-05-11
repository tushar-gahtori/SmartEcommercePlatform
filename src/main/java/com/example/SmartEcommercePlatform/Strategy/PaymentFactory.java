package com.example.SmartEcommercePlatform.Strategy;

import com.example.SmartEcommercePlatform.Exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentFactory {

    // Spring automatically injects all beans implementing PaymentStrategy into this list
    private final List<PaymentStrategy> strategies;

    public PaymentStrategy getStrategy(String methodName) {
        return strategies.stream()
                .filter(strategy -> strategy.getMethodName().equalsIgnoreCase(methodName))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Unsupported payment method: " + methodName));
    }
}