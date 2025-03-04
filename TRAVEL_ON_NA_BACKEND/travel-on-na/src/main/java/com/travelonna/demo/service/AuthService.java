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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse processOAuth2Login(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        log.info("Processing OAuth2 login for email: {}", email);

        // 사용자 조회 또는 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, name));
        
        log.info("User found/created with ID: {}", user.getUserId());

        // 기존 토큰 조회
        UserToken userToken = userTokenRepository.findByUserId(user.getUserId())
                .orElse(null);
        
        String accessToken;
        String refreshToken;
        
        if (userToken != null && !userToken.getRevoked() && isTokenValid(userToken)) {
            // 기존 토큰이 유효하면 재사용
            log.info("Reusing existing token for user ID: {}", user.getUserId());
            refreshToken = userToken.getRefreshToken();
            accessToken = jwtUtil.generateToken(email); // Access 토큰은 항상 새로 발급
        } else {
            // 토큰이 없거나 유효하지 않으면 새로 발급
            log.info("Generating new token for user ID: {}", user.getUserId());
            accessToken = jwtUtil.generateToken(email);
            refreshToken = jwtUtil.generateRefreshToken();
            
            if (userToken == null) {
                userToken = new UserToken();
            }
            
            userToken.setUserId(user.getUserId());
            userToken.setRefreshToken(refreshToken);
            userToken.setIssuedAt(LocalDateTime.now());
            userToken.setExpiresIn(jwtUtil.getRefreshTokenValidityInSeconds());
            userToken.setScope("refresh_token");
            userToken.setRevoked(false);
            
            userTokenRepository.save(userToken);
            log.info("UserToken saved with ID: {}", userToken.getTokenId());
        }

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

    // 토큰 유효성 검사 메서드 추가
    private boolean isTokenValid(UserToken userToken) {
        if (userToken.getIssuedAt() == null || userToken.getExpiresIn() == null) {
            return false;
        }
        
        LocalDateTime expiryTime = userToken.getIssuedAt().plusSeconds(userToken.getExpiresIn());
        return LocalDateTime.now().isBefore(expiryTime);
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
        User user = userRepository.findById(Integer.valueOf(userToken.getUserId()))
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