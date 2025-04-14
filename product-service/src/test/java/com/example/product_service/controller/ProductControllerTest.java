package com.example.product_service.controller;

import com.example.product_service.entity.Product;
import com.example.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;

    @BeforeEach
    void setUp() {
        // Setup a default product
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(999.99);
        product.setQuantity(10);
    }

    @Test
    void getAllProducts_success() throws Exception {
        // Arrange
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setPrice(499.99);
        product2.setQuantity(20);

        List<Product> products = Arrays.asList(product, product2);
        when(productRepository.findAll()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Phone"));
    }

    @Test
    void getAllProducts_emptyList() throws Exception {
        // Arrange
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getProductById_success() throws Exception {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        mockMvc.perform(get("/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void getProductById_notFound() throws Exception {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_success() throws Exception {
        // Arrange
        Product newProduct = new Product();
        newProduct.setName("Laptop");
        newProduct.setPrice(999.99);
        newProduct.setQuantity(10);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Laptop");
        savedProduct.setPrice(999.99);
        savedProduct.setQuantity(10);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act & Assert
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void updateProduct_success() throws Exception {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("New Laptop");
        updatedProduct.setPrice(999.99);
        updatedProduct.setQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Laptop"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void updateProduct_notFound() throws Exception {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("New Laptop");
        updatedProduct.setPrice(999.99);
        updatedProduct.setQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_success() throws Exception {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_notFound() throws Exception {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productRepository, never()).deleteById(1L);
    }
}