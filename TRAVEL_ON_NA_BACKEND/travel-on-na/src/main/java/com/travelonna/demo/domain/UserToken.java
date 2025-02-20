package com.travelonna.demo.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "USER_TOKEN")
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
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
}