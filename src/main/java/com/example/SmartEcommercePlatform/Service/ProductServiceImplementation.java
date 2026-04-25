package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.ProductRequestDTO;
import com.example.SmartEcommercePlatform.Dto.ProductResponseDTO;
import com.example.SmartEcommercePlatform.Entity.Product;
import com.example.SmartEcommercePlatform.Repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImplementation implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {

        Product product = modelMapper.map(dto, Product.class);
        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductResponseDTO.class);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Product not found with id: "+id));
    }

    @Override
    public Product updateProduct(Long id, Product updatedProduct) {

        Product existingProduct = getProductById(id);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());

        return  productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    @Override
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {

        // 1. Fetch the raw Page of entities from the database
        Page<Product> productPage = productRepository.findAll(pageable);

        // 2. Map the entities to DTOs.
        // The .map() function automatically preserves the pagination metadata (totalPages, totalElements, etc.)!
        return productPage.map(product -> {
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setPrice(product.getPrice());
            dto.setStock(product.getStock());
            return dto;
        });
    }
}
