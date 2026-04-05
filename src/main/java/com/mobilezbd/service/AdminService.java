package com.mobilezbd.service;

import com.mobilezbd.dto.AdminOrderUpdateRequest;
import com.mobilezbd.dto.AdminProductRequest;
import com.mobilezbd.dto.OrderItemDto;
import com.mobilezbd.dto.ProductDetailsDto;
import com.mobilezbd.dto.ProductSummaryDto;
import com.mobilezbd.entity.ProductDetails;
import com.mobilezbd.entity.ProductSellHistory;
import com.mobilezbd.entity.Products;
import com.mobilezbd.entity.ProductCategory;
import com.mobilezbd.entity.ProductOwnerRole;
import com.mobilezbd.exception.ResourceNotFoundException;
import com.mobilezbd.repository.ProductDetailsRepository;
import com.mobilezbd.repository.ProductSellHistoryRepository;
import com.mobilezbd.repository.ProductsRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final ProductDetailsRepository productDetailsRepository;
    private final ProductsRepository productsRepository;
    private final ProductSellHistoryRepository sellHistoryRepository;

    public AdminService(ProductDetailsRepository productDetailsRepository,
                        ProductsRepository productsRepository,
                        ProductSellHistoryRepository sellHistoryRepository) {
        this.productDetailsRepository = productDetailsRepository;
        this.productsRepository = productsRepository;
        this.sellHistoryRepository = sellHistoryRepository;
    }

    public ProductDetailsDto addProduct(AdminProductRequest request) {
        ProductDetails details = mapToEntity(request, ProductDetails.builder().build());
        ProductDetails saved = productDetailsRepository.save(details);

        Products summary = Products.builder()
                .name(saved.getName())
                .price(saved.getPrice())
                .discount(java.math.BigDecimal.ZERO)
                .image(saved.getImage())
                .category(saved.getCategory().name())
            .role(ProductOwnerRole.ADMIN)
                .productDetails(saved)
                .build();
        productsRepository.save(summary);
        return ProductMapper.toDetails(saved);
    }

    public ProductDetailsDto updateProduct(Long id, AdminProductRequest request) {
        ProductDetails details = productDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        ProductDetails updated = productDetailsRepository.save(mapToEntity(request, details));
        List<Products> summaries = productsRepository.findByProductDetailsId(id);
        for (Products p : summaries) {
            p.setName(updated.getName());
            p.setPrice(updated.getPrice());
            p.setImage(updated.getImage());
            p.setCategory(updated.getCategory().name());
            if (p.getRole() == null) {
                p.setRole(ProductOwnerRole.ADMIN);
            }
        }
        productsRepository.saveAll(summaries);
        return ProductMapper.toDetails(updated);
    }

    public void deleteProduct(Long id) {
        ProductDetails details = productDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        productsRepository.deleteAll(productsRepository.findByProductDetailsId(id));
        productDetailsRepository.delete(details);
    }

    public List<ProductDetailsDto> getAllProducts(String search,
                                                  String category,
                                                  LocalDate releaseDateFrom,
                                                  LocalDate releaseDateTo,
                                                  BigDecimal minPrice,
                                                  BigDecimal maxPrice,
                                                  Integer minQty,
                                                  Integer maxQty) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        ProductCategory categoryFilter = null;
        if (category != null && !category.isBlank()) {
            categoryFilter = ProductCategory.valueOf(category.trim().toUpperCase(Locale.ROOT));
        }

        ProductCategory finalCategoryFilter = categoryFilter;
        return productDetailsRepository.findAll().stream()
            .filter(p -> normalizedSearch.isBlank()
                || (p.getName() != null && p.getName().toLowerCase(Locale.ROOT).contains(normalizedSearch)))
                .filter(p -> finalCategoryFilter == null || p.getCategory() == finalCategoryFilter)
                .filter(p -> releaseDateFrom == null || (p.getReleaseDate() != null && !p.getReleaseDate().isBefore(releaseDateFrom)))
                .filter(p -> releaseDateTo == null || (p.getReleaseDate() != null && !p.getReleaseDate().isAfter(releaseDateTo)))
            .filter(p -> minPrice == null || (p.getPrice() != null && p.getPrice().compareTo(minPrice) >= 0))
            .filter(p -> maxPrice == null || (p.getPrice() != null && p.getPrice().compareTo(maxPrice) <= 0))
            .filter(p -> minQty == null || (p.getQuantity() != null && p.getQuantity() >= minQty))
            .filter(p -> maxQty == null || (p.getQuantity() != null && p.getQuantity() <= maxQty))
                .map(ProductMapper::toDetails)
                .toList();
    }

    public List<ProductDetailsDto> getAllProducts() {
        return getAllProducts(null, null, null, null, null, null, null, null);
    }

    public List<OrderItemDto> getAllOrders(String search,
                                           LocalDate sellDateFrom,
                                           LocalDate sellDateTo,
                                           Integer minQty,
                                           Integer maxQty,
                                           BigDecimal minPrice,
                                           BigDecimal maxPrice) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        return sellHistoryRepository.findAll().stream()
                .filter(order -> normalizedSearch.isBlank()
                || (order.getProductName() != null
                && order.getProductName().toLowerCase(Locale.ROOT).contains(normalizedSearch)))
            .filter(order -> sellDateFrom == null
                || (order.getSellDate() != null && !order.getSellDate().toLocalDate().isBefore(sellDateFrom)))
            .filter(order -> sellDateTo == null
                || (order.getSellDate() != null && !order.getSellDate().toLocalDate().isAfter(sellDateTo)))
            .filter(order -> minQty == null || (order.getSoldQuantity() != null && order.getSoldQuantity() >= minQty))
            .filter(order -> maxQty == null || (order.getSoldQuantity() != null && order.getSoldQuantity() <= maxQty))
            .filter(order -> minPrice == null || (order.getSellPrice() != null && order.getSellPrice().compareTo(minPrice) >= 0))
            .filter(order -> maxPrice == null || (order.getSellPrice() != null && order.getSellPrice().compareTo(maxPrice) <= 0))
                .map(this::toOrderItem)
                .toList();
    }

    public List<OrderItemDto> getAllOrders() {
        return getAllOrders(null, null, null, null, null, null, null);
    }

    public OrderItemDto updateOrder(Long id, AdminOrderUpdateRequest request) {
        ProductSellHistory history = sellHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        history.setSoldQuantity(request.getSoldQuantity());
        ProductSellHistory updated = sellHistoryRepository.save(history);
        return toOrderItem(updated);
    }

    public void deleteOrder(Long id) {
        if (!sellHistoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found: " + id);
        }
        sellHistoryRepository.deleteById(id);
    }

    private ProductDetails mapToEntity(AdminProductRequest request, ProductDetails details) {
        details.setName(request.getName());
        details.setCategory(request.getCategory());
        details.setPrice(request.getPrice());
        details.setReleaseDate(request.getReleaseDate());
        details.setQuantity(request.getQuantity());
        details.setDisplay(request.getDisplay());
        details.setChipset(request.getChipset());
        details.setCamera(request.getCamera());
        details.setWarranty(request.getWarranty());
        details.setColor(request.getColor());
        details.setMemory(request.getMemory());
        details.setUi(request.getUi());
        details.setOs(request.getOs());
        details.setBattery(request.getBattery());
        details.setImage(request.getImage());
        return details;
    }

    private OrderItemDto toOrderItem(ProductSellHistory history) {
        int soldQty = history.getSoldQuantity() == null ? 0 : history.getSoldQuantity();
        java.math.BigDecimal sellPrice = history.getSellPrice() == null ? java.math.BigDecimal.ZERO : history.getSellPrice();
        return OrderItemDto.builder()
                .id(history.getId())
                .productId(history.getProduct() != null ? history.getProduct().getId() : null)
            .customerName(history.getCustomerName())
                .productName(history.getProductName())
                .category(history.getCategory())
                .color(history.getColor())
            .quantity(soldQty)
            .unitPrice(sellPrice)
            .lineTotal(sellPrice.multiply(java.math.BigDecimal.valueOf(soldQty)))
                .sellDate(history.getSellDate())
                .build();
    }
}
