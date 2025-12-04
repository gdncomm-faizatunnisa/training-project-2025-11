package com.ecommerce.cart.service;

import com.ecommerce.cart.client.ProductClient;
import com.ecommerce.cart.dto.CartDTO;
import com.ecommerce.cart.dto.ProductDTO;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.repository.CartItemRepository;
import com.ecommerce.cart.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private ProductDTO productDTO;

    @BeforeEach
    public void setUp() {
        System.out.println("Setting up test data...");
        cart = new Cart(1L);
        cart.setId(1L);

        productDTO = new ProductDTO();
        productDTO.setId(100L);
        productDTO.setName("Test Product");
        productDTO.setPrice(new BigDecimal("10.00"));
        productDTO.setStock(10);
    }

    @Test
    public void testGetCart_ExistingCart() {
        System.out.println("Running testGetCart_ExistingCart...");
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartDTO result = cartService.getCart(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(cartRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void testGetCart_NewCart() {
        System.out.println("Running testGetCart_NewCart...");
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        CartDTO result = cartService.getCart(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testAddToCart_NewItem() {
        System.out.println("Running testAddToCart_NewItem...");
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productClient.getProductById(100L)).thenReturn(productDTO);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDTO result = cartService.addToCart(1L, 100L, 2);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getProductId()).isEqualTo(100L);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(result.getTotal()).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    public void testAddToCart_ExistingItem() {
        System.out.println("Running testAddToCart_ExistingItem...");
        CartItem existingItem = new CartItem(100L, 1, new BigDecimal("10.00"));
        cart.addItem(existingItem);
        cart.calculateTotal();

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productClient.getProductById(100L)).thenReturn(productDTO);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDTO result = cartService.addToCart(1L, 100L, 2);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(3); // 1 + 2
        assertThat(result.getTotal()).isEqualByComparingTo(new BigDecimal("30.00"));
    }
}
