package com.mobilezbd.controller;

import com.mobilezbd.dto.CheckoutRequest;
import com.mobilezbd.dto.OrderItemDto;
import com.mobilezbd.dto.OrderResponse;
import com.mobilezbd.dto.StockPatchRequest;
import com.mobilezbd.service.CustomerInfoService;
import com.mobilezbd.service.OrderService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CustomerInfoController {

    private final CustomerInfoService customerInfoService;
    private final OrderService orderService;

    public CustomerInfoController(CustomerInfoService customerInfoService, OrderService orderService) {
        this.customerInfoService = customerInfoService;
        this.orderService = orderService;
    }

    @PatchMapping("/products/stock")
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER')")
    public ResponseEntity<Void> patchStock(@Valid @RequestBody StockPatchRequest request) {
        customerInfoService.patchStock(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders")
    @PreAuthorize("hasAnyRole('CUSTOMER','SELLER')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CheckoutRequest request, Principal principal) {
        return ResponseEntity.ok(orderService.createOrder(request, principal.getName()));
    }

    @GetMapping("/customer/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderItemDto>> getMyOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(principal.getName()));
    }
}
