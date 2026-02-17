# F001 — Auto Capture Order Masuk

---

## BAGIAN USER

### 1. Tentang Fitur Ini

**Deskripsi:** Aplikasi otomatis menangkap data order yang tampil di layar Shopee Driver — tanpa driver melakukan apa pun. Setiap kali driver buka detail pesanan di Shopee, app kita diam-diam membaca semua informasi di layar dan menyimpannya. Ini memastikan data berharga (alamat, nominal, kode order) tidak terbuang.

**User Story:** Sebagai driver, saya ingin data order yang masuk otomatis tercatat tanpa saya harus screenshot atau ketik manual, agar tidak ada data yang terlewat dan saya bisa fokus bekerja.

**Referensi:** Implements F001 dari PRD.md

---

### 2. Alur Penggunaan

**Setup Pertama Kali (1x saja):**
1. Driver install aplikasi
2. Saat pertama buka, muncul panduan: "Aplikasi ini butuh izin untuk membaca layar Shopee Driver secara otomatis"
3. Driver tap "Aktifkan" → diarahkan ke Settings Android → aktifkan izin
4. Kembali ke app → muncul konfirmasi: "Auto Capture aktif ✅"
5. Selesai setup

**Fase Belajar (3-5 Hari Pertama — Otomatis):**
1. Driver kerja seperti biasa pakai Shopee Driver
2. App merekam semua tampilan Shopee yang driver lihat — belum diproses, hanya merekam mentah
3. Notifikasi kecil muncul: "📚 Sedang belajar mengenali pola Shopee... (15 contoh terkumpul)"
4. Setelah cukup contoh (minimal 10 per tipe halaman), app menganalisa pola otomatis
5. Notifikasi: "✅ Pola Shopee terkenali! Auto capture sekarang aktif"
6. Data yang direkam selama fase belajar diproses ulang — tidak ada data yang hilang

**Driver tidak perlu melakukan apa pun selama fase belajar — cukup kerja seperti biasa.**

**Harian Setelah Fase Belajar (Sepenuhnya Otomatis):**
1. Driver buka Shopee Driver dan mulai kerja (tap "Mulai Bekerja")
2. Order masuk → muncul pop-up:
   - Jika autobid (terima otomatis): pop-up berisi argo + tombol OK
   - Jika manual: pop-up berisi argo + jarak pickup awal + jarak total + countdown 12 detik
3. Order diterima → masuk ke Daftar Pesanan (bisa 1-5 pesanan dalam 1 trip)
4. Driver tap salah satu pesanan → muncul **Order Details** (alamat lengkap pickup & delivery, seller, customer, berat paket, pembayaran, kode order, dll)
5. **→ Di momen inilah app kita otomatis capture data dari layar**
6. Muncul notifikasi kecil: "📦 Order #6VUS tersimpan"
7. Driver kembali ke Daftar Pesanan, tap pesanan berikutnya → app capture lagi
8. Ulangi sampai semua pesanan dalam trip sudah dibuka detail-nya
9. Driver lanjut kerja: "Sampai Lokasi" → "Confirm Pickup" (foto barang) → antar → selesai
10. Setelah semua pesanan dalam 1 trip selesai diantar → data muncul di Riwayat Pesanan Shopee (dengan format berbeda, alamat disembunyikan)
11. Trip berikutnya masuk → proses berulang dari langkah 2

**Semua pesanan dalam 1 trip dikelompokkan otomatis oleh app.**

**Total interaksi driver: 0 (sepenuhnya otomatis setelah setup)**

---

### 3. Tampilan Layar

**Fitur ini berjalan sepenuhnya di belakang layar** — tidak ada layar khusus yang harus dibuka driver saat bekerja.

**Yang terlihat oleh driver:**

**A. Notifikasi tetap di status bar HP (selalu muncul selama app aktif):**
```
┌──────────────────────────────────────┐
│ 📦 Auto Capture aktif — 5 order     │
│    hari ini (2 trip)                 │
└──────────────────────────────────────┘
```

**B. Notifikasi pop-up singkat (muncul setiap order ter-capture, hilang sendiri):**
```
┌──────────────────────────────────────┐
│ 📦 Order #6VUS tersimpan            │
└──────────────────────────────────────┘
```

**C. Selama fase belajar (hari 1-3), notifikasi berbeda:**
```
┌──────────────────────────────────────┐
│ 📚 Belajar pola Shopee...           │
│    25 contoh terkumpul               │
└──────────────────────────────────────┘
```

**D. Di dalam app — halaman Capture Manager:**
```
┌──────────────────────────────────────┐
│  [←]    Capture Manager              │
├──────────────────────────────────────┤
│                                      │
│  Status: 🟢 Aktif (Auto Capture)    │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
│  Hari ini:                           │
│  📦 5 order ter-capture              │
│  🚗 2 trip                           │
│                                      │
│  ──────── Hari Ini ────────          │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ Trip #1 — 16:42 (3 pesanan)   │  │
│  │ SPX Instant · Rp39.200        │  │
│  │ ✅ Semua pesanan ter-capture   │  │
│  └────────────────────────────────┘  │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ Trip #2 — 20:43 (2 pesanan)   │  │
│  │ ShopeeFood · Rp19.200         │  │
│  │ ✅ Semua pesanan ter-capture   │  │
│  └────────────────────────────────┘  │
│                                      │
├──────────────────────────────────────┤
│  [  🔄 Nonaktifkan Auto Capture  ]  │
└──────────────────────────────────────┘
```

**E. Jika tap salah satu trip → detail per pesanan dalam trip:**
```
┌──────────────────────────────────────┐
│  [←]  Trip #1 — 16:42                │
│       3 pesanan · SPX Instant        │
├──────────────────────────────────────┤
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 📦 Pesanan 1 — #260210BVSAY2  │  │
│  │ Pickup: Taman Duta Mas,       │  │
│  │         Grogol Petamburan     │  │
│  │ Antar:  Jl. Kampung Norogtok, │  │
│  │         Nerogtog, Tangerang   │  │
│  │ 💰 COD · 1.2 kg              │  │
│  └────────────────────────────────┘  │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 📦 Pesanan 2 — #260210BA78D4  │  │
│  │ Pickup: Jl. Kusuma IV,        │  │
│  │         Grogol Petamburan     │  │
│  │ Antar:  Jl. Agus Salim,      │  │
│  │         Tanah Abang           │  │
│  │ 💰 Non-COD · 0.8 kg          │  │
│  └────────────────────────────────┘  │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 📦 Pesanan 3 — #260210C0XGW   │  │
│  │ Pickup: Jl. H Agus Salim,    │  │
│  │         Tanah Abang           │  │
│  │ Antar:  Jl. Pejompongan,     │  │
│  │         Kebayoran Baru        │  │
│  │ 💰 Non-COD · 2.1 kg          │  │
│  └────────────────────────────────┘  │
│                                      │
└──────────────────────────────────────┘
```

**Selama fase belajar, halaman Capture Manager sedikit berbeda:**
```
┌──────────────────────────────────────┐
│  [←]    Capture Manager              │
├──────────────────────────────────────┤
│                                      │
│  Status: 🟡 Sedang Belajar          │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
│                                      │
│  App sedang mempelajari tampilan     │
│  Shopee Driver kamu.                 │
│  Cukup kerja seperti biasa!          │
│                                      │
│  ████████████░░░░  25/30 contoh      │
│                                      │
│  Estimasi selesai: 1-2 hari lagi     │
│                                      │
│  ℹ️ Data tetap direkam dan akan     │
│  diproses otomatis setelah app       │
│  selesai belajar.                    │
│                                      │
└──────────────────────────────────────┘
```

---

### 4. Kasus Khusus

| Situasi | Apa yang User Lihat | Apa yang Terjadi |
|---------|---------------------|------------------|
| Driver belum kasih izin membaca layar | Banner di atas app: "⚠️ Auto Capture belum aktif. Tap untuk mengaktifkan" | Diarahkan ke Settings Android |
| HP matikan app di belakang layar (Xiaomi, Samsung, Oppo sering begini) | Notifikasi: "⚠️ Auto Capture mati. Tap untuk aktifkan ulang" + panduan setting khusus per merk HP | Service perlu diaktifkan ulang oleh driver |
| Driver buka detail pesanan yang sama 2x | Tidak ada notifikasi duplikat | App mendeteksi kode order yang sama, hanya update data jika ada info baru |
| Shopee update tampilan (layout berubah) | Notifikasi: "🔄 Mendeteksi perubahan tampilan Shopee... sedang menyesuaikan" | App otomatis kembali ke fase belajar, kumpulkan contoh baru, lalu aktif lagi. Data selama penyesuaian tetap direkam |
| Masih dalam fase belajar (hari 1-3) | Notifikasi: "📚 Belajar pola Shopee... (15/30 contoh). Data tetap direkam" | Data mentah tersimpan, diproses ulang setelah fase belajar selesai |
| Internet mati saat capture | Tidak ada pengaruh — capture tidak butuh internet | Data tersimpan di HP |
| Driver buka app Shopee biasa (bukan Shopee Driver) | Tidak terjadi apa-apa | App hanya membaca layar Shopee Driver, bukan app lain |
| Order masuk tapi driver tolak (countdown habis / tap tolak) | Tidak di-capture | App hanya capture saat driver membuka Order Details — jika order ditolak, driver tidak buka detail |
| HP low memory / RAM penuh | Notifikasi: "⚠️ Auto Capture mungkin terganggu. Tutup app lain yang tidak dipakai" | App tetap usaha jalan tapi bisa terganggu oleh sistem Android |

---

### 5. Info Teknis dari User

- App yang dibaca: **Shopee Driver** (bukan Shopee biasa)
- Data yang perlu di-capture dari halaman Order Details: alamat lengkap pickup & delivery, kode order (trip code + Order SN), nama seller, nama customer, nominal pembayaran, tipe layanan (SPX Instant, SPX Sameday, ShopeeFood), berat & dimensi paket, metode pembayaran (COD/non-COD), Order ID, waktu order diterima
- Capture terjadi di **momen driver buka Order Details** per pesanan
- Driver PASTI buka detail setiap pesanan (untuk tahu alamat), jadi tidak ada risiko pesanan terlewat
- 1 trip = 1-5 pesanan, harus dikelompokkan sebagai satu trip
- Skema pickup:antar bervariasi (2:3, 3:3, 3:2, dll)
- Format tampilan sama untuk semua tipe layanan, hanya tulisan yang beda
- App harus bisa "belajar sendiri" pola tampilan Shopee (fase belajar 3-5 hari)

---

## BAGIAN TEKNIS

> Bagian di bawah ini adalah terjemahan teknis dari semua yang sudah kita diskusikan. Bagian ini untuk AI Builder — kamu tidak perlu membaca atau memahaminya. Tapi kalau penasaran, silakan tanya.

### 6. Technical Implementation

#### 6.1 API Endpoints

Tidak ada API — semua lokal di device.

#### 6.2 Database Changes

**Tabel baru: `trips`**

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| trip_date | TEXT (YYYY-MM-DD) | Tanggal trip |
| service_type | TEXT | SPX_INSTANT, SPX_SAMEDAY, SHOPEEFOOD, UNKNOWN |
| total_orders | INTEGER | Jumlah pesanan dalam trip |
| pickup_count | INTEGER | Jumlah lokasi pickup |
| delivery_count | INTEGER | Jumlah lokasi delivery |
| started_at | TEXT (ISO 8601) | Waktu trip dimulai (dari timestamp pesanan pertama) |
| completed_at | TEXT (ISO 8601) | Waktu trip selesai (dari timestamp pesanan terakhir, jika ada) |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

**Tabel baru: `captured_orders`**

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| trip_id | TEXT (FK → trips.id) | Trip yang memiliki pesanan ini |
| trip_code | TEXT | Kode trip pendek (misal: #6VUS) |
| order_sn | TEXT | Serial number pesanan (misal: #260210BVSAY2V2) |
| order_id | TEXT | ID panjang pesanan (misal: 29548298...) |
| service_type | TEXT | SPX_INSTANT, SPX_SAMEDAY, SHOPEEFOOD, UNKNOWN |
| pickup_address | TEXT | Alamat lengkap pickup (seller) |
| pickup_area | TEXT | Area/kelurahan/kecamatan pickup (diekstrak dari alamat) |
| delivery_address | TEXT | Alamat lengkap delivery (customer) |
| delivery_area | TEXT | Area/kelurahan/kecamatan delivery (diekstrak dari alamat) |
| seller_name | TEXT | Nama seller/toko/restoran |
| customer_name | TEXT | Nama penerima |
| payment_amount | INTEGER | Nominal pembayaran dalam Rupiah (tanpa desimal) |
| payment_method | TEXT | COD, NON_COD, UNKNOWN |
| parcel_weight | TEXT | Berat paket (misal: "1.2 kg") |
| parcel_dimension | TEXT | Dimensi paket (misal: "30x25x4 cm") |
| order_acceptance_time | TEXT (ISO 8601) | Waktu order diterima driver |
| raw_text | TEXT | Teks mentah seluruh layar (untuk re-processing jika perlu) |
| parse_confidence | REAL | 0.0-1.0, seberapa yakin parser terhadap hasil ekstraksi |
| source_snapshot_id | TEXT (FK → screen_snapshots.id) | Snapshot asal data ini |
| captured_at | TEXT (ISO 8601) | Waktu capture |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

**Tabel baru: `screen_snapshots`** (Discovery Mode + raw backup)

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| package_name | TEXT | Package app yang di-capture (com.shopee.speedy) |
| screen_type | TEXT | UNKNOWN, ORDER_DETAIL, ORDER_LIST, ORDER_POPUP, OTHER |
| screen_type_confidence | REAL | 0.0-1.0, seberapa yakin deteksi tipe halaman |
| raw_text | TEXT | Semua teks di layar |
| node_tree_json | TEXT (JSON) | Struktur lengkap elemen layar: [{depth, className, text, resourceId, bounds, childCount}] |
| is_processed | INTEGER | 0 = belum diproses, 1 = sudah |
| captured_at | TEXT (ISO 8601) | Waktu capture |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

**Tabel baru: `parsing_patterns`** (Hasil belajar dari Discovery Mode)

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| screen_type | TEXT | Tipe halaman yang dikenali pattern ini |
| pattern_type | TEXT | SCREEN_DETECTOR (deteksi halaman) atau FIELD_EXTRACTOR (ekstrak field) |
| field_name | TEXT | Nama field yang diekstrak (order_code, address, amount, dll). NULL untuk SCREEN_DETECTOR |
| detection_method | TEXT | KEYWORD, REGEX, NODE_ID, NODE_POSITION, LLM_GENERATED |
| pattern_value | TEXT | Nilai pattern: keyword, regex, node ID, atau deskripsi posisi |
| sample_count | INTEGER | Berapa contoh yang digunakan untuk generate pattern ini |
| accuracy | REAL | 0.0-1.0, akurasi pattern berdasarkan validasi |
| is_active | INTEGER | 1 = aktif, 0 = non-aktif (diganti pattern baru) |
| created_at | TEXT (ISO 8601) | Waktu pattern dibuat |
| updated_at | TEXT (ISO 8601) | Waktu terakhir diupdate |

#### 6.3 Business Logic

1. **Dua Mode Operasi:**
   - **Discovery Mode**: Merekam semua layar Shopee ke `screen_snapshots`. Belum ada parsing. Aktif saat: pertama kali install, ATAU pattern belum cukup akurat, ATAU Shopee update UI.
   - **Parsing Mode**: Merekam ke `screen_snapshots` DAN langsung parse menggunakan `parsing_patterns` → INSERT ke `captured_orders` + `trips`.

2. **Transisi Discovery → Parsing Mode:**
   - Trigger: minimal 10 contoh per screen_type terkumpul
   - Proses: analisa contoh → generate parsing_patterns (rule-based + LLM-assisted)
   - Validasi: test pattern terhadap semua contoh → jika accuracy > 85% → aktifkan Parsing Mode
   - Backfill: semua screen_snapshots yang is_processed = 0 diproses ulang dengan pattern baru

3. **Auto-Detection Shopee UI Change:**
   - Saat Parsing Mode aktif, jika parse_confidence rata-rata turun di bawah 70% dalam 10 capture terakhir → kemungkinan Shopee update UI
   - Otomatis kembali ke Discovery Mode → kumpulkan contoh baru → generate pattern baru
   - Notifikasi ke driver: "Mendeteksi perubahan tampilan Shopee, sedang menyesuaikan..."

4. **Trip Grouping Logic:**
   - Pesanan dikelompokkan dalam 1 trip berdasarkan: waktu capture berdekatan (dalam jarak < 5 menit) DAN belum ada trip lain yang aktif
   - Trip dianggap "selesai" jika tidak ada pesanan baru dalam 30 menit setelah pesanan terakhir
   - Jika pesanan baru masuk setelah trip selesai → buat trip baru

5. **Capture Trigger:**
   - Hanya aktif untuk package Shopee Driver (com.shopee.speedy)
   - Capture terjadi saat mendeteksi halaman Order Details (berdasarkan pattern: ada kode trip, alamat pickup, alamat delivery)
   - Event type: TYPE_WINDOW_STATE_CHANGED, TYPE_WINDOW_CONTENT_CHANGED

6. **Duplikasi:**
   - Setiap capture dicek: apakah order_sn atau order_id sudah ada di captured_orders?
   - Jika sudah ada → UPDATE data (mungkin ada info baru), jangan INSERT baru

7. **Foreground Service:**
   - Service berjalan sebagai Foreground Service dengan persistent notification
   - Menampilkan status + jumlah order hari ini
   - Memastikan service tidak di-kill oleh OS

#### 6.4 External Dependencies

- **LLM API (opsional):** Untuk membantu generate parsing_patterns dari contoh tampilan. Bisa juga sepenuhnya rule-based tanpa LLM. Jika pakai LLM, hanya dipanggil saat transisi Discovery → Parsing Mode (jarang, bukan per-capture).
- **Tidak ada dependency lain.** Capture sepenuhnya lokal tanpa internet.

---

### 7. Acceptance Criteria

| # | Kriteria | Test Method |
|---|----------|-------------|
| 1 | Izin membaca layar bisa diaktifkan lewat panduan di app | Manual |
| 2 | Service hanya aktif untuk Shopee Driver, tidak membaca app lain | Integration |
| 3 | Discovery Mode: merekam screen snapshot ke database saat Shopee Driver terbuka | Integration |
| 4 | Discovery Mode: mencatat raw_text + node_tree_json per snapshot | Unit |
| 5 | Discovery Mode: menampilkan jumlah contoh terkumpul di notifikasi dan Capture Manager | Manual |
| 6 | Transisi: setelah 10+ contoh per tipe halaman, otomatis generate pattern | Integration |
| 7 | Transisi: backfill — data selama Discovery Mode diproses ulang | Integration |
| 8 | Parsing Mode: order yang ter-capture tersimpan di `captured_orders` dengan field yang benar | Integration |
| 9 | Parsing Mode: pesanan dikelompokkan per trip di tabel `trips` | Integration |
| 10 | Deteksi duplikat: order yang sama tidak disimpan 2x | Unit |
| 11 | Notifikasi tetap menampilkan status dan jumlah order hari ini | Manual |
| 12 | Notifikasi pop-up muncul setiap kali order berhasil ter-capture | Manual |
| 13 | Service tetap berjalan di belakang layar (foreground service) | Manual |
| 14 | Auto-detection: jika confidence turun drastis, kembali ke Discovery Mode otomatis | Integration |
| 15 | Capture berfungsi tanpa koneksi internet | Manual |
| 16 | Capture Manager menampilkan list trip + pesanan per trip hari ini | Manual |

---

### 8. Dependencies

- **Tidak bergantung pada fitur lain** — fitur ini berdiri sendiri sebagai sumber data input.
- **Dibutuhkan oleh:** F003 (AI Ekstraksi Data — mencocokkan data order), F005 (Dashboard — jumlah order & trip hari ini).
