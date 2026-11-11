package com.greenpass.demo.controller;

import com.greenpass.demo.entity.Admin;
import com.greenpass.demo.repository.AdminRepository;
import com.greenpass.demo.security.JwtService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Admin tapılmadı!"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return ResponseEntity.status(401).body("Şifrə səhvdir!");
        }

        // Şifrə düzgündürsө JWT Token yaradıb qaytarırıq
        String token = jwtService.generateToken(admin.getEmail());
        return ResponseEntity.ok(token);
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }
}