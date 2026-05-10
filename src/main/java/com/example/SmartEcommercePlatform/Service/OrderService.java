package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.OrderRequestDTO;
import com.example.SmartEcommercePlatform.Dto.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO request,String userEmail);
    List<OrderResponseDTO> getMyOrders(String userEmail);
    OrderResponseDTO checkoutMyCart(String userEmail);
}
