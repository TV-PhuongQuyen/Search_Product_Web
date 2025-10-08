package com.example.product_service.repository;

import com.example.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public interface ProductRepository extends JpaRepository<Product,Long> {
    public boolean existsById(Long id);
}
