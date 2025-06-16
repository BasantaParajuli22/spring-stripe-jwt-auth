package com.example.simple.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.simple.dto.CreatePaymentRequest;
import com.example.simple.dto.PaymentResponse;
import com.example.simple.service.StripeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/payments")
public class StripeController {

	private final StripeService stripeService;
	
	public StripeController(StripeService stripeService) {
		super();
		this.stripeService = stripeService;
	}

	@PostMapping("/create-payment-intent")
	public ResponseEntity<?> createPaymentParams(@Valid @RequestBody CreatePaymentRequest request){

		PaymentResponse paymentResponse= stripeService.createPaymentIntent(request);
		return ResponseEntity.status(HttpStatus.CREATED).body( paymentResponse );
	}
	
	@PostMapping("/confirm")
	public ResponseEntity<?> confirmPaymentIntent(@RequestBody String paymentIntent){
		stripeService.confirmPaymentIntent(paymentIntent);
		return ResponseEntity.status(HttpStatus.CREATED).body("Payment Intent confirmed");
	}
	
	@PostMapping("/cancel")
	public ResponseEntity<?> cancelaymentIntent(@RequestBody String paymentIntent){
		stripeService.cancelPaymentIntent(paymentIntent);
		return ResponseEntity.status(HttpStatus.CREATED).body("Payment Intent cancelled");
	}
}
