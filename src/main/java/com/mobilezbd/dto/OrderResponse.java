package com.mobilezbd.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private Long customerId;
    private String customerName;
    private String address;
    private String email;
    private String mobileNumber;
    private String notes;
    private LocalDateTime sellDate;
    private List<OrderItemDto> items;
    private BigDecimal grandTotal;
}
