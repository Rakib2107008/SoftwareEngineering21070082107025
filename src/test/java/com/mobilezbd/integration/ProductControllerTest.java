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
import com.mobilezbd.controller.AdminController;
import com.mobilezbd.dto.AdminProductRequest;
import com.mobilezbd.dto.ProductDetailsDto;
import com.mobilezbd.security.JwtAuthenticationFilter;
import com.mobilezbd.service.AdminService;
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

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void adminCrudFlowWithJwt() throws Exception {
        ProductDetailsDto dto = ProductDetailsDto.builder().id(1L).name("Phone").price(BigDecimal.TEN).category("SMARTPHONE").build();
        when(adminService.addProduct(any())).thenReturn(dto);
        when(adminService.updateProduct(eq(1L), any())).thenReturn(dto);
        doNothing().when(adminService).deleteProduct(1L);
        when(adminService.getAllProducts()).thenReturn(List.of());

        AdminProductRequest req = new AdminProductRequest();
        req.setName("Phone");
        req.setCategory(com.mobilezbd.entity.ProductCategory.SMARTPHONE);
        req.setPrice(new BigDecimal("100"));
        req.setQuantity(3);
        req.setReleaseDate(LocalDate.now());

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Phone"));

        mockMvc.perform(put("/api/admin/products/1")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/products").header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/admin/products/1").header("Authorization", "Bearer admin-token"))
                .andExpect(status().isNoContent());
    }
}
