package com.ecommerce.product.service;

import com.ecommerce.product.dto.CreateProductRequest;
import com.ecommerce.product.dto.PagedProductResponse;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.ProductEntity;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for product operations.
 * Handles business logic for product management.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Create a new product.
     *
     * @param request the product creation request
     * @return the created product response
     * @throws IllegalArgumentException if SKU already exists
     */
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        // Check if SKU already exists
        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Product with SKU '" + request.getSku() + "' already exists");
        }

        ProductEntity entity = ProductEntity.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();

        ProductEntity savedEntity = productRepository.save(entity);
        return mapToResponse(savedEntity);
    }

    /**
     * Get a product by ID.
     *
     * @param id the product ID
     * @return the product response
     * @throws IllegalArgumentException if product not found
     */
    public ProductResponse getProductById(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + id + " not found"));
        return mapToResponse(entity);
    }

    /**
     * Get all products with pagination.
     *
     * @param pageable pagination information
     * @return paginated product response
     */
    public PagedProductResponse getAllProducts(Pageable pageable) {
        Page<ProductEntity> page = productRepository.findAll(pageable);
        return mapToPagedResponse(page);
    }

    /**
     * Search products by name with wildcard matching.
     *
     * @param query    the search query
     * @param pageable pagination information
     * @return paginated product response
     */
    public PagedProductResponse searchProducts(String query, Pageable pageable) {
        Page<ProductEntity> page = productRepository.searchByName(query, pageable);
        return mapToPagedResponse(page);
    }

    /**
     * Map ProductEntity to ProductResponse.
     */
    private ProductResponse mapToResponse(ProductEntity entity) {
        return ProductResponse.builder()
                .id(entity.getId())
                .sku(entity.getSku())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .build();
    }

    /**
     * Map Page<ProductEntity> to PagedProductResponse.
     */
    private PagedProductResponse mapToPagedResponse(Page<ProductEntity> page) {
        List<ProductResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagedProductResponse.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
