package com.mobilezbd.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mobilezbd.dto.AdminProductRequest;
import com.mobilezbd.entity.ProductCategory;
import com.mobilezbd.entity.ProductDetails;
import com.mobilezbd.exception.ResourceNotFoundException;
import com.mobilezbd.repository.ProductDetailsRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductDetailsRepository repository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(repository);
    }

    @Test
    void testGetProductById_found() {
        ProductDetails product = ProductDetails.builder().id(1L).name("iPhone").category(ProductCategory.SMARTPHONE).build();
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        assertEquals("iPhone", productService.getProductById(1L).getName());
    }

    @Test
    void testGetProductById_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void testAddProduct_success() {
        AdminProductRequest req = request();
        when(repository.save(any(ProductDetails.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("Phone X", productService.addProduct(req).getName());
    }

    @Test
    void testUpdateProduct_success() {
        ProductDetails existing = ProductDetails.builder().id(10L).name("Old").build();
        when(repository.findById(10L)).thenReturn(Optional.of(existing));
        when(repository.save(any(ProductDetails.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals("Phone X", productService.updateProduct(10L, request()).getName());
    }

    @Test
    void testDeleteProduct_success() {
        when(repository.existsById(5L)).thenReturn(true);
        productService.deleteProduct(5L);
        verify(repository).deleteById(5L);
    }

    @Test
    void testGetByCategory_returnsFiltered() {
        ProductDetails product = ProductDetails.builder().id(1L).name("Tablet").category(ProductCategory.TABLET).build();
        when(repository.findByCategory(ProductCategory.TABLET)).thenReturn(List.of(product));

        assertEquals(1, productService.getByCategory(ProductCategory.TABLET).size());
    }

    private AdminProductRequest request() {
        AdminProductRequest req = new AdminProductRequest();
        req.setName("Phone X");
        req.setCategory(ProductCategory.SMARTPHONE);
        req.setPrice(new BigDecimal("999.00"));
        req.setQuantity(7);
        return req;
    }
}
