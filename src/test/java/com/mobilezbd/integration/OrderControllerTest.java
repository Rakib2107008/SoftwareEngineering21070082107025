package com.mobilezbd.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobilezbd.controller.CustomerInfoController;
import com.mobilezbd.dto.OrderItemDto;
import com.mobilezbd.dto.OrderResponse;
import com.mobilezbd.security.JwtAuthenticationFilter;
import com.mobilezbd.service.CustomerInfoService;
import com.mobilezbd.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

        @MockBean
        private CustomerInfoService customerInfoService;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void placeOrderFlowAsCustomer() throws Exception {
        OrderResponse response = OrderResponse.builder()
                .customerName("Rakib")
                .email("r@mail.com")
                .grandTotal(new BigDecimal("1500"))
                .sellDate(LocalDateTime.now())
                .items(List.of(OrderItemDto.builder().productId(1L).productName("Phone").quantity(1).unitPrice(new BigDecimal("1500")).lineTotal(new BigDecimal("1500")).build()))
                .build();

        when(orderService.createOrder(any(), any())).thenReturn(response);

        Map<String, Object> payload = Map.of(
                "customer", Map.of(
                        "name", "Rakib",
                        "address", "Dhaka",
                        "email", "r@mail.com",
                        "mobileNumber", "0170000",
                        "notes", ""
                ),
                "items", List.of(Map.of("productId", 1, "quantity", 1))
        );

        mockMvc.perform(post("/api/orders")
                        .principal(() -> "customer@mail.com")
                        .header("Authorization", "Bearer customer-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Rakib"));
    }
}
