package com.mobilezbd.controller;

import com.mobilezbd.dto.AdminOrderUpdateRequest;
import com.mobilezbd.dto.AdminProductRequest;
import com.mobilezbd.dto.OrderItemDto;
import com.mobilezbd.dto.ProductDetailsDto;
import com.mobilezbd.service.AdminService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDetailsDto> addProduct(@Valid @RequestBody AdminProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addProduct(request));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDetailsDto> updateProduct(@PathVariable Long id, @Valid @RequestBody AdminProductRequest request) {
        return ResponseEntity.ok(adminService.updateProduct(id, request));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDetailsDto>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate releaseDateFrom,
            @RequestParam(required = false) LocalDate releaseDateTo,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minQty,
            @RequestParam(required = false) Integer maxQty) {
        return ResponseEntity.ok(adminService.getAllProducts(
                search,
                category,
                releaseDateFrom,
                releaseDateTo,
                minPrice,
                maxPrice,
                minQty,
                maxQty));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderItemDto>> getOrders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate sellDateFrom,
            @RequestParam(required = false) LocalDate sellDateTo,
            @RequestParam(required = false) Integer minQty,
            @RequestParam(required = false) Integer maxQty,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return ResponseEntity.ok(adminService.getAllOrders(
                search,
                sellDateFrom,
                sellDateTo,
                minQty,
                maxQty,
                minPrice,
                maxPrice));
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<OrderItemDto> updateOrder(@PathVariable Long id, @Valid @RequestBody AdminOrderUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateOrder(id, request));
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        adminService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
