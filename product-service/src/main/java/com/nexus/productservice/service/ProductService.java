package com.nexus.productservice.service;

import com.nexus.productservice.dto.ProductRequestDto;
import com.nexus.productservice.dto.ProductResponseDto;
import com.nexus.productservice.model.Product;
import com.nexus.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    // Create
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Product product = new Product(
            requestDto.getName(),
            requestDto.getDescription(),
            requestDto.getPrice(),
            requestDto.getQuantity(),
            requestDto.getCategory(),
            requestDto.getBrand()
        );
        
        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }
    
    // Read All
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    // Read by ID
    public Optional<ProductResponseDto> getProductById(String id) {
        return productRepository.findByIdAndActiveTrue(id)
                .map(this::mapToResponseDto);
    }
    
    // Read by Category
    public List<ProductResponseDto> getProductsByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    // Read by Brand
    public List<ProductResponseDto> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    // Search by Name
    public List<ProductResponseDto> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    
    // Update
    public Optional<ProductResponseDto> updateProduct(String id, ProductRequestDto requestDto) {
        Optional<Product> existingProduct = productRepository.findByIdAndActiveTrue(id);
        
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setName(requestDto.getName());
            product.setDescription(requestDto.getDescription());
            product.setPrice(requestDto.getPrice());
            product.setQuantity(requestDto.getQuantity());
            product.setCategory(requestDto.getCategory());
            product.setBrand(requestDto.getBrand());
            product.setUpdatedAt(LocalDateTime.now());
            
            Product updatedProduct = productRepository.save(product);
            return Optional.of(mapToResponseDto(updatedProduct));
        }
        
        return Optional.empty();
    }
    
    // Delete (Soft Delete)
    public boolean deleteProduct(String id) {
        Optional<Product> product = productRepository.findByIdAndActiveTrue(id);
        
        if (product.isPresent()) {
            Product productToDelete = product.get();
            productToDelete.setActive(false);
            productToDelete.setUpdatedAt(LocalDateTime.now());
            productRepository.save(productToDelete);
            return true;
        }
        
        return false;
    }
    
    // Hard Delete (if needed)
    public boolean hardDeleteProduct(String id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Mapper methods
    private ProductResponseDto mapToResponseDto(Product product) {
        return new ProductResponseDto(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getQuantity(),
            product.getCategory(),
            product.getBrand(),
            product.getActive(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}
