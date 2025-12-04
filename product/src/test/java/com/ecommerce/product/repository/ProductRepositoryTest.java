package com.ecommerce.product.repository;

import com.ecommerce.product.entity.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ProductRepository.
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private ProductEntity testProduct1;
    private ProductEntity testProduct2;

    @BeforeEach
    void setUp() {
        // Clear the database
        productRepository.deleteAll();

        // Create test products
        testProduct1 = ProductEntity.builder()
                .sku("LAP-001")
                .name("Laptop")
                .description("High-performance laptop")
                .price(999.99)
                .stock(50)
                .build();

        testProduct2 = ProductEntity.builder()
                .sku("MOU-001")
                .name("Mouse")
                .description("Wireless mouse")
                .price(29.99)
                .stock(100)
                .build();

        productRepository.save(testProduct1);
        productRepository.save(testProduct2);
    }

    @Test
    void findBySku_Success() {
        // Act
        Optional<ProductEntity> result = productRepository.findBySku("LAP-001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("LAP-001", result.get().getSku());
        assertEquals("Laptop", result.get().getName());
    }

    @Test
    void findBySku_NotFound() {
        // Act
        Optional<ProductEntity> result = productRepository.findBySku("NONEXISTENT");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void existsBySku_True() {
        // Act
        boolean exists = productRepository.existsBySku("LAP-001");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsBySku_False() {
        // Act
        boolean exists = productRepository.existsBySku("NONEXISTENT");

        // Assert
        assertFalse(exists);
    }

    @Test
    void searchByName_FullMatch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ProductEntity> result = productRepository.searchByName("Laptop", pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("Laptop", result.getContent().get(0).getName());
    }

    @Test
    void searchByName_PartialMatch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ProductEntity> result = productRepository.searchByName("lap", pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("Laptop", result.getContent().get(0).getName());
    }

    @Test
    void searchByName_CaseInsensitive() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ProductEntity> result = productRepository.searchByName("LAPTOP", pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("Laptop", result.getContent().get(0).getName());
    }

    @Test
    void searchByName_NoMatch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ProductEntity> result = productRepository.searchByName("Keyboard", pageable);

        // Assert
        assertEquals(0, result.getContent().size());
    }

    @Test
    void searchByNameOrDescription_MatchInName() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ProductEntity> result = productRepository.searchByNameOrDescription("Mouse", pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("Mouse", result.getContent().get(0).getName());
    }

    @Test
    void searchByNameOrDescription_MatchInDescription() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ProductEntity> result = productRepository.searchByNameOrDescription("wireless", pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("Mouse", result.getContent().get(0).getName());
    }

    @Test
    void searchByNameOrDescription_MultipleMatches() {
        // Arrange
        ProductEntity product3 = ProductEntity.builder()
                .sku("KEY-001")
                .name("Wireless Keyboard")
                .description("Ergonomic keyboard")
                .price(79.99)
                .stock(75)
                .build();
        productRepository.save(product3);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ProductEntity> result = productRepository.searchByNameOrDescription("wireless", pageable);

        // Assert
        assertEquals(2, result.getContent().size());
    }

    @Test
    void findAll_WithPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 1);

        // Act
        Page<ProductEntity> result = productRepository.findAll(pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertFalse(result.isLast());
    }

    @Test
    void save_Success() {
        // Arrange
        ProductEntity newProduct = ProductEntity.builder()
                .sku("KEY-001")
                .name("Keyboard")
                .description("Mechanical keyboard")
                .price(149.99)
                .stock(30)
                .build();

        // Act
        ProductEntity saved = productRepository.save(newProduct);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("KEY-001", saved.getSku());
        assertEquals("Keyboard", saved.getName());
    }

    @Test
    void delete_Success() {
        // Arrange
        Long productId = testProduct1.getId();

        // Act
        productRepository.deleteById(productId);

        // Assert
        Optional<ProductEntity> result = productRepository.findById(productId);
        assertFalse(result.isPresent());
    }
}
