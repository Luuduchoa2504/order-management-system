package com.example.orderservice.controller;

import com.example.orderservice.config.ProductServiceConfig;
import com.example.orderservice.dto.OrderRequestDTO;
import com.example.orderservice.dto.OrderResponseDTO;
import com.example.orderservice.dto.ProductDTO;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductServiceConfig productServiceConfig;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody OrderRequestDTO orderRequest) {
        // Call Product Service to get product details
        String baseUrl = productServiceConfig.getUrl();
        // Ensure the base URL ends with a slash
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        String productServiceUrl = baseUrl + orderRequest.getProductId();
        ResponseEntity<ProductDTO> productResponse;
        try {
            productResponse = restTemplate.getForEntity(productServiceUrl, ProductDTO.class);
        } catch (RestClientException e) {
            System.err.println("Failed to connect to Product Service: " + e.getMessage());
            return ResponseEntity.status(503).body(null);
        }

        // Check if product exists
        if (!productResponse.getStatusCode().is2xxSuccessful() || productResponse.getBody() == null) {
            return ResponseEntity.notFound().build();
        }

        ProductDTO product = productResponse.getBody();

        // Validate quantity
        if (product.getQuantity() < orderRequest.getQuantity()) {
            return ResponseEntity.badRequest().build();
        }

        // Calculate total price
        double totalPrice = product.getPrice() * orderRequest.getQuantity();

        // Create and save the order
        Order order = new Order();
        order.setProductId(orderRequest.getProductId());
        order.setQuantity(orderRequest.getQuantity());
        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        // Prepare response
        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(savedOrder.getId());
        response.setProductName(product.getName());
        response.setTotalPrice(savedOrder.getTotalPrice());

        return ResponseEntity.ok(response);
    }
}

