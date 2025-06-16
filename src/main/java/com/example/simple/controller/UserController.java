package com.example.simple.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.simple.dto.UserDto;
import com.example.simple.model.User;
import com.example.simple.service.UserService;

@RestController
@RequestMapping("api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}

	@GetMapping("/me")
	public ResponseEntity<?> getUserProfile(){
		
		User user = userService.getMyProfile();
		
		return ResponseEntity.status(HttpStatus.OK).body(new UserDto(user) );
	}
}
