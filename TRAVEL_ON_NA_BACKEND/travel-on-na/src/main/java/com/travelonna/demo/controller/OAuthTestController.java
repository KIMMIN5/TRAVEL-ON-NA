package com.travelonna.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuthTestController {

    @GetMapping("/")
    public String home() {
        return "home";  // home.html을 렌더링
    }

    @GetMapping("/oauth/callback")
    public String oauthCallback() {
        return "callback";  // callback.html을 렌더링
    }
} 