package com.mobilezbd.service;

import com.mobilezbd.dto.ProductSummaryDto;
import com.mobilezbd.entity.Products;
import com.mobilezbd.repository.ProductsRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final ProductsRepository productsRepository;

    public CartService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public List<ProductSummaryDto> getProductsByCategory(String category) {
        return getProducts(category, null, null, null, null);
    }

    public List<ProductSummaryDto> getProducts(String category,
                                               String search,
                                               BigDecimal minPrice,
                                               BigDecimal maxPrice,
                                               String sortBy) {
        List<Products> products = (category == null || category.isBlank())
                ? productsRepository.findAll()
                : productsRepository.findByCategoryIgnoreCase(category);

        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);

        Comparator<Products> comparator = Comparator.comparing(Products::getId).reversed();
        if ("priceAsc".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Products::getPrice);
        } else if ("priceDesc".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Products::getPrice).reversed();
        } else if ("nameAsc".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(Products::getName, String.CASE_INSENSITIVE_ORDER);
        }

        return products.stream()
                .filter(product -> normalizedSearch.isBlank()
                        || product.getName().toLowerCase(Locale.ROOT).contains(normalizedSearch))
                .filter(product -> minPrice == null || product.getPrice().compareTo(minPrice) >= 0)
                .filter(product -> maxPrice == null || product.getPrice().compareTo(maxPrice) <= 0)
                .sorted(comparator)
                .map(ProductMapper::toSummary)
                .toList();
    }

    public Page<ProductSummaryDto> getProductsPage(String category,
                                                   String search,
                                                   BigDecimal minPrice,
                                                   BigDecimal maxPrice,
                                                   String sortBy,
                                                   int page,
                                                   int size) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        Specification<Products> specification = Specification.where(null);

        if (category != null && !category.isBlank()) {
            specification = specification.and((root, query, cb) -> cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase(Locale.ROOT)));
        }
        if (!normalizedSearch.isBlank()) {
            String searchPattern = "%" + normalizedSearch + "%";
            specification = specification.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), searchPattern));
        }
        if (minPrice != null) {
            specification = specification.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            specification = specification.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        Pageable pageable = PageRequest.of(page, size, resolveSort(sortBy));
        return productsRepository.findAll(specification, pageable).map(ProductMapper::toSummary);
    }

    private Sort resolveSort(String sortBy) {
        if ("priceAsc".equalsIgnoreCase(sortBy)) {
            return Sort.by(Sort.Direction.ASC, "price");
        }
        if ("priceDesc".equalsIgnoreCase(sortBy)) {
            return Sort.by(Sort.Direction.DESC, "price");
        }
        if ("nameAsc".equalsIgnoreCase(sortBy)) {
            return Sort.by(Sort.Direction.ASC, "name");
        }
        return Sort.by(Sort.Direction.DESC, "id");
    }
}
