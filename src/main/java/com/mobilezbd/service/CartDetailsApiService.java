package com.mobilezbd.service;

import com.mobilezbd.dto.ProductDetailsDto;
import org.springframework.stereotype.Service;

@Service
public class CartDetailsApiService {

    private final CartDetailsService cartDetailsService;

    public CartDetailsApiService(CartDetailsService cartDetailsService) {
        this.cartDetailsService = cartDetailsService;
    }

    public ProductDetailsDto findById(Long id) {
        return cartDetailsService.getProductDetails(id);
    }
}
