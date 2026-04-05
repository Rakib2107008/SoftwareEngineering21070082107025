package com.mobilezbd.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDetailsDto {
    private Long id;
    private String name;
    private String category;
    private String ownerRole;
    private String ownerEmail;
    private BigDecimal price;
    private LocalDate releaseDate;
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
