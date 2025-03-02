package com.travelonna.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @PostMapping("/save")
    public String saveProfile(@RequestParam("nickname") String nickname,
                            @RequestParam("profileImage") MultipartFile profileImage,
                            @RequestParam("introduction") String introduction) {
        
        profileService.saveProfile(nickname, profileImage, introduction);
        return "redirect:/main"; // 저장 후 메인 페이지로 리다이렉트
    }
}
