package com.mobilezbd.controller;

import com.mobilezbd.dto.AdminProductRequest;
import com.mobilezbd.dto.OrderItemDto;
import com.mobilezbd.dto.ProductDetailsDto;
import com.mobilezbd.dto.SellerProfileDto;
import com.mobilezbd.service.OrderService;
import com.mobilezbd.service.SellerService;
import com.mobilezbd.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasRole('SELLER')")
public class SellerController {

    private final SellerService sellerService;
    private final UserService userService;
    private final OrderService orderService;

    public SellerController(SellerService sellerService, UserService userService, OrderService orderService) {
        this.sellerService = sellerService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDetailsDto> addProduct(@Valid @RequestBody AdminProductRequest request,
                                                        Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sellerService.addOwnProduct(request, principal.getName()));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDetailsDto>> getProducts(Principal principal) {
        return ResponseEntity.ok(sellerService.getOwnProducts(principal.getName()));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDetailsDto> updateProduct(@PathVariable Long id,
                                                           @Valid @RequestBody AdminProductRequest request,
                                                           Principal principal) {
        return ResponseEntity.ok(sellerService.updateOwnProduct(id, request, principal.getName()));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, Principal principal) {
        sellerService.deleteOwnProduct(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<SellerProfileDto> profile(Principal principal) {
        return ResponseEntity.ok(userService.getSellerProfile(principal.getName()));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderItemDto>> myOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(principal.getName()));
    }
}
