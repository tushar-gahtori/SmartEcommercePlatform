package com.example.SmartEcommercePlatform.Controller;

import com.example.SmartEcommercePlatform.Dto.OrderRequestDTO;
import com.example.SmartEcommercePlatform.Dto.OrderResponseDTO;
import com.example.SmartEcommercePlatform.Entity.Order;
import com.example.SmartEcommercePlatform.Response.ApiResponse;
import com.example.SmartEcommercePlatform.Service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Place a new order",
            description = "Creates an order for the currently authenticated user. Deducts stock quantities from products."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @Valid @RequestBody OrderRequestDTO dto, Principal principal) {
        OrderResponseDTO response = orderService.createOrder(dto, principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Order created successfully", response, 201));
    }

    @Operation(
            summary = "Get All orders",
            description = "Fetches all past orders."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getAllOrders(Principal principal) {

        List<OrderResponseDTO> orders = orderService.getMyOrders(principal.getName());

        return ResponseEntity.ok(
                new ApiResponse<List<OrderResponseDTO>>("Orders fetched successfully", orders,200)
        );
    }

    @Operation(
            summary = "Get order history for current user",
            description = "Fetches all past orders belonging exclusively to the authenticated user. Uses the JWT to guarantee data isolation."
    )
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getMyOrders(Principal principal) {

        // principal.getName() automatically extracts the email from the JWT!
        List<OrderResponseDTO> orders = orderService.getMyOrders(principal.getName());

        return ResponseEntity.ok(
                new ApiResponse<>("Order history fetched successfully", orders)
        );
    }
}