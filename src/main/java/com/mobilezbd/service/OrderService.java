package com.mobilezbd.service;

import com.mobilezbd.dto.CheckoutRequest;
import com.mobilezbd.dto.OrderItemDto;
import com.mobilezbd.dto.OrderResponse;
import com.mobilezbd.dto.StockPatchRequest;
import com.mobilezbd.entity.ProductSellHistory;
import com.mobilezbd.repository.ProductSellHistoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final CustomerInfoService customerInfoService;
    private final ProductSellHistoryRepository sellHistoryRepository;

    public OrderService(CustomerInfoService customerInfoService, ProductSellHistoryRepository sellHistoryRepository) {
        this.customerInfoService = customerInfoService;
        this.sellHistoryRepository = sellHistoryRepository;
    }

    public OrderResponse createOrder(CheckoutRequest request, String email) {
        StockPatchRequest patchRequest = new StockPatchRequest();
        patchRequest.setItems(request.getItems());
        customerInfoService.patchStock(patchRequest);
        return customerInfoService.placeOrder(request, email);
    }

    public List<OrderItemDto> getOrdersByCustomer(String email) {
        return sellHistoryRepository.findByUserEmail(email).stream()
                .map(this::toOrderItem)
                .toList();
    }

    private OrderItemDto toOrderItem(ProductSellHistory history) {
        int soldQty = history.getSoldQuantity() == null ? 0 : history.getSoldQuantity();
        java.math.BigDecimal sellPrice = history.getSellPrice() == null ? java.math.BigDecimal.ZERO : history.getSellPrice();
        return OrderItemDto.builder()
                .id(history.getId())
                .productId(history.getProduct() != null ? history.getProduct().getId() : null)
            .customerName(history.getCustomerName())
                .productName(history.getProductName())
                .category(history.getCategory())
                .color(history.getColor())
            .quantity(soldQty)
            .unitPrice(sellPrice)
            .lineTotal(sellPrice.multiply(java.math.BigDecimal.valueOf(soldQty)))
                .sellDate(history.getSellDate())
                .build();
    }
}
