package com.example.SmartEcommercePlatform.Controller;

import com.example.SmartEcommercePlatform.Dto.ProductRequestDTO;
import com.example.SmartEcommercePlatform.Dto.ProductResponseDTO;
import com.example.SmartEcommercePlatform.Entity.Product;
import com.example.SmartEcommercePlatform.Repository.ProductRepository;
import com.example.SmartEcommercePlatform.Response.ApiResponse;
import com.example.SmartEcommercePlatform.Service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Create a new product", description = "Requires ADMIN role.")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(
            @Valid @RequestBody ProductRequestDTO requestDto) {

        ProductResponseDTO savedProduct = productService.createProduct(requestDto);

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(
                new ApiResponse<>("Product created successfully", savedProduct)
        );
    }

    @Operation(summary = "Get all products", description = "Returns a paginated list of products.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> getAllProducts(Pageable pageable) {

        Page<ProductResponseDTO> products = productService.getAllProducts(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>("Products fetched successfully", products)
        );
    }


    @Operation(summary = "Get a single product by ID")
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }


    @Operation(summary = "Update an existing product", description = "Requires ADMIN role.")
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return productService.updateProduct(id, updatedProduct);
    }

    @Operation(summary = "Delete a product", description = "Requires ADMIN role.")
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "Product deleted successfully";
    }
}
