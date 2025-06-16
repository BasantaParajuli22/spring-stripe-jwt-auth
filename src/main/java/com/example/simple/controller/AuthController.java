package com.example.simple.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.simple.dto.JwtResponse;
import com.example.simple.dto.LoginRequest;
import com.example.simple.dto.UserDto;
import com.example.simple.model.User;
import com.example.simple.security.CustomUserDetails;
import com.example.simple.security.JwtTokenProvider;
import com.example.simple.service.UserService;

@RestController
@RequestMapping("api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final UserService userService;
	

	public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
			UserService userService) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.userService = userService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest request){
		
		//get authentication  by authenticating username and password 
		//get userId role from customerDetails using authentication
		//store authentication in SecurityContextHolder
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
			
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String jwt = jwtTokenProvider.generateToken(request.getEmail(), userDetails.getUserId(), userDetails.getUserRole());
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(new JwtResponse(jwt));
		
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody UserDto userDto){
		
		User newUser = userService.registerUser(userDto);	
		String jwt = jwtTokenProvider.generateToken(newUser.getEmail(), newUser.getUserId(), newUser.getUserRole());
		return ResponseEntity.status(HttpStatus.CREATED).body(new JwtResponse(jwt));
		
	}
}
