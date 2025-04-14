package com.example.product_service.repository;

import com.example.product_service.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void saveProduct_success() {
        // Arrange
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(999.99);
        product.setQuantity(10);

        // Act
        Product savedProduct = productRepository.save(product);

        // Assert
        assertNotNull(savedProduct.getId());
        assertEquals("Laptop", savedProduct.getName());
        assertEquals(999.99, savedProduct.getPrice());
        assertEquals(10, savedProduct.getQuantity());

        // Verify persistence
        Product foundProduct = entityManager.find(Product.class, savedProduct.getId());
        assertEquals(savedProduct, foundProduct);
    }

    @Test
    void findAll_success() {
        // Arrange
        Product product1 = new Product();
        product1.setName("Laptop");
        product1.setPrice(999.99);
        product1.setQuantity(10);
        entityManager.persist(product1);

        Product product2 = new Product();
        product2.setName("Phone");
        product2.setPrice(499.99);
        product2.setQuantity(20);
        entityManager.persist(product2);

        // Act
        List<Product> products = productRepository.findAll();

        // Assert
        assertEquals(2, products.size());
        assertEquals("Laptop", products.get(0).getName());
        assertEquals("Phone", products.get(1).getName());
    }

    @Test
    void findById_success() {
        // Arrange
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(999.99);
        product.setQuantity(10);
        Long id = entityManager.persistAndGetId(product, Long.class);

        // Act
        Optional<Product> foundProduct = productRepository.findById(id);

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals("Laptop", foundProduct.get().getName());
        assertEquals(999.99, foundProduct.get().getPrice());
        assertEquals(10, foundProduct.get().getQuantity());
    }

    @Test
    void findById_notFound() {
        // Act
        Optional<Product> foundProduct = productRepository.findById(999L);

        // Assert
        assertTrue(foundProduct.isEmpty());
    }

    @Test
    void deleteById_success() {
        // Arrange
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(999.99);
        product.setQuantity(10);
        Long id = entityManager.persistAndGetId(product, Long.class);

        // Act
        productRepository.deleteById(id);

        // Assert
        Product foundProduct = entityManager.find(Product.class, id);
        assertNull(foundProduct);
    }

    @Test
    void existsById_success() {
        // Arrange
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(999.99);
        product.setQuantity(10);
        Long id = entityManager.persistAndGetId(product, Long.class);

        // Act & Assert
        assertTrue(productRepository.existsById(id));
        assertFalse(productRepository.existsById(999L));
    }
}