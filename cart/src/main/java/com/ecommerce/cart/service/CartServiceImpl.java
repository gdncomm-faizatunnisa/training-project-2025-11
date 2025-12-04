package com.ecommerce.cart.service;

import com.ecommerce.cart.client.ProductClient;
import com.ecommerce.cart.dto.CartDTO;
import com.ecommerce.cart.dto.CartItemDTO;
import com.ecommerce.cart.dto.ProductDTO;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;

    public CartServiceImpl(CartRepository cartRepository,
            ProductClient productClient) {
        this.cartRepository = cartRepository;
        this.productClient = productClient;
    }

    @Override
    public CartDTO getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));
        return mapToDTO(cart);
    }

    @Override
    public CartDTO addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        // Call product service to get price
        ProductDTO product = productClient.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            item.setPrice(product.getPrice()); // Update price in case it changed
        } else {
            CartItem newItem = new CartItem(productId, quantity, product.getPrice());
            cart.addItem(newItem);
        }

        cart.calculateTotal();
        Cart savedCart = cartRepository.save(cart);
        return mapToDTO(savedCart);
    }

    @Override
    public CartDTO removeFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cart.removeItem(itemToRemove);
        cart.calculateTotal();

        Cart savedCart = cartRepository.save(cart);
        return mapToDTO(savedCart);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        cart.clearItems();
        cart.setTotal(java.math.BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private Cart createNewCart(Long userId) {
        Cart cart = new Cart(userId);
        return cartRepository.save(cart);
    }

    private CartDTO mapToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setTotal(cart.getTotal());
        dto.setItems(cart.getItems().stream()
                .map(this::mapItemToDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private CartItemDTO mapItemToDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setSubtotal(item.getSubtotal());
        // Note: We don't have product name stored in CartItem,
        // if needed we would need to fetch it from Product Service or store it in
        // CartItem
        return dto;
    }
}
