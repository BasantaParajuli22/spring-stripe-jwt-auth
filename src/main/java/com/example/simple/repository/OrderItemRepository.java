package com.example.simple.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.simple.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
