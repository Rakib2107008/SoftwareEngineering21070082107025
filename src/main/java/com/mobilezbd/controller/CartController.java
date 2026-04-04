package com.mobilezbd.controller;

import com.mobilezbd.dto.ProductSummaryDto;
import com.mobilezbd.service.CartService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<ProductSummaryDto>> getCartProducts(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(cartService.getProductsByCategory(category));
    }
}
