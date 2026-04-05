package com.mobilezbd.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mobilezbd.dto.CartItemRequest;
import com.mobilezbd.dto.CheckoutRequest;
import com.mobilezbd.dto.CustomerInfoRequest;
import com.mobilezbd.dto.OrderResponse;
import com.mobilezbd.entity.ProductSellHistory;
import com.mobilezbd.exception.BusinessException;
import com.mobilezbd.repository.ProductSellHistoryRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CustomerInfoService customerInfoService;

    @Mock
    private ProductSellHistoryRepository sellHistoryRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(customerInfoService, sellHistoryRepository);
    }

    @Test
    void testCreateOrder_success() {
        OrderResponse response = OrderResponse.builder().customerName("Rakib").grandTotal(new BigDecimal("100")).build();
        when(customerInfoService.placeOrder(any(), any())).thenReturn(response);

        OrderResponse actual = orderService.createOrder(request(), "u@mail.com");
        assertEquals("Rakib", actual.getCustomerName());
    }

    @Test
    void testCreateOrder_stockShortage() {
        doThrow(new BusinessException("Insufficient stock")).when(customerInfoService).patchStock(any());
        assertThrows(BusinessException.class, () -> orderService.createOrder(request(), "u@mail.com"));
    }

    @Test
    void testGetOrdersByCustomer() {
        ProductSellHistory h = ProductSellHistory.builder().id(1L).productName("Phone").build();
        when(sellHistoryRepository.findByUserEmail("u@mail.com")).thenReturn(List.of(h));

        assertEquals(1, orderService.getOrdersByCustomer("u@mail.com").size());
    }

    private CheckoutRequest request() {
        CheckoutRequest req = new CheckoutRequest();
        CustomerInfoRequest customer = new CustomerInfoRequest();
        customer.setName("Rakib");
        customer.setAddress("Dhaka");
        customer.setEmail("u@mail.com");
        customer.setMobileNumber("0170000");
        req.setCustomer(customer);

        CartItemRequest item = new CartItemRequest();
        item.setProductId(1L);
        item.setQuantity(1);
        req.setItems(List.of(item));
        return req;
    }
}
