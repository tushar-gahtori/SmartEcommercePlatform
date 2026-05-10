package com.example.SmartEcommercePlatform.Repository;

import com.example.SmartEcommercePlatform.Entity.Cart;
import com.example.SmartEcommercePlatform.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUser(User user);
}
