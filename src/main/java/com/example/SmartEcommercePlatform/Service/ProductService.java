package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.PaginatedResponse;
import com.example.SmartEcommercePlatform.Dto.ProductRequestDTO;
import com.example.SmartEcommercePlatform.Dto.ProductResponseDTO;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO dto);

    PaginatedResponse<ProductResponseDTO> getAllProducts(Pageable pageable);

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO updatedProduct);

    void deleteProduct(Long id);
}