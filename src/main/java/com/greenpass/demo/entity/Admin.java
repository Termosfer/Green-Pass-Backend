package com.greenpass.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Şifrə şifrələnmiş (encoded) şəkildə saxlanılacaq
    
    private String role; // Məsələn: "ROLE_ADMIN"
}