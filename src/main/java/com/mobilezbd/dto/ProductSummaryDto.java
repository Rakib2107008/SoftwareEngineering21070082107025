package com.mobilezbd.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductSummaryDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal discount;
    private String image;
    private String category;
    private Long productDetailsId;
}
