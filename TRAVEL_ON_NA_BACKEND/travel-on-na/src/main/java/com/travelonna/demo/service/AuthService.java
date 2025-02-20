package com.travelonna.demo.service;

import java.time.LocalDateTime;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelonna.demo.dto.LoginResponse;
import com.travelonna.demo.entity.User;
import com.travelonna.demo.entity.UserToken;
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
        
        UserToken userToken = userTokenRepository.findByUserId(user.getUserId())
                .orElse(new UserToken());
        
        userToken.setUserId(user.getUserId());
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
                        .user_id(String.valueOf(user.getUserId()))
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .build();
    }

    private User createUser(String email, String name) {
        User user = new User(name, email);
        return userRepository.save(user);
    }

    public LoginResponse login(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // Refresh 토큰 저장 또는 업데이트
        UserToken userToken = userTokenRepository.findByUserId(user.getUserId())
                .orElse(new UserToken());
        
        userToken.setUserId(user.getUserId());
        userToken.setRefreshToken(refreshToken);
        userToken.setIssuedAt(LocalDateTime.now());
        userToken.setExpiresIn(604800); // 7일
        userToken.setScope("read,write");
        userToken.setRevoked(false);
        
        userTokenRepository.save(userToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(LoginResponse.UserDto.builder()
                        .user_id(String.valueOf(user.getUserId()))
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .build();
    }

    public void logout(String refreshToken) {
        UserToken userToken = userTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        userToken.setRevoked(true);
        userTokenRepository.save(userToken);
    }

    public LoginResponse refreshToken(String refreshToken) {
        UserToken userToken = userTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (userToken.getRevoked()) {
            throw new RuntimeException("Token has been revoked");
        }

        // userId로 User 정보 조회
        User user = userRepository.findById(Long.valueOf(userToken.getUserId()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtUtil.generateAccessToken(user);
        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .user(LoginResponse.UserDto.builder()
                        .user_id(String.valueOf(user.getUserId()))
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .build();
    }
}