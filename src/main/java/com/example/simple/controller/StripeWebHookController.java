package com.example.simple.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.simple.enums.OrderStatus;
import com.example.simple.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;


@RestController
@RequestMapping("/webhook")
public class StripeWebHookController {
//this is the route that stripe sends webhook 

	private final Logger logger = LoggerFactory.getLogger(StripeWebHookController.class);
	private final OrderService orderService;
	
	public StripeWebHookController(OrderService orderService) {
		super();
		this.orderService = orderService;
	}


	@Value("${stripe.webhook.secret}")
	private String webhookSecret;
	

	//we get this response from stripe everytime there is transaction or event being triggered
	//we get payload Stripe-Signature 
	//then create  event  using payload stripeSignature and StripSecretKey(from stripe dashboard)
	//get eventDataObject by  desirialize ing the event
	// then get StripeObject (paymentIntent) from dataObject
	//then check event type with paymentIntent -> Id  status
	//then we can update our orderStatus based on this  
	
	@PostMapping
	public ResponseEntity<?> handleStripeWebHook(@RequestBody String payload,
			@RequestHeader("Strie-Signature") String sigHead){
		
		try {
			Event event = Webhook.constructEvent(payload, sigHead, webhookSecret);
			EventDataObjectDeserializer eventDataObjectDeserializer = event.getDataObjectDeserializer();
			StripeObject stripeObject =eventDataObjectDeserializer.getObject().get();
			
			PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
			
			handlePaymentIntent(paymentIntent, event.getType());
			return ResponseEntity.status(HttpStatus.OK).body("Successfull stripe transaction");
		} catch (SignatureVerificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook failed");
		}
	}


	private void handlePaymentIntent(PaymentIntent paymentIntent, String eventType) {
		
		 OrderStatus newStatus;
	        switch (eventType) {//depending on stripe event type we update our orderStatus
	        
	        case "payment_intent.succeeded":
	        		newStatus = OrderStatus.PAID;
	        		break;
	        		
            case "payment_intent.payment_failed":
            		newStatus = OrderStatus.FAILED;
                	break;	  
                	
	            case "payment_intent.created":
	                newStatus = OrderStatus.PENDING;
	                break;
	                
	            case "payment_intent.processing":
	                newStatus = OrderStatus.PROCESSING;
	                break;
	                
	            case "payment_intent.canceled":
	                newStatus = OrderStatus.CANCELED;
	                break;
	                
	            default:
	                logger.warn("Unhandled payment intent status change event: {}", eventType);
	                return;
	        }
	        orderService.updateOrderStatus(paymentIntent.getId(), newStatus);
	        logger.info("Order linked to PaymentIntent {} updated to {}.", paymentIntent.getId(), newStatus);
		
	}

}
