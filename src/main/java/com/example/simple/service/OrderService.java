package com.example.simple.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.simple.enums.OrderStatus;
import com.example.simple.model.Order;
import com.example.simple.model.OrderItem;
import com.example.simple.model.Product;
import com.example.simple.model.User;
import com.example.simple.repository.OrderItemRepository;
import com.example.simple.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
		super();
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
	}

    
    //used by stripe webhook
    @Transactional
	public Order updateOrderStatus(String paymentIntentId, OrderStatus newStatus) {
		 Optional<Order> optionalOrder = getByStripePaymentIntentId(paymentIntentId);
	        if (optionalOrder.isPresent()) {
	            Order order = optionalOrder.get();
	            order.setOrderStaus(newStatus);
	            return orderRepository.save(order);
	        } else {
	            throw new IllegalArgumentException("Order not found by PaymentIntent ID: " + paymentIntentId);
	        }
	}
    public Optional<Order> getByStripePaymentIntentId(String paymentIntentId) {
 	   return orderRepository.findByPaymentIntentId(paymentIntentId);
	}

    //when saving
	public OrderItem createOrderItem(Product product, Long quantity) {
		OrderItem orderItem = new OrderItem();
	    orderItem.setProduct(product);
	    orderItem.setProductName(product.getName());
	    orderItem.setQuantity(quantity);
	    orderItem.setUnitPrice(product.getPrice());
	    return orderItem;
	}

	public Order createOrder(User user, List<OrderItem> orderItems, long calculatedAmount, String currency) {
		Order order = new Order();
	    order.setUser(user);
	    order.setTotalAmount(calculatedAmount);
	    order.setCurrency(currency);
	    
	    for (OrderItem item : orderItems) {
	        item.setOrder(order); // set back-reference before saving items 
	    }
	    
	    order.setOrderItems(orderItems);//assign order items to order before saving order
	    Order savedOrder = orderRepository.save(order);//saving order saves orderItems

	    return savedOrder;
		    
	}
	
	public Order savePaymentIntentId(Long orderId, String paymnentIntentId) {
		 Optional<Order> optionalOrder = orderRepository.findById(orderId);
	        if (optionalOrder.isPresent()) {
	            Order order = optionalOrder.get();
	            order.setPaymentIntentId(paymnentIntentId);
	            return orderRepository.save(order);
	        } else {
	            throw new IllegalArgumentException("Order not found with ID: " + orderId);
	        }
	}
	
}
