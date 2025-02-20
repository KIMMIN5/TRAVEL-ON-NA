package com.travelonna.demo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api")
public class TestController {
    
    @Operation(summary = "테스트 API", description = "Hello World를 반환하는 테스트 API입니다.")
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}