package com.example.orderservice.repository;

import com.example.orderservice.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void saveOrder_success() {
        // Arrange
        Order order = new Order();
        order.setProductId(1L);
        order.setQuantity(2);
        order.setTotalPrice(1999.98);

        // Act
        Order savedOrder = orderRepository.save(order);

        // Assert
        assertNotNull(savedOrder.getId());
        assertEquals(1L, savedOrder.getProductId());
        assertEquals(2, savedOrder.getQuantity());
        assertEquals(1999.98, savedOrder.getTotalPrice());

        // Verify the order is persisted
        Order foundOrder = entityManager.find(Order.class, savedOrder.getId());
        assertEquals(savedOrder, foundOrder);
    }

    @Test
    void findById_success() {
        // Arrange
        Order order = new Order();
        order.setProductId(1L);
        order.setQuantity(2);
        order.setTotalPrice(1999.98);
        Long id = entityManager.persistAndGetId(order, Long.class);

        // Act
        Optional<Order> foundOrder = orderRepository.findById(id);

        // Assert
        assertTrue(foundOrder.isPresent());
        assertEquals(1L, foundOrder.get().getProductId());
        assertEquals(2, foundOrder.get().getQuantity());
        assertEquals(1999.98, foundOrder.get().getTotalPrice());
    }

    @Test
    void findById_notFound() {
        // Act
        Optional<Order> foundOrder = orderRepository.findById(999L);

        // Assert
        assertTrue(foundOrder.isEmpty());
    }
}