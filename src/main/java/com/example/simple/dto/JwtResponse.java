package com.example.simple.dto;

public class JwtResponse {

    private String accessToken;

	public JwtResponse(String accessToken) {
		super();
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
    
}
