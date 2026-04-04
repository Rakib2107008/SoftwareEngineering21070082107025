package com.mobilezbd.service;

import com.mobilezbd.dto.ProductDetailsDto;
import com.mobilezbd.exception.ResourceNotFoundException;
import com.mobilezbd.repository.ProductDetailsRepository;
import org.springframework.stereotype.Service;

@Service
public class CartDetailsService {

    private final ProductDetailsRepository productDetailsRepository;

    public CartDetailsService(ProductDetailsRepository productDetailsRepository) {
        this.productDetailsRepository = productDetailsRepository;
    }

    public ProductDetailsDto getProductDetails(Long productId) {
        return productDetailsRepository.findById(productId)
                .map(ProductMapper::toDetails)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }
}
