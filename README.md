# 🇺🇸 Green Pass - US Green Card Application & Admin Backend

A robust, secure, and production-ready **Spring Boot** backend system designed for managing **US Green Card (DV Lottery)** applications, image optimization via cloud storage, simulated payment gateways, and a JWT-secured Admin Panel.

---

## 🚀 Technologies Used
* **Java 17** / **Spring Boot 3.x**
* **Spring Data JPA** (Hibernate)
* **Spring Security** & **JWT (JSON Web Token)** - Secured Admin Authentication
* **PostgreSQL** (Neon.tech Cloud Database)
* **Cloudinary API** - Automated image upload, compression, and optimization
* **Spring Mail (JavaMailSender)** - HTML-designed responsive email notifications
* **Springdoc OpenAPI (Swagger)** - Interactive API Documentation

---

## 📌 Core Features
1. **Multi-Step Application Form:** Handles personal details, family status (spouse and children tracking via separate relations), package selection (Individual, Family, VIP), and photo uploads.
2. **Cloudinary Integration:** Automatically uploads and compresses user-submitted photos (`quality: auto`) to save bandwidth and storage.
3. **Secured Admin Panel (JWT):** Restricts access to sensitive endpoints (`/api/admin/**`) using token-based authentication. Pre-configured with a default admin account upon startup.
4. **HTML Email Templates:** 
   - Automated confirmation emails upon successful application submission.
   - Dynamic status update emails (`APPROVED` - Green badge, `REJECTED` - Red badge) triggered by admin actions.
5. **Payment Simulation (Mock Gateway):** Automatically calculates package prices (19 AZN, 35 AZN, 59 AZN) and simulates secure card processing with instant database status updates (`PAID`).
6. **Swagger UI Support:** Fully documented endpoints available at `http://localhost:8080/swagger-ui/index.html`.

---

## ⚙️ Local Setup & Installation

To run this project locally, follow these steps:

### 1. Clone the repository
```bash
git clone https://github.com/YOUR_USERNAME/green-pass-backend.git
cd green-pass-backend
