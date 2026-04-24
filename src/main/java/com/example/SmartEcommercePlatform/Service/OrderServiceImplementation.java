package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.*;
import com.example.SmartEcommercePlatform.Entity.*;
import com.example.SmartEcommercePlatform.Exception.BadRequestException;
import com.example.SmartEcommercePlatform.Exception.ResourceNotFoundException;
import com.example.SmartEcommercePlatform.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImplementation implements OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {

        // 1. Fetch User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> orderItems = new ArrayList<>();

        // 🔥 FIX 2: CART CONSOLIDATION (Merge duplicate products)
        Map<Long, Integer> consolidatedCart = new HashMap<>();
        for (OrderItemRequestDTO item : request.getItems()) {
            consolidatedCart.put(
                    item.getProductId(),
                    consolidatedCart.getOrDefault(item.getProductId(), 0) + item.getQuantity()
            );
        }

        // 2. Loop through the CLEANED, consolidated cart
        for (Map.Entry<Long, Integer> entry : consolidatedCart.entrySet()) {
            Long productId = entry.getKey();
            int totalQuantityRequired = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

            // 🔥 FIX 1: GRACEFUL STOCK VALIDATION
            if (product.getStock() < totalQuantityRequired) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName() + ". Only " + product.getStock() + " left in stock.");
            }

            // 3. Deduct stock and save product
            product.setStock(product.getStock() - totalQuantityRequired);
            productRepository.save(product);

            // 4. Create Bridge Entity
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(totalQuantityRequired); // Use the merged quantity!

            orderItems.add(orderItem);
        }

        // 5. Save Order and Items
        order.setOrderItems(orderItems); // Note: Using setOrderItems() to match your implementation
        Order savedOrder = orderRepository.save(order);

        // 6. Map to clean Response DTO
        return mapToResponse(savedOrder);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponseDTO> responseList = new ArrayList<>();

        for (Order order : orders) {
            responseList.add(mapToResponse(order));
        }

        return responseList;
    }

    // 🧠 SMART MAPPER: Automatically calculates the total amount for ANY order passed to it!
    private OrderResponseDTO mapToResponse(Order order) {
        List<OrderItemResponseDTO> itemDTOs = new ArrayList<>();
        double calculatedTotal = 0;

        for (OrderItem item : order.getOrderItems()) { // Note: Using getOrderItems() to match your implementation
            OrderItemResponseDTO dto = new OrderItemResponseDTO();
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setQuantity(item.getQuantity());

            // Add price mapping if your OrderItemResponseDTO supports it
            // dto.setPrice(item.getProduct().getPrice());

            dto.setPrice(item.getProduct().getPrice());

            itemDTOs.add(dto);

            // Add to the running total
            calculatedTotal += (item.getProduct().getPrice() * item.getQuantity());
        }

        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setItems(itemDTOs);

        // Ensure your OrderResponseDTO has a setTotalAmount(double) method!
        response.setTotalAmount(calculatedTotal);

        return response;
    }
}