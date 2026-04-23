package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.OrderItemResponseDTO;
import com.example.SmartEcommercePlatform.Dto.OrderRequestDTO;
import com.example.SmartEcommercePlatform.Dto.OrderResponseDTO;
import com.example.SmartEcommercePlatform.Entity.Order;
import com.example.SmartEcommercePlatform.Entity.OrderItem;
import com.example.SmartEcommercePlatform.Entity.Product;
import com.example.SmartEcommercePlatform.Entity.User;
import com.example.SmartEcommercePlatform.Repository.OrderRepository;
import com.example.SmartEcommercePlatform.Repository.ProductRepository;
import com.example.SmartEcommercePlatform.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public OrderResponseDTO createOrder(OrderRequestDTO request){

        User user = userRepository.findById(request.getUserId()).orElseThrow();

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> items = request.getProductIds().stream().map(productId -> {

            Product product = productRepository.findById(productId).orElseThrow();

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setOrder(order);
            item.setQuantity(1);

            return item;

        }).toList();

        order.setItems(items);

        Order savedOrder = orderRepository.save(order);

        return mapToResponseDTO(savedOrder);
    }

    private OrderResponseDTO mapToResponseDTO(Order order){

        OrderResponseDTO dto = new OrderResponseDTO();

        dto.setOrderId(order.getId());
        dto.setUserId(order.getUser().getId());

        List<OrderItemResponseDTO> itemDTOs=order.getItems().stream().map(item->{

            OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();

            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setPrice(item.getProduct().getPrice());
            itemDTO.setQuantity(item.getQuantity());

            return itemDTO;
        }).toList();

        dto.setItems(itemDTOs);

        double total=itemDTOs.stream()
                .mapToDouble(i->i.getPrice()*i.getQuantity())
                .sum();

        dto.setTotalAmount(total);
        return dto;
    }
}
