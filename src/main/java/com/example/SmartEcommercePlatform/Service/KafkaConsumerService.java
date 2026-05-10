package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.OrderEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final EmailService emailService; //

    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void consumeOrderEvent(String message) {
        // 🔥 Print this immediately so we know the message arrived!
        System.out.println("📥 RAW KAFKA MESSAGE RECEIVED: " + message);

        try {
            OrderEventDTO event = objectMapper.readValue(message, OrderEventDTO.class);
            emailService.sendOrderConfirmation(event.getUserEmail(), event.getOrderId(), event.getTotalAmount());
        } catch (Exception e) {
            System.err.println("❌ CONSUMER ERROR: Failed to parse JSON -> " + e.getMessage());
        }
    }
}