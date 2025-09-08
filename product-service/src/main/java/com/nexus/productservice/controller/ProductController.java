package com.nexus.productservice.controller;

import com.nexus.productservice.dto.ProductRequestDto;
import com.nexus.productservice.dto.ProductResponseDto;
import com.nexus.productservice.service.ProductService;
import com.nexus.productservice.service.TokenBucketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    
    private final ProductService productService;
    
    // CREATE - POST /api/products
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto requestDto) {
        ProductResponseDto createdProduct = productService.createProduct(requestDto);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }
    
    // READ - GET /api/products
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        log.info("🌐 API REQUEST: GET /api/products - Attempting to fetch all products");
        long startTime = System.currentTimeMillis();

        List<ProductResponseDto> products = productService.getAllProducts();

        long duration = System.currentTimeMillis() - startTime;
        if (duration < 15) { // If response is very fast, likely from cache
            log.info("⚡ CACHE HIT: Response served in {}ms (likely from CACHE)", duration);
        } else {
            log.info("🐌 Possible CACHE MISS: Response took {}ms (likely from DATABASE)", duration);
        }

        log.info("🌐 API RESPONSE: Returning {} products to client", products.size());
        return ResponseEntity.ok(products);
    }
    
    // READ - GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable String id) {
        Optional<ProductResponseDto> product = productService.getProductById(id);
        return product.map(p -> ResponseEntity.ok(p))
                     .orElse(ResponseEntity.notFound().build());
    }
    
    // READ - GET /api/products/category/{category}
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@PathVariable String category) {
        List<ProductResponseDto> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
    
    // READ - GET /api/products/brand/{brand}
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByBrand(@PathVariable String brand) {
        List<ProductResponseDto> products = productService.getProductsByBrand(brand);
        return ResponseEntity.ok(products);
    }
    
    // READ - GET /api/products/search?name={name}
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(@RequestParam String name) {
        List<ProductResponseDto> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }
    
    // UPDATE - PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable String id, @RequestBody ProductRequestDto requestDto) {
        Optional<ProductResponseDto> updatedProduct = productService.updateProduct(id, requestDto);
        return updatedProduct.map(p -> ResponseEntity.ok(p))
                            .orElse(ResponseEntity.notFound().build());
    }
    
    // DELETE - DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        boolean deleted = productService.deleteProduct(id);
        return deleted ? ResponseEntity.noContent().build() 
                      : ResponseEntity.notFound().build();
    }
    
    // HARD DELETE - DELETE /api/products/{id}/hard
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteProduct(@PathVariable String id) {
        boolean deleted = productService.hardDeleteProduct(id);
        return deleted ? ResponseEntity.noContent().build() 
                      : ResponseEntity.notFound().build();
    }
}