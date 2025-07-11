package com.example.simple.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.simple.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}