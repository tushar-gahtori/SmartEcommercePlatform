package com.example.SmartEcommercePlatform.Service.Decorator;

public abstract class PriceDecorator implements PriceCalculator {
    protected final PriceCalculator customCalculator;

    public PriceDecorator(PriceCalculator customCalculator) {
        this.customCalculator = customCalculator;
    }

    @Override
    public double calculate(double basePrice) {
        return customCalculator.calculate(basePrice);
    }

    @Override
    public String getBreakdown() {
        return customCalculator.getBreakdown();
    }
}