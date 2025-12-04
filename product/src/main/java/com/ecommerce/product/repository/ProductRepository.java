package com.ecommerce.product.repository;

import com.ecommerce.product.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Product entity.
 * Provides CRUD operations and custom query methods for product management.
 * Supports pagination and wildcard search functionality.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * Find a product by its SKU.
     *
     * @param sku the product SKU
     * @return Optional containing the product if found
     */
    Optional<ProductEntity> findBySku(String sku);

    /**
     * Check if a product exists with the given SKU.
     *
     * @param sku the product SKU
     * @return true if a product with the SKU exists
     */
    boolean existsBySku(String sku);

    /**
     * Search products by name with wildcard matching (case-insensitive).
     * Supports pagination.
     *
     * @param name     the search query (partial name)
     * @param pageable pagination information
     * @return Page of products matching the search criteria
     */
    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<ProductEntity> searchByName(@Param("name") String name, Pageable pageable);

    /**
     * Search products by name or description with wildcard matching
     * (case-insensitive).
     * Supports pagination.
     *
     * @param query    the search query
     * @param pageable pagination information
     * @return Page of products matching the search criteria
     */
    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<ProductEntity> searchByNameOrDescription(@Param("query") String query, Pageable pageable);

    /**
     * Find all products with pagination support.
     * This method is inherited from JpaRepository but explicitly documented here.
     *
     * @param pageable pagination information
     * @return Page of all products
     */
    Page<ProductEntity> findAll(Pageable pageable);
}
