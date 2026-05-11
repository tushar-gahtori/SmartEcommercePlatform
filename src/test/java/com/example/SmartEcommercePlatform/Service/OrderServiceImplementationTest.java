package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.OrderItemRequestDTO;
import com.example.SmartEcommercePlatform.Dto.OrderRequestDTO;
import com.example.SmartEcommercePlatform.Dto.OrderResponseDTO;
import com.example.SmartEcommercePlatform.Entity.Order;
import com.example.SmartEcommercePlatform.Entity.Product;
import com.example.SmartEcommercePlatform.Entity.User;
import com.example.SmartEcommercePlatform.Repository.OrderRepository;
import com.example.SmartEcommercePlatform.Repository.ProductRepository;
import com.example.SmartEcommercePlatform.Repository.UserRepository;
import com.example.SmartEcommercePlatform.Service.Implementation.OrderServiceImplementation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplementationTest {

    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private KafkaProducerService kafkaProducerService; // Faking Kafka so we don't actually send messages during a test

    @InjectMocks private OrderServiceImplementation orderService;

    @Test
    void createOrder_Success_ReducesStock() {
        // 1. ARRANGE
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("buyer@test.com");

        Product mockProduct = new Product();
        mockProduct.setId(50L);
        mockProduct.setName("Headphones");
        mockProduct.setPrice(200.0);
        mockProduct.setStock(10); // Starts with 10

        OrderItemRequestDTO itemReq = new OrderItemRequestDTO();
        itemReq.setProductId(50L);
        itemReq.setQuantity(3); // Buying 3

        OrderRequestDTO request = new OrderRequestDTO();
        request.setItems(List.of(itemReq));

        when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(50L)).thenReturn(Optional.of(mockProduct));

        // Mocking the save to just return the order that was passed to it, but we give it a fake ID so our DTO mapper doesn't fail
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(999L);
            return savedOrder;
        });

        // 2. ACT
        OrderResponseDTO response = orderService.createOrder(request, "buyer@test.com");

        // 3. ASSERT
        assertNotNull(response);
        assertEquals(999L, response.getOrderId());
        assertEquals(600.0, response.getTotalAmount()); // 3 * $200

        // CRITICAL CHECK: Did the stock actually reduce?
        assertEquals(7, mockProduct.getStock()); // 10 - 3 = 7

        // Verify product was updated in the DB
        verify(productRepository, times(1)).save(mockProduct);
        verify(orderRepository, times(1)).save(any(Order.class));

    }
}