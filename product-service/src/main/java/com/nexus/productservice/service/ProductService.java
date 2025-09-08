package com.nexus.productservice.service;

import com.nexus.productservice.dto.ProductRequestDto;
import com.nexus.productservice.dto.ProductResponseDto;
import com.nexus.productservice.model.Product;
import com.nexus.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    
    // Create
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        log.info("Creating new product with name: {} and category: {}", requestDto.getName(), requestDto.getCategory());
        log.debug("Product creation request details - Name: {}, Price: {}, Quantity: {}, Brand: {}", 
            requestDto.getName(), requestDto.getPrice(), requestDto.getQuantity(), requestDto.getBrand());
        
        try {
            Product product = new Product(
                requestDto.getName(),
                requestDto.getDescription(),
                requestDto.getPrice(),
                requestDto.getQuantity(),
                requestDto.getCategory(),
                requestDto.getBrand()
            );
            
            Product savedProduct = productRepository.save(product);
            log.info("Successfully created product with ID: {} and name: {}", savedProduct.getId(), savedProduct.getName());
            log.debug("Product created with full details - ID: {}, Name: {}, Price: {}, Quantity: {}", 
                savedProduct.getId(), savedProduct.getName(), savedProduct.getPrice(), savedProduct.getQuantity());
            
            return mapToResponseDto(savedProduct);
        } catch (Exception e) {
            log.error("Failed to create product with name: {}. Error: {}", requestDto.getName(), e.getMessage(), e);
            throw e;
        }
    }
    
    // Read All
    @Cacheable(value = "products", key = "'all-products'")
    public List<ProductResponseDto> getAllProducts() {
        log.info("Fetching all active products");
        long startTime = System.currentTimeMillis();
        
        try {
            List<ProductResponseDto> products = productRepository.findByActiveTrue()
                    .stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Successfully retrieved {} active products in {}ms", products.size(), duration);
            log.debug("Product retrieval performance - Count: {}, Duration: {}ms", products.size(), duration);
            
            return products;
        } catch (Exception e) {
            log.error("Failed to retrieve all products. Error: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    // Read by ID
    public Optional<ProductResponseDto> getProductById(String id) {
        log.info("Fetching product by ID: {}", id);
        
        try {
            Optional<ProductResponseDto> product = productRepository.findByIdAndActiveTrue(id)
                    .map(this::mapToResponseDto);
            
            if (product.isPresent()) {
                log.info("Successfully found product with ID: {} and name: {}", id, product.get().getName());
                log.debug("Product details - ID: {}, Name: {}, Category: {}, Price: {}", 
                    id, product.get().getName(), product.get().getCategory(), product.get().getPrice());
            } else {
                log.warn("Product not found with ID: {}", id);
            }
            
            return product;
        } catch (Exception e) {
            log.error("Failed to retrieve product by ID: {}. Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    
    // Read by Category
    public List<ProductResponseDto> getProductsByCategory(String category) {
        log.info("Fetching products by category: {}", category);
        long startTime = System.currentTimeMillis();
        
        try {
            List<ProductResponseDto> products = productRepository.findByCategory(category)
                    .stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Found {} products in category '{}' in {}ms", products.size(), category, duration);
            log.debug("Category search performance - Category: {}, Count: {}, Duration: {}ms", 
                category, products.size(), duration);
            
            return products;
        } catch (Exception e) {
            log.error("Failed to retrieve products by category: {}. Error: {}", category, e.getMessage(), e);
            throw e;
        }
    }
    
    // Read by Brand
    public List<ProductResponseDto> getProductsByBrand(String brand) {
        log.info("Fetching products by brand: {}", brand);
        long startTime = System.currentTimeMillis();
        
        try {
            List<ProductResponseDto> products = productRepository.findByBrand(brand)
                    .stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Found {} products for brand '{}' in {}ms", products.size(), brand, duration);
            log.debug("Brand search performance - Brand: {}, Count: {}, Duration: {}ms", 
                brand, products.size(), duration);
            
            return products;
        } catch (Exception e) {
            log.error("Failed to retrieve products by brand: {}. Error: {}", brand, e.getMessage(), e);
            throw e;
        }
    }
    
    // Search by Name
    public List<ProductResponseDto> searchProductsByName(String name) {
        log.info("Searching products by name containing: {}", name);
        long startTime = System.currentTimeMillis();
        
        try {
            List<ProductResponseDto> products = productRepository.findByNameContainingIgnoreCase(name)
                    .stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Found {} products matching name search '{}' in {}ms", products.size(), name, duration);
            log.debug("Name search performance - Query: {}, Count: {}, Duration: {}ms", 
                name, products.size(), duration);
            
            return products;
        } catch (Exception e) {
            log.error("Failed to search products by name: {}. Error: {}", name, e.getMessage(), e);
            throw e;
        }
    }
    
    // Update
    @CacheEvict(value = "products", allEntries = true)
    public Optional<ProductResponseDto> updateProduct(String id, ProductRequestDto requestDto) {
        log.info("Updating product with ID: {}", id);
        log.debug("Product update request - ID: {}, Name: {}, Price: {}, Quantity: {}", 
            id, requestDto.getName(), requestDto.getPrice(), requestDto.getQuantity());
        
        try {
            Optional<Product> existingProduct = productRepository.findByIdAndActiveTrue(id);
            
            if (existingProduct.isPresent()) {
                Product product = existingProduct.get();
                
                // Log what fields are being changed
                if (!product.getName().equals(requestDto.getName())) {
                    log.debug("Updating product name from '{}' to '{}'", product.getName(), requestDto.getName());
                }
                if (!product.getPrice().equals(requestDto.getPrice())) {
                    log.debug("Updating product price from {} to {}", product.getPrice(), requestDto.getPrice());
                }
                if (!product.getQuantity().equals(requestDto.getQuantity())) {
                    log.debug("Updating product quantity from {} to {}", product.getQuantity(), requestDto.getQuantity());
                }
                
                product.setName(requestDto.getName());
                product.setDescription(requestDto.getDescription());
                product.setPrice(requestDto.getPrice());
                product.setQuantity(requestDto.getQuantity());
                product.setCategory(requestDto.getCategory());
                product.setBrand(requestDto.getBrand());
                product.setUpdatedAt(LocalDateTime.now());
                
                Product updatedProduct = productRepository.save(product);
                log.info("Successfully updated product with ID: {} and name: {}", id, updatedProduct.getName());
                log.debug("Product update completed - ID: {}, Updated fields logged above", id);
                
                return Optional.of(mapToResponseDto(updatedProduct));
            } else {
                log.warn("Cannot update product - Product not found with ID: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Failed to update product with ID: {}. Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    
    // Delete (Soft Delete)
    @CacheEvict(value = "products", allEntries = true)
    public boolean deleteProduct(String id) {
        log.info("Soft deleting product with ID: {}", id);
        
        try {
            Optional<Product> product = productRepository.findByIdAndActiveTrue(id);
            
            if (product.isPresent()) {
                Product productToDelete = product.get();
                log.debug("Soft deleting product - ID: {}, Name: {}, Category: {}", 
                    id, productToDelete.getName(), productToDelete.getCategory());
                
                productToDelete.setActive(false);
                productToDelete.setUpdatedAt(LocalDateTime.now());
                productRepository.save(productToDelete);
                
                log.info("Successfully soft deleted product with ID: {} and name: {}", 
                    id, productToDelete.getName());
                return true;
            } else {
                log.warn("Cannot delete product - Product not found with ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to soft delete product with ID: {}. Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    
    // Hard Delete (if needed)
    @CacheEvict(value = "products", allEntries = true)
    public boolean hardDeleteProduct(String id) {
        log.warn("Hard deleting product with ID: {} - This action is irreversible", id);
        
        try {
            if (productRepository.existsById(id)) {
                productRepository.deleteById(id);
                log.info("Successfully hard deleted product with ID: {}", id);
                return true;
            } else {
                log.warn("Cannot hard delete product - Product not found with ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to hard delete product with ID: {}. Error: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    
    // Mapper methods
    private ProductResponseDto mapToResponseDto(Product product) {
        log.trace("Mapping product to response DTO - ID: {}, Name: {}", product.getId(), product.getName());
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
