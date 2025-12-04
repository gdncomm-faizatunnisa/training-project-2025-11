package com.ecommerce.product.controller;

import com.ecommerce.product.dto.CreateProductRequest;
import com.ecommerce.product.dto.PagedProductResponse;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ProductController.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductResponse testProductResponse;
    private CreateProductRequest createRequest;

    @BeforeEach
    void setUp() {
        testProductResponse = ProductResponse.builder()
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
    void createProduct_Success() throws Exception {
        // Arrange
        when(productService.createProduct(any(CreateProductRequest.class)))
                .thenReturn(testProductResponse);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sku").value("LAP-001"))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.stock").value(50));

        verify(productService).createProduct(any(CreateProductRequest.class));
    }

    @Test
    void createProduct_ValidationError_MissingName() throws Exception {
        // Arrange
        CreateProductRequest invalidRequest = CreateProductRequest.builder()
                .sku("LAP-001")
                .price(999.99)
                .stock(50)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(CreateProductRequest.class));
    }

    @Test
    void createProduct_ValidationError_NegativePrice() throws Exception {
        // Arrange
        CreateProductRequest invalidRequest = CreateProductRequest.builder()
                .sku("LAP-001")
                .name("Laptop")
                .price(-10.0)
                .stock(50)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(CreateProductRequest.class));
    }

    @Test
    void createProduct_DuplicateSku() throws Exception {
        // Arrange
        when(productService.createProduct(any(CreateProductRequest.class)))
                .thenThrow(new IllegalArgumentException("Product with SKU 'LAP-001' already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product with SKU 'LAP-001' already exists"));

        verify(productService).createProduct(any(CreateProductRequest.class));
    }

    @Test
    void getProductById_Success() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(testProductResponse);

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sku").value("LAP-001"))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService).getProductById(1L);
    }

    @Test
    void getProductById_NotFound() throws Exception {
        // Arrange
        when(productService.getProductById(999L))
                .thenThrow(new IllegalArgumentException("Product with ID 999 not found"));

        // Act & Assert
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product with ID 999 not found"));

        verify(productService).getProductById(999L);
    }

    @Test
    void getAllProducts_Success() throws Exception {
        // Arrange
        ProductResponse product2 = ProductResponse.builder()
                .id(2L)
                .sku("MOU-001")
                .name("Mouse")
                .description("Wireless mouse")
                .price(29.99)
                .stock(100)
                .build();

        PagedProductResponse pagedResponse = PagedProductResponse.builder()
                .content(Arrays.asList(testProductResponse, product2))
                .page(0)
                .size(20)
                .totalElements(2L)
                .totalPages(1)
                .last(true)
                .build();

        when(productService.getAllProducts(any(Pageable.class))).thenReturn(pagedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "20")
                .param("sort", "sku,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));

        verify(productService).getAllProducts(any(Pageable.class));
    }

    @Test
    void getAllProducts_WithDefaultParameters() throws Exception {
        // Arrange
        PagedProductResponse pagedResponse = PagedProductResponse.builder()
                .content(Arrays.asList(testProductResponse))
                .page(0)
                .size(20)
                .totalElements(1L)
                .totalPages(1)
                .last(true)
                .build();

        when(productService.getAllProducts(any(Pageable.class))).thenReturn(pagedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));

        verify(productService).getAllProducts(any(Pageable.class));
    }

    @Test
    void searchProducts_Success() throws Exception {
        // Arrange
        PagedProductResponse pagedResponse = PagedProductResponse.builder()
                .content(Arrays.asList(testProductResponse))
                .page(0)
                .size(20)
                .totalElements(1L)
                .totalPages(1)
                .last(true)
                .build();

        when(productService.searchProducts(anyString(), any(Pageable.class)))
                .thenReturn(pagedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/products/search")
                .param("query", "lap")
                .param("page", "0")
                .param("size", "20")
                .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Laptop"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(productService).searchProducts(eq("lap"), any(Pageable.class));
    }

    @Test
    void searchProducts_NoResults() throws Exception {
        // Arrange
        PagedProductResponse emptyResponse = PagedProductResponse.builder()
                .content(Arrays.asList())
                .page(0)
                .size(20)
                .totalElements(0L)
                .totalPages(0)
                .last(true)
                .build();

        when(productService.searchProducts(anyString(), any(Pageable.class)))
                .thenReturn(emptyResponse);

        // Act & Assert
        mockMvc.perform(get("/api/products/search")
                .param("query", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(productService).searchProducts(eq("nonexistent"), any(Pageable.class));
    }
}
