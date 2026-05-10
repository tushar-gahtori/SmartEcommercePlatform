package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.OrderEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper objectMapper;

    private static final String TOPIC = "order-events";

    public void sendOrderConfirmationEvent(Long orderId, String userEmail, double totalAmount) {
        try {
            OrderEventDTO event = new OrderEventDTO(
                    UUID.randomUUID().toString(),
                    orderId,
                    userEmail,
                    totalAmount,
                    "ORDER_PLACED"
            );

            // 🔥 FIX: Hand the 'event' OBJECT directly to Kafka.
            // Spring's JsonSerializer (configured in application.properties) will handle the JSON conversion.
            kafkaTemplate.send(TOPIC, event).whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("🚀 PRODUCER SUCCESS: Sent Order #" + orderId + " to Kafka.");
                } else {
                    System.err.println("🚀 PRODUCER ERROR: " + ex.getMessage());
                }
            });

        } catch (Exception e) {
            System.err.println("❌ PRODUCER ERROR: " + e.getMessage());
        }
    }
}