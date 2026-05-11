package com.example.SmartEcommercePlatform.Controller;

import com.example.SmartEcommercePlatform.Dto.OrderRequestDTO;
import com.example.SmartEcommercePlatform.Dto.OrderResponseDTO;
import com.example.SmartEcommercePlatform.Response.ApiResponse;
import com.example.SmartEcommercePlatform.Security.Idempotent;
import com.example.SmartEcommercePlatform.Service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Idempotent
    @Operation(summary = "Place a new order")
    @Parameter(in = ParameterIn.HEADER, name = "Idempotency-Key", required = true, description = "Unique string (e.g., UUID-1234) to prevent duplicate orders") // <-- Add this!
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @Valid @RequestBody OrderRequestDTO dto, Principal principal) {
        OrderResponseDTO response = orderService.createOrder(dto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Order created successfully", response, 201));
    }

    @Idempotent
    @Operation(summary = "Checkout Active Cart")
    @Parameter(in = ParameterIn.HEADER, name = "Idempotency-Key", required = true, description = "Unique string (e.g., UUID-1234) to prevent duplicate orders") // <-- Add this!
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> checkoutCart(Principal principal) {
        OrderResponseDTO response = orderService.checkoutMyCart(principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Checkout successful! Order placed.", response, 201));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getMyOrders(Principal principal) {
        List<OrderResponseDTO> orders = orderService.getMyOrders(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Order history fetched successfully", orders, 200));
    }
}