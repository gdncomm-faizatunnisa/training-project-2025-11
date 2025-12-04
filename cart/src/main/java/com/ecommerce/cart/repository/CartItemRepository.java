package com.ecommerce.cart.repository;

import com.ecommerce.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CartItem entity.
 * Provides CRUD operations and custom queries for cart item management.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Find all cart items by cart ID
     * 
     * @param cartId the cart ID
     * @return list of cart items
     */
    List<CartItem> findByCartId(Long cartId);

    /**
     * Find a cart item by cart ID and product ID
     * 
     * @param cartId    the cart ID
     * @param productId the product ID
     * @return Optional containing the cart item if found
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Delete all cart items by cart ID
     * 
     * @param cartId the cart ID
     */
    void deleteByCartId(Long cartId);

    /**
     * Delete a cart item by cart ID and product ID
     * 
     * @param cartId    the cart ID
     * @param productId the product ID
     */
    void deleteByCartIdAndProductId(Long cartId, Long productId);
}
