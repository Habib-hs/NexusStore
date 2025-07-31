package com.nexus.productservice.repository;

// JUnit 5 imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Spring Boot testing imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

// AssertJ for assertions
import static org.assertj.core.api.Assertions.assertThat;

// Your application classes
import com.nexus.productservice.TestcontainersConfiguration;
import com.nexus.productservice.model.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * 🗄️ REPOSITORY LAYER TESTING
 *
 * Purpose: Test MongoDB repository operations with REAL database
 *
 * Why @DataMongoTest?
 * - Loads ONLY MongoDB components (repositories, templates)
 * - Faster than @SpringBootTest (doesn't load web layer, services, etc.)
 *
 * Why @Import(TestcontainersConfiguration.class)?
 * - @DataMongoTest doesn't provide a database by itself
 * - TestcontainersConfiguration gives us a real MongoDB running in Docker
 * - Same MongoDB version as production
 * - Clean database for each test run
 */
@DataMongoTest  //  Only loads data layer components
@Import(TestcontainersConfiguration.class)  // Provides real MongoDB container
@DisplayName("ProductRepository Data Layer Tests")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;  // Real repository with real database

    @BeforeEach
    void setUp() {
        // Clean database before each test for isolation
        productRepository.deleteAll();
    }

    /**
     * 🧪 TEST: findByActiveTrue() method
     *
     * Business Rule: Only active products should be returned (hide deleted products)
     *
     * Test Scenario:
     * 1. Save one active product and one inactive product to database
     * 2. Call findByActiveTrue()
     * 3. Should return only the active product
     */
    @Test
    @DisplayName("Should find only active products and ignore inactive ones")
    void shouldFindOnlyActiveProducts() {
        // GIVEN - Create and save test data to real MongoDB
        Product activeProduct = new Product(
                "iPhone 15",               // name
                "Latest Apple smartphone", // description
                new BigDecimal("999.99"),  // price
                50,                        // quantity
                "Electronics",             // category
                "Apple"                    // brand
        );
        // Note: active = true by default from Product constructor

        Product inactiveProduct = new Product(
                "Old Samsung",             // name
                "Discontinued phone",      // description
                new BigDecimal("299.99"),  // price
                0,                         // quantity
                "Electronics",             // category
                "Samsung"                  // brand
        );
        inactiveProduct.setActive(false);  // Mark as inactive (soft deleted)

        // Save both products to database
        productRepository.saveAll(List.of(activeProduct, inactiveProduct));

        // WHEN - Call the repository method we're testing
        List<Product> activeProducts = productRepository.findByActiveTrue();

        // THEN - Verify only active product is returned
        assertThat(activeProducts).hasSize(1);  // Should return exactly 1 product
        assertThat(activeProducts.get(0).getName()).isEqualTo("iPhone 15");  // Should be the active one
        assertThat(activeProducts.get(0).getActive()).isTrue();  // Confirm it's active
    }
}