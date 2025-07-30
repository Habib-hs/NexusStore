package com.nexus.productservice.service;

// JUnit 5 imports for testing framework
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

// Mockito imports for creating fake objects
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

// AssertJ for readable assertions
import static org.assertj.core.api.Assertions.assertThat;
// Mockito static methods for mocking behavior
import static org.mockito.Mockito.*;

// Your application classes
import com.nexus.productservice.dto.ProductRequestDto;
import com.nexus.productservice.dto.ProductResponseDto;
import com.nexus.productservice.model.Product;
import com.nexus.productservice.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Unit Tests for ProductService
 *
 * Purpose: Test business logic in ProductService class in isolation
 * Approach: Mock all dependencies (ProductRepository) and test only service logic
 *
 * What we test:
 * - createProduct() - Creates new product
 * - getAllProducts() - Gets all active products
 * - getProductById() - Gets product by ID
 * - updateProduct() - Updates existing product
 * - deleteProduct() - Soft deletes product
 */

@ExtendWith(MockitoExtension.class)// Enable Mockito framework
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    // CREATE FAKE DEPENDENCIES
    @Mock
    private ProductRepository productRepository;  // Fake database repository

    // INJECT FAKE DEPENDENCIES INTO REAL SERVICE
    @InjectMocks
    private ProductService productService;  // Real service with fake repository

    // TEST DATA - Will be created before each test
    private Product testProduct;
    private ProductRequestDto testRequest;

    /**
     * Setup method - runs before each test
     * Purpose: Create test data that all tests can use
     */
    @BeforeEach
    void setUp() {
        // Create a sample product (what we expect from database)
        testProduct = new Product(
                "iPhone 15",                // name
                "Latest Apple smartphone",  // description
                new BigDecimal("999.99"),   // price
                50,                         // quantity
                "Electronics",              // category
                "Apple"                     // brand
        );
        testProduct.setId("product-123");

        // Create a sample request (what user sends)
        testRequest = new ProductRequestDto(
                "iPhone 15",
                "Latest Apple smartphone",
                new BigDecimal("999.99"),
                50,
                "Electronics",
                "Apple"
        );
    }


    //  Usually NOT needed in unit tests
    // @AfterEach
    // void tearDown() {
    //     // Mockito automatically resets mocks
    //     // Test data is garbage collected
    // }

    /**
     * Test: ProductService.createProduct()
     * Scenario: Successfully create a product with valid data
     */
    @Test
    @DisplayName("Should create product successfully when valid data provided")
    void shouldCreateProductSuccessfully() {
        // GIVEN - Mock repository behavior
        // When productRepository.save() is called with any Product, return testProduct
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // WHEN - Call the method we're testing
        ProductResponseDto result = productService.createProduct(testRequest);

        // THEN - Verify the results
        assertThat(result).isNotNull();                              // Result exists
        assertThat(result.getName()).isEqualTo(testProduct.getName());         // Name matches
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("999.99")); // Price matches
        assertThat(result.getCategory()).isEqualTo("Electronics");   // Category matches

        // VERIFY - Check that repository.save() was called exactly once
        verify(productRepository, times(1)).save(any(Product.class));
    }

    /**
     * Test: ProductService.getAllProducts()
     * Scenario: Successfully retrieve all active products
     */
    @Test
    @DisplayName("Should return all active products")
    void shouldReturnAllActiveProducts() {
        // GIVEN - Mock repository returns list of products
        List<Product> mockProducts = List.of(testProduct);
        when(productRepository.findByActiveTrue()).thenReturn(mockProducts);

        // WHEN - Call getAllProducts
        List<ProductResponseDto> result = productService.getAllProducts();

        // THEN - Verify results
        assertThat(result).hasSize(1);                               // One product returned
        assertThat(result.get(0).getName()).isEqualTo("iPhone 15");  // Correct product data

        // VERIFY - Repository method was called
        verify(productRepository, times(1)).findByActiveTrue();
    }

    /**
     * Test: ProductService.getProductById() - Happy Path
     * Scenario: Product exists and is found successfully
     */
    @Test
    @DisplayName("Should return product when valid ID provided")
    void shouldReturnProductWhenIdExists() {
        // GIVEN - Mock repository returns the product
        when(productRepository.findByIdAndActiveTrue("product-123"))
                .thenReturn(Optional.of(testProduct));

        // WHEN - Get product by ID
        Optional<ProductResponseDto> result = productService.getProductById("product-123");

        // THEN - Verify product is found
        assertThat(result).isPresent();                              // Optional contains value
        assertThat(result.get().getId()).isEqualTo("product-123");   // Correct ID
        assertThat(result.get().getName()).isEqualTo("iPhone 15");   // Correct name

        // VERIFY - Repository was called with correct ID
        verify(productRepository, times(1)).findByIdAndActiveTrue("product-123");
    }

    /**
     * Test: ProductService.getProductById() - Sad Path
     * Scenario: Product does not exist
     */
    @Test
    @DisplayName("Should return empty when product ID does not exist")
    void shouldReturnEmptyWhenProductNotFound() {
        // GIVEN - Mock repository returns empty (product not found)
        when(productRepository.findByIdAndActiveTrue("non-existent-id"))
                .thenReturn(Optional.empty());

        // WHEN - Try to get non-existent product
        Optional<ProductResponseDto> result = productService.getProductById("non-existent-id");

        // THEN - Should get empty result
        assertThat(result).isEmpty();  // Optional is empty

        // VERIFY - Repository was called with the non-existent ID
        verify(productRepository, times(1)).findByIdAndActiveTrue("non-existent-id");
    }

    /**
     * Test: ProductService.updateProduct() - Happy Path
     * Scenario: Product exists and gets updated successfully
     */
    @Test
    @DisplayName("Should update product successfully when product exists")
    void shouldUpdateProductSuccessfully() {
        // GIVEN - Prepare update data
        ProductRequestDto updateRequest = new ProductRequestDto(
                "iPhone 15 Pro",            // Updated name
                "Pro version iPhone",       // Updated description
                new BigDecimal("1199.99"),  // Updated price
                30,                         // Updated quantity
                "Electronics",
                "Apple"
        );

        // Mock: Product exists in database
        when(productRepository.findByIdAndActiveTrue("product-123"))
                .thenReturn(Optional.of(testProduct));

        // Mock: Save operation returns updated product
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // WHEN - Update the product
        Optional<ProductResponseDto> result = productService.updateProduct("product-123", updateRequest);

        // THEN - Verify update was successful
        assertThat(result).isPresent();  // Update successful

        // VERIFY - Both find and save were called
        verify(productRepository, times(1)).findByIdAndActiveTrue("product-123");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    /**
     * Test: ProductService.updateProduct() - Sad Path
     * Scenario: Product does not exist for update
     */
    @Test
    @DisplayName("Should return empty when updating non-existent product")
    void shouldReturnEmptyWhenUpdatingNonExistentProduct() {
        // GIVEN - Product does not exist
        when(productRepository.findByIdAndActiveTrue("non-existent-id"))
                .thenReturn(Optional.empty());

        // WHEN - Try to update non-existent product
        Optional<ProductResponseDto> result = productService.updateProduct("non-existent-id", testRequest);

        // THEN - Should return empty
        assertThat(result).isEmpty();

        // VERIFY - Only findById called, save should NOT be called
        verify(productRepository, times(1)).findByIdAndActiveTrue("non-existent-id");
        verify(productRepository, never()).save(any(Product.class));  // Save never called
    }

    /**
     * Test: ProductService.deleteProduct() - Happy Path
     * Scenario: Product exists and gets soft deleted successfully
     */
    @Test
    @DisplayName("Should soft delete product successfully when product exists")
    void shouldSoftDeleteProductSuccessfully() {
        // GIVEN - Product exists in database
        when(productRepository.findByIdAndActiveTrue("product-123"))
                .thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // WHEN - Delete the product
        boolean result = productService.deleteProduct("product-123");

        // THEN - Verify deletion was successful
        assertThat(result).isTrue();                    // Deletion successful
        assertThat(testProduct.getActive()).isFalse();  // Product marked as inactive

        // VERIFY - Both find and save were called
        verify(productRepository, times(1)).findByIdAndActiveTrue("product-123");
        verify(productRepository, times(1)).save(testProduct);
    }

    /**
     * Test: ProductService.deleteProduct() - Sad Path
     * Scenario: Product does not exist for deletion
     */
    @Test
    @DisplayName("Should return false when deleting non-existent product")
    void shouldReturnFalseWhenDeletingNonExistentProduct() {
        // GIVEN - Product does not exist
        when(productRepository.findByIdAndActiveTrue("non-existent-id"))
                .thenReturn(Optional.empty());

        // WHEN - Try to delete non-existent product
        boolean result = productService.deleteProduct("non-existent-id");

        // THEN - Should return false
        assertThat(result).isFalse();

        // VERIFY - Only findById called, save should NOT be called
        verify(productRepository, times(1)).findByIdAndActiveTrue("non-existent-id");
        verify(productRepository, never()).save(any(Product.class));  // Save never called
    }
}