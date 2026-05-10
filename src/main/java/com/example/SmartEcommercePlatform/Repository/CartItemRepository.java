package com.example.SmartEcommercePlatform.Repository;

import com.example.SmartEcommercePlatform.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

}
