package com.example.SmartEcommercePlatform.Repository;

import com.example.SmartEcommercePlatform.Entity.Order;
import com.example.SmartEcommercePlatform.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

}