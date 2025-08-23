package com.nexus.productservice.controller;

// JUnit 5 imports for testing framework
import com.nexus.productservice.service.TokenBucketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

// Spring Boot testing imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// Jackson for JSON processing
import com.fasterxml.jackson.databind.ObjectMapper;

// Mockito for mocking behavior
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

// Spring Test MVC static imports
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

// Your application classes
import com.nexus.productservice.dto.ProductRequestDto;
import com.nexus.productservice.dto.ProductResponseDto;
import com.nexus.productservice.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 🎯 CONTROLLER LAYER TESTING
 *
 * Purpose: Test the WEB LAYER (HTTP endpoints) of ProductController in isolation
 *
 * What we test:
 * ✅ HTTP request/response handling
 * ✅ JSON serialization/deserialization
 * ✅ Status codes (200, 201, 404, 400, etc.)
 * ✅ Request validation
 * ✅ URL path mapping
 * ✅ Request/Response body structure
 *
 * What we DON'T test:
 *  Business logic (that's in service layer)
 *  Database operations (that's in repository layer)
 *  Complete integration (that's in integration tests)
 *
 * Approach:
 * - Use @WebMvcTest to load ONLY web layer components
 * - Mock the service layer with @MockBean
 * - Use MockMvc to simulate HTTP requests
 * - Test HTTP status codes, headers, and JSON responses
 */

@WebMvcTest(ProductController.class)  // Only loads web layer, not full Spring context
@DisplayName("ProductController Web Layer Tests")
class ProductControllerTest {

    // TESTING TOOLS
    @Autowired
    private MockMvc mockMvc;  // Simulates HTTP requests without starting server

    @Autowired
    private ObjectMapper objectMapper;  //  Converts Java objects to/from JSON

    // MOCKED DEPENDENCIES
    @MockitoBean  //  Creates mock and adds to Spring context (different from @Mock)
    private ProductService productService;  // Fake service layer
    @MockitoBean
    private TokenBucketService tokenBucketService; //fake token bucket service

    // TEST DATA
    private ProductRequestDto validRequest;
    private ProductRequestDto invalidRequest;
    private ProductResponseDto sampleResponse;

    /**
     * 🔧 SETUP METHOD
     * Creates test data before each test method runs
     * This ensures each test has fresh, consistent data
     */
    @BeforeEach
    void setUp() {
        // Configure TokenBucketService mock to allow all requests in tests
        when(tokenBucketService.tryConsumeToken(anyString())).thenReturn(true);

        // Valid request data (what a client would send)
        validRequest = new ProductRequestDto(
                "iPhone 15 Pro",           // name
                "Latest iPhone Pro model", // description
                new BigDecimal("1199.99"), // price
                25,                        // quantity
                "Electronics",             // category
                "Apple"                    // brand
        );

        // Valid request data (what a client would send)
        validRequest = new ProductRequestDto(
                "iPhone 15 Pro",           // name
                "Latest iPhone Pro model", // description
                new BigDecimal("1199.99"), // price
                25,                        // quantity
                "Electronics",             // category
                "Apple"                    // brand
        );

        // Invalid request data (for testing validation)
        invalidRequest = new ProductRequestDto(
                "",                        //  Empty name (should fail validation)
                "Description",
                new BigDecimal("-100"),    //  Negative price (should fail validation)
                -5,                        //  Negative quantity (should fail validation)
                "Electronics",
                "Apple"
        );

        // Sample response data (what service would return)
        sampleResponse = new ProductResponseDto(
                "product-123",             // id
                "iPhone 15 Pro",           // name
                "Latest iPhone Pro model", // description
                new BigDecimal("1199.99"), // price
                25,                        // quantity
                "Electronics",             // category
                "Apple",                   // brand
                true,                      // active
                LocalDateTime.now(),       // createdAt
                LocalDateTime.now()        // updatedAt
        );
    }

    /**
     * 🧪 TEST: GET /api/products
     * Scenario: Successfully retrieve all active products
     * Expected: 200 OK with JSON array of products
     */
    @Test
    @DisplayName("Should return all products with 200 status")
    void shouldReturnAllProducts() throws Exception {
        // GIVEN - Mock service returns list of products
        List<ProductResponseDto> mockProducts = List.of(sampleResponse);
        when(productService.getAllProducts()).thenReturn(mockProducts);

        // WHEN & THEN - Perform GET request and verify response
        mockMvc.perform(get("/api/products"))  //  Send GET request
                .andDo(print())  // 🖨 Print request/response for debugging
                .andExpect(status().isOk())  //  Expect 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  //  JSON response
                .andExpect(jsonPath("$").isArray())  //  Response is an array
                .andExpect(jsonPath("$[0].id").value("product-123"))  //  Check first product ID
                .andExpect(jsonPath("$[0].name").value("iPhone 15 Pro"))  //  Check first product name
                .andExpect(jsonPath("$[0].price").value(1199.99))  //  Check first product price
                .andExpect(jsonPath("$[0].active").value(true));  // Check active status

        // VERIFY - Confirm service method was called
        verify(productService, times(1)).getAllProducts();
    }

    /**
     * 🧪 TEST: GET /api/products (Empty result)
     * Scenario: No products exist in database
     * Expected: 200 OK with empty JSON array
     */
    @Test
    @DisplayName("Should return empty array when no products exist")
    void shouldReturnEmptyArrayWhenNoProducts() throws Exception {
        // GIVEN - Service returns empty list
        when(productService.getAllProducts()).thenReturn(List.of());

        // WHEN & THEN - Verify empty array response
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());  //  Array should be empty

        verify(productService, times(1)).getAllProducts();
    }

    /**
     * 🧪 TEST: POST /api/products
     * Scenario: Successfully create a new product with valid data
     * Expected: 201 Created with product details in response body
     */
    @Test
    @DisplayName("Should create product and return 201 with product details")
    void shouldCreateProductSuccessfully() throws Exception {
        // GIVEN - Mock service creates and returns product
        when(productService.createProduct(any(ProductRequestDto.class))).thenReturn(sampleResponse);

        // WHEN & THEN - Send POST request with valid JSON
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)  //  Set content type
                        .content(objectMapper.writeValueAsString(validRequest)))  // Convert object to JSON
                .andDo(print())
                .andExpect(status().isCreated())  //  Expect 201 Created
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("product-123"))  //  Check returned ID
                .andExpect(jsonPath("$.name").value("iPhone 15 Pro"))  // Check returned name
                .andExpect(jsonPath("$.price").value(1199.99))  //  Check returned price
                .andExpect(jsonPath("$.active").value(true));  // Check active status

        // VERIFY - Service method was called with correct data
        verify(productService, times(1)).createProduct(any(ProductRequestDto.class));
    }


    /**
     * 🧪 TEST: GET /api/products/{id}
     * Scenario: Successfully retrieve product by existing ID
     * Expected: 200 OK with product details
     */
    @Test
    @DisplayName("Should return product by ID with 200 status")
    void shouldReturnProductById() throws Exception {
        // GIVEN - Mock service returns product for valid ID
        when(productService.getProductById("product-123")).thenReturn(Optional.of(sampleResponse));

        // WHEN & THEN - Send GET request for specific product
        mockMvc.perform(get("/api/products/product-123"))
                .andDo(print())
                .andExpect(status().isOk())  // Expect 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("product-123"))  //  Check correct product returned
                .andExpect(jsonPath("$.name").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.price").value(1199.99));

        // VERIFY - Service was called with correct ID
        verify(productService, times(1)).getProductById("product-123");
    }

    /**
     * 🧪 TEST: GET /api/products/{id} (Not found)
     * Scenario: Try to retrieve product with non-existent ID
     * Expected: 404 Not Found
     */
    @Test
    @DisplayName("Should return 404 when product ID does not exist")
    void shouldReturn404WhenProductNotFound() throws Exception {
        // GIVEN - Mock service returns empty for non-existent ID
        when(productService.getProductById("non-existent-id")).thenReturn(Optional.empty());

        // WHEN & THEN - Request non-existent product
        mockMvc.perform(get("/api/products/non-existent-id"))
                .andDo(print())
                .andExpect(status().isNotFound());  //  Expect 404 Not Found

        // VERIFY - Service was called with the non-existent ID
        verify(productService, times(1)).getProductById("non-existent-id");
    }

    /**
     * 🧪 TEST: PUT /api/products/{id}
     * Scenario: Successfully update existing product
     * Expected: 200 OK with updated product details
     */
    @Test
    @DisplayName("Should update product and return 200 with updated details")
    void shouldUpdateProductSuccessfully() throws Exception {
        // GIVEN - Mock service returns updated product
        ProductResponseDto updatedResponse = new ProductResponseDto(
                "product-123", "iPhone 15 Pro Max", "Updated iPhone Pro Max",
                new BigDecimal("1299.99"), 15, "Electronics", "Apple",
                true, LocalDateTime.now(), LocalDateTime.now()
        );
        when(productService.updateProduct(anyString(), any(ProductRequestDto.class)))
                .thenReturn(Optional.of(updatedResponse));

        // Prepare update request
        ProductRequestDto updateRequest = new ProductRequestDto(
                "iPhone 15 Pro Max", "Updated iPhone Pro Max", new BigDecimal("1299.99"),
                15, "Electronics", "Apple"
        );

        // WHEN & THEN - Send PUT request
        mockMvc.perform(put("/api/products/product-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())  //  Expect 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("product-123"))
                .andExpect(jsonPath("$.name").value("iPhone 15 Pro Max"))  // Updated name
                .andExpect(jsonPath("$.price").value(1299.99));  //  Updated price

        // VERIFY - Service update method was called
        verify(productService, times(1)).updateProduct(anyString(), any(ProductRequestDto.class));
    }

    /**
     * 🧪 TEST: PUT /api/products/{id} (Not found)
     * Scenario: Try to update non-existent product
     * Expected: 404 Not Found
     */
    @Test
    @DisplayName("Should return 404 when updating non-existent product")
    void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
        // GIVEN - Mock service returns empty for non-existent product
        when(productService.updateProduct(anyString(), any(ProductRequestDto.class)))
                .thenReturn(Optional.empty());

        // WHEN & THEN - Try to update non-existent product
        mockMvc.perform(put("/api/products/non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());  //  Expect 404 Not Found

        verify(productService, times(1)).updateProduct(anyString(), any(ProductRequestDto.class));
    }

    /**
     * 🧪 TEST: DELETE /api/products/{id}
     * Scenario: Successfully soft delete existing product
     * Expected: 204 No Content
     */
    @Test
    @DisplayName("Should delete product and return 204 No Content")
    void shouldDeleteProductSuccessfully() throws Exception {
        // GIVEN - Mock service successfully deletes product
        when(productService.deleteProduct("product-123")).thenReturn(true);

        // WHEN & THEN - Send DELETE request
        mockMvc.perform(delete("/api/products/product-123"))
                .andDo(print())
                .andExpect(status().isNoContent());  //  Expect 204 No Content

        // VERIFY - Service delete method was called
        verify(productService, times(1)).deleteProduct("product-123");
    }

    /**
     * 🧪 TEST: DELETE /api/products/{id} (Not found)
     * Scenario: Try to delete non-existent product
     * Expected: 404 Not Found
     */
    @Test
    @DisplayName("Should return 404 when deleting non-existent product")
    void shouldReturn404WhenDeletingNonExistentProduct() throws Exception {
        // GIVEN - Mock service returns false (product not found)
        when(productService.deleteProduct("non-existent-id")).thenReturn(false);

        // WHEN & THEN - Try to delete non-existent product
        mockMvc.perform(delete("/api/products/non-existent-id"))
                .andDo(print())
                .andExpect(status().isNotFound());  //  Expect 404 Not Found

        verify(productService, times(1)).deleteProduct("non-existent-id");
    }

    /**
     * 🧪 TEST: GET /api/products/search
     * Scenario: Successfully search products by name
     * Expected: 200 OK with matching products
     */
    @Test
    @DisplayName("Should search products by name and return 200")
    void shouldSearchProductsByName() throws Exception {
        // GIVEN - Mock service returns search results
        List<ProductResponseDto> searchResults = List.of(sampleResponse);
        when(productService.searchProductsByName("iPhone")).thenReturn(searchResults);

        // WHEN & THEN - Send search request
        mockMvc.perform(get("/api/products/search")
                        .param("name", "iPhone"))  //  Search parameter
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("iPhone 15 Pro"));

        // VERIFY - Service search method was called
        verify(productService, times(1)).searchProductsByName("iPhone");
    }

    /**
     * 🧪 TEST: GET /api/products/category/{category}
     * Scenario: Successfully get products by category
     * Expected: 200 OK with products in specified category
     */
    @Test
    @DisplayName("Should return products by category with 200 status")
    void shouldReturnProductsByCategory() throws Exception {
        // GIVEN - Mock service returns products for category
        List<ProductResponseDto> categoryProducts = List.of(sampleResponse);
        when(productService.getProductsByCategory("Electronics")).thenReturn(categoryProducts);

        // WHEN & THEN - Request products by category
        mockMvc.perform(get("/api/products/category/Electronics"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].category").value("Electronics"));

        verify(productService, times(1)).getProductsByCategory("Electronics");
    }

    /**
     * 🧪 TEST: Invalid HTTP Methods
     * Scenario: Try to use unsupported HTTP method
     * Expected: 405 Method Not Allowed
     */
    @Test
    @DisplayName("Should return 405 for unsupported HTTP methods")
    void shouldReturn405ForUnsupportedMethods() throws Exception {
        // WHEN & THEN - Try PATCH method (not supported)
        mockMvc.perform(patch("/api/products/product-123"))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());  //Expect 405 Method Not Allowed
    }
}