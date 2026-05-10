package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.CartResponseDTO;
import com.example.SmartEcommercePlatform.Dto.CartItemRequestDTO;
import com.example.SmartEcommercePlatform.Entity.Cart;

public interface CartService {
    CartResponseDTO getMyCart(String userEmail);
    CartResponseDTO addItemToCart(String userEmail,CartItemRequestDTO dto);
    CartResponseDTO removeItemFromCart(String userEmail, Long productId);
    void clearCart(String userEmail);

}
