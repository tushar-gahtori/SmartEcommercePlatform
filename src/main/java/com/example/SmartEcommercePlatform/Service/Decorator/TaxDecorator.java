package com.example.SmartEcommercePlatform.Service.Decorator;

public class TaxDecorator extends PriceDecorator {
    public TaxDecorator(PriceCalculator customCalculator) {
        super(customCalculator);
    }

    @Override
    public double calculate(double basePrice) {
        return super.calculate(basePrice) * 1.08;
    }

    @Override
    public String getBreakdown() {
        return super.getBreakdown() + " + 8% State Tax";
    }
}
