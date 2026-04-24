package com.example.SmartEcommercePlatform.Dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDTO {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid // This tells Spring to also validate the OrderItemRequestDTOs inside the list!
    private List<OrderItemRequestDTO> items;
}