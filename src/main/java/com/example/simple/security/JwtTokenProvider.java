package com.example.simple.security;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.simple.enums.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenProvider {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(JwtTokenProvider.class);
	
	@Value("${app.jwt.secret}")
	private String jwtSecretKey;
	
	@Value("${app.jwt.expiration-milliseconds}")
	private Long jwtExpirationTime;
	
	private Key key() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	//here we get Authorization Header -> Bearer randomJwtToken
	public String getJwtFromRequest(HttpServletRequest request) {
	
		String authHeader = request.getHeader("Authorization");
		if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
			String jwtToken = authHeader.substring(7);
			return jwtToken;
		}
		return null;
	}

	//to generate token we need 
	// signging key  
	//username or email
	//issuedAt
	//expiredAt
	//userId and userRole are metadata
	public String generateToken(String email, Long userId, UserRole userRole) {
		
		Date currentDate = new Date();
		//currentDate.getTime() Returns the number of milliseconds since January 1, 1970
		Date expriredTime = new Date(currentDate.getTime() + jwtExpirationTime);
		
		return Jwts.builder()
			.setSubject(email)
			.claim("id", userId)
			.claim("role", userRole)
			.setIssuedAt(currentDate)
			.setExpiration(expriredTime)
			.signWith(key())
			.compact();
	}
	
	public boolean validateJwtToken(String jwt) {
		try {
			Jwts.parserBuilder()
			.setSigningKey(key())
			.build()
			.parseClaimsJws(jwt);//If parsing succeeds, the token is considered valid
			return true;		//then return true
		} catch (Exception e) {
			logger.error("error while validating jwt token");
		}
		return false;
	}

	public String getUsernameFromJwt(String jwt) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(key())
				.build()
				.parseClaimsJws(jwt)
				.getBody();
		return claims.getSubject();
	}

	
	/**
	 *	
	 * What parseClaimsJws(token) does:
	 * 
		Validates the signature using the provided key.	
		It ensures that the JWT hasn’t been tampered with.		
		If the signature is invalid (e.g., key mismatch), it throws an exception
				
		Checks the token structure.		
		Verifies the JWT has 3 parts (header.payload.signature) and is correctly formatted.	
			
		Parses the claims (payload).		
		You can extract user info, expiration date, roles, etc. from the claims if needed.
	 */
}
