package com.ecommerce.cart.repository;

import com.ecommerce.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Cart entity.
 * Provides CRUD operations and custom queries for cart management.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Find a cart by user ID
     * 
     * @param userId the user ID
     * @return Optional containing the cart if found
     */
    Optional<Cart> findByUserId(Long userId);

    /**
     * Check if a cart exists for a user
     * 
     * @param userId the user ID
     * @return true if cart exists, false otherwise
     */
    boolean existsByUserId(Long userId);

    /**
     * Delete a cart by user ID
     * 
     * @param userId the user ID
     */
    void deleteByUserId(Long userId);
}
