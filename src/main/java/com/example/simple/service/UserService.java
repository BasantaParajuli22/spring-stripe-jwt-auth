package com.example.simple.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.simple.dto.UserDto;
import com.example.simple.model.User;
import com.example.simple.repository.UserRepository;
import com.example.simple.security.CustomUserDetails;
import com.example.simple.security.CustomUserDetailsService;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			CustomUserDetailsService customUserDetailsService) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.customUserDetailsService = customUserDetailsService;
	}

    @Transactional
   	public User registerUser(UserDto userDto) {
   	    Optional<User> existingEmail = userRepository.findByEmail(userDto.getEmail());
   	    if (existingEmail.isPresent()) {
   	        throw new RuntimeException("Email already exists");
   	    }
   	    
   	    try {
   	        User user = new User();
   	        user.setEmail(userDto.getEmail());
   	        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
   	        user.setUserRole(userDto.getUserRole());
   	        return userRepository.save(user);
   	        
   	    } catch (Exception e) {
   	        throw new RuntimeException("User creation failed: " + e.getMessage());
   	    }
   	}
    
	private UserDto convertToUserDto(User user) {
		UserDto userDto = new UserDto();
    	userDto.setEmail(user.getEmail());
    	userDto.setEmail(user.getEmail());
    	userDto.setUserRole(user.getUserRole());
    	userDto.setUserId(user.getUserId());
    	return userDto;
    }
    

	//get authenticated CustomUserDetails from SecurityContextHolder
    public CustomUserDetails getCustomUserDetails() {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	if(authentication != null && authentication.isAuthenticated()) {
    		CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(authentication.getName());
    		return customUserDetails;    		
    	}
    	return null;
    }
    
    //get Authenticated UserId
    public Long getCustomUserDetailsUserId() {
    	return getCustomUserDetails().getUserId();
    }
    
    //get Authenticated email
    public String getCustomUserDetailsEmail() {
    	return getCustomUserDetails().getUsername();
    }

	public User getMyProfile() {
		Long userId = getCustomUserDetailsUserId();
		User user = getUserById(userId);
		return user;
	}

	public User getUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow( () -> new RuntimeException("User not found using UserId "+ userId)); 
	}
    
}
