# F002 — Capture Riwayat & Rincian Pesanan

---

## BAGIAN USER

### 1. Tentang Fitur Ini

**Deskripsi:** Fitur ini menangkap data dari halaman Riwayat Pesanan dan Rincian Pesanan di Shopee Driver. Data yang ditangkap melengkapi F001 — khususnya pendapatan per trip, bonus/poin, detail COD, dan timeline pengerjaan pesanan. Dengan data ini, app punya gambaran lengkap: dari alamat (F001) sampai berapa yang didapat (F002).

**User Story:** Sebagai driver, saya ingin data pendapatan, bonus, dan timeline dari riwayat pesanan otomatis tercatat, agar saya punya gambaran lengkap penghasilan tanpa harus hitung manual.

**Referensi:** Implements F002 dari PRD.md

---

### 2. Alur Penggunaan

**Fase Belajar (Minggu Pertama):**
1. App mengirim notifikasi: "📋 Bantu app belajar! Buka 5-10 rincian pesanan di Riwayat Shopee kamu. Cukup 1x ini saja!"
2. Driver buka Shopee Driver → masuk Riwayat Pesanan
3. Driver scroll list riwayat — app merekam tampilan list di belakang layar
4. Driver tap salah satu trip → masuk halaman Rincian Pesanan — app merekam tampilan rincian
5. Driver kembali ke list, tap trip lain → app merekam lagi
6. Ulangi sampai 5-10 rincian sudah dibuka
7. Notifikasi muncul: "✅ Sudah cukup! App sedang belajar polanya..."
8. Setelah app selesai belajar (analisa pola): "✅ Riwayat Pesanan sudah bisa dibaca otomatis!"
9. Data yang direkam selama fase belajar diproses ulang — tidak ada yang hilang

**Driver hanya perlu melakukan ini 1x saja.**

**Harian Setelah Fase Belajar:**
1. Jam 23:00, muncul notifikasi: "📋 Jangan lupa cek riwayat Shopee hari ini supaya data lengkap!"
2. Driver buka Shopee Driver → masuk Riwayat Pesanan
3. Driver scroll list riwayat → app otomatis capture data dari list (waktu, tipe layanan, total pendapatan per trip, label Pesanan Gabungan)
4. Driver tap rincian trip pertama → app otomatis capture data rincian (pendapatan detail, bonus/poin, COD, timeline per pesanan)
5. Driver kembali, tap rincian trip kedua → app capture lagi
6. Ulangi sampai semua trip hari ini sudah dibuka rinciannya
7. Notifikasi: "📋 3 trip hari ini sudah ter-capture lengkap ✅"
8. Data dari F002 otomatis dicocokkan dengan data F001 (berdasarkan kode order) — sehingga setiap pesanan punya data lengkap: alamat + pendapatan + timeline

**Cadangan Manual (jika otomatis gagal atau belum selesai belajar):**
1. Driver buka app kita → Capture Manager → tab "Riwayat"
2. Tap tombol "Mulai Rekam"
3. Driver pindah ke Shopee → scroll riwayat + buka rincian seperti biasa
4. Kembali ke app kita → tap "Selesai Rekam"
5. App memproses semua yang direkam

**Total interaksi harian: ~10 tap (buka rincian per trip) + scroll. Waktu: 2-3 menit di akhir hari.**

---

### 3. Tampilan Layar

**A. Notifikasi pengingat harian (jam 23:00):**
```
┌──────────────────────────────────────┐
│ 📋 Jangan lupa cek riwayat Shopee   │
│    hari ini supaya data lengkap!     │
└──────────────────────────────────────┘
```

**B. Notifikasi saat fase belajar (1x saja):**
```
┌──────────────────────────────────────┐
│ 📋 Bantu app belajar! Buka 5-10     │
│    rincian pesanan di Riwayat        │
│    Shopee kamu. Cukup 1x ini saja!  │
└──────────────────────────────────────┘
```

**C. Notifikasi setelah capture berhasil:**
```
┌──────────────────────────────────────┐
│ 📋 3 trip hari ini sudah            │
│    ter-capture lengkap ✅            │
└──────────────────────────────────────┘
```

**D. Capture Manager — Tab "Riwayat":**
```
┌──────────────────────────────────────┐
│  [←]    Capture Manager              │
├──────────────────────────────────────┤
│  [ Order Masuk ]  [ Riwayat ✦ ]     │
├──────────────────────────────────────┤
│                                      │
│  Status: 🟢 Aktif (Auto Capture)    │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
│  Hari ini:                           │
│  📋 3 trip dari riwayat             │
│  ✅ 3 rincian lengkap               │
│                                      │
│  ──────── 17 Feb 2026 ────────      │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 📋 Trip 16:42 — SPX Instant   │  │
│  │ Pendapatan: Rp39.200          │  │
│  │ Bonus: 250 Poin               │  │
│  │ 3 pesanan · Gabungan          │  │
│  │ ✅ Rincian lengkap            │  │
│  └────────────────────────────────┘  │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 📋 Trip 20:43 — ShopeeFood    │  │
│  │ Pendapatan: Rp19.200          │  │
│  │ Bonus: 150 Poin               │  │
│  │ 2 pesanan · Gabungan          │  │
│  │ ✅ Rincian lengkap            │  │
│  └────────────────────────────────┘  │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 📋 Trip 14:20 — SPX Sameday   │  │
│  │ Pendapatan: Rp52.000          │  │
│  │ ⚠️ Rincian belum dibuka       │  │
│  └────────────────────────────────┘  │
│                                      │
├──────────────────────────────────────┤
│  [  🔴 Mulai Rekam (Manual)  ]      │
└──────────────────────────────────────┘
```

**E. Jika tap salah satu trip → detail rincian yang ter-capture:**
```
┌──────────────────────────────────────┐
│  [←]  Riwayat: Trip 16:42           │
│       SPX Instant · 3 pesanan       │
├──────────────────────────────────────┤
│                                      │
│  Rangkuman Pendapatan                │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
│  Biaya Pengantaran    Rp39.200      │
│  Total Pendapatan     Rp39.200      │
│                                      │
│  Insentif                            │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
│  Bonus Harian Jabodetabek  250 Poin │
│                                      │
│  ──────── Per Pesanan ────────      │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ Pesanan #260210BVSAY2V2       │  │
│  │ Tagih Tunai: Rp0              │  │
│  │ Penyesuaian: Rp0              │  │
│  │ ───────────────────────────── │  │
│  │ Diterima  Tiba  Diambil Selesai│  │
│  │ 16:43    17:03  17:05   18:13 │  │
│  │ 🔗 Cocok dengan F001 ✅       │  │
│  └────────────────────────────────┘  │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ Pesanan #260210BA78D4F9       │  │
│  │ Tagih Tunai: Rp0              │  │
│  │ Penyesuaian: Rp0              │  │
│  │ ───────────────────────────── │  │
│  │ Diterima  Tiba  Diambil Selesai│  │
│  │ 16:43    16:54  16:57   18:06 │  │
│  │ 🔗 Cocok dengan F001 ✅       │  │
│  └────────────────────────────────┘  │
│                                      │
└──────────────────────────────────────┘
```

**F. Selama fase belajar — Tab Riwayat berbeda:**
```
┌──────────────────────────────────────┐
│  [←]    Capture Manager              │
├──────────────────────────────────────┤
│  [ Order Masuk ]  [ Riwayat ✦ ]     │
├──────────────────────────────────────┤
│                                      │
│  Status: 🟡 Sedang Belajar          │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │
│                                      │
│  App perlu belajar pola Riwayat     │
│  Pesanan Shopee kamu.                │
│                                      │
│  Buka 5-10 rincian pesanan di       │
│  Riwayat Shopee. Cukup 1x saja!    │
│                                      │
│  ████████░░░░░░░░  4/10 rincian     │
│                                      │
│  ℹ️ Data tetap direkam dan akan    │
│  diproses setelah app selesai       │
│  belajar.                            │
│                                      │
├──────────────────────────────────────┤
│  [  🔴 Mulai Rekam (Manual)  ]      │
└──────────────────────────────────────┘
```

---

### 4. Kasus Khusus

| Situasi | Apa yang User Lihat | Apa yang Terjadi |
|---------|---------------------|------------------|
| Driver tidak buka rincian pesanan (hanya scroll list riwayat) | App hanya capture data dari list (waktu, tipe layanan, total pendapatan). Di Capture Manager: "⚠️ Rincian belum dibuka" | Data tidak lengkap — tidak ada timeline, poin, dan detail COD per pesanan |
| Driver buka riwayat hari sebelumnya (bukan hari ini) | App tetap capture dan simpan sesuai tanggal asli trip | Data lama tetap berguna untuk analisa historis |
| Riwayat trip yang sama dibuka 2x | Tidak ada notifikasi duplikat | App mendeteksi kode pesanan yang sama, hanya update data jika ada info baru |
| Internet mati saat capture riwayat | Tidak ada pengaruh — capture tidak butuh internet | Data tersimpan di HP |
| HP matikan app di belakang layar saat driver scroll riwayat | Notifikasi: "⚠️ Auto Capture mati. Tap untuk aktifkan ulang" | Rekaman terhenti. Driver bisa pakai tombol "Mulai Rekam" manual sebagai cadangan |
| Shopee update tampilan riwayat | Notifikasi: "🔄 Mendeteksi perubahan tampilan Riwayat Shopee... sedang menyesuaikan" | App kembali ke fase belajar untuk halaman riwayat. Minta driver buka beberapa rincian lagi |
| Driver lupa buka riwayat hari ini | Besoknya, pengingat muncul lagi. Driver bisa buka riwayat kemarin — data tetap ter-capture sesuai tanggal asli | Tidak ada data yang hilang permanen, hanya tertunda |
| Data F002 tidak cocok dengan F001 (kode pesanan tidak matching) | Di Capture Manager: "⚠️ 2 pesanan belum tercocokkan" | App simpan data F002 terpisah. Bisa dicocokkan manual nanti, atau otomatis saat data F001 tersedia |
| Driver pakai tombol "Mulai Rekam" manual | Muncul indikator merah kecil "● REC" di layar | App merekam semua yang tampil di layar sampai driver tap "Selesai Rekam" |

---

### 5. Info Teknis dari User

- Data yang perlu di-capture dari **halaman list Riwayat Pesanan**: waktu trip, tipe layanan (ShopeeFood/SPX Instant/SPX Sameday), total pendapatan per trip, label "Pesanan Gabungan" (jika ada), nama restoran (ShopeeFood), tanggal
- Data yang perlu di-capture dari **halaman Rincian Pesanan**: biaya pengantaran, total pendapatan, bonus/poin (Bonus Harian Jabodetabek), per pesanan: No. Pesanan, Kompensasi Bayar Tunai, Tagih Tunai yang Diterima, Penyesuaian Pesanan, timeline (Diterima → Tiba → Diambil → Selesai dengan timestamp masing-masing)
- Driver biasanya buka riwayat di akhir hari (sekitar jam 23:00)
- Pengingat harian jam 23:00 lewat notifikasi biasa
- Capture Manager punya tab terpisah: "Order Masuk" (F001) dan "Riwayat" (F002)
- Data F002 harus dicocokkan dengan F001 berdasarkan kode pesanan (Order SN)

---

## BAGIAN TEKNIS

> Bagian di bawah ini adalah terjemahan teknis dari semua yang sudah kita diskusikan. Bagian ini untuk AI Builder — kamu tidak perlu membaca atau memahaminya. Tapi kalau penasaran, silakan tanya.

### 6. Technical Implementation

#### 6.1 API Endpoints

Tidak ada API — semua lokal di device.

#### 6.2 Database Changes

**Tabel baru: `history_trips`** (data dari list Riwayat Pesanan)

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| linked_trip_id | TEXT (FK → trips.id) | Referensi ke trip dari F001 (jika sudah tercocokkan). NULL jika belum |
| trip_date | TEXT (YYYY-MM-DD) | Tanggal trip |
| trip_time | TEXT (HH:MM) | Waktu trip (dari list riwayat) |
| service_type | TEXT | SPX_INSTANT, SPX_SAMEDAY, SHOPEEFOOD, UNKNOWN |
| total_earning | INTEGER | Total pendapatan trip dalam Rupiah |
| is_combined | INTEGER | 1 = Pesanan Gabungan, 0 = pesanan tunggal |
| restaurant_name | TEXT | Nama restoran (khusus ShopeeFood). NULL untuk tipe lain |
| raw_text | TEXT | Teks mentah dari list item |
| source_snapshot_id | TEXT (FK → screen_snapshots.id) | Snapshot asal data ini |
| captured_at | TEXT (ISO 8601) | Waktu capture |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

**Tabel baru: `history_details`** (data dari halaman Rincian Pesanan)

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| history_trip_id | TEXT (FK → history_trips.id) | Riwayat trip yang memiliki rincian ini |
| linked_order_id | TEXT (FK → captured_orders.id) | Referensi ke pesanan dari F001 (jika tercocokkan). NULL jika belum |
| order_sn | TEXT | Serial number pesanan (misal: #260210BVSAY2V2) |
| order_id_long | TEXT | ID panjang pesanan (misal: 29548298...) |
| delivery_fee | INTEGER | Biaya pengantaran dalam Rupiah |
| total_earning | INTEGER | Total pendapatan dalam Rupiah |
| bonus_type | TEXT | Tipe bonus (misal: "Bonus Harian Jabodetabek") |
| bonus_points | INTEGER | Jumlah poin bonus |
| cash_compensation | INTEGER | Kompensasi Bayar Tunai dalam Rupiah |
| cash_collected | INTEGER | Tagih Tunai yang Diterima dalam Rupiah |
| order_adjustment | INTEGER | Penyesuaian Pesanan dalam Rupiah |
| time_accepted | TEXT (HH:MM) | Waktu status "Diterima" |
| time_arrived | TEXT (HH:MM) | Waktu status "Tiba" |
| time_picked_up | TEXT (HH:MM) | Waktu status "Diambil" |
| time_completed | TEXT (HH:MM) | Waktu status "Selesai" |
| raw_text | TEXT | Teks mentah dari halaman rincian |
| parse_confidence | REAL | 0.0-1.0 |
| source_snapshot_id | TEXT (FK → screen_snapshots.id) | Snapshot asal data ini |
| captured_at | TEXT (ISO 8601) | Waktu capture |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

**Tabel reuse:** `screen_snapshots` dan `parsing_patterns` dari F001 — ditambahkan screen_type baru: `HISTORY_LIST`, `HISTORY_DETAIL`.

#### 6.3 Business Logic

1. **Dua Mode Operasi** (sama dengan F001, reuse mekanisme yang sudah ada):
   - **Discovery Mode** untuk halaman riwayat: aktif saat pertama kali, butuh minimal 10 contoh halaman list + 10 contoh halaman rincian.
   - **Parsing Mode**: otomatis parse saat driver buka riwayat/rincian.

2. **Pengingat Harian:**
   - Scheduled notification jam 23:00 setiap hari.
   - Jika semua trip hari ini sudah punya rincian lengkap → pengingat tidak muncul.
   - Jika belum ada trip hari ini (driver tidak kerja) → pengingat tidak muncul.
   - Pengingat tetap aktif sampai app divalidasi bisa baca otomatis dengan akurat.

3. **Matching F001 ↔ F002:**
   - Setelah data F002 ter-parse, cocokkan dengan data F001 menggunakan kode pesanan (order_sn).
   - Jika cocok → update `linked_trip_id` di `history_trips` dan `linked_order_id` di `history_details`.
   - Jika tidak cocok (misal: F001 belum capture pesanan ini) → simpan terpisah, coba cocokkan ulang saat data F001 tersedia.

4. **Manual Recording:**
   - Tombol "Mulai Rekam" mengaktifkan continuous capture mode — merekam semua perubahan layar ke `screen_snapshots`.
   - Tombol "Selesai Rekam" menghentikan dan memproses batch.
   - Indikator "● REC" tampil selama rekaman aktif.

5. **Capture dari List Riwayat:**
   - Saat driver scroll list riwayat, app capture setiap item yang muncul di layar.
   - Data per item: waktu, tipe layanan, pendapatan, label gabungan, nama restoran (jika ShopeeFood).
   - Duplikasi dicek berdasarkan: tanggal + waktu + tipe layanan + pendapatan.

6. **Capture dari Rincian Pesanan:**
   - Saat driver buka rincian, app capture seluruh halaman.
   - Karena halaman rincian bisa panjang (perlu scroll), app capture setiap kali ada perubahan konten layar (scroll event).
   - Data per rincian: semua field di tabel `history_details`.

7. **Duplikasi:**
   - Riwayat trip: cek berdasarkan tanggal + waktu + tipe layanan + pendapatan.
   - Rincian pesanan: cek berdasarkan order_sn atau order_id_long.
   - Jika sudah ada → UPDATE, jangan INSERT baru.

#### 6.4 External Dependencies

- **Tidak ada.** Semua lokal di device.
- Reuse mekanisme Discovery/Parsing Mode dari F001 (termasuk opsional LLM untuk generate pattern).

---

### 7. Acceptance Criteria

| # | Kriteria | Test Method |
|---|----------|-------------|
| 1 | Discovery Mode: merekam tampilan list Riwayat Pesanan saat driver scroll | Integration |
| 2 | Discovery Mode: merekam tampilan Rincian Pesanan saat driver buka | Integration |
| 3 | Discovery Mode: menampilkan progress belajar (X/10 rincian) di Capture Manager | Manual |
| 4 | Transisi: setelah cukup contoh, otomatis parse halaman riwayat dan rincian | Integration |
| 5 | Transisi: backfill — data selama fase belajar diproses ulang | Integration |
| 6 | Parsing Mode: data dari list riwayat tersimpan di `history_trips` dengan field benar | Integration |
| 7 | Parsing Mode: data dari rincian pesanan tersimpan di `history_details` dengan field benar | Integration |
| 8 | Matching: data F002 otomatis dicocokkan dengan F001 berdasarkan kode pesanan | Integration |
| 9 | Pengingat harian muncul jam 23:00 jika ada trip yang belum lengkap rinciannya | Manual |
| 10 | Pengingat tidak muncul jika semua trip sudah lengkap atau driver tidak kerja hari itu | Manual |
| 11 | Tombol "Mulai Rekam" manual berfungsi sebagai cadangan | Manual |
| 12 | Indikator "● REC" tampil selama rekaman manual aktif | Manual |
| 13 | Capture Manager tab "Riwayat" menampilkan list trip + status rincian (lengkap/belum) | Manual |
| 14 | Duplikasi: trip dan rincian yang sama tidak disimpan 2x | Unit |
| 15 | Capture halaman rincian yang panjang (perlu scroll) menangkap semua data | Integration |
| 16 | Data riwayat hari sebelumnya tetap bisa di-capture dengan tanggal yang benar | Integration |

---

### 8. Dependencies

- **Bergantung pada F001:** Reuse mekanisme Discovery/Parsing Mode, tabel `screen_snapshots` dan `parsing_patterns`. Data F002 dicocokkan dengan `captured_orders` dan `trips` dari F001 berdasarkan kode pesanan.
- **Dibutuhkan oleh:** F003 (AI Ekstraksi Data — data pendapatan & timeline), F005 (Dashboard — total pendapatan hari ini).
