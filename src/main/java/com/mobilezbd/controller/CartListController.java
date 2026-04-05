package com.mobilezbd.controller;

import com.mobilezbd.dto.CartItemRequest;
import com.mobilezbd.dto.CartValidationResponse;
import com.mobilezbd.service.CartListService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartListController {

    private final CartListService cartListService;

    public CartListController(CartListService cartListService) {
        this.cartListService = cartListService;
    }

    @PostMapping("/validate")
    public ResponseEntity<CartValidationResponse> validate(@Valid @RequestBody List<CartItemRequest> items) {
        return ResponseEntity.ok(cartListService.validate(items));
    }
}
