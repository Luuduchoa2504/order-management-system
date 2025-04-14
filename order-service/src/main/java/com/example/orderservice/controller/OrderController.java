package com.example.orderservice.controller;

import com.example.orderservice.config.ProductServiceConfig;
import com.example.orderservice.dto.OrderRequestDTO;
import com.example.orderservice.dto.OrderResponseDTO;
import com.example.orderservice.dto.ProductDTO;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final ProductServiceConfig productServiceConfig;

    public OrderController(OrderRepository orderRepository, RestTemplate restTemplate, ProductServiceConfig productServiceConfig) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.productServiceConfig = productServiceConfig;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderRequestDTO orderRequest) {
        String baseUrl = productServiceConfig.getUrl();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        String productServiceUrl = baseUrl + orderRequest.getProductId();
        ResponseEntity<ProductDTO> productResponse;
        try {
            productResponse = restTemplate.getForEntity(productServiceUrl, ProductDTO.class);
        } catch (RestClientException e) {
            throw new ResourceAccessException("Failed to connect to Product Service: " + e.getMessage());
        }

        if (!productResponse.getStatusCode().is2xxSuccessful() || productResponse.getBody() == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Product not found with ID: " + orderRequest.getProductId());
        }

        ProductDTO product = productResponse.getBody();

        if (product.getQuantity() < orderRequest.getQuantity()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", HttpStatus.BAD_REQUEST.value());
            error.put("error", "Bad Request");
            error.put("message", "Insufficient quantity. Available: " + product.getQuantity() + ", Requested: " + orderRequest.getQuantity());
            return ResponseEntity.status(400).body(error);
        }

        double totalPrice = product.getPrice() * orderRequest.getQuantity();

        Order order = new Order();
        order.setProductId(orderRequest.getProductId());
        order.setQuantity(orderRequest.getQuantity());
        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(savedOrder.getId());
        response.setProductName(product.getName());
        response.setTotalPrice(savedOrder.getTotalPrice());

        return ResponseEntity.ok(response);
    }
}

