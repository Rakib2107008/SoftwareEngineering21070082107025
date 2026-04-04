package com.mobilezbd.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.mobilezbd.dto.CartItemRequest;
import com.mobilezbd.entity.ProductCategory;
import com.mobilezbd.entity.ProductDetails;
import com.mobilezbd.repository.ProductDetailsRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ProductDetailsRepository repository;

    private CartListService cartListService;

    @BeforeEach
    void setUp() {
        cartListService = new CartListService(repository);
    }

    @Test
    void testValidateCart_sufficient() {
        when(repository.findById(1L)).thenReturn(Optional.of(product(10)));

        CartItemRequest item = new CartItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);

        assertTrue(cartListService.validate(List.of(item)).isValid());
    }

    @Test
    void testValidateCart_insufficient() {
        when(repository.findById(1L)).thenReturn(Optional.of(product(1)));

        CartItemRequest item = new CartItemRequest();
        item.setProductId(1L);
        item.setQuantity(3);

        assertFalse(cartListService.validate(List.of(item)).isValid());
    }

    private ProductDetails product(int qty) {
        return ProductDetails.builder()
                .id(1L)
                .name("Phone")
                .category(ProductCategory.SMARTPHONE)
                .price(BigDecimal.TEN)
                .quantity(qty)
                .build();
    }
}
