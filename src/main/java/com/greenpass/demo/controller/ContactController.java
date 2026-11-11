package com.greenpass.demo.controller;

import com.greenpass.demo.entity.ContactMessage;
import com.greenpass.demo.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ContactController {

    private final ContactRepository contactRepository;

    // 1. İstifadəçi mesaj göndərəndə hər kəs üçün açıqdır
    @PostMapping
    public ResponseEntity<String> sendMessage(@RequestBody ContactMessage contactMessage) {
        contactRepository.save(contactMessage);
        return ResponseEntity.ok("Mesajınız uğurla göndərildi! Ən qısa zamanda sizinlə əlaqə saxlayacağıq.");
    }

    // 2. Admin panel üçün gələn bütün mesajlar - İNDİ ARTIQ JWT TOKEN TƏLƏB EDİR!
    @GetMapping("/admin/messages")
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        return ResponseEntity.ok(contactRepository.findAll());
    }
}