package com.example.simple.dto;

import com.example.simple.enums.UserRole;
import com.example.simple.model.User;

public class UserDto {

	private Long userId;
	private String email;
	private String password;
	private UserRole userRole;
	
	public UserDto() {
		
	}
	
	public UserDto(User user) {
		this.userId = user.getUserId();
		this.email = user.getEmail();
		this.userRole = user.getUserRole();
		
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public UserRole getUserRole() {
		return userRole;
	}
	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}
	
	
}
