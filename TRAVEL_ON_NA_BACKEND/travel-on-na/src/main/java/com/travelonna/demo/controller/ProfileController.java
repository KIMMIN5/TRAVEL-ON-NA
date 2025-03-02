package com.travelonna.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.travelonna.demo.service.ProfileService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    // 프로필 페이지를 보여주는 GET 매핑 추가
    @GetMapping
    public String showProfilePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        return "profile"; // profile.html을 렌더링
    }

    @PostMapping("/save")
    public String saveProfile(@RequestParam("nickname") String nickname,
                              @RequestParam("profileImage") MultipartFile profileImage,
                              @RequestParam("introduction") String introduction) {

        profileService.saveProfile(nickname, profileImage, introduction);
        return "redirect:/main";
    }
}
