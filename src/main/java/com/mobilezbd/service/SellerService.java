package com.mobilezbd.service;

import com.mobilezbd.dto.AdminProductRequest;
import com.mobilezbd.dto.ProductDetailsDto;
import com.mobilezbd.entity.ProductDetails;
import com.mobilezbd.entity.ProductOwnerRole;
import com.mobilezbd.entity.Products;
import com.mobilezbd.entity.User;
import com.mobilezbd.entity.UserRole;
import com.mobilezbd.exception.BusinessException;
import com.mobilezbd.exception.ResourceNotFoundException;
import com.mobilezbd.repository.ProductDetailsRepository;
import com.mobilezbd.repository.ProductsRepository;
import com.mobilezbd.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SellerService {

    private final ProductDetailsRepository productDetailsRepository;
    private final ProductsRepository productsRepository;
    private final UserRepository userRepository;

    public SellerService(ProductDetailsRepository productDetailsRepository,
                         ProductsRepository productsRepository,
                         UserRepository userRepository) {
        this.productDetailsRepository = productDetailsRepository;
        this.productsRepository = productsRepository;
        this.userRepository = userRepository;
    }

    public ProductDetailsDto addOwnProduct(AdminProductRequest request, String sellerEmail) {
        User seller = loadSeller(sellerEmail);

        ProductDetails details = mapToEntity(request, ProductDetails.builder().build());
        ProductDetails saved = productDetailsRepository.save(details);

        Products summary = Products.builder()
                .name(saved.getName())
                .price(saved.getPrice())
                .discount(BigDecimal.ZERO)
                .image(saved.getImage())
                .category(saved.getCategory().name())
                .role(ProductOwnerRole.SELLER)
                .ownerUser(seller)
                .productDetails(saved)
                .build();
        productsRepository.save(summary);
        return ProductMapper.toDetails(summary);
    }

    public List<ProductDetailsDto> getOwnProducts(String sellerEmail) {
        return productsRepository.findByOwnerUserEmail(sellerEmail).stream()
                .map(ProductMapper::toDetails)
                .toList();
    }

    public ProductDetailsDto updateOwnProduct(Long productDetailsId, AdminProductRequest request, String sellerEmail) {
        List<Products> ownedRows = productsRepository.findByOwnerUserEmailAndProductDetailsId(sellerEmail, productDetailsId);
        if (ownedRows.isEmpty()) {
            throw new ResourceNotFoundException("Seller product not found: " + productDetailsId);
        }

        ProductDetails details = productDetailsRepository.findById(productDetailsId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productDetailsId));
        ProductDetails updated = productDetailsRepository.save(mapToEntity(request, details));

        for (Products row : ownedRows) {
            row.setName(updated.getName());
            row.setPrice(updated.getPrice());
            row.setImage(updated.getImage());
            row.setCategory(updated.getCategory().name());
            row.setRole(ProductOwnerRole.SELLER);
        }
        productsRepository.saveAll(ownedRows);

        return ProductMapper.toDetails(ownedRows.get(0));
    }

    public void deleteOwnProduct(Long productDetailsId, String sellerEmail) {
        List<Products> ownedRows = productsRepository.findByOwnerUserEmailAndProductDetailsId(sellerEmail, productDetailsId);
        if (ownedRows.isEmpty()) {
            throw new ResourceNotFoundException("Seller product not found: " + productDetailsId);
        }

        ProductDetails details = productDetailsRepository.findById(productDetailsId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productDetailsId));

        productsRepository.deleteAll(ownedRows);
        productDetailsRepository.delete(details);
    }

    private User loadSeller(String email) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found: " + email));
        if (seller.getRole() != UserRole.ROLE_SELLER) {
            throw new BusinessException("Only sellers can manage seller products");
        }
        return seller;
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
}
