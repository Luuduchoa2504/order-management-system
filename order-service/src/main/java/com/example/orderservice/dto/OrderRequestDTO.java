package com.example.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequestDTO {
    @NotNull(message = "Product ID must not be null")
    @Min(value = 1, message = "Product ID must be a positive number")
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}