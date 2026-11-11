package com.greenpass.demo.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Şəxsi Məlumatlar
    private String firstName; 
    private String lastName;  
    private String gender;    
    private LocalDate birthDate; 
    private String birthCity;   
    
    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    // Ailə Vəziyyəti
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus; 
    private int childrenCount; 

    // Fotoşəkil
    private String photoPath; 
    private boolean photoNotReady; 

    // Əlaqə və Paket
    private String whatsappNumber;
    private String email;
    
    @Enumerated(EnumType.STRING)
    private PackageType packageType;

    // --- YENİ ƏLAVƏ OLUNANLAR (Ödəniş üçün) ---
    private double amount; // Paketin qiyməti (19, 35 və ya 59 AZN)
    
    @Builder.Default
    private String paymentStatus = "UNPAID"; // UNPAID (Ödənilməyib), PAID (Ödənildi)

    // Sistem məlumatları
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private String status = "PENDING"; 

    public enum EducationLevel {
        PRIMARY, HIGH_SCHOOL, VOCATIONAL, BACHELOR, MASTER, DOCTORATE
    }

    public enum MaritalStatus {
        SINGLE, MARRIED, DIVORCED, WIDOWED
    }

    public enum PackageType {
        INDIVIDUAL, FAMILY, VIP
    }
    
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FamilyMember> familyMembers;
}