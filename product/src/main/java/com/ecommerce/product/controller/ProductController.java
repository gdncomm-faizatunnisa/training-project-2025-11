package com.ecommerce.product.controller;

import com.ecommerce.product.dto.CreateProductRequest;
import com.ecommerce.product.dto.PagedProductResponse;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for product management.
 * Provides endpoints for CRUD operations, search, and pagination.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Create a new product.
     * POST /api/products
     *
     * @param request the product creation request
     * @return the created product with 201 status
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get a product by ID.
     * GET /api/products/{id}
     *
     * @param id the product ID
     * @return the product response with 200 status
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all products with pagination.
     * GET /api/products
     *
     * @param page the page number (default: 0)
     * @param size the page size (default: 20)
     * @param sort the sort criteria (default: "sku,asc")
     * @return paginated product list with 200 status
     */
    @GetMapping
    public ResponseEntity<PagedProductResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "sku,asc") String sort) {

        Pageable pageable = createPageable(page, size, sort);
        PagedProductResponse response = productService.getAllProducts(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Search products by name with wildcard matching.
     * GET /api/products/search
     *
     * @param query the search query
     * @param page  the page number (default: 0)
     * @param size  the page size (default: 20)
     * @param sort  the sort criteria (default: "sku,asc")
     * @return paginated search results with 200 status
     */
    @GetMapping("/search")
    public ResponseEntity<PagedProductResponse> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "sku,asc") String sort) {

        Pageable pageable = createPageable(page, size, sort);
        PagedProductResponse response = productService.searchProducts(query, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Create a Pageable object from pagination parameters.
     *
     * @param page the page number
     * @param size the page size
     * @param sort the sort criteria in format "property,direction"
     * @return the Pageable object
     */
    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String property = sortParams[0];
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}
