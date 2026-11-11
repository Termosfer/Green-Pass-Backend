package com.greenpass.demo.repository;

import com.greenpass.demo.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    // Əlavə sorğular lazıq olarsa bura yazacağıq (məsələn, emailə görə axtarış)
}