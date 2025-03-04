package com.travelonna.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.travelonna.demo.entity.Profile;
import com.travelonna.demo.entity.User;
import com.travelonna.demo.repository.ProfileRepository;
import com.travelonna.demo.repository.UserRepository;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveProfile(String email, String nickname, MultipartFile profileImage, String introduction) {
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // 기존 프로필이 있는지 확인
        Profile profile = profileRepository.findByUser(user)
                .orElse(new Profile());

        // 프로필 정보 설정
        profile.setUser(user);
        profile.setNickname(nickname);
        profile.setIntroduction(introduction);

        // 프로필 이미지 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImagePath = handleProfileImageUpload(profileImage);
            profile.setProfileImage(profileImagePath);
        }

        // 프로필 저장
        profileRepository.save(profile);
    }

    private String handleProfileImageUpload(MultipartFile file) {
        // 파일 업로드 로직 구현
        // 실제 파일 저장 후 경로 반환
        return "저장된_이미지_경로";
    }
}
