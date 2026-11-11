package com.greenpass.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenpass.demo.dto.ApplicationRequestDto;
import com.greenpass.demo.entity.Application;
import com.greenpass.demo.entity.FamilyMember;
import com.greenpass.demo.repository.ApplicationRepository;
import com.greenpass.demo.repository.FamilyMemberRepository;
import com.greenpass.demo.service.CloudinaryService;
import com.greenpass.demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final FamilyMemberRepository familyMemberRepository; // Ailə üzvlərini saxlamaq üçün
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/with-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createApplicationWithPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("data") String dataJson
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Şəkil faylı boşdur!");
            }

            // 1. JSON sətrini ApplicationRequestDto obyektinə çeviririk
            ApplicationRequestDto requestDto = objectMapper.readValue(dataJson, ApplicationRequestDto.class);

            // 2. Paketə uyğun qiyməti avtomatik təyin edirik
            // Fərdi: 19 AZN, Ailəvi: 35 AZN, VIP: 59 AZN
            double packagePrice = switch (requestDto.getPackageType()) {
                case INDIVIDUAL -> 19.0;
                case FAMILY -> 35.0;
                case VIP -> 59.0;
            };

            // 3. Əsas şəkli Cloudinary-ə yükləyirik
            String photoUrl = cloudinaryService.uploadAndCompressFile(file);

            // 4. Müraciəti yaradıb bazaya yazırıq
            Application application = Application.builder()
                    .firstName(requestDto.getFirstName())
                    .lastName(requestDto.getLastName())
                    .gender(requestDto.getGender())
                    .birthDate(requestDto.getBirthDate())
                    .birthCity(requestDto.getBirthCity())
                    .educationLevel(requestDto.getEducationLevel())
                    .maritalStatus(requestDto.getMaritalStatus())
                    .childrenCount(requestDto.getChildrenCount())
                    .photoPath(photoUrl)
                    .photoNotReady(requestDto.isPhotoNotReady())
                    .whatsappNumber(requestDto.getWhatsappNumber())
                    .email(requestDto.getEmail())
                    .packageType(requestDto.getPackageType())
                    .amount(packagePrice) // Qiymət təyin olundu
                    .paymentStatus("UNPAID") // İlkin ödəniş statusu
                    .status("PENDING")
                    .build();

            Application savedApplication = applicationRepository.save(application);

            // 5. Əgər ailə üzvləri də göndərilibsə, onları ayrıca cədvələ yazırıq
            if (requestDto.getFamilyMembers() != null && !requestDto.getFamilyMembers().isEmpty()) {
                for (ApplicationRequestDto.FamilyMemberDto memberDto : requestDto.getFamilyMembers()) {
                    FamilyMember familyMember = FamilyMember.builder()
                            .firstName(memberDto.getFirstName())
                            .lastName(memberDto.getLastName())
                            .gender(memberDto.getGender())
                            .birthDate(memberDto.getBirthDate())
                            .birthCity(memberDto.getBirthCity())
                            .relation(memberDto.getRelation())
                            .application(savedApplication) // Əsas müraciətə bağlayırıq
                            .build();

                    familyMemberRepository.save(familyMember);
                }
            }

            // 6. İstifadəçiyə səliqəli HTML dizaynlı təsdiq emaili göndəririk
            try {
                String subject = "Green Card Müraciətiniz Qəbul Olundu! - Green Pass";
                String logoUrl = "https://res.cloudinary.com/dmtaqfxap/image/upload/v1793159127/logo-header_awngrs.png";

                String statusColor = "#f57c00"; // Narıncı (PENDING)
                String statusBgColor = "#fff3e0"; 

                String htmlBody = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #f9f9f9;">
                        <div style="text-align: center; padding-bottom: 20px; border-bottom: 2px solid #0f2a4a;">
                            <img src="%s" alt="Green Pass Logo" style="max-width: 160px; height: auto; margin-bottom: 10px;"/>
                            <p style="color: #666; font-size: 14px; margin: 5px 0 0 0;">Amerika Birləşmiş Ştatları Green Card Xidmətləri</p>
                        </div>
                        
                        <div style="padding: 20px 0; color: #333;">
                            <h3 style="color: #0f2a4a;">Hörmətli %s %s,</h3>
                            <p>Green Card müraciətiniz uğurla sistemimizə daxil edildi. Seçdiyiniz paket üzrə ödəniş gözlənilir.</p>
                            
                            <div style="background-color: #ffffff; padding: 15px; border-left: 4px solid %s; margin: 20px 0; border-radius: 4px; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                                <p style="margin: 0 0 10px 0; font-size: 14px; color: #555;"><strong>Seçdiyiniz Paket:</strong> %s (%.2f AZN)</p>
                                <p style="margin: 0 0 10px 0; font-size: 14px; color: #555;"><strong>Ödəniş Statusu:</strong> Ödənilməyib (UNPAID)</p>
                                <p style="margin: 0; font-size: 14px; color: #555;">
                                    <strong>Müraciət Statusu:</strong> 
                                    <span style="background-color: %s; color: %s; padding: 4px 10px; border-radius: 4px; font-weight: bold; font-size: 13px;">
                                        PENDING (Gözləmədədir)
                                    </span>
                                </p>
                            </div>
                            
                            <p>Ödənişi tamamladıqdan sonra müraciətiniz rəsmi olaraq icraya alınacaq.</p>
                        </div>
                        
                        <div style="text-align: center; padding-top: 20px; border-top: 1px solid #e0e0e0; color: #888; font-size: 12px;">
                            <p style="margin: 0;">&copy; 2026 Green Pass. Bütün hüquqlar qorunur.</p>
                            <p style="margin: 5px 0 0 0;">Bakı şəhəri, Azərbaycan | info@greencard.az</p>
                        </div>
                    </div>
                    """.formatted(
                        logoUrl, 
                        requestDto.getFirstName(), 
                        requestDto.getLastName(), 
                        statusColor, 
                        requestDto.getPackageType(), 
                        packagePrice,
                        statusBgColor, 
                        statusColor
                    );

                emailService.sendHtmlEmail(requestDto.getEmail(), subject, htmlBody);
            } catch (Exception e) {
                System.out.println("Email göndərilə bilmədi: " + e.getMessage());
            }

            return ResponseEntity.ok("Müraciət yaradıldı! Paket: " + requestDto.getPackageType() + ", Məbləğ: " + packagePrice + " AZN, Ödəniş gözlənilir (UNPAID).");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Xəta baş verdi: " + e.getMessage());
        }
    }
}