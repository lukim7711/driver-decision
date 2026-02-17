# F005: Dashboard Harian

## Deskripsi
Halaman utama app — layar pertama yang dilihat driver setiap buka app. Menampilkan ringkasan hari ini: sisa target, profit bersih, pendapatan, pengeluaran, jumlah trip/pesanan, dan poin bonus. Dirancang agar semua info penting terbaca dalam **2 detik tanpa scroll** (glanceable). Juga menjadi pusat navigasi ke semua fitur lain lewat tab bar di bawah.

## User Story
> Sebagai driver, saya ingin langsung tahu apakah penghasilan hari ini sudah cukup untuk menutup semua kewajiban, tanpa harus hitung manual atau scroll panjang.

## Referensi
- Implements **F005** dari PRD.md

---

# BAGIAN USER

---

## 1. Tentang Fitur Ini

Dashboard ini adalah **layar utama** — yang pertama kelihatan setiap buka app. Semua angka penting hari ini langsung tampil tanpa perlu klik apa-apa.

---

## 2. Alur Penggunaan

### Buka App Setiap Hari
1. Driver buka app → langsung melihat Dashboard
2. Yang pertama terlihat: **Sisa Target** (hero number) → "Kurang Rp24.000 lagi" dengan progress bar
3. Di bawahnya: **Profit Bersih** Rp106.000 (pendapatan - pengeluaran)
4. Di bawahnya: ringkasan singkat (jumlah trip, pesanan, poin)
5. Driver sudah tahu posisi keuangannya dalam **2 detik**

### Saat Target Tercapai
1. Hero number berubah jadi hijau: "🎉 Target Tercapai! Lebih Rp20.000"
2. Progress bar penuh 100%
3. Memberikan rasa pencapaian harian

### Navigasi ke Fitur Lain
1. Tab bar di bawah selalu terlihat: **Home | Order | AI | Input | Lain**
2. Tap tab mana saja → pindah ke halaman yang dimaksud
3. Tombol **Input** paling menonjol (lebih besar, shadow) → buka Input Cepat (F004)

### Tap Ringkasan di Dashboard → Masuk ke Detail
1. Tap ringkasan trip/pesanan → pindah ke tab Order (halaman detail)
2. Tap angka pendapatan → pindah ke tab Order (breakdown pendapatan per trip)
3. Tap angka pengeluaran → lihat riwayat pengeluaran hari ini
4. Tap angka target → lihat detail perhitungan target (F007)

**Total interaksi:** 0 untuk lihat info. Tap untuk navigasi jika perlu detail.

---

## 3. Tampilan Layar

### A. Dashboard — Target Belum Tercapai (tampilan utama)

```
┌─────────────────────────────────────┐
│  Selasa, 17 Feb 2026          🔔    │
│                                     │
│     Kurang Rp 24.000 lagi          │
│     ████████████████░░░ 87%        │
│     Target hari ini: Rp 130.000    │
│                                     │
│  ─────────────────────────────────  │
│                                     │
│  Profit Bersih       Rp 106.000    │
│  Pendapatan Rp156.000 - Keluar Rp50.000│
│                                     │
│  8 trip · 15 pesanan               │
│    5 SPX Instant · 2 ShopeeFood    │
│    1 Sameday                        │
│  450 poin hari ini                  │
│                                     │
│  Pemasukan Lain     Rp20.000       │
│    Tip Cash Rp10.000 + Rp10.000    │
│                                     │
│  ⚠️ 1 pesanan belum masuk hitungan │
│                                     │
├─────────────────────────────────────┤
│  Home  Order  AI  [Input]  Lain    │
└─────────────────────────────────────┘
```

### B. Dashboard — Target Tercapai

```
┌─────────────────────────────────────┐
│  Selasa, 17 Feb 2026          🔔    │
│                                     │
│  🎉 Target Tercapai!               │
│     Lebih Rp 20.000 dari target    │
│     ████████████████████ 100%      │
│     Target hari ini: Rp 130.000    │
│                                     │
│  ─────────────────────────────────  │
│                                     │
│  Profit Bersih       Rp 150.000    │
│  Pendapatan Rp200.000 - Keluar Rp50.000│
│                                     │
│  12 trip · 22 pesanan              │
│    7 SPX Instant · 3 ShopeeFood    │
│    2 Sameday                        │
│  700 poin hari ini                  │
│                                     │
│  Pemasukan Lain     Rp20.000       │
│                                     │
├─────────────────────────────────────┤
│  Home  Order  AI  [Input]  Lain    │
└─────────────────────────────────────┘
```

### C. Dashboard — Hari Kosong (belum ada data)

```
┌─────────────────────────────────────┐
│  Selasa, 17 Feb 2026          🔔    │
│                                     │
│     Target Hari Ini: Rp 130.000    │
│     ░░░░░░░░░░░░░░░░░░░ 0%        │
│     Belum ada pendapatan hari ini  │
│                                     │
│  ─────────────────────────────────  │
│                                     │
│  Profit Bersih            Rp 0     │
│                                     │
│  0 trip · 0 pesanan                │
│  0 poin                             │
│                                     │
├─────────────────────────────────────┤
│  Home  Order  AI  [Input]  Lain    │
└─────────────────────────────────────┘
```

### D. Tab Bar — Detail Desain

```
┌─────────────────────────────────────┐
│  Home  Order  AI  [Input]  Lain    │
│                     ↑              │
│               lebih besar          │
│          keluar garis atas         │
│          shadow halus              │
│          warna hijau/menonjol      │
└─────────────────────────────────────┘
```

Tab bar specs:
- Tombol **Input** di posisi ke-4 dari kiri
- Ukuran lebih besar dari tab lain (keluar 8dp di atas garis tab bar)
- Background warna menonjol (hijau atau warna primer app)
- Shadow halus di bawah tombol (bukan floating terpisah)
- Icon putih, lebih besar
- Tab aktif: icon + label berwarna. Tab non-aktif: icon + label abu-abu

### E. Notifikasi Bell (saat ada yang perlu perhatian — tap → dropdown)

```
🔔 3
├── ⚠️ 3 data perlu dicek (F003)
├── 📸 Jangan lupa riwayat (F002)
└── 🔴 Capture mati (F001)
```

---

## 4. Kasus Khusus

| Situasi | Apa yang User Lihat | Apa yang Terjadi |
|---------|---------------------|------------------|
| Belum ada data hari ini (belum mulai kerja) | Dashboard kosong: target tampil, semua angka Rp0, progress 0% | Tidak ada peringatan atau paksaan |
| Target belum di-set (F007 belum aktif) | Hero number diganti → **Profit Bersih** jadi hero. Tidak ada progress bar target | Target muncul setelah F007 dikonfigurasi |
| Ada data confidence rendah (F003) | Muncul note: "⚠️ X pesanan belum masuk hitungan" | Tap → masuk halaman "Data Perlu Dicek" |
| Pendapatan melebihi target | Hero number hijau: "🎉 Target Tercapai! Lebih RpXXX" | Memberikan positive reinforcement |
| Profit bersih minus (pengeluaran > pendapatan) | Profit ditampilkan merah: "Profit Bersih: -Rp30.000" | Tidak ada peringatan berlebihan — hanya angka merah |
| Internet mati | Semua data tetap tampil (offline-first) | Data dari database lokal |
| Driver buka app tengah malam (hari berganti) | Dashboard reset ke hari baru. Data kemarin tetap tersimpan | Auto-reset berdasarkan tanggal |
| Pemasukan Lain (F004) ada | Muncul section "Pemasukan Lain: RpXXX" | Terpisah dari pendapatan Shopee, tapi masuk hitungan profit |
| Banyak trip hari ini (layar penuh) | Ringkasan tetap 1 baris: "20 trip · 45 pesanan". Tap ke tab Order untuk detail | Dashboard tidak membengkak |

---

## 5. Info Teknis dari User

- Dashboard harus **glanceable** — semua info terbaca dalam 2 detik tanpa scroll
- Hero number = Sisa Target (Goal Gradient Effect — makin kecil makin memotivasi)
- Angka kedua = Profit Bersih (angka "jujur")
- Data Order detail ada di tab terpisah (Order), bukan di dashboard
- Navigasi bawah: Home | Order | AI (tengah) | Input (posisi 4, lebih besar + shadow) | Lain
- Driver pakai tangan kiri → tombol Input di posisi 4 paling nyaman untuk jempol kiri
- Tab "Lain" berisi: Hutang (F006), Biaya Tetap & Jadwal Kerja (F009), Capture Manager (F001/F002), Settings

---

> Bagian di bawah ini adalah terjemahan teknis dari semua yang sudah kita diskusikan. Bagian ini untuk AI Builder — kamu tidak perlu membaca atau memahaminya.

---

# BAGIAN TEKNIS

---

## 6. Technical Implementation

### 6.1 API Endpoints

Tidak ada API — semua lokal di device.

### 6.2 Database Changes

Tidak ada tabel baru. F005 adalah **consumer/reader** — membaca data dari tabel milik fitur lain.

<!-- P4-FIX: Semua sumber data sekarang punya sumber tunggal yang jelas (tidak ada "ATAU" ambigu) -->

#### Sumber Data Dashboard

| Data | Sumber Tabel (Pemilik) | Query |
|------|------------------------|-------|
| Pendapatan Shopee hari ini | `history_trips` (F002) | SUM(total_earning) WHERE trip_date = TODAY |
| Pendapatan per tipe layanan | `history_trips` (F002) | GROUP BY service_type WHERE trip_date = TODAY |
| Jumlah trip hari ini | `history_trips` (F002) | COUNT WHERE trip_date = TODAY |
| Jumlah pesanan hari ini | `history_details` (F002) | COUNT WHERE capture_date = TODAY |
| Poin bonus hari ini | `history_details` (F002) | SUM(bonus_points) WHERE capture_date = TODAY |
| Pengeluaran hari ini | `quick_entries` (F004) | SUM(amount) WHERE type = 'EXPENSE' AND entry_date = TODAY AND is_deleted = 0 |
| Pemasukan Lain hari ini | `quick_entries` (F004) | SUM(amount) WHERE type = 'INCOME' AND entry_date = TODAY AND is_deleted = 0 |
| Target harian | **Dihitung real-time oleh F007** | Tidak ada tabel — F007 menghitung target dari: `debts` (F006), `fixed_expenses` (F009), `work_schedules` (F009), `debt_payments` (F006), `quick_entries` (F004), `history_trips` (F002). F005 memanggil kalkulasi F007, bukan query tabel. |
| Data ambigu | `data_reviews` (F003) | COUNT WHERE review_status = 'PENDING' AND confidence < 0.30 AND DATE(created_at) = TODAY |

<!-- P4-FIX: "trips (F001) ATAU history_trips (F002)" → sumber tunggal history_trips (F002) -->
<!-- P4-FIX: "captured_orders (F001) ATAU history_details (F002)" → sumber tunggal history_details (F002) -->
<!-- P2-FIX: "daily_targets (F007)" → dihitung real-time oleh F007 (tabel daily_targets tidak ada) -->

**Catatan penting tentang sumber data trip/pesanan:**
- **F002 (`history_trips` + `history_details`)** adalah **sumber kebenaran tunggal** untuk semua data pendapatan, trip, pesanan, dan poin di dashboard.
- **F001** (`trips`, `captured_orders`) menyimpan data Order Details (alamat, seller, customer). Data ini di-enrichment oleh F003 ke `matched_orders`, BUKAN langsung di-query oleh dashboard.
- **Alasan:** Data F002 sudah mengandung informasi pendapatan yang final (dari halaman riwayat Shopee), sementara F001 hanya punya data order masuk tanpa pendapatan. Jika sumber di-"ATAU"-kan, AI Builder tidak tahu mana yang diprioritaskan dan bisa double-count.

#### View/computed values (tidak disimpan, dihitung real-time)

| Computed Value | Formula |
|----------------|---------|
| Total Pendapatan | Pendapatan Shopee + Pemasukan Lain |
| Total Pengeluaran | SUM pengeluaran dari quick_entries |
| Profit Bersih | Total Pendapatan − Total Pengeluaran |
| Sisa Target | Target Harian − Profit Bersih (jika > 0 → "Kurang RpXXX"; jika ≤ 0 → "Target Tercapai") |
| Progress % | Profit Bersih ÷ Target Harian × 100, max 100% |

### 6.3 Business Logic

1. **Dashboard Refresh**
   - Auto-refresh setiap kali app dibuka (onResume)
   - Auto-refresh setelah data baru masuk dari F001, F002, F003, atau F004
   - Tidak ada polling/timer — event-driven refresh saja (hemat baterai)

2. **Hero Number Logic**
   - **Jika Target Harian tersedia** (F007 aktif):
     - Profit < Target → tampilkan "Kurang RpXXX lagi" + progress bar warna oranye/kuning
     - Profit ≥ Target → tampilkan "🎉 Target Tercapai! Lebih RpXXX" + progress bar 100% warna hijau
   - **Jika Target Harian belum di-set** (F007 belum aktif):
     - Hero number = **Profit Bersih**
     - Tidak ada progress bar
     - Muncul hint kecil: "Set target harian di Settings"

3. **Profit Bersih Color**
   - Positif (≥ 0) → warna hitam/default
   - Negatif (< 0) → warna merah

4. **Data Ambigu Indicator**
   - Hitung jumlah `data_reviews` dengan confidence < 0.30 dan status = PENDING hari ini
   - Jika > 0 → tampilkan "⚠️ X pesanan belum masuk hitungan"
   - Tap → navigate ke halaman "Data Perlu Dicek" (F003)

5. **Ringkasan Trip/Pesanan**
   - Format: "X trip · Y pesanan"
   - Breakdown: "A SPX Instant · B ShopeeFood · C Sameday"
   - Poin: "Z poin hari ini"
   - Tap area ini → navigate ke tab Order

6. **Pemasukan Lain**
   - Hanya tampil jika ada pemasukan (type = INCOME) hari ini
   - Format: "Pemasukan Lain: RpXXX"
   - Detail breakdown hanya jika ≤ 3 item. Lebih dari 3 → "X pemasukan"

7. **Day Reset**
   - Dashboard menampilkan data berdasarkan tanggal hari ini (device local time)
   - Saat hari berganti (00:00), dashboard otomatis reset jika app sedang terbuka
   - Data hari sebelumnya tetap tersimpan di database

8. **Notification Bell**
   - Badge angka muncul di bell jika ada notifikasi aktif
   - Tap → dropdown list notifikasi yang belum di-dismiss
   - Sumber notifikasi: F001 (capture mati), F002 (pengingat riwayat), F003 (data perlu dicek)
   - Tap item notifikasi → navigate ke halaman terkait

9. **Tab Bar**
   - 5 tab: Home, Order, AI, Input, Lain
   - Tab Input: elevated button, lebih besar (8dp di atas garis tab bar), background warna primer (hijau), shadow elevation 4dp, icon putih
   - Tab aktif: icon + label warna primer. Tab non-aktif: abu-abu
   - Tab bar selalu visible di semua halaman utama (kecuali halaman detail/overlay)

### 6.4 External Dependencies

Tidak ada. Semua lokal di device.

---

## 7. Acceptance Criteria

| # | Kriteria | Test Method |
|---|----------|-------------|
| 1 | Dashboard menampilkan sisa target sebagai hero number jika F007 aktif | Manual |
| 2 | Dashboard menampilkan profit bersih sebagai hero number jika F007 belum aktif | Manual |
| 3 | Progress bar menunjukkan persentase pencapaian target | Manual |
| 4 | Saat target tercapai, hero number berubah hijau "🎉 Target Tercapai!" | Manual |
| 5 | Profit bersih dihitung benar: Pendapatan Shopee + Pemasukan Lain − Pengeluaran | Integration |
| 6 | Pendapatan Shopee diambil dari `history_trips` (F002) — bukan dari `trips` (F001) | Integration |
| 7 | Jumlah trip dan pesanan diambil dari `history_trips` dan `history_details` (F002) | Integration |
| 8 | Ringkasan trip/pesanan/poin tampil dengan angka benar | Integration |
| 9 | Pemasukan Lain tampil jika ada, tersembunyi jika tidak ada | Manual |
| 10 | Note "X pesanan belum masuk hitungan" tampil jika ada data confidence < 30% | Integration |
| 11 | Tap ringkasan trip → navigate ke tab Order | Manual |
| 12 | Tap angka pengeluaran → lihat riwayat pengeluaran hari ini | Manual |
| 13 | Tap angka target → lihat detail perhitungan target (F007) | Manual |
| 14 | Dashboard auto-refresh saat app dibuka dan saat data baru masuk | Integration |
| 15 | Dashboard reset saat hari berganti | Integration |
| 16 | Semua info terbaca tanpa scroll (glanceable) | Manual |
| 17 | Tab bar 5 tab dengan posisi benar: Home, Order, AI, Input, Lain | Manual |
| 18 | Tombol Input lebih besar, keluar garis atas, shadow, warna menonjol | Manual |
| 19 | Notification bell menampilkan badge dan dropdown notifikasi | Manual |
| 20 | Dashboard berfungsi offline (data dari database lokal) | Manual |

<!-- P4-FIX: Criteria #6 dan #7 baru — memastikan sumber data tunggal dari F002 -->

---

## 8. Dependencies

<!-- P4-FIX: F001 bukan direct dependency lagi (data F001 diakses via F003 matched_orders). Sumber utama = F002 -->

- **Bergantung pada:**
  - **F002 (Capture Riwayat) — sumber utama:** Pendapatan Shopee, jumlah trip, jumlah pesanan, poin bonus — semua dari `history_trips` dan `history_details`. F002 adalah **single source of truth** untuk data order/trip di dashboard.
  - **F003 (AI Ekstraksi Data):** Status data ambigu dari `data_reviews`. Data enrichment (gabungan F001+F002) ada di `matched_orders`, tapi dashboard tidak langsung query tabel ini.
  - **F004 (Input Cepat):** Total pengeluaran dan pemasukan lain hari ini dari `quick_entries`.
  - **F007 (Target Harian):** Target harian dihitung real-time oleh F007 berdasarkan data dari F006 dan F009. **Tidak ada tabel `daily_targets`** — F005 memanggil kalkulasi F007 setiap refresh.
  - **F009 (Kewajiban & Jadwal):** Data biaya tetap dan jadwal kerja ditampilkan di halaman Detail Target (diakses dari Dashboard via tap hero number).
- **Catatan tentang F001:** Dashboard **tidak langsung query** tabel `trips` atau `captured_orders` milik F001. Data order details dari F001 di-enrich menjadi `matched_orders` oleh F003, dan pendapatan finalnya ada di `history_trips` (F002). Ini menghindari double-count dan ambiguitas sumber.
- **Dibutuhkan oleh:**
  - Tidak ada fitur lain yang bergantung pada F005 secara teknis. F005 adalah consumer/reader, bukan producer.
