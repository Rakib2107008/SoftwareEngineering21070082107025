package com.mobilezbd.service;

import com.mobilezbd.dto.CartItemRequest;
import com.mobilezbd.dto.CheckoutRequest;
import com.mobilezbd.dto.OrderItemDto;
import com.mobilezbd.dto.OrderResponse;
import com.mobilezbd.dto.StockPatchRequest;
import com.mobilezbd.entity.CustomerAccount;
import com.mobilezbd.entity.ProductDetails;
import com.mobilezbd.entity.ProductSellHistory;
import com.mobilezbd.entity.User;
import com.mobilezbd.exception.BusinessException;
import com.mobilezbd.exception.ResourceNotFoundException;
import com.mobilezbd.repository.CustomerAccountRepository;
import com.mobilezbd.repository.ProductDetailsRepository;
import com.mobilezbd.repository.ProductSellHistoryRepository;
import com.mobilezbd.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CustomerInfoService {

    private final ProductDetailsRepository productDetailsRepository;
    private final ProductSellHistoryRepository sellHistoryRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final UserRepository userRepository;

    public CustomerInfoService(ProductDetailsRepository productDetailsRepository,
                               ProductSellHistoryRepository sellHistoryRepository,
                               CustomerAccountRepository customerAccountRepository,
                               UserRepository userRepository) {
        this.productDetailsRepository = productDetailsRepository;
        this.sellHistoryRepository = sellHistoryRepository;
        this.customerAccountRepository = customerAccountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void patchStock(StockPatchRequest request) {
        for (CartItemRequest item : request.getItems()) {
            ProductDetails product = productDetailsRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));
            int remaining = product.getQuantity() - item.getQuantity();
            if (remaining < 0) {
                throw new BusinessException("Insufficient stock for " + product.getName());
            }
            product.setQuantity(remaining);
            productDetailsRepository.save(product);
        }
    }

    @Transactional
    public OrderResponse placeOrder(CheckoutRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        CustomerAccount account = customerAccountRepository.findByUserEmail(userEmail)
                .orElseGet(() -> customerAccountRepository.save(CustomerAccount.builder()
                        .name(request.getCustomer().getName())
                        .email(request.getCustomer().getEmail())
                        .account(request.getCustomer().getNotes())
                .user(user)
                        .build()));

        if (account.getUser() == null) {
            account.setUser(user);
        }

        account.setName(request.getCustomer().getName());
        account.setEmail(request.getCustomer().getEmail());
        account.setAccount(request.getCustomer().getNotes());
        customerAccountRepository.save(account);

        LocalDateTime now = LocalDateTime.now();
        List<OrderItemDto> items = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (CartItemRequest item : request.getItems()) {
            ProductDetails product = productDetailsRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));

            ProductSellHistory history = ProductSellHistory.builder()
                    .customer(account)
                    .customerName(account.getName())
                    .sellDate(now)
                    .product(product)
                    .productName(product.getName())
                    .sellPrice(product.getPrice())
                    .soldQuantity(item.getQuantity())
                    .color(product.getColor())
                    .category(product.getCategory().name())
                    .build();
            sellHistoryRepository.save(history);

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            grandTotal = grandTotal.add(lineTotal);

            items.add(OrderItemDto.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .color(product.getColor())
                    .category(product.getCategory().name())
                    .quantity(item.getQuantity())
                    .unitPrice(product.getPrice())
                    .lineTotal(lineTotal)
                    .build());
        }

        return OrderResponse.builder()
                .customerId(account.getCustomerId())
                .customerName(request.getCustomer().getName())
                .address(request.getCustomer().getAddress())
                .email(request.getCustomer().getEmail())
                .mobileNumber(request.getCustomer().getMobileNumber())
                .notes(request.getCustomer().getNotes())
                .sellDate(now)
                .items(items)
                .grandTotal(grandTotal)
                .build();
    }
}
