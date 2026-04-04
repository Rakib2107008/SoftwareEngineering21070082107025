package com.mobilezbd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class StockPatchRequest {
    @NotNull
    @Valid
    private List<CartItemRequest> items;
}
