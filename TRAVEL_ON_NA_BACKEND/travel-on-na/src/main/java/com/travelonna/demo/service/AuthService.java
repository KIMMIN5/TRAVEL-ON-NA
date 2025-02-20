package com.travelonna.demo.service;

import java.time.LocalDateTime;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelonna.demo.domain.User;
import com.travelonna.demo.domain.UserToken;
import com.travelonna.demo.dto.LoginResponse;
import com.travelonna.demo.repository.UserRepository;
import com.travelonna.demo.repository.UserTokenRepository;
import com.travelonna.demo.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse processOAuth2Login(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 사용자 조회 또는 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, name));

        // Access Token 생성
        String accessToken = jwtUtil.generateToken(email);
        
        // Refresh Token 생성 및 저장
        String refreshToken = jwtUtil.generateRefreshToken();
        
        UserToken userToken = userTokenRepository.findByUserId(user.getId())
                .orElse(new UserToken());
        
        userToken.setUser(user);
        userToken.setRefreshToken(refreshToken);
        userToken.setIssuedAt(LocalDateTime.now());
        userToken.setExpiresIn(jwtUtil.getRefreshTokenValidityInSeconds());
        userToken.setScope("refresh_token");
        userToken.setRevoked(false);
        
        userTokenRepository.save(userToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(LoginResponse.UserDto.builder()
                        .id(user.getId().toString())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .build();
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }
}