# ALLO-SPLITBILL — Split Bill REST API

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Multi--Stage-blue.svg)](https://www.docker.com/)
[![Port](https://img.shields.io/badge/Port-4110-blueviolet.svg)](http://localhost:4110)

REST API Split Bill berkinerja tinggi untuk mempermudah perhitungan pembagian tagihan kelompok (*group expense sharing*), penyederhanaan utang antar anggota (*greedy debt simplification*), dan personalisasi biaya layanan (*service charge*) berbasis profil GitHub.

---

## Daftar Isi
- [Arsitektur & Struktur Direktori](#arsitektur--struktur-direktori)
- [Prasyarat Sistem](#prasyarat-sistem)
- [Cara Build & Menjalankan Aplikasi](#cara-build--menjalankan-aplikasi)
- [Database & Monitoring Data (H2 Web Console)](#database--monitoring-data-h2-web-console)
- [Personalisasi Service Charge (GitHub Username)](#personalisasi-service-charge-github-username)
- [Keputusan Desain & Arsitektur (Submission Response)](#keputusan-desain--arsitektur-submission-response)
- [cURL API Documentation & Examples](#curl-api-documentation--examples)

---

## Arsitektur & Struktur Direktori

Aplikasi dirancang mengikuti **Clean Layered Architecture** dengan pemisahan tanggung jawab yang tegas (*Separation of Concerns*):

```
src/main/java/com/allobank/splitbill/
├── SplitBillApplication.java      # Application bootstrap & @EnableJpaAuditing
├── controller/                   # HTTP REST handlers (Input validation & status code mapping)
│   ├── BillGroupController.java
│   ├── ExpenseController.java
│   └── SettlementController.java
├── service/                      # Core business logic & calculation algorithms
│   ├── BillGroupService.java
│   ├── ExpenseService.java
│   ├── SettlementService.java
│   └── SettlementCalculator.java # Pure algorithmic debt simplification & service charge
├── repository/                   # Spring Data JPA interfaces
│   ├── BillGroupRepository.java
│   ├── ParticipantRepository.java
│   ├── ExpenseRepository.java
│   └── ExpenseShareRepository.java
├── entity/                       # JPA entities with BigDecimal precision (18, 2)
│   ├── BillGroup.java
│   ├── Participant.java
│   ├── Expense.java
│   └── ExpenseShare.java
├── dto/                          # Data Transfer Objects with snake_case naming strategy
│   ├── request/
│   │   ├── CreateGroupRequest.java
│   │   ├── ParticipantRequest.java
│   │   ├── CreateExpenseRequest.java
│   │   └── ExpenseSplitRequest.java
│   └── response/
│       ├── ApiResponse.java      # Uniform {status, message, data} envelope
│       ├── BillGroupResponse.java
│       ├── ParticipantResponse.java
│       ├── ExpenseResponse.java
│       ├── ExpenseShareResponse.java
│       ├── SettlementResponse.java
│       └── DebtEntryResponse.java
└── exception/                    # Custom exceptions & Centralized @RestControllerAdvice
    ├── ResourceNotFoundException.java
    ├── InvalidSplitException.java
    ├── BadRequestException.java
    └── GlobalExceptionHandler.java
```

### Standar Presisi Finansial
- Seluruh nilai moneter (`amount`, `share_amount`, `balance`, `service_charge_amount`) **WAJIB** menggunakan `BigDecimal` dengan presisi `precision = 18, scale = 2` dan `RoundingMode.HALF_UP`.
- Tidak ada penggunaan `float` atau `double` di seluruh layer untuk menghindari *floating-point drift*.

---

## Prasyarat Sistem
- **Java**: JDK 17 atau JDK 21+
- **Maven**: 3.8+ (atau gunakan `./mvnw` bawaan)
- **Docker**: Opsional (untuk *containerized execution*)

---

## Cara Build & Menjalankan Aplikasi

### 1. Menjalankan secara Lokal dengan Maven

```bash
# Jalankan seluruh test suite
./mvnw clean test

# Build executable JAR package
./mvnw clean package

# Jalankan aplikasi Spring Boot (Port 4110)
./mvnw spring-boot:run
```

Atau jalankan file JAR hasil build:
```bash
java -jar target/splitbill-0.0.1-SNAPSHOT.jar
```

### 2. Menjalankan dengan Docker Multi-Stage Build

Project ini dilengkapi `Dockerfile` multi-stage build (`eclipse-temurin:21-jdk-alpine` builder dan `eclipse-temurin:21-jre-alpine` runtime):

```bash
# Build Docker image
docker build -t allo-splitbill .

# Run Docker container pada port 4110
docker run -p 4110:4110 allo-splitbill
```

Aplikasi aktif di `http://localhost:4110`.

---

## Database & Monitoring Data (H2 Web Console)

Aplikasi menggunakan **In-Memory H2 Database** yang berjalan langsung di memori (*zero external database setup*). Untuk memantau dan melihat isi database secara visual:

1. Buka browser dan akses: **[http://localhost:4110/h2-console](http://localhost:4110/h2-console)**
2. Masukkan parameter login berikut:
   - **Driver Class**: `org.h2.Driver`
   - **JDBC URL**: `jdbc:h2:mem:splitbilldb`
   - **User Name**: `sa`
   - **Password**: *(biarkan kosong)*
3. Klik **Connect**.
4. Anda dapat melihat struktur tabel relasional dan menjalankan query SQL seperti:
   ```sql
   -- Melihat seluruh grup
   SELECT * FROM BILL_GROUP;

   -- Melihat seluruh anggota
   SELECT * FROM PARTICIPANT;

   -- Melihat catatan pengeluaran
   SELECT * FROM EXPENSE;

   -- Melihat pembagian split tagihan per orang
   SELECT * FROM EXPENSE_SHARE;
   ```

---

## Personalisasi Service Charge (GitHub Username)

Sesuai spesifikasi, persentase service charge dihitung secara dinamis dari **ASCII sum** karakter *lowercase* username GitHub modulo 10:

- **Configured GitHub Username**: `harycp` (dikonfigurasi pada `application.properties`: `app.github.username=harycp`)
- **Perhitungan Nilai ASCII**:
  $$\text{ASCII}(\text{'h'}) = 104$$
  $$\text{ASCII}(\text{'a'}) = 97$$
  $$\text{ASCII}(\text{'r'}) = 114$$
  $$\text{ASCII}(\text{'y'}) = 121$$
  $$\text{ASCII}(\text{'c'}) = 99$$
  $$\text{ASCII}(\text{'p'}) = 112$$
  $$\sum \text{ASCII} = 104 + 97 + 114 + 121 + 99 + 112 = 647$$
- **Persentase Service Charge**:
  $$\text{service\_charge\_pct} = 647 \pmod{10} = 7\%$$
- **Formula Biaya Layanan**:
  $$\text{service\_charge\_amount} = \text{total\_expenses} \times \frac{7}{100} \quad (\text{RoundingMode.HALF\_UP})$$

---

## Keputusan Desain & Arsitektur (Submission Response)

> **Submission Design Decision**:
> Keputusan desain terberat adalah menentukan representasi state dan algoritma penyelesaian utang (*debt simplification*) yang meminimalkan total transaksi antar peserta tanpa kehilangan presisi finansial. Kami memilih algoritma *greedy min-cash-flow* yang menghitung *net balance* ($\sum \text{Paid} - \sum \text{Owed}$) per peserta dan secara iteratif memasangkan debitur terbesar (*max debtor*) dengan kreditur terbesar (*max creditor*). Pendekatan ini menjamin kompleksitas transaksi maksimal $N-1$ serta dieksekusi murni menggunakan `BigDecimal` dengan `RoundingMode.HALF_UP` guna mencegah akumulasi error pembulatan (*floating point drift*) pada pembagian moneter.

---

## cURL API Documentation & Examples

Semua response API dibungkus dalam wrapper seragam:
```json
{
  "status": "success",
  "message": "Deskripsi aksi",
  "data": { ... }
}
```

### 1. Membuat Bill Group Baru
- **Endpoint**: `POST /api/v1/groups`
- **Request**:
```bash
curl -X POST http://localhost:4110/api/v1/groups \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bali Trip 2026",
    "participants": [
      {"name": "Andi"},
      {"name": "Budi"},
      {"name": "Cici"}
    ]
  }'
```
- **Response (201 Created)**:
```json
{
  "status": "success",
  "message": "Group created successfully",
  "data": {
    "group_id": "c0a80101-9123-4abc-8def-123456789abc",
    "name": "Bali Trip 2026",
    "participants": [
      {"participant_id": "p-101", "name": "Andi"},
      {"participant_id": "p-102", "name": "Budi"},
      {"participant_id": "p-103", "name": "Cici"}
    ],
    "created_at": "2026-08-19T12:00:00"
  }
}
```

---

### 2. Mengambil Detail Bill Group
- **Endpoint**: `GET /api/v1/groups/{groupId}`
- **Request**:
```bash
curl -X GET http://localhost:4110/api/v1/groups/c0a80101-9123-4abc-8def-123456789abc
```
- **Response (200 OK)**:
```json
{
  "status": "success",
  "message": "Group retrieved successfully",
  "data": {
    "group_id": "c0a80101-9123-4abc-8def-123456789abc",
    "name": "Bali Trip 2026",
    "participants": [
      {"participant_id": "p-101", "name": "Andi"},
      {"participant_id": "p-102", "name": "Budi"},
      {"participant_id": "p-103", "name": "Cici"}
    ],
    "created_at": "2026-08-19T12:00:00"
  }
}
```

---

### 3. Mencatat Pengeluaran Baru (*Record Expense*)
- **Endpoint**: `POST /api/v1/groups/{groupId}/expenses`
- **Validasi**:
  - `amount > 0`
  - `paid_by` harus anggota grup
  - Setiap peserta split harus anggota grup
  - $\sum \text{splits} == \text{amount}$ (diverifikasi menggunakan `BigDecimal.compareTo == 0`)
- **Request**:
```bash
curl -X POST http://localhost:4110/api/v1/groups/c0a80101-9123-4abc-8def-123456789abc/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "paid_by": "p-101",
    "description": "Hotel Villa Seminyak",
    "amount": 600000.00,
    "splits": [
      {"participant_id": "p-101", "amount": 200000.00},
      {"participant_id": "p-102", "amount": 200000.00},
      {"participant_id": "p-103", "amount": 200000.00}
    ]
  }'
```
- **Response (201 Created)**:
```json
{
  "status": "success",
  "message": "Expense recorded successfully",
  "data": {
    "expense_id": "exp-201",
    "group_id": "c0a80101-9123-4abc-8def-123456789abc",
    "paid_by": "p-101",
    "paid_by_name": "Andi",
    "description": "Hotel Villa Seminyak",
    "amount": "600000.00",
    "created_at": "2026-08-19T12:30:00",
    "splits": [
      {"share_id": "s-1", "participant_id": "p-101", "participant_name": "Andi", "amount": "200000.00"},
      {"share_id": "s-2", "participant_id": "p-102", "participant_name": "Budi", "amount": "200000.00"},
      {"share_id": "s-3", "participant_id": "p-103", "participant_name": "Cici", "amount": "200000.00"}
    ]
  }
}
```

---

### 4. Mengambil Daftar Pengeluaran (*List Expenses*)
- **Endpoint**: `GET /api/v1/groups/{groupId}/expenses`
- **Request**:
```bash
curl -X GET http://localhost:4110/api/v1/groups/c0a80101-9123-4abc-8def-123456789abc/expenses
```
- **Response (200 OK)**:
```json
{
  "status": "success",
  "message": "Expenses retrieved successfully",
  "data": [
    {
      "expense_id": "exp-201",
      "group_id": "c0a80101-9123-4abc-8def-123456789abc",
      "paid_by": "p-101",
      "paid_by_name": "Andi",
      "description": "Hotel Villa Seminyak",
      "amount": "600000.00",
      "created_at": "2026-08-19T12:30:00",
      "splits": [
        {"share_id": "s-1", "participant_id": "p-101", "participant_name": "Andi", "amount": "200000.00"},
        {"share_id": "s-2", "participant_id": "p-102", "participant_name": "Budi", "amount": "200000.00"},
        {"share_id": "s-3", "participant_id": "p-103", "participant_name": "Cici", "amount": "200000.00"}
      ]
    }
  ]
}
```

---

### 5. Menghitung Settlement & Service Charge
- **Endpoint**: `GET /api/v1/groups/{groupId}/settlement`
- **Request**:
```bash
curl -X GET http://localhost:4110/api/v1/groups/c0a80101-9123-4abc-8def-123456789abc/settlement
```
- **Response (200 OK)**:
```json
{
  "status": "success",
  "message": "Settlement calculated successfully",
  "data": {
    "group_id": "c0a80101-9123-4abc-8def-123456789abc",
    "total_expenses": "900000.00",
    "service_charge_pct": 7,
    "service_charge_amount": "63000.00",
    "settlements": [
      {
        "from": "Cici",
        "to": "Andi",
        "amount": "300000.00"
      }
    ]
  }
}
```
