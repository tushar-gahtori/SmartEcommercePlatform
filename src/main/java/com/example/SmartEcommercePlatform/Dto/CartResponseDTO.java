package com.example.SmartEcommercePlatform.Dto;

import lombok.Data;
import java.util.List;

@Data
public class CartResponseDTO {
    private Long cartId;
    private Long userId;
    private List<CartItemResponseDTO> items;
    private double totalCartPrice;


    @Data
    public static class CartItemResponseDTO {
        private Long productId;
        private String productName;
        private int quantity;
        private double unitPrice;
        private double totalPrice;
    }
}