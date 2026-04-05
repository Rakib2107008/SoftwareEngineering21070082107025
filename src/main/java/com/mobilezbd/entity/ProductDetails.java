package com.mobilezbd.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    private LocalDate releaseDate;

    @Column(nullable = false)
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

    @Column(columnDefinition = "text")
    private String image;

    @OneToMany(mappedBy = "productDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Products> products = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ProductSellHistory> sellHistories = new ArrayList<>();
}
