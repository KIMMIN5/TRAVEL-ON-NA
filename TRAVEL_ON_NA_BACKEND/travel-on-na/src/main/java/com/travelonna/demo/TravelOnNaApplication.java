package com.travelonna.demo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.travelonna.demo.config.FileStorageConfig;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageConfig.class})
public class TravelOnNaApplication {
    public static void main(String[] args) {
        SpringApplication.run(TravelOnNaApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
