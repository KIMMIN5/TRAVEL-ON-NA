package com.travelonna.demo.util;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.travelonna.demo.entity.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;
    
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
    
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
    
    public String generateRefreshToken(User user) {
        return generateRefreshToken();
    }
    
    public Integer getRefreshTokenValidityInSeconds() {
        return 604800; // 7일
    }

    public String generateAccessToken(User user) {
        return generateToken(user.getEmail());
    }
}