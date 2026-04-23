package com.example.SmartEcommercePlatform.Repository;

import com.example.SmartEcommercePlatform.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
