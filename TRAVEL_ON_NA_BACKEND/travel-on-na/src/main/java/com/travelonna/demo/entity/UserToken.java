package com.travelonna.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_token")
@Getter
@Setter
@NoArgsConstructor
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Integer tokenId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "expires_in")
    private Integer expiresIn;

    @Column(name = "scope")
    private String scope;

    @Column(name = "revoked")
    private Boolean revoked;

    public UserToken(Integer userId, String refreshToken, Integer expiresIn, String scope) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.issuedAt = LocalDateTime.now();
        this.expiresIn = expiresIn;
        this.scope = scope;
        this.revoked = false;
    }
} 