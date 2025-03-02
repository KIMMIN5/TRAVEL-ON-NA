package com.travelonna.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.travelonna.demo.entity.Profile;
import com.travelonna.demo.repository.ProfileRepository;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Value("${spring.file.upload-dir}")  // application.properties에서 설정후 여기 작성해줘야함!
    private String uploadDir;

    public void saveProfile(String nickname, MultipartFile profileImage, String introduction) {
        Profile profile = new Profile();
        profile.setNickname(nickname);
        profile.setIntroduction(introduction);

        // 이미지 파일 처리
        if (profileImage != null && !profileImage.isEmpty()) {
            String fileName = saveProfileImage(profileImage);
            profile.setProfileImage(fileName);
        }

        profileRepository.save(profile);
    }

    private String saveProfileImage(MultipartFile file) {
        try {
            // 원본 파일명
            String originalFileName = file.getOriginalFilename();
            // 파일 확장자 추출
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            // UUID를 이용해 고유한 파일명 생성
            String fileName = UUID.randomUUID().toString() + extension;

            // 업로드 디렉토리가 없으면 생성
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            // 파일 저장
            File destFile = new File(uploadPath, fileName);
            file.transferTo(destFile);

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage());
        }
    }
}
