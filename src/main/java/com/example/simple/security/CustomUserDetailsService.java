package com.example.simple.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.simple.model.User;
import com.example.simple.repository.UserRepository;

@Service
public class CustomUserDetailsService  implements UserDetailsService{

	private final UserRepository userRepository;
	
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//find User from db
		User user = userRepository.findByEmail(username)
				.orElseThrow( () -> new RuntimeException("User not found. "));
		return new CustomUserDetails(user); //create a cutomUserDetails using existing user
	}

}
