package com.example.SmartEcommercePlatform.Service.Decorator;

public interface PriceCalculator {
    double calculate(double basePrice);
    String getBreakdown();
}
