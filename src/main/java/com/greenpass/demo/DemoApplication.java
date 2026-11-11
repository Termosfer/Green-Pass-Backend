package com.greenpass.demo;

import com.greenpass.demo.entity.Admin;
import com.greenpass.demo.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DemoApplication {

    
    @Value("${admin.default.email}")
    private String adminEmail;

    @Value("${admin.default.password}")
    private String adminPassword;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner initAdmin(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (adminRepository.findByEmail(adminEmail).isEmpty()) {
                Admin admin = Admin.builder()
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role("ROLE_ADMIN")
                        .build();
                adminRepository.save(admin);
                System.out.println("Default admin yaradıldı: " + adminEmail);
            } else {
                System.out.println("Admin artıq bazada mövcuddur, yenidən yaradılmadı.");
            }
        };
    }
}