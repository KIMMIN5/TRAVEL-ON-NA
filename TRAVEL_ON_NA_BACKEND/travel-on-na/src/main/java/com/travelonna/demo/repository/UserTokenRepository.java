package com.travelonna.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelonna.demo.entity.UserToken;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Integer> {
    Optional<UserToken> findByUserId(Integer userId);
    Optional<UserToken> findByRefreshToken(String refreshToken);
}