package com.travelonna.demo.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.travelonna.demo.entity.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.refresh-token.expiration:604800000}")
    private Long refreshTokenExpiration;
    
    public String generateToken(String email) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
    
    public String generateRefreshToken(User user) {
        return generateRefreshToken();
    }
    
    public Integer getRefreshTokenValidityInSeconds() {
        return (int)(refreshTokenExpiration / 1000);
    }

    public String generateAccessToken(User user) {
        return generateToken(user.getEmail());
    }
}