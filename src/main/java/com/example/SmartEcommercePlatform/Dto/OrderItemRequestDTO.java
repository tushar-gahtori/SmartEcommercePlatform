package com.example.SmartEcommercePlatform.Dto;

import lombok.Data;

@Data
public class OrderItemRequestDTO {
    private Long productId;
    private int quantity;
}