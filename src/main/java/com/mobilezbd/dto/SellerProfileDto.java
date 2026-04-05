package com.mobilezbd.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerProfileDto {
    private Long sellerId;
    private String name;
    private String email;
    private String account;
    private String role;
}
