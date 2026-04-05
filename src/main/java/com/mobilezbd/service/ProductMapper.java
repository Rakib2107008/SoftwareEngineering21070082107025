package com.mobilezbd.service;

import com.mobilezbd.dto.ProductDetailsDto;
import com.mobilezbd.dto.ProductSummaryDto;
import com.mobilezbd.entity.ProductDetails;
import com.mobilezbd.entity.Products;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static ProductSummaryDto toSummary(Products product) {
        return ProductSummaryDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .image(product.getImage())
                .category(product.getCategory())
                .ownerRole(product.getRole() == null ? null : product.getRole().name())
                .productDetailsId(product.getProductDetails().getId())
                .build();
    }

    public static ProductDetailsDto toDetails(ProductDetails details) {
        String category = details.getCategory() == null ? null : details.getCategory().name();
        return ProductDetailsDto.builder()
                .id(details.getId())
                .name(details.getName())
                .category(category)
                .price(details.getPrice())
                .releaseDate(details.getReleaseDate())
                .quantity(details.getQuantity())
                .display(details.getDisplay())
                .chipset(details.getChipset())
                .camera(details.getCamera())
                .warranty(details.getWarranty())
                .color(details.getColor())
                .memory(details.getMemory())
                .ui(details.getUi())
                .os(details.getOs())
                .battery(details.getBattery())
                .image(details.getImage())
                .build();
    }

    public static ProductDetailsDto toDetails(Products product) {
        ProductDetails details = product.getProductDetails();
        String category = details.getCategory() == null ? null : details.getCategory().name();
        return ProductDetailsDto.builder()
                .id(details.getId())
                .name(details.getName())
                .category(category)
                .ownerRole(product.getRole() == null ? null : product.getRole().name())
                .ownerEmail(product.getOwnerUser() == null ? null : product.getOwnerUser().getEmail())
                .price(details.getPrice())
                .releaseDate(details.getReleaseDate())
                .quantity(details.getQuantity())
                .display(details.getDisplay())
                .chipset(details.getChipset())
                .camera(details.getCamera())
                .warranty(details.getWarranty())
                .color(details.getColor())
                .memory(details.getMemory())
                .ui(details.getUi())
                .os(details.getOs())
                .battery(details.getBattery())
                .image(details.getImage())
                .build();
    }
}
