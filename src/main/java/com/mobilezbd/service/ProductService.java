package com.mobilezbd.service;

import com.mobilezbd.dto.AdminProductRequest;
import com.mobilezbd.dto.ProductDetailsDto;
import com.mobilezbd.entity.ProductCategory;
import com.mobilezbd.entity.ProductDetails;
import com.mobilezbd.exception.ResourceNotFoundException;
import com.mobilezbd.repository.ProductDetailsRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductDetailsRepository productDetailsRepository;

    public ProductService(ProductDetailsRepository productDetailsRepository) {
        this.productDetailsRepository = productDetailsRepository;
    }

    public ProductDetailsDto getProductById(Long id) {
        ProductDetails product = productDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return ProductMapper.toDetails(product);
    }

    public ProductDetailsDto addProduct(AdminProductRequest request) {
        ProductDetails product = ProductDetails.builder().build();
        map(request, product);
        return ProductMapper.toDetails(productDetailsRepository.save(product));
    }

    public ProductDetailsDto updateProduct(Long id, AdminProductRequest request) {
        ProductDetails existing = productDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        map(request, existing);
        return ProductMapper.toDetails(productDetailsRepository.save(existing));
    }

    public void deleteProduct(Long id) {
        if (!productDetailsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        productDetailsRepository.deleteById(id);
    }

    public List<ProductDetailsDto> getByCategory(ProductCategory category) {
        return productDetailsRepository.findByCategory(category).stream().map(ProductMapper::toDetails).toList();
    }

    private void map(AdminProductRequest request, ProductDetails entity) {
        entity.setName(request.getName());
        entity.setCategory(request.getCategory());
        entity.setPrice(request.getPrice());
        entity.setReleaseDate(request.getReleaseDate());
        entity.setQuantity(request.getQuantity());
        entity.setDisplay(request.getDisplay());
        entity.setChipset(request.getChipset());
        entity.setCamera(request.getCamera());
        entity.setWarranty(request.getWarranty());
        entity.setColor(request.getColor());
        entity.setMemory(request.getMemory());
        entity.setUi(request.getUi());
        entity.setOs(request.getOs());
        entity.setBattery(request.getBattery());
        entity.setImage(request.getImage());
    }
}
