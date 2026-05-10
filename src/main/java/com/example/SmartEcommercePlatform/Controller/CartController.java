package com.example.SmartEcommercePlatform.Controller;

import com.example.SmartEcommercePlatform.Dto.CartItemRequestDTO;
import com.example.SmartEcommercePlatform.Dto.CartResponseDTO;
import com.example.SmartEcommercePlatform.Response.ApiResponse;
import com.example.SmartEcommercePlatform.Service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get my cart", description = "Fetches the active shopping cart for the logged-in user.")
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponseDTO>> getMyCart(Principal principal) {
        CartResponseDTO cart = cartService.getMyCart(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Cart fetched successfully", cart, 200));
    }

    @Operation(summary = "Add item to cart", description = "Adds a product or increases its quantity in the cart.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addItemToCart(
            @Valid @RequestBody CartItemRequestDTO dto, Principal principal) {
        CartResponseDTO cart = cartService.addItemToCart(principal.getName(), dto);
        return ResponseEntity.ok(new ApiResponse<>("Item added to cart", cart, 200));
    }

    @Operation(summary = "Remove item from cart", description = "Completely removes a product from the cart.")
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> removeItemFromCart(
            @PathVariable Long productId, Principal principal) {
        CartResponseDTO cart = cartService.removeItemFromCart(principal.getName(), productId);
        return ResponseEntity.ok(new ApiResponse<>("Item removed from cart", cart, 200));
    }

    @Operation(summary = "Clear cart", description = "Empties the entire shopping cart.")
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>("Cart cleared successfully", null, 200));
    }
}