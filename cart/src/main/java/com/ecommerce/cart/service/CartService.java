package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.CartDTO;

public interface CartService {
    CartDTO getCart(Long userId);

    CartDTO addToCart(Long userId, Long productId, Integer quantity);

    CartDTO removeFromCart(Long userId, Long productId);

    void clearCart(Long userId);
}
