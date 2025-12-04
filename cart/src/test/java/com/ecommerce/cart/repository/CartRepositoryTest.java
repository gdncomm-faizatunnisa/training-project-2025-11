package com.ecommerce.cart.repository;

import com.ecommerce.cart.entity.Cart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Test
    public void testSaveAndFindCart() {
        System.out.println("Running testSaveAndFindCart...");
        Cart cart = new Cart(1L);
        cartRepository.save(cart);

        Optional<Cart> foundCart = cartRepository.findByUserId(1L);
        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getUserId()).isEqualTo(1L);
    }

    @Test
    public void testExistsByUserId() {
        System.out.println("Running testExistsByUserId...");
        Cart cart = new Cart(2L);
        cartRepository.save(cart);

        boolean exists = cartRepository.existsByUserId(2L);
        assertThat(exists).isTrue();

        boolean notExists = cartRepository.existsByUserId(99L);
        assertThat(notExists).isFalse();
    }
}
