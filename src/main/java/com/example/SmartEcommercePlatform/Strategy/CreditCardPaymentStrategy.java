package com.example.SmartEcommercePlatform.Strategy;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreditCardPaymentStrategy implements PaymentStrategy {

    // Pulls the secret key from your application.properties
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    // This runs automatically when Spring boots up to configure the Stripe SDK
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        log.info("Stripe SDK Initialized.");
    }

    @Override
    public boolean processPayment(double amount) {
        log.info("Initiating Stripe PaymentIntent for amount: ${}", amount);

        try {
            // Stripe requires amounts to be in the smallest currency unit (e.g., cents for USD)
            long amountInCents = (long) (amount * 100);

            // Build the payment request
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("usd")
                    .setPaymentMethod("pm_card_visa") // Simulates a successful Visa card in test mode
                    .setConfirm(true) // Automatically confirm the payment for this demonstration
                    .setReturnUrl("https://your-website.com/checkout/success") // Required by Stripe for confirmed intents
                    .build();

            // Send the request to Stripe's servers
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Check if the payment succeeded
            if ("succeeded".equals(paymentIntent.getStatus())) {
                log.info("✅ Stripe Payment Succeeded! Transaction ID: {}", paymentIntent.getId());
                return true;
            } else {
                log.warn("⚠️ Stripe Payment requires further action or failed. Status: {}", paymentIntent.getStatus());
                return false;
            }

        } catch (StripeException e) {
            // Catches API errors, declined cards, network issues, etc.
            log.error("❌ Stripe API Error: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getMethodName() {
        return "CREDIT_CARD";
    }
}