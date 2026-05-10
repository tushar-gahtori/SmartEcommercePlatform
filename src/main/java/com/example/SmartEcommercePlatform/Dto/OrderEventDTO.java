package com.example.SmartEcommercePlatform.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventDTO {
    private String eventId;
    private Long orderId;
    private String userEmail;
    private double totalAmount;
    private String status;
}