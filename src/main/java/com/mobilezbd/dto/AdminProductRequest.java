package com.mobilezbd.dto;

import com.mobilezbd.entity.ProductCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class AdminProductRequest {
    @NotBlank
    private String name;

    @NotNull
    private ProductCategory category;

    @NotNull
    @Min(0)
    private BigDecimal price;

    private LocalDate releaseDate;

    @NotNull
    @Min(0)
    private Integer quantity;

    private String display;
    private String chipset;
    private String camera;
    private String warranty;
    private String color;
    private String memory;
    private String ui;
    private String os;
    private String battery;
    private String image;
}
