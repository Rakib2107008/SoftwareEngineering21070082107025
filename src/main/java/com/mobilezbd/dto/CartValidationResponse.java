package com.mobilezbd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CartValidationResponse {
    private boolean valid;
    private String message;
}
