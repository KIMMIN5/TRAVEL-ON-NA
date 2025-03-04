package com.travelonna.demo.controller;

import com.travelonna.demo.entity.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.travelonna.demo.service.ProfileService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    // 프로필 페이지를 보여주는 GET 매핑
    @GetMapping
    public String showProfilePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
            String email = oauth2User.getAttribute("email");

            // 사용자 프로필 정보 가져오기
            Profile profile = profileService.getProfileByEmail(email);
            model.addAttribute("profile", profile);
        }
        return "profile";  // profile.html 렌더링
    }

    // 프로필 저장을 처리하는 POST 매핑
    @PostMapping("/save")
    public String saveProfile(@RequestParam("nickname") String nickname,
                              @RequestParam("profileImage") MultipartFile profileImage,
                              @RequestParam("introduction") String introduction) {
        // OAuth2User에서 이메일 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = null;

        if (auth.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
            email = oauth2User.getAttribute("email");
        }

        if (email == null) {
            throw new RuntimeException("User email not found");
        }

        // email 정보를 이용해 프로필 저장 (ProfileService의 saveProfile 메서드 호출)
        profileService.saveProfile(email, nickname, profileImage, introduction);

        // 저장 후 프로필 저장 완료 페이지로 리다이렉트
        return "redirect:/profile/profile-saved";
    }

    // 저장 완료 후 결과 페이지를 보여주는 GET 매핑
    @GetMapping("/profile-saved")
    public String profileSaved() {
        return "profileSaved";  // profileSaved.html 템플릿 반환
    }

    // GET 요청에 대한 임시 매핑 추가:
    // 브라우저 주소창에서 /profile/save로 접근 시 profile 페이지로 리다이렉트
    @GetMapping("/save")
    public String handleSaveProfileGet() {
        return "redirect:/profile";
    }

    @GetMapping("/view")
    public String viewProfile(Model model) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = null;

        if (auth.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
            email = oauth2User.getAttribute("email");
        }

        if (email == null) {
            throw new RuntimeException("User email not found");
        }

        // 서비스에서 프로필 정보 가져오기
        Profile profile = profileService.getProfileByEmail(email);
        model.addAttribute("profile", profile);

        return "profileView";  // profileView.html 템플릿 반환
    }
}
