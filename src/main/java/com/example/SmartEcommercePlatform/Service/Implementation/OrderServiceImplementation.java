package com.example.SmartEcommercePlatform.Service.Implementation;

import com.example.SmartEcommercePlatform.Dto.*;
import com.example.SmartEcommercePlatform.Entity.*;
import com.example.SmartEcommercePlatform.Exception.BadRequestException;
import com.example.SmartEcommercePlatform.Exception.ResourceNotFoundException;
import com.example.SmartEcommercePlatform.Repository.*;
import com.example.SmartEcommercePlatform.Service.KafkaProducerService;
import com.example.SmartEcommercePlatform.Service.OrderService;
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
    private final CartRepository cartRepository;

    private final KafkaProducerService kafkaProducerService;

    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
        Order order = new Order();
        order.setUser(user);
        List<OrderItem> orderItems = new ArrayList<>();
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
            product.setStock(product.getStock() - totalQuantityRequired);
            productRepository.save(product);
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(totalQuantityRequired);
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        OrderResponseDTO response = mapToResponse(savedOrder);
        System.out.println("🚀 [ORDER SERVICE] Triggering Kafka Event for Order #" + response.getOrderId());
        kafkaProducerService.sendOrderConfirmationEvent(
                response.getOrderId(),
                userEmail,
                response.getTotalAmount()
        );
        return response;
    }


    @Transactional
    @Override
    public OrderResponseDTO checkoutMyCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("No active cart found"));
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty! Add products before checking out.");
        }
        OrderRequestDTO orderRequest = new OrderRequestDTO();
        List<OrderItemRequestDTO> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItemRequestDTO dto = new OrderItemRequestDTO();
            dto.setProductId(cartItem.getProduct().getId());
            dto.setQuantity(cartItem.getQuantity());
            orderItems.add(dto);
        }
        orderRequest.setItems(orderItems);
        OrderResponseDTO completedOrder = this.createOrder(orderRequest, userEmail);
        cart.getItems().clear();
        cart.setTotalCartPrice(0.0);
        cartRepository.save(cart);
        return completedOrder;
    }

    @Override
    public List<OrderResponseDTO> getMyOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Order> userOrders = orderRepository.findByUser(user);
        return userOrders.stream().map(this::mapToResponse).collect(Collectors.toList());
    }


    private OrderResponseDTO mapToResponse(Order order) {
        List<OrderItemResponseDTO> itemDTOs = new ArrayList<>();
        double calculatedTotal = 0;
        for (OrderItem item : order.getItems()) {
            OrderItemResponseDTO dto = new OrderItemResponseDTO();
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPriceAtPurchase());
            itemDTOs.add(dto);
            calculatedTotal += item.getPriceAtPurchase() * item.getQuantity();
        }
        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setItems(itemDTOs);
        response.setTotalAmount(calculatedTotal);
        return response;
    }
}