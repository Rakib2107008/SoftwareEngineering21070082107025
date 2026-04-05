package com.mobilezbd.service;

import com.mobilezbd.dto.CartItemRequest;
import com.mobilezbd.dto.CartValidationResponse;
import com.mobilezbd.entity.ProductDetails;
import com.mobilezbd.exception.ResourceNotFoundException;
import com.mobilezbd.repository.ProductDetailsRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CartListService {

    private final ProductDetailsRepository productDetailsRepository;

    public CartListService(ProductDetailsRepository productDetailsRepository) {
        this.productDetailsRepository = productDetailsRepository;
    }

    public CartValidationResponse validate(List<CartItemRequest> items) {
        for (CartItemRequest item : items) {
            ProductDetails product = productDetailsRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));

            int remaining = product.getQuantity() - item.getQuantity();
            if (remaining < 0) {
                int shortage = Math.abs(remaining);
                return CartValidationResponse.builder()
                        .valid(false)
                        .message("Shortage of " + shortage + " units for " + product.getName())
                        .build();
            }
        }

        return CartValidationResponse.builder().valid(true).message("Cart validated").build();
    }
}
