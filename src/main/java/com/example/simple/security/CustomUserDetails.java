package com.example.simple.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.simple.enums.UserRole;
import com.example.simple.model.User;

public class CustomUserDetails implements UserDetails {

	private Long userId;
	private String email;
	private String password;
	private UserRole userRole;
	
	//takes user and setting custom userDetails fields by user
	public CustomUserDetails(User user) {
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.userId = user.getUserId();
		this.userRole = user.getUserRole();
	}
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public void setEmail(String email) {
		this.email = email;
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
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
	    return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));

	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}
}
