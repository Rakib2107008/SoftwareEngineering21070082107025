package com.mobilezbd.controller;

import com.mobilezbd.dto.ProductDetailsDto;
import com.mobilezbd.service.CartDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class CartDetailsController {

    private final CartDetailsService cartDetailsService;

    public CartDetailsController(CartDetailsService cartDetailsService) {
        this.cartDetailsService = cartDetailsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsDto> getDetails(@PathVariable("id") Long productId) {
        return ResponseEntity.ok(cartDetailsService.getProductDetails(productId));
    }
}
