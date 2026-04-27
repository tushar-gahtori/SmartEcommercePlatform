package com.example.SmartEcommercePlatform.Controller;

import com.example.SmartEcommercePlatform.Dto.PaginatedResponse;
import com.example.SmartEcommercePlatform.Dto.ProductRequestDTO;
import com.example.SmartEcommercePlatform.Dto.ProductResponseDTO;
import com.example.SmartEcommercePlatform.Response.ApiResponse;
import com.example.SmartEcommercePlatform.Service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

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
                new ApiResponse<>("Product created successfully", savedProduct, 201)
        );
    }

    @Operation(summary = "Get all products", description = "Returns a paginated list of products.")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponseDTO>>> getAllProducts(Pageable pageable) {

        PaginatedResponse<ProductResponseDTO> products = productService.getAllProducts(pageable);

        return ResponseEntity.ok(
                new ApiResponse<>("Products fetched successfully", products, 200)
        );
    }

    @Operation(summary = "Get a single product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(@PathVariable Long id) {
        ProductResponseDTO dto = productService.getProductById(id);
        return ResponseEntity.ok(
                new ApiResponse<>("Product fetched successfully", dto, 200)
        );
    }

    @Operation(summary = "Update an existing product", description = "Requires ADMIN role.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO requestDto) {

        ProductResponseDTO updatedProduct = productService.updateProduct(id, requestDto);

        return ResponseEntity.ok(
                new ApiResponse<>("Product updated successfully", updatedProduct, 200)
        );
    }

    @Operation(summary = "Delete a product", description = "Requires ADMIN role.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(
                new ApiResponse<>("Product deleted successfully", null, 200)
        );
    }
}