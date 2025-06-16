package com.example.simple.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter{

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(JwtAuthFilter.class);
	
	private final JwtTokenProvider jwtTokenProvider;
	private final CustomUserDetailsService customUserDetailsService;

	public JwtAuthFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
		super();
		this.jwtTokenProvider = jwtTokenProvider;
		this.customUserDetailsService = customUserDetailsService;
	}

	//get jwt from req
	//get usename form jwt
	//get userDetails from username
	//create authentication using userDetails
	//set authentication to SecurityContextHolder -> memory
	//give control to next filter or controller
	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
			) throws ServletException, IOException {
		
		try {
			String jwt = jwtTokenProvider.getJwtFromRequest(request);
			
			if(StringUtils.hasText(jwt) && jwtTokenProvider.validateJwtToken(jwt)) {
				
				String username =jwtTokenProvider.getUsernameFromJwt(jwt);			
				UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
				
				//create authentication
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities()
						);
				
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		} catch (Exception e) {
			logger.error("Couldnot set user Authentication in SecurityContextHolder "+e);
		}	
		filterChain.doFilter(request, response);
	}

}
