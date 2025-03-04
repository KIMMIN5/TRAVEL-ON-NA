package com.travelonna.demo.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.travelonna.demo.entity.User;
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

        // 여기서는 토큰 저장을 하지 않고, 사용자 정보만 저장
        // 토큰 발급 및 저장은 AuthService에서 일관되게 처리

        return oauth2User;
    }
} 