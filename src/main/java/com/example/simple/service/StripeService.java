package com.example.simple.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.simple.dto.CreatePaymentRequest;
import com.example.simple.dto.PaymentResponse;
import com.example.simple.model.Order;
import com.example.simple.model.OrderItem;
import com.example.simple.model.Product;
import com.example.simple.model.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

@Service
public class StripeService {

	private final Logger logger = LoggerFactory.getLogger(StripeService.class);
	
	private final UserService userService;
	private final ProductService productService;
	private final OrderService orderService;
	
	
	public StripeService(UserService userService, ProductService productService, OrderService orderService) {
		super();
		this.userService = userService;
		this.productService = productService;
		this.orderService = orderService;
	}

	@Value("${stripe.secret.key}")
	private String stripeKey;
	
	
	//Stripe expects you to configure the key once,
	//and then stripe uses it internally for all requests.
	//so initialize the key
	@PostConstruct
	public void init() {		
		Stripe.apiKey = stripeKey;//this key is used by stripe internally
		logger.info("Stripe key initialized "+stripeKey);
	}

	public PaymentResponse createPaymentIntent(@Valid CreatePaymentRequest request){
		
		//authenticated userId
		Long userId = userService.getCustomUserDetailsUserId();
        // Find user by authenticated userId
        User user = userService.getUserById(userId);
        
		//check product 	
		//check price and quantity of product
		//calculate total price matches with requested price
		//create orderItem
		//create order
		
		Optional<Product> productOptional = productService.findProductById(request.getProductId()); 
        if (productOptional.isEmpty()) {
            throw new IllegalArgumentException("Product not found with ID: " + request.getProductId());
        }
        Product product = productOptional.get();
        
        // Calculate total amount in cents/smallest currency unit
        long calculatedAmount = product.getPrice() * request.getQuantity();

        // Basic validation: ensure the amount from frontend matches backend calculation
        if (calculatedAmount != request.getAmount()) {
            throw new IllegalArgumentException("Mismatched amount. Frontend amount: " + request.getAmount() +
                    ", Backend calculated amount: " + calculatedAmount);
        }

        //first create list of orderItems 
        //then save order with list of items
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderService.createOrderItem(product, request.getQuantity()));
      
        Order order = orderService.createOrder(user, orderItems, calculatedAmount, request.getCurrency());
       
        try {	        
			PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
					.setAmount(request.getAmount())
					.setCurrency(request.getCurrency())
					
					.setAutomaticPaymentMethods( //here we create auto payment methods for frontend to use 
							PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
							.setEnabled(true)
							.build())
					
					//for testing test cards 
//					.setPaymentMethod("pm_card_visa")//attach test card
//					.setConfirm(true) // Confirm immediately
					// //no redirects 
//					.setAutomaticPaymentMethods(
//					        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
//					            .setEnabled(true)
//					            .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
//					            .build()
//					    )
					.putMetadata("orderId", order.getOrderId().toString())
					.putMetadata("email", request.getEmail())
					.build();
			
			PaymentIntent paymentIntent = PaymentIntent.create(params);
				
			orderService.savePaymentIntentId(order.getOrderId(), paymentIntent.getId());
			
			//send response
			PaymentResponse paymentResponse = new PaymentResponse(
					paymentIntent.getClientSecret(), paymentIntent.getId(), paymentIntent.getStatus()
					);
			return paymentResponse;
		} catch (Exception e) {
			logger.error("Error while creating payment intent "+ e.getMessage());
		}
		throw new RuntimeException("Error while creating payment intent");
	}
	
	//retrieve paymentIntent from paymentIntentId and confirm
	public PaymentIntent confirmPaymentIntent(String paymentIntentId) {
		try {
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
			return paymentIntent.confirm();
		} catch (StripeException e) {
			logger.error("Error while confirming payment intent "+ e.getMessage());
		}
		return null;
	}

	//retrieve paymentIntent from paymentIntentId and cancel
	public PaymentIntent cancelPaymentIntent(String paymentIntentId) {
		try {
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
			return paymentIntent.cancel();
		} catch (StripeException e) {
			logger.error("Error while confirming payment intent "+ e.getMessage());
		}
		return null;
	}
}
