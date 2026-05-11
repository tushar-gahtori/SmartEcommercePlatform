package com.example.SmartEcommercePlatform.Service.Implementation;

import com.example.SmartEcommercePlatform.Dto.CartItemRequestDTO;
import com.example.SmartEcommercePlatform.Dto.CartResponseDTO;
import com.example.SmartEcommercePlatform.Entity.Cart;
import com.example.SmartEcommercePlatform.Entity.CartItem;
import com.example.SmartEcommercePlatform.Entity.Product;
import com.example.SmartEcommercePlatform.Entity.User;
import com.example.SmartEcommercePlatform.Exception.BadRequestException;
import com.example.SmartEcommercePlatform.Exception.ResourceNotFoundException;
import com.example.SmartEcommercePlatform.Repository.CartRepository;
import com.example.SmartEcommercePlatform.Repository.ProductRepository;
import com.example.SmartEcommercePlatform.Repository.UserRepository;
import com.example.SmartEcommercePlatform.Service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImplementation implements CartService {

     private final UserRepository userRepository;
     private final CartRepository cartRepository;
     private final ProductRepository productRepository;

     private Cart getOrCreateCart(User user) {
         return cartRepository.findByUser(user).orElseGet(()->{
             Cart newCart = new Cart();
             newCart.setUser(user);
             newCart.setTotalCartPrice(0.0);
             return cartRepository.save(newCart);
         });
     }


     @Override
    public CartResponseDTO getMyCart(String userEmail) {
         User user=userRepository.findByEmail(userEmail)
                 .orElseThrow(()-> new ResourceNotFoundException("User not Found"));
         Cart cart=getOrCreateCart(user);
         return mapToCartResponse(cart);
     }


     @Transactional
    @Override
    public CartResponseDTO addItemToCart(String userEmail, CartItemRequestDTO dto)
     {
         User user=userRepository.findByEmail(userEmail)
                 .orElseThrow(()->new ResourceNotFoundException("User not Found"));
         Product product=productRepository.findById(dto.getProductId())
                 .orElseThrow(()->new ResourceNotFoundException("Product not Found"));
         if(product.getStock()<dto.getQuantity())
         {
             throw new BadRequestException("Not enough stock available!");
         }
         Cart cart=getOrCreateCart(user);
         Optional<CartItem> existingItemOpt=cart.getItems().stream()
                 .filter(item->item.getProduct().getId().equals(product.getId()))
                 .findFirst();
         if (existingItemOpt.isPresent()) {
             CartItem existingItem = existingItemOpt.get();
             int newQuantity = existingItem.getQuantity() + dto.getQuantity();
             if (product.getStock() < newQuantity) {
                 throw new BadRequestException("Cannot add more. Exceeds available stock!");
             }
             existingItem.setQuantity(newQuantity);
             existingItem.setTotalPrice(existingItem.getQuantity() * existingItem.getUnitPrice());
         } else {
             CartItem newItem = new CartItem();
             newItem.setCart(cart);
             newItem.setProduct(product);
             newItem.setQuantity(dto.getQuantity());
             newItem.setUnitPrice(product.getPrice());
             newItem.setTotalPrice(dto.getQuantity() * product.getPrice());
             cart.getItems().add(newItem);
         }
         recalculateCartTotal(cart);
         Cart savedCart = cartRepository.save(cart);
         return mapToCartResponse(savedCart);
     }


    @Transactional
    @Override
    public CartResponseDTO removeItemFromCart(String userEmail, Long productId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        boolean exists = cart.getItems().stream().anyMatch(item -> item.getProduct().getId().equals(productId));
        if (!exists) {
            throw new ResourceNotFoundException("Product not found in your cart");
        }
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        recalculateCartTotal(cart);
        Cart savedCart = cartRepository.save(cart);
        return mapToCartResponse(savedCart);
    }


    @Transactional
    @Override
    public void clearCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = cartRepository.findByUser(user).orElse(null);
        if (cart != null) {
            cart.getItems().clear();
            cart.setTotalCartPrice(0.0);
            cartRepository.save(cart);
        }
    }


    private void recalculateCartTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        cart.setTotalCartPrice(total);
    }


    private CartResponseDTO mapToCartResponse(Cart cart) {
        CartResponseDTO response = new CartResponseDTO();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUser().getId());
        response.setTotalCartPrice(cart.getTotalCartPrice());
        List<CartResponseDTO.CartItemResponseDTO> itemDTOs = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            CartResponseDTO.CartItemResponseDTO itemDto = new CartResponseDTO.CartItemResponseDTO();
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setUnitPrice(item.getUnitPrice());
            itemDto.setTotalPrice(item.getTotalPrice());
            itemDTOs.add(itemDto);
        }
        response.setItems(itemDTOs);
        return response;
    }
}
