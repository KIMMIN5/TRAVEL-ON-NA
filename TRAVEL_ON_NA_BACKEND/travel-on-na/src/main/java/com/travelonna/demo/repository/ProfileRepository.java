package com.travelonna.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelonna.demo.entity.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    // 프로필 저장 메서드 추가
    Profile save(Profile profile);
}
