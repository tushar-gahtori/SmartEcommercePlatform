package com.example.SmartEcommercePlatform.Service.Decorator;

public class BasePriceCalculator implements PriceCalculator {
    @Override
    public double calculate(double basePrice) {
        return basePrice;
    }

    @Override
    public String getBreakdown() {
        return "Base Price";
    }
}