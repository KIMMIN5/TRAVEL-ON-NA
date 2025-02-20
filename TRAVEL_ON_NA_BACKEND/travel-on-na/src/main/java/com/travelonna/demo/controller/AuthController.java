package com.travelonna.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travelonna.demo.dto.LoginResponse;
import com.travelonna.demo.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "OAuth2 로그인 콜백", 
              description = "구글 로그인 후 콜백을 처리합니다.")
    @GetMapping("/oauth2/callback/google")
    public ResponseEntity<LoginResponse> googleCallback(OAuth2AuthenticationToken authentication) {
        return ResponseEntity.ok(authService.processOAuth2Login(authentication.getPrincipal()));
    }
}