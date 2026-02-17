# F007 — Target Harian Otomatis

---

## BAGIAN USER

### 1. Tentang Fitur Ini

**Deskripsi:** Fitur yang menghitung berapa minimum profit harian yang harus dicapai driver agar semua kewajiban bulanan (cicilan hutang + biaya tetap) terbayar tepat waktu. Target bersifat dinamis — berubah setiap hari berdasarkan profit terkumpul, sisa kewajiban, sisa hari kerja, dan deadline terdekat. Jika driver libur, target otomatis naik. Jika profit sudah cukup, app memberitahu kewajiban tercukupi. Fitur ini adalah **mesin hitung + tampilan** — data kewajiban dan jadwal dikelola di F009.

**User Story:** Sebagai driver, saya ingin tahu berapa minimal yang harus saya dapatkan hari ini supaya semua tagihan bulan ini bisa terbayar tepat waktu, agar saya bisa narik dengan tenang tanpa was-was di akhir bulan.

**Referensi:** Implements F007 dari PRD.md

---

### 2. Alur Penggunaan

**Lihat Target di Dashboard:**
1. Driver buka app → Dashboard (F005)
2. Di bagian hero number, tampil: "Kurang RpXXX lagi" atau "✅ Kewajiban tercukupi!"
3. Angka ini = target harian yang dihitung otomatis oleh F007
4. Tap hero number → masuk halaman Detail Target

**Lihat Detail Target:**
1. Driver tap hero number di Dashboard
2. Muncul halaman detail yang menjelaskan:
   - Breakdown kewajiban bulan ini (hutang apa saja + biaya tetap apa saja)
   - Profit terkumpul bulan ini
   - Sisa kewajiban yang belum tercover
   - Sisa hari kerja bulan ini
   - Cara app menghitung target harian
3. Jika ada kewajiban yang mendekat → tampil peringatan
4. Di bagian bawah: ringkasan jadwal minggu ini (data dari F009) dan toggle Mode Ambisius (data dari F009)

---

### 3. Tampilan Layar

**A. Hero Number di Dashboard (bagian dari F005):**
```
┌──────────────────────────────────────┐
│                                      │
│        Selasa, 17 Feb 2026           │
│                                      │
│  ┌────────────────────────────────┐  │
│  │     Kurang Rp24.000 lagi      │  │  ← tap untuk detail
│  │     ━━━━━━━━━━━━━━━░░░ 85%    │  │
│  │     Target: Rp126.000         │  │
│  └────────────────────────────────┘  │
│                                      │
└──────────────────────────────────────┘
```

Variasi hero number:
```
Belum mulai narik:
  "Target hari ini: Rp126.000"

Sedang progress:
  "Kurang Rp24.000 lagi"
  ━━━━━━━━━━━━━━━░░░ 81%

Target tercapai:
  "✅ Target tercapai! Lebih Rp14.000"
  ━━━━━━━━━━━━━━━━━━━ 111%

Kewajiban bulan ini tercukupi:
  "🎉 Kewajiban bulan ini tercukupi!"
  "Sisa hari ini = bonus"
```

**B. Halaman Detail Target:**
```
┌──────────────────────────────────────┐
│  [←]    Target Harian                │
├──────────────────────────────────────┤
│                                      │
│  Target Hari Ini: Rp126.000         │
│  ━━━━━━━━━━━━━━━░░░ 81%            │
│  Profit hari ini: Rp102.000         │
│  Kurang: Rp24.000                    │
│                                      │
│  ──────── Kewajiban Bulan Ini ───── │
│                                      │
│  Cicilan Hutang:                     │
│  🏍️ Motor (tgl 15)     Rp650.000   │
│  📱 Pinjol (tgl 10)    Rp750.000   │
│                         ───────────  │
│  Subtotal Cicilan:      Rp1.400.000 │
│                                      │
│  Biaya Tetap:                        │
│  📱 Pulsa               Rp50.000    │
│  ⚡ Listrik             Rp200.000   │
│                         ───────────  │
│  Subtotal Biaya Tetap:  Rp250.000   │
│                                      │
│  ════════════════════════════════    │
│  Total Kewajiban:       Rp1.650.000 │
│                                      │
│  ──────── Progress Bulan Ini ─────  │
│                                      │
│  Profit terkumpul:      Rp1.180.000 │
│  Sudah bayar cicilan:   -Rp750.000  │
│  Profit tersedia:       Rp430.000   │
│                                      │
│  Sisa kewajiban:        Rp900.000   │
│  (Rp1.650.000 - Rp750.000 yg       │
│   sudah dibayar)                     │
│                                      │
│  Profit tersedia:       -Rp430.000  │
│  ─────────────────────────────────  │
│  Harus dikumpulkan:     Rp470.000   │
│  Sisa hari kerja:       ÷ 8 hari   │
│  ─────────────────────────────────  │
│  = Target per hari:     Rp58.750    │
│                                      │
│  ⚠️ Tapi cicilan motor jatuh tempo  │
│     3 hari lagi → butuh Rp650.000   │
│     Tersedia: Rp430.000             │
│     Kurang: Rp220.000               │
│     → Target hari ini: Rp126.000    │
│     (disesuaikan deadline terdekat)  │
│                                      │
│  ──────── Jadwal Minggu Ini ─────── │
│  (data dari F009)                    │
│  16-22 Feb                           │
│                                      │
│  Min  Sen  Sel  Rab  Kam  Jum  Sab  │
│  [✅] [✅] [✅] [❌] [✅] [✅] [✅] │
│                                      │
│  6 hari narik · 1 hari libur        │
│  Tap hari untuk toggle narik/libur  │
│                                      │
│  ──────── 🚀 Mode Ambisius ─────── │
│  (data dari F009)                    │
│                                      │
│  [ OFF ○────── ]                     │
│  Aktifkan untuk percepat pelunasan  │
│  hutang                              │
│                                      │
└──────────────────────────────────────┘
```

> **Catatan:** Section "Jadwal Minggu Ini" dan "Mode Ambisius" di halaman ini membaca dan menulis data ke tabel milik F009 (`work_schedules` dan `ambitious_mode`). Interaksi toggle di halaman ini langsung mengubah data F009. Untuk kelola lengkap (biaya tetap, jadwal full), driver buka halaman F009 via tab "Lain".

**C. Peringatan Deadline:**
```
┌──────────────────────────────────────┐
│  ⚠️ Kewajiban Mendekat              │
├──────────────────────────────────────┤
│                                      │
│  📱 Pinjol Akulaku                   │
│  Jatuh tempo: 3 hari lagi (tgl 10) │
│                                      │
│  Butuh:     Rp750.000               │
│  Tersedia:  Rp625.000               │
│  Kurang:    Rp125.000               │
│                                      │
│  ℹ️ Target harian sudah disesuaikan │
│     agar cukup sebelum tgl 10.      │
│                                      │
└──────────────────────────────────────┘
```

---

### 4. Kasus Khusus

| Situasi | Apa yang User Lihat | Apa yang Terjadi |
|---------|---------------------|------------------|
| Hari pertama pakai app (belum ada data) | App arahkan ke F009 untuk set jadwal + biaya tetap. Target langsung dihitung setelah selesai | Onboarding sederhana sebelum target bisa muncul |
| Belum ada hutang dan biaya tetap | Hero number: "Belum ada kewajiban bulan ini. Tambahkan hutang atau biaya tetap" | Tidak bisa hitung target kalau tidak ada kewajiban |
| Semua kewajiban sudah terbayar | "🎉 Kewajiban bulan ini tercukupi!" + sisa = bonus | Target = 0, progress bar penuh |
| Deadline besok tapi profit belum cukup | Peringatan merah: "⚠️ [Nama hutang] jatuh tempo besok! Kurang RpXXX" | Target hari ini disesuaikan agar minimal cukup |
| Deadline sudah lewat (telat bayar) | Peringatan: "🔴 [Nama hutang] sudah lewat jatuh tempo!" | Kewajiban tetap dihitung. Jika ada denda (F006), otomatis bertambah |
| Driver bayar cicilan di luar app | Driver edit sisa hutang manual di F006 → kewajiban berkurang → target ikut turun | Selalu sinkron dengan data F006 |
| Ganti bulan (tgl 1) | Semua reset: profit terkumpul = 0, kewajiban bulan baru dihitung ulang | Fresh start setiap bulan |
| Sisa hari kerja = 0 tapi masih ada kewajiban | "⚠️ Tidak ada sisa hari kerja bulan ini. Kewajiban belum tercukupi RpXXX" | Tidak bisa bagi dengan 0 — tampilkan pesan |
| Target belum di-set (F009 belum dikonfigurasi) | Hero number di F005: Profit Bersih jadi hero. Tidak ada progress bar target | Target muncul setelah F009 dikonfigurasi |

---

### 5. Info Teknis dari User

- Target = **batas minimum** berdasarkan kewajiban bulanan
- Metrik: **Profit** (pendapatan − pengeluaran harian)
- Kewajiban = cicilan hutang (F006) + biaya tetap bulanan (F009)
- Hari kerja dan jadwal dikelola di F009
- Mode ambisius (opsional) dikelola di F009
- Target memperhitungkan **deadline terdekat yang paling ketat** (bukan bagi rata)
- Pembayaran cicilan mengurangi profit tersedia (walaupun tidak masuk pengeluaran harian)
- Profit ≥ kewajiban → "Kewajiban tercukupi!", sisa = bonus

---

## BAGIAN TEKNIS

> Bagian di bawah ini adalah terjemahan teknis dari semua yang sudah kita diskusikan. Bagian ini untuk AI Builder — kamu tidak perlu membaca atau memahaminya.

### 6. Technical Implementation

#### 6.1 API Endpoints

Tidak ada API — semua lokal di device.

#### 6.2 Database Changes

**Tabel baru: `daily_target_cache`** — cache hasil perhitungan target harian. Karena algoritma deadline-aware cukup berat dan hasilnya dibaca oleh banyak fitur (F005 Dashboard, F008 AI Chat), hasil perhitungan di-cache di tabel ini agar tidak perlu hitung ulang setiap kali dashboard dibuka.

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| target_date | TEXT (YYYY-MM-DD) | Tanggal target. **UNIQUE** — 1 record per hari |
| target_amount | INTEGER | Target harian dalam Rupiah (hasil algoritma deadline-aware) |
| total_obligation | INTEGER | Total kewajiban bulan ini dalam Rupiah |
| obligation_paid | INTEGER | Kewajiban yang sudah dibayar bulan ini |
| remaining_obligation | INTEGER | Sisa kewajiban bulan ini |
| profit_accumulated | INTEGER | Profit terkumpul bulan ini |
| profit_available | INTEGER | Profit tersedia (setelah dikurangi pembayaran cicilan non-expense) |
| remaining_work_days | INTEGER | Sisa hari kerja sampai akhir bulan |
| status | TEXT | ON_TRACK / BEHIND / ACHIEVED / NO_OBLIGATION |
| urgent_deadline_name | TEXT | Nama kewajiban dengan deadline paling ketat. NULL jika tidak ada |
| urgent_deadline_date | INTEGER | Tanggal jatuh tempo deadline paling ketat (day of month). NULL jika tidak ada |
| urgent_deadline_gap | INTEGER | Selisih Rupiah antara yang tersedia dan yang dibutuhkan untuk deadline terdekat. NULL jika tidak ada |
| calculated_at | TEXT (ISO 8601) | Waktu terakhir dihitung |
| created_at | TEXT (ISO 8601) | Waktu record pertama dibuat |

F007 juga **membaca** data dari tabel milik fitur lain:

| Data | Sumber Tabel | Pemilik | Query |
|------|-------------|---------|-------|
| Pendapatan Shopee harian | `history_trips` | F002 | SUM(total_earning) WHERE trip_date = [tanggal] |
| Pengeluaran harian | `quick_entries` | F004 | SUM(amount) WHERE type = EXPENSE AND entry_date = [tanggal] |
| Pemasukan lain harian | `quick_entries` | F004 | SUM(amount) WHERE type = INCOME AND entry_date = [tanggal] |
| Cicilan hutang per bulan | `debts` | F006 | WHERE status = ACTIVE AND monthly_installment IS NOT NULL |
| Riwayat pembayaran hutang | `debt_payments` | F006 | WHERE payment_date dalam bulan berjalan |
| Biaya tetap bulanan | `fixed_expenses` | F009 | WHERE is_active = 1 |
| Jadwal kerja | `work_schedules` | F009 | WHERE schedule_date BETWEEN today AND end_of_month |
| Mode ambisius | `ambitious_mode` | F009 | Record terbaru |

#### 6.3 Business Logic

1. **Definisi Kewajiban Bulanan:**
   - Total Cicilan Hutang = SUM(monthly_installment) FROM `debts` (F006) WHERE status = ACTIVE AND monthly_installment IS NOT NULL
   - Total Biaya Tetap = SUM(amount) FROM `fixed_expenses` (F009) WHERE is_active = 1
   - **Kewajiban Normal** = Total Cicilan Hutang + Total Biaya Tetap
   - Jika Mode Ambisius aktif (dari `ambitious_mode` F009):
     - Cicilan Ambisius = SUM(remaining_amount) FROM `debts` (F006) WHERE status = ACTIVE ÷ target_months
     - **Kewajiban Ambisius** = MAX(Cicilan Ambisius, Total Cicilan Hutang) + Total Biaya Tetap

2. **Definisi Profit:**
   - Pendapatan Shopee Hari Ini = SUM(total_earning) FROM `history_trips` (F002) WHERE trip_date = TODAY
   - Pemasukan Lain Hari Ini = SUM(amount) FROM `quick_entries` (F004) WHERE type = INCOME AND entry_date = TODAY
   - Pengeluaran Hari Ini = SUM(amount) FROM `quick_entries` (F004) WHERE type = EXPENSE AND entry_date = TODAY
   - **Profit Hari Ini** = Pendapatan Shopee Hari Ini + Pemasukan Lain Hari Ini − Pengeluaran Hari Ini
   - **Profit Terkumpul Bulan Ini** = SUM(Profit Hari Ini) dari tgl 1 sampai hari ini
   - Pembayaran Cicilan Bulan Ini = SUM(amount) FROM `debt_payments` (F006) WHERE include_as_expense = 0 AND payment_date dalam bulan berjalan
   - **Profit Tersedia** = Profit Terkumpul Bulan Ini − Pembayaran Cicilan Bulan Ini

3. **Sisa Kewajiban Bulan Ini:**
   - Cicilan Yang Sudah Dibayar = SUM(amount) FROM `debt_payments` (F006) WHERE payment_date dalam bulan berjalan
   - **Sisa Kewajiban** = Total Kewajiban − Cicilan Yang Sudah Dibayar
   - Catatan: Biaya tetap (F009) adalah angka statis tanpa mekanisme "bayar" di database. Biaya tetap selalu dihitung penuh sebagai kewajiban setiap bulan. Ketika driver membayar biaya tetap (misal: bayar listrik), itu tercatat sebagai pengeluaran harian di `quick_entries` (F004) yang mengurangi profit — sehingga secara alami mengurangi kemampuan bayar kewajiban lain.

4. **Sisa Hari Kerja:**
   - Dari hari ini sampai akhir bulan
   - Cek `work_schedules` (F009): jika ada record → gunakan is_working. Jika tidak ada record → default is_working = 1
   - Sisa Hari Kerja = COUNT hari dari besok sampai akhir bulan WHERE is_working = 1
   - Hari ini dihitung terpisah (profit hari ini sudah masuk Profit Tersedia)

5. **Algoritma Target Harian (Deadline-Aware):**
   ```
   Input:
     - List kewajiban dengan deadline: [{nama, amount, due_date_day}, ...]
     - profit_tersedia (sudah dikurangi pembayaran cicilan)
     - work_days[] (array hari kerja dari besok sampai akhir bulan, dari F009)

   Langkah:
   a. Sort semua kewajiban by due_date_day ASC
   b. Untuk setiap deadline d_i:
      - cumulative_obligation_i = SUM semua kewajiban yang jatuh tempo ≤ d_i DAN belum dibayar
      - work_days_until_i = COUNT hari kerja dari besok sampai d_i (inklusif)
      - needed_i = cumulative_obligation_i - profit_tersedia
      - Jika needed_i ≤ 0 → target_i = 0
      - Jika work_days_until_i = 0 DAN needed_i > 0 → URGENT (tampilkan peringatan)
      - Else → target_i = CEIL(needed_i / work_days_until_i)

   c. Target Harian = MAX(target_i untuk semua i)
   d. Jika Target Harian ≤ 0 → status = TERCUKUPI
   ```

6. **Progress Hari Ini:**
   - Progress = Profit Hari Ini / Target Harian × 100%
   - Jika profit hari ini ≥ target harian → "✅ Target tercapai! Lebih RpXXX"
   - Jika belum → "Kurang RpXXX lagi"

7. **Peringatan Deadline:**
   - Untuk setiap kewajiban yang jatuh tempo ≤ 3 hari lagi:
     - Hitung: butuh berapa, tersedia berapa, kurang berapa
     - Tampilkan peringatan di Detail Target
   - Untuk kewajiban yang sudah lewat jatuh tempo:
     - Tampilkan peringatan merah: "🔴 Sudah lewat jatuh tempo!"

8. **Reset Bulanan:**
   - Setiap tanggal 1: profit terkumpul reset ke 0
   - Kewajiban bulan baru dihitung ulang (cicilan hutang yang masih aktif + biaya tetap)

9. **Cache Strategy (`daily_target_cache`):**
   - Setelah algoritma selesai menghitung target, hasilnya INSERT OR REPLACE ke `daily_target_cache` WHERE target_date = TODAY
   - Semua field breakdown (total_obligation, profit_accumulated, remaining_work_days, dll) ikut disimpan agar F005 dan F008 bisa langsung baca tanpa hitung ulang
   - Cache di-invalidate (hitung ulang) saat:
     - Data pendapatan Shopee baru masuk (`history_trips` dari F002 berubah)
     - Data pengeluaran/pemasukan lain baru masuk (`quick_entries` dari F004 berubah)
     - Pembayaran hutang terjadi (`debt_payments` dari F006 berubah)
     - Jadwal kerja berubah (`work_schedules` dari F009 di-toggle)
     - Mode ambisius di-toggle (`ambitious_mode` dari F009 berubah)
     - Biaya tetap ditambah/edit/hapus (`fixed_expenses` dari F009 berubah)
     - Hari berganti (00:00 → hitung untuk hari baru)
   - Invalidation bersifat **event-driven** — tidak pakai timer/polling (hemat baterai)
   - F005 (Dashboard) dan F008 (AI Chat) **membaca dari `daily_target_cache`** WHERE target_date = TODAY
   - Jika cache kosong untuk hari ini (belum pernah dihitung) → trigger perhitungan pertama otomatis

#### 6.4 External Dependencies

Tidak ada. Semua lokal di device.

---

### 7. Acceptance Criteria

| # | Kriteria | Test Method |
|---|----------|-------------|
| 1 | Target harian muncul di hero number Dashboard (F005) | Manual |
| 2 | Target = (Sisa Kewajiban − Profit Tersedia) ÷ Sisa Hari Kerja | Integration |
| 3 | Target memperhitungkan deadline terdekat (deadline-aware algorithm) | Integration |
| 4 | Target yang tampil = MAX dari semua target per deadline | Unit |
| 5 | Profit terkumpul mengurangi sisa kewajiban → target turun | Integration |
| 6 | Pembayaran cicilan mengurangi profit tersedia (walaupun bukan pengeluaran harian) | Integration |
| 7 | Profit ≥ kewajiban → "Kewajiban bulan ini tercukupi!" | Integration |
| 8 | Peringatan muncul untuk kewajiban yang jatuh tempo ≤ 3 hari | Integration |
| 9 | Detail Target menampilkan breakdown: kewajiban, profit, sisa, hari kerja | Manual |
| 10 | Detail Target menampilkan ringkasan jadwal minggu ini (dari F009) | Manual |
| 11 | Detail Target menampilkan toggle Mode Ambisius (dari F009) | Manual |
| 12 | Reset bulanan: profit terkumpul kembali ke 0 setiap tanggal 1 | Integration |
| 13 | Sisa hari kerja = 0 tapi kewajiban belum tercukupi → pesan peringatan | Edge case |
| 14 | Data target berfungsi offline | Manual |
| 15 | Cache `daily_target_cache` ter-update saat data sumber berubah (event-driven) | Integration |
| 16 | Cache kosong untuk hari ini → trigger perhitungan pertama otomatis | Integration |

---

### 8. Dependencies

- **Bergantung pada:**
  - **F002 (Capture Riwayat):** Data pendapatan Shopee harian dari `history_trips` — sumber pendapatan UTAMA untuk hitung profit
  - **F004 (Input Cepat):** Data pengeluaran harian dan pemasukan lain (non-Shopee) dari `quick_entries` untuk hitung profit
  - **F006 (Manajemen Hutang):** Data cicilan per bulan, sisa hutang, jatuh tempo per hutang, dan riwayat pembayaran hutang dari `debts` dan `debt_payments`
  - **F009 (Kewajiban & Jadwal):** Data biaya tetap bulanan dari `fixed_expenses`, jadwal kerja dari `work_schedules`, dan mode ambisius dari `ambitious_mode`
- **Tampil di:**
  - **F005 (Dashboard Harian):** Hero number di dashboard membaca `daily_target_cache` dari F007
- **Dibutuhkan oleh:**
  - **F008 (AI Chat):** Membaca `daily_target_cache` untuk data target dan progress dalam context AI