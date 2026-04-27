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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImplementation implements OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request,String userEmail) {

        // 1. Fetch User
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

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

        for (Map.Entry<Long, Integer> entry : consolidatedCart.entrySet()) {
            Long productId = entry.getKey();
            int totalQuantityRequired = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

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
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItems.add(orderItem);
        }

        // 5. Save Order and Items
        order.setItems(orderItems); // Note: Using setOrderItems() to match your implementation
        Order savedOrder = orderRepository.save(order);

        // 6. Map to clean Response DTO
        return mapToResponse(savedOrder);
    }

    @Override
    public List<OrderResponseDTO> getMyOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Order> userOrders = orderRepository.findByUser(user);

        return userOrders.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // 🧠 SMART MAPPER: Automatically calculates the total amount for ANY order passed to it!
    private OrderResponseDTO mapToResponse(Order order) {
        List<OrderItemResponseDTO> itemDTOs = new ArrayList<>();
        double calculatedTotal = 0;

        for (OrderItem item : order.getItems()) { // Note: Using getOrderItems() to match your implementation
            OrderItemResponseDTO dto = new OrderItemResponseDTO();
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPriceAtPurchase());

            dto.setPrice(item.getProduct().getPrice());

            itemDTOs.add(dto);

            // Add to the running total
            calculatedTotal += item.getPriceAtPurchase() * item.getQuantity();
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