package com.travelonna.demo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private UserDto user;
    
    @Getter
    @Builder
    public static class UserDto {
        private String user_id;
        private String name;
        private String email;
    }
}