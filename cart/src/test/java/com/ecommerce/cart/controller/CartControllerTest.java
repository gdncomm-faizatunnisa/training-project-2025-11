package com.ecommerce.cart.controller;

import com.ecommerce.cart.dto.CartDTO;
import com.ecommerce.cart.dto.CartItemDTO;
import com.ecommerce.cart.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetCart() throws Exception {
        System.out.println("Running testGetCart...");
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(1L);
        cartDTO.setUserId(1L);
        cartDTO.setTotal(BigDecimal.ZERO);
        cartDTO.setItems(Collections.emptyList());

        when(cartService.getCart(1L)).thenReturn(cartDTO);

        mockMvc.perform(get("/api/carts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    public void testAddToCart() throws Exception {
        System.out.println("Running testAddToCart...");
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(1L);
        cartDTO.setUserId(1L);
        cartDTO.setTotal(new BigDecimal("20.00"));

        CartItemDTO itemDTO = new CartItemDTO();
        itemDTO.setProductId(100L);
        itemDTO.setQuantity(2);
        cartDTO.setItems(Collections.singletonList(itemDTO));

        when(cartService.addToCart(eq(1L), eq(100L), eq(2))).thenReturn(cartDTO);

        CartController.AddToCartRequest request = new CartController.AddToCartRequest();
        request.setProductId(100L);
        request.setQuantity(2);

        mockMvc.perform(post("/api/carts/1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productId").value(100))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }
}
