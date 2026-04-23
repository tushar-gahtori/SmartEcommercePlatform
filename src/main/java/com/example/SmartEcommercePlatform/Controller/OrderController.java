package com.example.SmartEcommercePlatform.Controller;

import com.example.SmartEcommercePlatform.Dto.OrderRequestDTO;
import com.example.SmartEcommercePlatform.Dto.OrderResponseDTO;
import com.example.SmartEcommercePlatform.Entity.Order;
import com.example.SmartEcommercePlatform.Response.ApiResponse;
import com.example.SmartEcommercePlatform.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(@RequestBody OrderRequestDTO request)
    {

        OrderResponseDTO response=orderService.createOrder(request);

        return ResponseEntity.ok(new ApiResponse<>("Order created sucessfully",response,200));

    }
}
