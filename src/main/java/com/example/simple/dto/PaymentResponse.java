package com.example.simple.dto;

public class PaymentResponse {
    private String clientSecret;
    private String paymentId;
    private String status;

    public PaymentResponse(String clientSecret, String paymentId, String status) {
        this.clientSecret = clientSecret;
        this.paymentId = paymentId;
        this.status = status;
    }

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
 
    
}