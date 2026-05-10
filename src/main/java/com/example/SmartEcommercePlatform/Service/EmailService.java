package com.example.SmartEcommercePlatform.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender; // Real Spring Mail Tool

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOrderConfirmation(String toEmail, Long orderId, double totalAmount) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Order Confirmation #" + orderId);
            message.setText("Thank you! Your order of $" + totalAmount + " has been placed successfully.");

            mailSender.send(message);
            System.out.println("✅ REAL EMAIL SENT TO: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ MAIL ERROR: " + e.getMessage());
        }
    }
}