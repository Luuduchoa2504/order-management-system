package com.example.orderservice.controller;

import com.example.orderservice.config.ProductServiceConfig;
import com.example.orderservice.dto.OrderRequestDTO;
import com.example.orderservice.dto.ProductDTO;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private ProductServiceConfig productServiceConfig;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderRequestDTO validRequest;
    private ProductDTO product;
    private Order savedOrder;

    @BeforeEach
    void setUp() {
        // Setup valid request
        validRequest = new OrderRequestDTO();
        validRequest.setProductId(1L);
        validRequest.setQuantity(2);

        // Setup product response
        product = new ProductDTO();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(999.99);
        product.setQuantity(10);

        // Setup saved order
        savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setProductId(1L);
        savedOrder.setQuantity(2);
        savedOrder.setTotalPrice(1999.98);

        // Default mock for product service URL
        when(productServiceConfig.getUrl()).thenReturn("http://localhost:8080/products");
    }

    @Test
    void createOrder_success() throws Exception {
        // Arrange
        when(restTemplate.getForEntity(eq("http://localhost:8080/products/1"), eq(ProductDTO.class)))
                .thenReturn(new ResponseEntity<>(product, HttpStatus.OK));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.productName").value("Laptop"))
                .andExpect(jsonPath("$.totalPrice").value(1999.98));
    }

    @Test
    void createOrder_insufficientQuantity() throws Exception {
        validRequest.setQuantity(15);
        when(restTemplate.getForEntity(eq("http://localhost:8080/products/1"), eq(ProductDTO.class)))
                .thenReturn(new ResponseEntity<>(product, HttpStatus.OK));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Insufficient quantity. Available: 10, Requested: 15"));
    }

    @Test
    void createOrder_validationFails_productIdNull() throws Exception {
        // Arrange
        OrderRequestDTO invalidRequest = new OrderRequestDTO();
        invalidRequest.setProductId(null); // Should fail validation
        invalidRequest.setQuantity(2);

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: productId - Product ID must not be null; "));
    }

    @Test
    void createOrder_validationFails_quantityLessThanOne() throws Exception {
        // Arrange
        OrderRequestDTO invalidRequest = new OrderRequestDTO();
        invalidRequest.setProductId(1L);
        invalidRequest.setQuantity(0); // Should fail validation

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: quantity - Quantity must be at least 1; "));
    }
}