package com.example.SmartEcommercePlatform.Service.Decorator;

public class VipDiscountDecorator extends PriceDecorator {
    public VipDiscountDecorator(PriceCalculator customCalculator) {
        super(customCalculator);
    }

    @Override
    public double calculate(double basePrice) {
        // Takes 10% off whatever the current wrapped price is
        return super.calculate(basePrice) * 0.90;
    }

    @Override
    public String getBreakdown() {
        return super.getBreakdown() + " - 10% VIP Discount";
    }
}