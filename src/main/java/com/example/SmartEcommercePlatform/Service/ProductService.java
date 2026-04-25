package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.ProductRequestDTO;
import com.example.SmartEcommercePlatform.Dto.ProductResponseDTO;
import com.example.SmartEcommercePlatform.Entity.Product;
import com.example.SmartEcommercePlatform.Exception.ResourceNotFoundException;
import com.example.SmartEcommercePlatform.Repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO dto);

    Page<ProductResponseDTO> getAllProducts(Pageable pageable);

    Product getProductById(Long id);

    Product updateProduct(Long id, Product updatedProduct);

    void deleteProduct(Long id);
}
