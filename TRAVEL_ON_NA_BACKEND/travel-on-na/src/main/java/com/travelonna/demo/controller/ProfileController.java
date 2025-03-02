package com.travelonna.demo.controller;

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

    @GetMapping
    public String showProfilePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
            String email = oauth2User.getAttribute("email");
            model.addAttribute("userEmail", email);
        }
        return "profile";
    }

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
        
        // email 매개변수를 추가하여 ProfileService의 saveProfile 메서드 호출
        profileService.saveProfile(email, nickname, profileImage, introduction);
        return "redirect:/main";
    }
}
