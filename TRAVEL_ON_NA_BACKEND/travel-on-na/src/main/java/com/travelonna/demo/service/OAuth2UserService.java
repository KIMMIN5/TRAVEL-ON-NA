package com.travelonna.demo.service;

import java.time.LocalDateTime;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.travelonna.demo.entity.User;
import com.travelonna.demo.entity.UserToken;
import com.travelonna.demo.repository.UserRepository;
import com.travelonna.demo.repository.UserTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        // 사용자 정보 저장 또는 업데이트
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new User(name, email)));

        // 토큰 정보 저장 또는 업데이트
        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        UserToken userToken = userTokenRepository.findByUserId(user.getUserId())
                .orElse(new UserToken());
        
        // 토큰 정보 업데이트
        userToken.setUserId(user.getUserId());
        userToken.setRefreshToken(accessToken.getTokenValue());
        userToken.setIssuedAt(LocalDateTime.now());
        userToken.setExpiresIn(3600); // 1시간
        userToken.setScope(String.join(",", accessToken.getScopes()));
        userToken.setRevoked(false);
        
        userTokenRepository.save(userToken);

        return oauth2User;
    }
} 