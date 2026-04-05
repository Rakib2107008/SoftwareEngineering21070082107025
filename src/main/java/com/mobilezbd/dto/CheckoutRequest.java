package com.mobilezbd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class CheckoutRequest {

    @Valid
    @NotNull
    private CustomerInfoRequest customer;

    @Valid
    @NotNull
    private List<CartItemRequest> items;
}
