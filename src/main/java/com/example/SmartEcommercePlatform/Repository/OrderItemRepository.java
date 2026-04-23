package com.example.SmartEcommercePlatform.Repository;

import com.example.SmartEcommercePlatform.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}