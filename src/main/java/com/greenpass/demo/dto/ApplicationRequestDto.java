package com.greenpass.demo.dto;

import com.greenpass.demo.entity.Application;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ApplicationRequestDto {
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate birthDate;
    private String birthCity;
    private Application.EducationLevel educationLevel;

    private Application.MaritalStatus maritalStatus;
    private int childrenCount;

    private boolean photoNotReady;

    private String whatsappNumber;
    private String email;
    private Application.PackageType packageType;

    // Əgər ailəlidirsə, həyat yoldaşı və ya uşaqların məlumatları bura gələcək
    private List<FamilyMemberDto> familyMembers;

    @Data
    public static class FamilyMemberDto {
        private String firstName;
        private String lastName;
        private String gender;
        private LocalDate birthDate;
        private String birthCity;
        private String relation; // SPOUSE / CHILD
    }
}