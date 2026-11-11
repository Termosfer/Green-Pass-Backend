package com.greenpass.demo.service;

import com.greenpass.demo.entity.Application;
import com.greenpass.demo.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ApplicationRepository applicationRepository;

    public String createPayment(Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Müraciət tapılmadı! ID: " + applicationId));

        // Öz yerli simulyasiya ödəniş səhifəmizin linkini qaytarırıq
        return "http://localhost:8080/api/payment/simulate-page/" + app.getId();
    }
}