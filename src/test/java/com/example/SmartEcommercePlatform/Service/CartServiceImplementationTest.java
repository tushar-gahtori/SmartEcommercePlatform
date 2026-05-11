package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.CartItemRequestDTO;
import com.example.SmartEcommercePlatform.Dto.CartResponseDTO;
import com.example.SmartEcommercePlatform.Entity.Cart;
import com.example.SmartEcommercePlatform.Entity.Product;
import com.example.SmartEcommercePlatform.Entity.User;
import com.example.SmartEcommercePlatform.Exception.BadRequestException;
import com.example.SmartEcommercePlatform.Repository.CartRepository;
import com.example.SmartEcommercePlatform.Repository.ProductRepository;
import com.example.SmartEcommercePlatform.Repository.UserRepository;
import com.example.SmartEcommercePlatform.Service.Implementation.CartServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Tells JUnit to enable Mockito
class CartServiceImplementationTest {

    // @Mock creates fake "dummy" versions of our database repositories.
    // We do this because unit tests should NOT connect to a real MySQL database.
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CartRepository cartRepository;

    // @InjectMocks creates the REAL service, but injects the FAKE databases into it.
    @InjectMocks private CartServiceImplementation cartService;

    private User mockUser;
    private Product mockProduct;

    // @BeforeEach runs before EVERY single test to set up fresh dummy data
    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@test.com");

        mockProduct = new Product();
        mockProduct.setId(100L);
        mockProduct.setName("Laptop");
        mockProduct.setPrice(1000.0);
        mockProduct.setStock(5); // 5 items in stock
    }

    @Test
    void addItemToCart_Success() {
        // 1. ARRANGE: Set up the rules for our fake databases
        CartItemRequestDTO request = new CartItemRequestDTO();
        request.setProductId(100L);
        request.setQuantity(2); // User wants to buy 2 laptops

        Cart emptyCart = new Cart();
        emptyCart.setUser(mockUser);
        emptyCart.setItems(new ArrayList<>());

        // "When the service asks for a user, return our mockUser"
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        // "When it asks for the product, return our mockProduct (which has 5 in stock)"
        when(productRepository.findById(100L)).thenReturn(Optional.of(mockProduct));
        // "When it looks for a cart, return the empty cart"
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(emptyCart));
        // "When it saves the cart, just return what was saved"
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. ACT: Actually call the method we are testing
        CartResponseDTO response = cartService.addItemToCart("test@test.com", request);

        // 3. ASSERT: Check if the result is exactly what we expect
        assertNotNull(response);
        assertEquals(2000.0, response.getTotalCartPrice()); // 2 laptops * $1000
        assertEquals(1, response.getItems().size()); // 1 type of item in cart

        // Verify that the save method was called exactly once on our fake DB
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addItemToCart_ThrowsException_WhenOutOfStock() {
        // 1. ARRANGE
        CartItemRequestDTO request = new CartItemRequestDTO();
        request.setProductId(100L);
        request.setQuantity(10); // User wants 10 laptops, but we only have 5!

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(100L)).thenReturn(Optional.of(mockProduct));

        // 2 & 3. ACT & ASSERT
        // We assert that calling this method WILL throw a BadRequestException
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            cartService.addItemToCart("test@test.com", request);
        });

        assertEquals("Not enough stock available!", exception.getMessage());

        // Verify we NEVER tried to save a bad cart to the database
        verify(cartRepository, never()).save(any());
    }
}
