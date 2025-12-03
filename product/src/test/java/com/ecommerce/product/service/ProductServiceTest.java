package com.ecommerce.product.service;

import com.ecommerce.product.dto.CreateProductRequest;
import com.ecommerce.product.dto.PagedProductResponse;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.ProductEntity;
import com.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductEntity testProduct;
    private CreateProductRequest createRequest;

    @BeforeEach
    void setUp() {
        testProduct = ProductEntity.builder()
                .id(1L)
                .sku("LAP-001")
                .name("Laptop")
                .description("High-performance laptop")
                .price(999.99)
                .stock(50)
                .build();

        createRequest = CreateProductRequest.builder()
                .sku("LAP-001")
                .name("Laptop")
                .description("High-performance laptop")
                .price(999.99)
                .stock(50)
                .build();
    }

    @Test
    void createProduct_Success() {
        // Arrange
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(testProduct);

        // Act
        ProductResponse response = productService.createProduct(createRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("LAP-001", response.getSku());
        assertEquals("Laptop", response.getName());
        assertEquals(999.99, response.getPrice());
        assertEquals(50, response.getStock());

        verify(productRepository).existsBySku("LAP-001");
        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    void createProduct_DuplicateSku_ThrowsException() {
        // Arrange
        when(productRepository.existsBySku(anyString())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(createRequest));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(productRepository).existsBySku("LAP-001");
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
    void getProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        ProductResponse response = productService.getProductById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("LAP-001", response.getSku());
        assertEquals("Laptop", response.getName());

        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_NotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductById(999L));

        assertTrue(exception.getMessage().contains("not found"));
        verify(productRepository).findById(999L);
    }

    @Test
    void getAllProducts_Success() {
        // Arrange
        ProductEntity product2 = ProductEntity.builder()
                .id(2L)
                .sku("MOU-001")
                .name("Mouse")
                .description("Wireless mouse")
                .price(29.99)
                .stock(100)
                .build();

        List<ProductEntity> products = Arrays.asList(testProduct, product2);
        Page<ProductEntity> page = new PageImpl<>(products, PageRequest.of(0, 20), 2);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        PagedProductResponse response = productService.getAllProducts(PageRequest.of(0, 20));

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getPage());
        assertEquals(20, response.getSize());
        assertEquals(2L, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.getLast());

        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    void searchProducts_Success() {
        // Arrange
        List<ProductEntity> products = Arrays.asList(testProduct);
        Page<ProductEntity> page = new PageImpl<>(products, PageRequest.of(0, 20), 1);

        when(productRepository.searchByName(anyString(), any(Pageable.class))).thenReturn(page);

        // Act
        PagedProductResponse response = productService.searchProducts("lap", PageRequest.of(0, 20));

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Laptop", response.getContent().get(0).getName());
        assertEquals(1L, response.getTotalElements());

        verify(productRepository).searchByName("lap", PageRequest.of(0, 20));
    }

    @Test
    void searchProducts_NoResults() {
        // Arrange
        Page<ProductEntity> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 20), 0);

        when(productRepository.searchByName(anyString(), any(Pageable.class))).thenReturn(emptyPage);

        // Act
        PagedProductResponse response = productService.searchProducts("nonexistent", PageRequest.of(0, 20));

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getContent().size());
        assertEquals(0L, response.getTotalElements());

        verify(productRepository).searchByName("nonexistent", PageRequest.of(0, 20));
    }
}
