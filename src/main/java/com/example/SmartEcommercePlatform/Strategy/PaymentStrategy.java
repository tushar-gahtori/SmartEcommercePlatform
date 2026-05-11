package com.example.SmartEcommercePlatform.Strategy;

public interface PaymentStrategy {
    boolean processPayment(double amount);
    String getMethodName();
}