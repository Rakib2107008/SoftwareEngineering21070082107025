package com.mobilezbd.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobilezbd.controller.SellerController;
import com.mobilezbd.dto.AdminProductRequest;
import com.mobilezbd.dto.OrderItemDto;
import com.mobilezbd.dto.ProductDetailsDto;
import com.mobilezbd.dto.SellerProfileDto;
import com.mobilezbd.entity.ProductCategory;
import com.mobilezbd.security.JwtAuthenticationFilter;
import com.mobilezbd.service.OrderService;
import com.mobilezbd.service.SellerService;
import com.mobilezbd.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SellerController.class)
@AutoConfigureMockMvc(addFilters = false)
class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SellerService sellerService;

    @MockBean
    private UserService userService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void sellerCrudAndProfileFlow() throws Exception {
        ProductDetailsDto dto = ProductDetailsDto.builder()
                .id(5L)
                .name("Seller Phone")
                .category("SMARTPHONE")
                .ownerRole("SELLER")
                .ownerEmail("seller@x.com")
                .price(new BigDecimal("45000"))
                .quantity(3)
                .build();

        when(sellerService.addOwnProduct(any(), eq("seller@x.com"))).thenReturn(dto);
        when(sellerService.getOwnProducts("seller@x.com")).thenReturn(List.of(dto));
        when(sellerService.updateOwnProduct(eq(5L), any(), eq("seller@x.com"))).thenReturn(dto);
        doNothing().when(sellerService).deleteOwnProduct(5L, "seller@x.com");

        when(userService.getSellerProfile("seller@x.com")).thenReturn(SellerProfileDto.builder()
                .sellerId(1L)
                .name("Seller")
                .email("seller@x.com")
                .role("ROLE_SELLER")
                .account("shop")
                .build());

        when(orderService.getOrdersByCustomer("seller@x.com")).thenReturn(List.of(OrderItemDto.builder()
                .id(7L)
                .productName("Seller Phone")
                .quantity(1)
                .unitPrice(new BigDecimal("45000"))
                .build()));

        AdminProductRequest req = new AdminProductRequest();
        req.setName("Seller Phone");
        req.setCategory(ProductCategory.SMARTPHONE);
        req.setPrice(new BigDecimal("45000"));
        req.setQuantity(3);
        req.setReleaseDate(LocalDate.now());

        mockMvc.perform(post("/api/seller/products")
                        .principal(() -> "seller@x.com")
                        .header("Authorization", "Bearer seller-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Seller Phone"));

        mockMvc.perform(get("/api/seller/products")
                        .principal(() -> "seller@x.com")
                        .header("Authorization", "Bearer seller-token"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/seller/products/5")
                        .principal(() -> "seller@x.com")
                        .header("Authorization", "Bearer seller-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/seller/products/5")
                        .principal(() -> "seller@x.com")
                        .header("Authorization", "Bearer seller-token"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/seller/profile")
                        .principal(() -> "seller@x.com")
                        .header("Authorization", "Bearer seller-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ROLE_SELLER"));

        mockMvc.perform(get("/api/seller/orders")
                        .principal(() -> "seller@x.com")
                        .header("Authorization", "Bearer seller-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Seller Phone"));
    }
}
