# F003 — AI Ekstraksi Data dari Capture

---

## BAGIAN USER

### 1. Tentang Fitur Ini

**Deskripsi:** Fitur ini adalah "otak AI" yang bekerja di belakang layar. Tugasnya: mencocokkan data order masuk (F001) dengan data riwayat/rincian (F002) supaya setiap pesanan punya data lengkap, menangani data yang kurang jelas, dan belajar dari koreksi driver supaya makin akurat dari waktu ke waktu.

**User Story:** Sebagai driver, saya ingin data dari order masuk dan riwayat pesanan otomatis terhubung dan akurat, agar saya tidak perlu mencocokkan sendiri dan bisa langsung lihat gambaran lengkap di dashboard.

**Referensi:** Implements F003 dari PRD.md

---

### 2. Alur Penggunaan

**Alur Utama (Sepenuhnya Otomatis — Driver Tidak Perlu Melakukan Apa Pun):**
1. F001 capture data dari Order Details (alamat, seller, customer, berat, kode pesanan, dll)
2. F002 capture data dari Riwayat & Rincian (pendapatan, poin, COD, timeline, kode pesanan, dll)
3. F003 otomatis jalan di belakang layar — mencocokkan data F001 dan F002 berdasarkan kode pesanan
4. Jika cocok → data digabung menjadi 1 record lengkap per pesanan (alamat + pendapatan + timeline)
5. Data lengkap ini yang tampil di Dashboard (F005) dan bisa ditanyakan ke AI Chat (F008)

**Alur Saat Ada Data Kurang Yakin:**
1. AI selesai memproses data, tapi beberapa data confidence-nya rendah
2. Data tetap disimpan — yang confidence 60%+ langsung masuk dashboard
3. Yang confidence di bawah 30% disimpan tapi TIDAK masuk perhitungan dashboard
4. Di akhir hari (bersamaan dengan pengingat F002 jam 23:00), muncul 1 notifikasi ringkasan jika ada data yang perlu dicek: "📊 Ada 3 data yang kurang yakin hari ini. Cek kalau sempat."
5. Driver bisa buka halaman "Data Perlu Dicek" kapan saja — tidak ada deadline, tidak ada paksaan

**Alur Koreksi Data oleh Driver (Opsional):**
1. Driver buka app → masuk halaman "Data Perlu Dicek"
2. Muncul list data yang kurang yakin, diurutkan dari confidence paling rendah
3. Per item, app tunjukkan: data yang terbaca + pilihan koreksi
4. Contoh: "Pendapatan terbaca Rp1.920 — maksudnya Rp19.200?" → driver tap "Ya" atau "Bukan, edit manual"
5. Koreksi tersimpan → AI belajar dari koreksi ini untuk meningkatkan akurasi ke depan
6. Data yang sudah dikoreksi masuk ke dashboard

**Alur Generate Pattern (Saat Fase Belajar F001/F002):**
1. F001 atau F002 sedang dalam fase belajar — sudah cukup contoh terkumpul
2. F003 menganalisa semua contoh yang terkumpul
3. F003 generate aturan/pattern untuk mengenali setiap field di halaman Shopee
4. F003 validasi pattern terhadap semua contoh — jika akurasi > 85% → aktifkan
5. Pattern yang aktif dipakai oleh F001/F002 untuk capture otomatis

**Total interaksi driver: 0 (otomatis). Koreksi opsional jika driver mau.**

---

### 3. Tampilan Layar

**A. Notifikasi ringkasan akhir hari (hanya muncul jika ada data kurang yakin):**
```
┌──────────────────────────────────────┐
│ 📊 Ada 3 data yang kurang yakin     │
│    hari ini. Cek kalau sempat.       │
└──────────────────────────────────────┘
```

**B. Halaman "Data Perlu Dicek":**
```
┌──────────────────────────────────────┐
│  [←]    Data Perlu Dicek             │
├──────────────────────────────────────┤
│                                      │
│  3 data butuh perhatian              │
│  Tidak wajib — cek kalau sempat     │
│                                      │
│  ──────── Hari Ini ────────          │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 🔴 Confidence: 25%            │  │
│  │ Pesanan #260210C0XGW           │  │
│  │                                │  │
│  │ Pendapatan terbaca: Rp1.920   │  │
│  │ Maksudnya Rp19.200?           │  │
│  │                                │  │
│  │ [ Ya, Rp19.200 ]  [ Edit ]    │  │
│  └────────────────────────────────┘  │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 🟡 Confidence: 45%            │  │
│  │ Pesanan #260210BA78D4          │  │
│  │                                │  │
│  │ Alamat pickup terbaca:        │  │
│  │ "Jl. Kus??? IV C Blok D6"    │  │
│  │                                │  │
│  │ [ Biarkan ]  [ Edit ]         │  │
│  └────────────────────────────────┘  │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 🟡 Confidence: 55%            │  │
│  │ Pesanan #260213KAVHCTMG        │  │
│  │                                │  │
│  │ Tipe layanan tidak terdeteksi │  │
│  │ Pilih tipe:                   │  │
│  │                                │  │
│  │ [SPX Instant] [ShopeeFood]    │  │
│  │ [SPX Sameday] [Lainnya]       │  │
│  └────────────────────────────────┘  │
│                                      │
└──────────────────────────────────────┘
```

**C. Halaman "Data Perlu Dicek" — saat Edit manual:**
```
┌──────────────────────────────────────┐
│  [←]    Edit Data                    │
│         Pesanan #260210C0XGW         │
├──────────────────────────────────────┤
│                                      │
│  Pendapatan                          │
│  ┌────────────────────────────────┐  │
│  │ Rp [           19.200       ] │  │
│  └────────────────────────────────┘  │
│  Terbaca: Rp1.920 (confidence 25%)  │
│                                      │
│  Tipe Layanan                        │
│  ┌────────────────────────────────┐  │
│  │ SPX Instant ✅                 │  │
│  └────────────────────────────────┘  │
│                                      │
│  Alamat Pickup                       │
│  ┌────────────────────────────────┐  │
│  │ Jl. Kusuma IV C Blok D6...   │  │
│  └────────────────────────────────┘  │
│                                      │
├──────────────────────────────────────┤
│  [       💾 Simpan Koreksi       ]  │
└──────────────────────────────────────┘
```

**D. Indikator di Dashboard (untuk data yang ditandai):**
```
┌──────────────────────────────────────┐
│  Pendapatan Hari Ini                 │
│                                      │
│  Rp 156.200                          │
│  ⚠️ 1 pesanan belum masuk hitungan  │
│     (data kurang yakin)              │
│                                      │
└──────────────────────────────────────┘
```

**E. Status Matching di Capture Manager (tambahan di tab Order Masuk):**
```
┌──────────────────────────────────────┐
│  Matching F001 ↔ F002                │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
│  ✅ 12 pesanan cocok (data lengkap) │
│  ⏳ 3 pesanan belum ada riwayat     │
│  ⚠️ 1 pesanan tidak cocok           │
└──────────────────────────────────────┘
```

---

### 4. Kasus Khusus

| Situasi | Apa yang User Lihat | Apa yang Terjadi |
|---------|---------------------|------------------|
| Data F001 ada tapi F002 belum (driver belum buka riwayat) | Di Capture Manager: "⏳ 3 pesanan belum ada riwayat" | Data F001 tetap tersimpan. Matching akan jalan otomatis saat data F002 tersedia |
| Data F002 ada tapi F001 tidak (driver tidak capture detail order) | Data F002 tersimpan mandiri tanpa data alamat | Pendapatan tetap masuk dashboard, tapi tidak ada info alamat/seller |
| Kode pesanan di F001 dan F002 tidak cocok (seharusnya tidak terjadi) | Di Capture Manager: "⚠️ 1 pesanan tidak cocok" | App simpan terpisah, masuk "Data Perlu Dicek" agar driver bisa cocokkan manual |
| Driver tidak pernah buka "Data Perlu Dicek" | Data tetap tersimpan. Data confidence 60%+ tetap masuk dashboard. Yang di bawah 30% tidak masuk dashboard | Tidak ada penalti. Pengingat akhir hari tetap muncul tapi tidak memaksa |
| Driver koreksi data yang salah | Muncul konfirmasi: "✅ Data diperbarui" | AI simpan koreksi sebagai pelajaran untuk improve pattern |
| Semua data hari ini confidence tinggi (85%+) | Tidak ada notifikasi "Data Perlu Dicek" | Semuanya otomatis masuk dashboard tanpa gangguan |
| Fase belajar: AI gagal generate pattern yang akurat (< 85%) | Di Capture Manager: "🟡 Masih belajar — butuh lebih banyak contoh" | App kembali kumpulkan contoh. Minta driver buka beberapa halaman lagi |
| Shopee update UI → pattern lama tidak akurat | Notifikasi: "🔄 Menyesuaikan dengan tampilan Shopee baru..." | F003 generate pattern baru dari contoh yang dikumpulkan ulang oleh F001/F002 |
| Banyak data ambigu sekaligus (misal: 10+ pesanan confidence rendah) | Notifikasi: "📊 Ada 10 data yang kurang yakin hari ini" | Data tetap tersimpan. Halaman "Data Perlu Dicek" menampilkan semua, diurutkan dari confidence terendah |

---

### 5. Info Teknis dari User

- Kode pesanan (Order SN / Order ID) diasumsikan sama persis antara Order Details (F001) dan Rincian Pesanan (F002)
- Koreksi driver dibuat semudah mungkin — tap pilihan, bukan ketik manual (kecuali edit)
- Tidak ada deadline untuk koreksi — driver bebas cek kapan saja
- Notifikasi "Data Perlu Dicek" muncul bersamaan dengan pengingat F002 (jam 23:00), maksimal 1 notifikasi per hari

---

## BAGIAN TEKNIS

> Bagian di bawah ini adalah terjemahan teknis dari semua yang sudah kita diskusikan. Bagian ini untuk AI Builder — kamu tidak perlu membaca atau memahaminya. Tapi kalau penasaran, silakan tanya.

### 6. Technical Implementation

#### 6.1 API Endpoints

Tidak ada API — semua lokal di device.

#### 6.2 Database Changes

**Tabel baru: `matched_orders`** (hasil penggabungan F001 + F002)

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| captured_order_id | TEXT (FK → captured_orders.id) | Data dari F001. NULL jika hanya punya data F002 |
| history_detail_id | TEXT (FK → history_details.id) | Data dari F002. NULL jika hanya punya data F001 |
| match_status | TEXT | MATCHED (F001+F002 cocok), F001_ONLY, F002_ONLY, MISMATCH |
| match_confidence | REAL | 0.0-1.0, seberapa yakin matching-nya |
| matched_at | TEXT (ISO 8601) | Waktu matching dilakukan |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

**Tabel baru: `data_reviews`** (data yang perlu dicek driver)

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| source_table | TEXT | captured_orders, history_details, atau matched_orders |
| source_id | TEXT | ID record yang bermasalah |
| field_name | TEXT | Nama field yang kurang yakin (payment_amount, pickup_address, service_type, dll) |
| original_value | TEXT | Nilai yang terbaca AI |
| suggested_value | TEXT | Saran koreksi dari AI (jika ada). NULL jika tidak ada saran |
| corrected_value | TEXT | Nilai koreksi dari driver. NULL jika belum dikoreksi |
| confidence | REAL | 0.0-1.0, confidence untuk field ini |
| review_status | TEXT | PENDING, CONFIRMED, CORRECTED, DISMISSED |
| reviewed_at | TEXT (ISO 8601) | Waktu driver review. NULL jika belum |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

**Tabel baru: `ai_corrections_log`** (log koreksi untuk AI learning)

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| data_review_id | TEXT (FK → data_reviews.id) | Review yang dikoreksi |
| pattern_id | TEXT (FK → parsing_patterns.id) | Pattern yang menghasilkan data salah. NULL jika tidak relevan |
| original_value | TEXT | Nilai sebelum koreksi |
| corrected_value | TEXT | Nilai setelah koreksi |
| correction_type | TEXT | AMOUNT, ADDRESS, SERVICE_TYPE, ORDER_CODE, TIMELINE, OTHER |
| applied_to_pattern | INTEGER | 1 = sudah dipakai untuk improve pattern, 0 = belum |
| created_at | TEXT (ISO 8601) | Waktu koreksi |

#### 6.3 Business Logic

1. **Matching Engine:**
   - Trigger: setiap kali ada data baru di `captured_orders` (F001) atau `history_details` (F002)
   - Matching berdasarkan kode pesanan: `captured_orders.order_sn` = `history_details.order_sn` ATAU `captured_orders.order_id` = `history_details.order_id_long`
   - Jika cocok → INSERT ke `matched_orders` dengan status MATCHED
   - Jika F001 ada tapi F002 belum → status F001_ONLY (akan di-retry saat data F002 masuk)
   - Jika F002 ada tapi F001 tidak → status F002_ONLY
   - Re-matching: setiap kali data baru masuk, cek ulang semua record yang status-nya F001_ONLY atau F002_ONLY

2. **Confidence Tier System:**
   - 85-100%: simpan langsung ke database, masuk dashboard, tidak ada review
   - 60-84%: simpan ke database, masuk dashboard, CREATE `data_reviews` dengan status PENDING (tapi tidak urgent)
   - 30-59%: simpan ke database, masuk dashboard (ditandai), CREATE `data_reviews` dengan status PENDING, masuk ringkasan akhir hari
   - 0-29%: simpan ke database, TIDAK masuk dashboard, CREATE `data_reviews` dengan status PENDING, masuk ringkasan akhir hari

3. **Dashboard Inclusion Logic:**
   - Query dashboard hanya menjumlahkan data yang: confidence >= 30% ATAU review_status = CONFIRMED/CORRECTED
   - Jika ada data confidence < 30% yang belum di-review → tampilkan note: "⚠️ X pesanan belum masuk hitungan"

4. **Review Notification:**
   - Scheduled job jam 23:00 (bersamaan dengan pengingat F002)
   - Hitung jumlah `data_reviews` yang status = PENDING dan created_at = hari ini
   - Jika > 0 → kirim 1 notifikasi ringkasan
   - Jika = 0 → tidak kirim notifikasi

5. **AI Learning dari Koreksi:**
   - Setiap driver koreksi data → INSERT ke `ai_corrections_log`
   - Setiap 10 koreksi baru (atau mingguan, mana yang duluan), jalankan Pattern Improvement:
     - Ambil semua koreksi yang `applied_to_pattern` = 0
     - Analisa pola kesalahan: apakah pattern tertentu sering salah?
     - Jika pattern tertentu accuracy turun → generate pattern baru/revisi
     - Update `parsing_patterns.accuracy` dan `parsing_patterns.updated_at`
     - Set `applied_to_pattern` = 1 untuk koreksi yang sudah diproses

6. **Pattern Generation (untuk Discovery Mode F001/F002):**
   - Menerima batch `screen_snapshots` dari F001/F002
   - Analisa raw_text + node_tree_json untuk menemukan pola:
     - Keyword detection: cari kata kunci tetap ("No. Pesanan", "Biaya Pengantaran", "Total Pendapatan")
     - Regex generation: buat regex untuk field yang punya format tetap (kode order, nominal Rupiah, timestamp)
     - Node position: catat posisi relatif elemen yang mengandung data target
   - Opsional: kirim ke LLM API untuk bantu identifikasi pola yang kompleks
   - Validasi: test semua pattern terhadap sample → hitung accuracy
   - Jika accuracy > 85% → aktifkan pattern, set `is_active` = 1
   - Jika accuracy <= 85% → butuh lebih banyak sample, atau minta bantuan LLM

7. **Duplikasi di matched_orders:**
   - Cek sebelum INSERT: apakah kombinasi captured_order_id + history_detail_id sudah ada?
   - Jika sudah → UPDATE match_confidence jika berubah
   - Jangan INSERT duplikat

#### 6.4 External Dependencies

- **LLM API (opsional):** Untuk membantu generate parsing patterns yang kompleks. Provider: Groq API (gratis/murah). Fallback: rule-based saja tanpa LLM. Hanya dipanggil saat: (1) generate pattern baru, (2) pattern improvement dari koreksi. BUKAN per-capture.
- **Tidak ada dependency lain.** Semua proses lokal di device.

---

### 7. Acceptance Criteria

| # | Kriteria | Test Method |
|---|----------|-------------|
| 1 | Matching otomatis: pesanan dengan kode yang sama dari F001 dan F002 terhubung di `matched_orders` | Integration |
| 2 | Matching retry: data F001_ONLY otomatis di-match ulang saat data F002 masuk | Integration |
| 3 | Confidence 85-100%: data masuk dashboard tanpa review | Integration |
| 4 | Confidence 60-84%: data masuk dashboard + tercatat di `data_reviews` | Integration |
| 5 | Confidence 30-59%: data masuk dashboard (ditandai) + masuk ringkasan akhir hari | Integration |
| 6 | Confidence di bawah 30%: data TIDAK masuk dashboard sampai dikonfirmasi | Integration |
| 7 | Notifikasi ringkasan akhir hari muncul jam 23:00 jika ada data PENDING | Manual |
| 8 | Notifikasi tidak muncul jika semua data confidence tinggi | Manual |
| 9 | Halaman "Data Perlu Dicek" menampilkan list data + pilihan koreksi | Manual |
| 10 | Koreksi dengan tap pilihan berfungsi (bukan hanya ketik manual) | Manual |
| 11 | Koreksi tersimpan di `ai_corrections_log` | Integration |
| 12 | Setelah koreksi, data masuk dashboard dengan nilai yang benar | Integration |
| 13 | Pattern generation: menghasilkan pattern dengan accuracy > 85% dari 10+ sample | Integration |
| 14 | Pattern improvement: setelah 10 koreksi, pattern diperbarui | Integration |
| 15 | Dashboard menampilkan note "X pesanan belum masuk hitungan" jika ada data confidence < 30% | Manual |
| 16 | Status matching tampil di Capture Manager (X cocok, X belum, X tidak cocok) | Manual |

---

### 8. Dependencies

- **Bergantung pada F001:** Mengambil data dari `captured_orders` dan `trips`. Reuse `screen_snapshots` dan `parsing_patterns` untuk generate pattern.
- **Bergantung pada F002:** Mengambil data dari `history_trips` dan `history_details`. Reuse mekanisme Discovery/Parsing Mode.
- **Dibutuhkan oleh:** F005 (Dashboard — data lengkap per pesanan untuk perhitungan), F008 (AI Chat — data lengkap untuk menjawab pertanyaan driver).
