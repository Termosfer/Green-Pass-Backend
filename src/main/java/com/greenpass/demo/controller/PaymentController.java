package com.greenpass.demo.controller;

import com.greenpass.demo.entity.Application;
import com.greenpass.demo.repository.ApplicationRepository;
import com.greenpass.demo.service.EmailService;
import com.greenpass.demo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ApplicationRepository applicationRepository;
    private final EmailService emailService; // Email göndərmək üçün əlavə olundu

    // 1. Ödəniş linki yaradır
    @PostMapping("/create/{applicationId}")
    public ResponseEntity<String> createPaymentLink(@PathVariable Long applicationId) {
        String paymentUrl = paymentService.createPayment(applicationId);
        return ResponseEntity.ok("Ödəniş linki yaradıldı: " + paymentUrl);
    }

    // 2. Saxta ödəniş səhifəsi
    @GetMapping("/simulate-page/{applicationId}")
    public ResponseEntity<String> simulatePaymentPage(@PathVariable Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Müraciət tapılmadı!"));

        String htmlResponse = """
            <!DOCTYPE html>
            <html lang="az">
            <head>
                <meta charset="UTF-8">
                <title>Green Pass - Test Ödəniş Şlüzü</title>
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f6f9; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
                    .card { background: white; padding: 30px; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); width: 380px; text-align: center; }
                    h2 { color: #0f2a4a; margin-bottom: 5px; }
                    .amount { color: #2e7d32; font-size: 22px; font-weight: bold; margin: 15px 0; }
                    .info { font-size: 14px; color: #666; margin-bottom: 20px; }
                    .input-group { text-align: left; margin-bottom: 15px; }
                    .input-group label { font-size: 12px; color: #555; font-weight: bold; }
                    .input-group input { width: 100%%; padding: 10px; box-sizing: border-box; margin-top: 5px; border: 1px solid #ddd; border-radius: 6px; font-size: 14px; }
                    .pay-btn { background-color: #0f2a4a; color: white; border: none; padding: 12px; font-size: 16px; border-radius: 6px; cursor: pointer; width: 100%%; font-weight: bold; transition: background 0.3s; }
                    .pay-btn:hover { background-color: #1a4478; }
                </style>
            </head>
            <body>
                <div class="card">
                    <h2>Green Pass Şlüzü</h2>
                    <p class="info">Müraciət Nömrəsi: <b>#%d</b><br>Paket: <b>%s</b></p>
                    <div class="amount">Cəmi: %.2f AZN</div>
                    
                    <form action="/api/payment/success" method="GET">
                        <input type="hidden" name="applicationId" value="%d" />
                        
                        <div class="input-group">
                            <label>Kart Nömrəsi</label>
                            <input type="text" placeholder="4169 7388 8650 1026" required />
                        </div>
                        
                        <div style="display: flex; gap: 10px;">
                            <div class="input-group" style="flex: 1;">
                                <label>Son İstifadə</label>
                                <input type="text" placeholder="10/28" required />
                            </div>
                            <div class="input-group" style="flex: 1;">
                                <label>CVV / CVC</label>
                                <input type="password" placeholder="369" maxlength="3" required />
                            </div>
                        </div>

                        <button type="submit" class="pay-btn">Ödənişi Təsdiq Et</button>
                    </form>
                </div>
            </body>
            </html>
            """.formatted(app.getId(), app.getPackageType(), app.getAmount(), app.getId());

        return ResponseEntity.ok()
                .contentType(new MediaType("text", "html", java.nio.charset.StandardCharsets.UTF_8))
                .body(htmlResponse);
    }

    // 3. Ödəniş uğurlu olduqda bazanı PAID edir və istifadəçiyə yaşıl rəngli email göndərir
    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(@RequestParam Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Müraciət tapılmadı!"));
        
        app.setPaymentStatus("PAID");
        applicationRepository.save(app);

        // İstifadəçiyə uğurlu ödəniş emaili göndəririk
        try {
            String subject = "Ödənişiniz Uğurla Tamamlandı! - Green Pass";
            String logoUrl = "https://res.cloudinary.com/dmtaqfxap/image/upload/v1793159127/logo-header_awngrs.png";

            String statusColor = "#2e7d32"; // Yaşıl (PAID)
            String statusBgColor = "#e8f5e9"; 

            String htmlBody = """
                <!DOCTYPE html>
                <html lang="az">
                <head><meta charset="UTF-8"></head>
                <body style="margin: 0; padding: 0; background-color: #f4f6f9;">
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #ffffff;">
                        <div style="text-align: center; padding-bottom: 20px; border-bottom: 2px solid #0f2a4a;">
                            <img src="%s" alt="Green Pass Logo" style="max-width: 160px; height: auto; margin-bottom: 10px;"/>
                            <p style="color: #666; font-size: 14px; margin: 5px 0 0 0;">Ödəniş Təsdiqi</p>
                        </div>
                        
                        <div style="padding: 20px 0; color: #333;">
                            <h3 style="color: #0f2a4a;">Hörmətli %s %s,</h3>
                            <p>Müraciətiniz üzrə etdiyiniz ödəniş uğurla qeydə alındı. Artıq müraciətiniz rəsmi olaraq baxılmaq üzrə növbəyə alındı.</p>
                            
                            <div style="background-color: %s; padding: 15px; border-left: 4px solid %s; margin: 20px 0; border-radius: 4px;">
                                <p style="margin: 0 0 10px 0; font-size: 14px; color: #333;"><strong>Müraciət ID:</strong> #%d</p>
                                <p style="margin: 0 0 10px 0; font-size: 14px; color: #333;"><strong>Ödənilən Məbləş:</strong> %.2f AZN</p>
                                <p style="margin: 0; font-size: 14px; color: #333;">
                                    <strong>Ödəniş Statusu:</strong> 
                                    <span style="color: %s; font-weight: bold; font-size: 14px;">
                                        PAID (Ödənildi)
                                    </span>
                                </p>
                            </div>
                            
                            <p>Tezliklə nəticələr barədə yenidən məlumatlandırılacaqsınız.</p>
                        </div>
                        
                        <div style="text-align: center; padding-top: 20px; border-top: 1px solid #e0e0e0; color: #888; font-size: 12px;">
                            <p style="margin: 0;">&copy; 2026 Green Pass. Bütün hüquqlar qorunur.</p>
                            <p style="margin: 5px 0 0 0;">Bakı şəhəri, Azərbaycan | info@greenpass.az</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                    logoUrl, 
                    app.getFirstName(), 
                    app.getLastName(), 
                    statusBgColor,
                    statusColor,
                    app.getId(),
                    app.getAmount(),
                    statusColor
                );

            emailService.sendHtmlEmail(app.getEmail(), subject, htmlBody);
        } catch (Exception e) {
            System.out.println("Ödəniş emaili göndərilə bilmədi: " + e.getMessage());
        }

        String successHtml = """
            <!DOCTYPE html>
            <html lang="az">
            <head><meta charset="UTF-8"><title>Ödəniş Uğurludur</title></head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f6f9; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0;">
                <div style="max-width: 400px; padding: 40px; border-radius: 12px; background: white; box-shadow: 0 4px 15px rgba(0,0,0,0.1); text-align: center;">
                    <h1 style="color: #2e7d32; margin-top: 0;">Ödəniş Uğurludur!</h1>
                    <p style="color: #555; font-size: 16px;"><b>#%d</b> nömrəli müraciətiniz üzrə <b>%.2f AZN</b> ödəniş qəbul olundu.</p>
                    <p style="color: #888; font-size: 13px;">E-poçt ünvanınıza təsdiq məktubu göndərildi. Təşəkkür edirik!</p>
                </div>
            </body>
            </html>
            """.formatted(app.getId(), app.getAmount());

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(successHtml);
    }
}