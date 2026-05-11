package com.example.SmartEcommercePlatform.Service.Implementation;

import com.example.SmartEcommercePlatform.Dto.PaginatedResponse;
import com.example.SmartEcommercePlatform.Dto.ProductRequestDTO;
import com.example.SmartEcommercePlatform.Dto.ProductResponseDTO;
import com.example.SmartEcommercePlatform.Entity.Product;
import com.example.SmartEcommercePlatform.Exception.ResourceNotFoundException;
import com.example.SmartEcommercePlatform.Repository.ProductRepository;
import com.example.SmartEcommercePlatform.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImplementation implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        Product product = modelMapper.map(dto, Product.class);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductResponseDTO.class);
    }

    @Override
    @Cacheable(value = "product_details", key = "#id")
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return modelMapper.map(product, ProductResponseDTO.class);
    }

    @Override
    @Cacheable(value = "products", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
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

    @Override
    @CacheEvict(value = {"products", "product_details"}, allEntries = true)
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        existingProduct.setName(dto.getName());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setStock(dto.getStockQuantity());
        return modelMapper.map(productRepository.save(existingProduct), ProductResponseDTO.class);
    }

    @Override
    @CacheEvict(value = {"products", "product_details"}, allEntries = true)
    public void deleteProduct(Long id) {
        if(!productRepository.existsById(id)) throw new ResourceNotFoundException("Product not found");
        productRepository.deleteById(id);
    }
}