package com.greenpass.demo.controller;

import com.greenpass.demo.entity.Application;
import com.greenpass.demo.repository.ApplicationRepository;
import com.greenpass.demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminController {

    private final ApplicationRepository applicationRepository;
    private final EmailService emailService;

    // 1. Bütün müraciətləri siyahı kimi gətirir
    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getAllApplications() {
        List<Application> applications = applicationRepository.findAll();
        return ResponseEntity.ok(applications);
    }

    // 2. Müraciətin statusunu yeniləyir və rəngli HTML email göndərir
    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<String> updateApplicationStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Müraciət tapılmadı!"));
        
        String oldStatus = application.getStatus();
        application.setStatus(status.toUpperCase());
        applicationRepository.save(application);
        
        try {
            String subject = "Green Card Müraciət Statusunuz Yeniləndi - Green Pass";
            
            // Statusa uyğun olaraq açıqlama və rənglər (Badge dizaynı üçün)
            String statusColor;
            String statusBgColor;
            String statusText;

            switch (status.toUpperCase()) {
                case "APPROVED" -> {
                    statusColor = "#2e7d32"; // Tünd yaşıl
                    statusBgColor = "#e8f5e9"; // Açıq yaşıl fon
                    statusText = "Təsdiqləndi.Məlumatlarınız uğurla yoxlanıldı.";
                }
                case "REJECTED" -> {
                    statusColor = "#c62828"; // Tünd qırmızı
                    statusBgColor = "#ffebee"; // Açıq qırmızı fon
                    statusText = "Rədd edildi.Lütfən şərtləri nəzərdən keçirin.";
                }
                default -> {
                    statusColor = "#f57c00"; // Narıncı / Sarı
                    statusBgColor = "#fff3e0"; // Açıq narıncı fon
                    statusText = "Gözləmədədir.Mütəxəssis baxışındadır.";
                }
            }

            String logoUrl = "https://res.cloudinary.com/dmtaqfxap/image/upload/v1793159127/logo-header_awngrs.png";

            String htmlBody = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #f9f9f9;">
                    <div style="text-align: center; padding-bottom: 20px; border-bottom: 2px solid #0f2a4a;">
                        <img src="%s" alt="Green Pass Logo" style="max-width: 160px; height: auto; margin-bottom: 10px;"/>
                        <p style="color: #666; font-size: 14px; margin: 5px 0 0 0;">Status Bildirişi</p>
                    </div>
                    
                    <div style="padding: 20px 0; color: #333;">
                        <h3 style="color: #0f2a4a;">Hörmətli %s %s,</h3>
                        <p>Green Card müraciətinizin statusunda yenilənmə baş verdi.</p>
                        
                        <div style="background-color: #ffffff; padding: 15px; border-left: 4px solid %s; margin: 20px 0; border-radius: 4px; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                            <p style="margin: 0 0 10px 0; font-size: 14px; color: #555;"><strong>Köhnə Status:</strong> %s</p>
                            <p style="margin: 0 0 10px 0; font-size: 14px; color: #555;">
                                <strong>Yeni Status:</strong> 
                                <span style="background-color: %s; color: %s; padding: 4px 10px; border-radius: 4px; font-weight: bold; font-size: 13px;">
                                    %s
                                </span>
                            </p>
                            <p style="margin: 0; font-size: 14px; color: #555;"><strong>Açıqlama:</strong> %s</p>
                        </div>
                        
                        <p>Hər hansı sualınız olarsa, bizə 7/24 yaza bilərsiniz.</p>
                    </div>
                    
                    <div style="text-align: center; padding-top: 20px; border-top: 1px solid #e0e0e0; color: #888; font-size: 12px;">
                        <p style="margin: 0;">&copy; 2026 Green Pass. Bütün hüquqlar qorunur.</p>
                        <p style="margin: 5px 0 0 0;">Bakı şəhəri, Azərbaycan | info@greenpass.az</p>
                    </div>
                </div>
                """.formatted(
                    logoUrl,
                    application.getFirstName(), 
                    application.getLastName(), 
                    statusColor,
                    oldStatus, 
                    statusBgColor,
                    statusColor,
                    status.toUpperCase(), 
                    statusText
                );

            emailService.sendHtmlEmail(application.getEmail(), subject, htmlBody);
        } catch (Exception e) {
            System.out.println("Status dəyişmə emaili göndərilə bilmədi: " + e.getMessage());
        }
        
        return ResponseEntity.ok("Müraciət statusu uğurla yeniləndi və istifadəçiyə rəngli email göndərildi: " + status);
    }
}