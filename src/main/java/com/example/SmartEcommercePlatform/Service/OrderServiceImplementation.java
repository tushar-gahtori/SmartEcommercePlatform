package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.*;
import com.example.SmartEcommercePlatform.Entity.*;
import com.example.SmartEcommercePlatform.Exception.ResourceNotFoundException;
import com.example.SmartEcommercePlatform.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImplementation implements OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDTO itemDTO : request.getItems()) {

            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setOrder(order);

            orderItems.add(item);
        }

        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

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

    private OrderResponseDTO mapToResponse(Order order) {

        List<OrderItemResponseDTO> itemDTOs = new ArrayList<>();

        for (OrderItem item : order.getOrderItems()) {
            OrderItemResponseDTO dto = new OrderItemResponseDTO();
            dto.setProductName(item.getProduct().getName());
            dto.setQuantity(item.getQuantity());
            itemDTOs.add(dto);
        }

        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setItems(itemDTOs);

        return response;
    }
}