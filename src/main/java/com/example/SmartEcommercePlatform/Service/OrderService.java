package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.OrderRequestDTO;
import com.example.SmartEcommercePlatform.Dto.OrderResponseDTO;
import com.example.SmartEcommercePlatform.Entity.*;
import com.example.SmartEcommercePlatform.Repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO request,String userEmail);

    List<OrderResponseDTO> getMyOrders(String userEmail);
}
