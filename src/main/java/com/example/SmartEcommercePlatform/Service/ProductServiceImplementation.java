package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.PaginatedResponse;
import com.example.SmartEcommercePlatform.Dto.ProductRequestDTO;
import com.example.SmartEcommercePlatform.Dto.ProductResponseDTO;
import com.example.SmartEcommercePlatform.Entity.Product;
import com.example.SmartEcommercePlatform.Exception.ResourceNotFoundException;
import com.example.SmartEcommercePlatform.Repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @CacheEvict(value = "products", allEntries = true)
    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        Product product = modelMapper.map(dto, Product.class);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductResponseDTO.class);
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return modelMapper.map(product, ProductResponseDTO.class);
    }

    @CacheEvict(value = "products", allEntries = true)
    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.setName(dto.getName());
        existingProduct.setPrice(dto.getPrice());
        // existingProduct.setDescription(dto.getDescription()); // Uncomment if your Entity has this
        existingProduct.setStock(dto.getStockQuantity());

        Product savedProduct = productRepository.save(existingProduct);
        return modelMapper.map(savedProduct, ProductResponseDTO.class);
    }

    @CacheEvict(value = "products", allEntries = true)
    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    @Cacheable(value = "products", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    @Override
    public PaginatedResponse<ProductResponseDTO> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductResponseDTO> content = productPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .toList();

        PaginatedResponse<ProductResponseDTO> response = new PaginatedResponse<>();
        response.setContent(content);
        response.setPageNumber(productPage.getNumber());
        response.setPageSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setLast(productPage.isLast());

        return response;
    }
}