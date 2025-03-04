package com.travelonna.demo.service;

import com.travelonna.demo.entity.Profile;
import com.travelonna.demo.entity.User;
import com.travelonna.demo.repository.ProfileRepository;
import com.travelonna.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    // 기존의 saveProfile 메서드 등 다른 메서드들이 존재한다고 가정

    // 이메일로 사용자를 조회하고, 해당 사용자의 프로필을 반환하는 메서드 추가
    public Profile getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + email));
    }

    // 예시로 saveProfile 메서드도 존재해야 합니다.
    public void saveProfile(String email, String nickname, org.springframework.web.multipart.MultipartFile profileImage, String introduction) {
        // 사용자를 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        // 프로필을 조회 또는 새로 생성
        Profile profile = profileRepository.findByUser(user).orElse(new Profile());
        profile.setUser(user);
        profile.setNickname(nickname);
        // 프로필 이미지는 별도의 파일 업로드 처리가 필요할 수 있습니다.
        // 여기서는 profileImage.getOriginalFilename()으로 저장할 파일명을 예시로 사용합니다.
        profile.setProfileImage(profileImage.getOriginalFilename());
        profile.setIntroduction(introduction);
        profileRepository.save(profile);
    }
}
