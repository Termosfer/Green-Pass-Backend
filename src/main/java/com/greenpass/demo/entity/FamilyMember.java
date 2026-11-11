package com.greenpass.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "family_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate birthDate;
    private String birthCity;
    private String photoPath; // Ailə üzvünün Cloudinary şəkli
    
    private String relation; // "SPOUSE" (Həyat yoldaşı) və ya "CHILD" (Övlad)

    // Hansı əsas müraciətə aid olduğunu göstərir
    @ManyToOne
    @JoinColumn(name = "application_id")
    @JsonIgnore // JSON dövrə düşməsinin (infinite recursion) qarşısını alır
    private Application application;
}