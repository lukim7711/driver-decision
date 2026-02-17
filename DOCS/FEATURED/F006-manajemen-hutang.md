# F006: Manajemen Hutang

## Deskripsi
Fitur untuk mencatat, mengelola, dan memantau semua hutang aktif driver. Mendukung 3 tipe hutang: cicilan tetap (motor, dll), pinjol/paylater (ada denda otomatis), dan pinjaman personal (fleksibel). Sisa hutang dan denda dihitung otomatis. Driver bisa bayar/cicil langsung dari app, dan hutang yang lunas tetap tersimpan sebagai riwayat.

## User Story
> Sebagai driver, saya ingin semua hutang tercatat di satu tempat dan tahu persis berapa sisa yang harus dibayar (termasuk denda), agar saya punya gambaran jelas kewajiban finansial dan bisa merencanakan pelunasan.

## Referensi
- Implements **F006** dari PRD.md

---

# BAGIAN USER

---

## 1. Tentang Fitur Ini

Fitur ini untuk **catat dan kelola semua hutang** — cicilan motor, pinjol, hutang ke teman, dll. App bisa hitung denda otomatis kalau telat, dan driver bisa bayar/cicil langsung dari app.

---

## 2. Alur Penggunaan

### Tambah Hutang Baru
1. Driver buka tab "Lain" → tap "💰 Hutang"
2. Muncul list hutang aktif (atau kosong kalau belum ada)
3. Tap "➕ Tambah Hutang"
4. Pilih tipe: [ Cicilan Tetap ] [ Pinjol/Paylater ] [ Personal ]
5. Isi data sesuai tipe (field yang tidak relevan otomatis tersembunyi)
6. Tap "💾 Simpan" → hutang tercatat

### Lihat Daftar Hutang
1. Driver buka Hutang → muncul list semua hutang
2. Setiap hutang tampil: nama, sisa hutang, cicilan per bulan, status (on-track / telat / lunas)
3. Kalau ada yang telat → denda berjalan tampil otomatis: "⚠️ Telat 5 hari · Denda Rp75.000"
4. Di bagian atas: total semua sisa hutang + total cicilan per bulan

### Bayar Cicilan (Cicilan Tetap / Pinjol/Paylater)
1. Tap salah satu hutang → masuk detail
2. Tap "💰 Bayar"
3. Muncul nominal preset (= cicilan per bulan) + opsi ketik nominal lain
4. Driver tap nominal → konfirmasi:
   - "Cicilan Motor: -Rp650.000"
   - "Sisa hutang: Rp12.350.000 → Rp11.700.000"
   - "ℹ️ Tidak masuk pengeluaran harian (sudah dihitung di target)"
5. Tap "✅ Bayar" → tersimpan, sisa hutang berkurang

### Bayar Denda (Terpisah dari Cicilan)
1. Di detail hutang pinjol/paylater yang telat, muncul: "⚠️ Denda berjalan: Rp75.000"
2. Tap "Bayar Denda"
3. Nominal otomatis terisi sesuai denda berjalan
4. Konfirmasi:
   - "Denda Pinjol Akulaku: -Rp75.000"
   - "⚠️ Masuk pengeluaran hari ini (tidak direncanakan)"
5. Tap "✅ Bayar" → tersimpan, denda masuk pengeluaran harian (F004)

### Bayar Hutang Personal
1. Tap hutang personal → masuk detail
2. Tap "💰 Bayar"
3. Pilih nominal (preset atau custom)
4. Konfirmasi + pertanyaan tambahan:
   - "Hutang ke Budi: -Rp200.000"
   - "Sisa hutang: Rp700.000 → Rp500.000"
   - "Masukkan ke pengeluaran hari ini?"
   - "[ Ya ] [ Tidak ]"
5. Driver pilih → tersimpan

### Edit / Hapus Hutang
1. Di detail hutang → tap "✏️ Edit" untuk ubah data, atau "🗑️ Hapus" untuk hapus
2. Hutang bisa diedit kapan saja (sifat dinamis)
3. Hapus = soft delete (tetap ada di riwayat)

---

## 3. Tampilan Layar

### A. List Hutang Aktif

```
┌──────────────────────────────────────┐
│  [←]    Hutang                       │
├──────────────────────────────────────┤
│                                      │
│  Total Sisa Hutang: Rp14.575.000    │
│  Cicilan Per Bulan: Rp1.400.000     │
│                                      │
│  ──────── Aktif ────────             │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 🏍️ Cicilan Motor Honda BeAT    │  │
│  │ Sisa: Rp12.350.000             │  │
│  │ Cicilan: Rp650.000/bln         │  │
│  │ Jatuh tempo: tgl 15            │  │
│  │ 🟢 On-track                    │  │
│  └────────────────────────────────┘  │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 📱 Pinjol Akulaku              │  │
│  │ Sisa: Rp1.575.000              │  │
│  │ Cicilan: Rp750.000/bln         │  │
│  │ Jatuh tempo: tgl 10            │  │
│  │ 🔴 Telat 5 hari · Denda Rp75.000│ │
│  └────────────────────────────────┘  │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 👤 Hutang ke Budi              │  │
│  │ Sisa: Rp700.000                │  │
│  │ Jatuh tempo: tgl 31            │  │
│  │ 🟡 11 hari lagi                │  │
│  └────────────────────────────────┘  │
│                                      │
│  ──────── Lunas ────────             │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 📱 SPayLater                   │  │
│  │ ✅ Lunas · 3 Feb 2026          │  │
│  └────────────────────────────────┘  │
│                                      │
│  [  ➕ Tambah Hutang  ]             │
│                                      │
└──────────────────────────────────────┘
```

### B. Detail Hutang — Cicilan Tetap

```
┌──────────────────────────────────────┐
│  [←]  Cicilan Motor Honda BeAT      │
├──────────────────────────────────────┤
│                                      │
│  Tipe: Cicilan Tetap                 │
│  Status: 🟢 On-track                │
│                                      │
│  Total Hutang Awal    Rp17.000.000  │
│  Sisa Hutang          Rp12.350.000  │
│  Sudah Dibayar        Rp4.650.000   │
│                                      │
│  ████████░░░░░░░░░░░  27% lunas     │
│                                      │
│  Cicilan/Bulan        Rp650.000     │
│  Jatuh Tempo          Tanggal 15    │
│  Sisa Cicilan         19 bulan lagi │
│                                      │
│  ──────── Riwayat Bayar ────────    │
│                                      │
│  15 Feb 2026  Rp650.000  ✅         │
│  15 Jan 2026  Rp650.000  ✅         │
│  15 Des 2025  Rp650.000  ✅         │
│  ... lihat semua                     │
│                                      │
├──────────────────────────────────────┤
│  [ 💰 Bayar ] [ ✏️ Edit ] [ 🗑️ ]  │
└──────────────────────────────────────┘
```

### C. Detail Hutang — Pinjol (Telat + Denda)

```
┌──────────────────────────────────────┐
│  [←]  Pinjol Akulaku                 │
├──────────────────────────────────────┤
│                                      │
│  Tipe: Pinjol/Paylater              │
│  Status: 🔴 Telat 5 hari            │
│                                      │
│  Total Hutang Awal    Rp2.000.000   │
│  Sisa Hutang          Rp1.500.000   │
│  Denda Berjalan       Rp75.000      │
│  ─────────────────────────────────  │
│  Sisa Hutang Real     Rp1.575.000   │
│                                      │
│  ████████████░░░░░░░  25% lunas     │
│                                      │
│  Cicilan/Bulan        Rp750.000     │
│  Jatuh Tempo          Tanggal 10    │
│  Denda                Rp15.000/hari │
│  Sisa Cicilan         2 bulan lagi  │
│                                      │
│  ⚠️ Denda bertambah Rp15.000       │
│     setiap hari sampai dibayar!     │
│                                      │
│  ──────── Riwayat Bayar ────────    │
│                                      │
│  10 Jan 2026  Rp750.000  ✅         │
│  ... lihat semua                     │
│                                      │
├──────────────────────────────────────┤
│  [ 💰 Bayar ] [Bayar Denda] [✏️]   │
└──────────────────────────────────────┘
```

### D. Detail Hutang — Personal

```
┌──────────────────────────────────────┐
│  [←]  Hutang ke Budi                 │
├──────────────────────────────────────┤
│                                      │
│  Tipe: Pinjaman Personal            │
│  Status: 🟡 Jatuh tempo 11 hari lagi│
│                                      │
│  Total Hutang Awal    Rp1.000.000   │
│  Sisa Hutang          Rp700.000     │
│  Sudah Dibayar        Rp300.000     │
│                                      │
│  ██████████░░░░░░░░░  30% lunas     │
│                                      │
│  Jatuh Tempo          Tanggal 31    │
│  Catatan: "Bayar kalau sudah ada"   │
│                                      │
│  ──────── Riwayat Bayar ────────    │
│                                      │
│  5 Feb 2026   Rp100.000  ✅         │
│  20 Jan 2026  Rp100.000  ✅         │
│  3 Jan 2026   Rp100.000  ✅         │
│                                      │
├──────────────────────────────────────┤
│  [ 💰 Bayar ]  [ ✏️ Edit ] [ 🗑️ ] │
└──────────────────────────────────────┘
```

### E. Form Tambah Hutang — Pinjol/Paylater

```
┌──────────────────────────────────────┐
│  [←]  Tambah Hutang                  │
├──────────────────────────────────────┤
│                                      │
│  Tipe:                               │
│  [ Cicilan ] [ Pinjol ✦ ] [ Personal ]│
│                                      │
│  Nama Hutang:                        │
│  ┌──────────────────────────────────┐│
│  │ Pinjol Akulaku                   ││
│  └──────────────────────────────────┘│
│                                      │
│  Total Hutang Awal:                  │
│  ┌──────────────────────────────────┐│
│  │ Rp 2.000.000                     ││
│  └──────────────────────────────────┘│
│                                      │
│  Sisa Hutang Saat Ini:               │
│  ┌──────────────────────────────────┐│
│  │ Rp 1.500.000                     ││
│  └──────────────────────────────────┘│
│                                      │
│  Cicilan Per Bulan:                  │
│  ┌──────────────────────────────────┐│
│  │ Rp 750.000                       ││
│  └──────────────────────────────────┘│
│                                      │
│  Tanggal Jatuh Tempo:                │
│  ┌──────────────────────────────────┐│
│  │ Tanggal 10 setiap bulan          ││
│  └──────────────────────────────────┘│
│                                      │
│  Denda Keterlambatan:                │
│  [ Per Hari ✦ ]  [ Per Bulan ]      │
│  ┌──────────────────────────────────┐│
│  │ Rp 15.000                        ││
│  └──────────────────────────────────┘│
│                                      │
│  Catatan (opsional):                 │
│  ┌──────────────────────────────────┐│
│  │                                  ││
│  └──────────────────────────────────┘│
│                                      │
├──────────────────────────────────────┤
│  [         💾 Simpan         ]      │
└──────────────────────────────────────┘
```

### F. Konfirmasi Bayar Cicilan

```
┌──────────────────────────────────────┐
│  Bayar Cicilan                       │
├──────────────────────────────────────┤
│                                      │
│  Cicilan Motor Honda BeAT            │
│                                      │
│  Bayar: Rp650.000                    │
│                                      │
│  Sisa hutang:                        │
│  Rp12.350.000 → Rp11.700.000       │
│                                      │
│  ℹ️ Tidak masuk pengeluaran harian  │
│     (sudah dihitung di target)      │
│                                      │
├──────────────────────────────────────┤
│  [ Batal ]        [ ✅ Bayar ]      │
└──────────────────────────────────────┘
```

### G. Konfirmasi Bayar Hutang Personal

```
┌──────────────────────────────────────┐
│  Bayar Hutang                        │
├──────────────────────────────────────┤
│                                      │
│  Hutang ke Budi                      │
│                                      │
│  Bayar: Rp200.000                    │
│                                      │
│  Sisa hutang:                        │
│  Rp700.000 → Rp500.000             │
│                                      │
│  Masukkan ke pengeluaran hari ini?  │
│  [ Ya ]  [ Tidak ]                   │
│                                      │
├──────────────────────────────────────┤
│  [ Batal ]        [ ✅ Bayar ]      │
└──────────────────────────────────────┘
```

---

## 4. Kasus Khusus

| Situasi | Apa yang User Lihat | Apa yang Terjadi |
|---------|---------------------|------------------|
| Hutang pinjol telat bayar | Status merah: "🔴 Telat X hari · Denda RpXXX". Denda bertambah otomatis setiap hari | Denda dihitung: nominal denda × hari telat (sejak jatuh tempo atau sejak terakhir bayar denda) |
| Hutang paylater telat bayar | Status merah: "🔴 Telat X bulan · Denda RpXXX". Denda bertambah per bulan | Denda dihitung: nominal denda × bulan telat (sejak jatuh tempo atau sejak terakhir bayar denda) |
| Driver bayar denda tapi belum bayar cicilan | Denda di-reset ke 0, lalu mulai berjalan lagi keesokan harinya | Denda dihitung sejak tanggal terakhir bayar denda (bukan dari jatuh tempo lagi) |
| Driver bayar cicilan (telat) | Denda berhenti total. Status kembali on-track | Cicilan bulan ini terbayar → tidak telat lagi → denda berhenti |
| Driver bayar lebih dari cicilan per bulan | App terima nominal berapa pun. Sisa hutang berkurang sesuai | Misal: cicilan Rp650.000 tapi bayar Rp1.000.000 → sisa berkurang Rp1.000.000 |
| Driver bayar lebih dari sisa hutang | App otomatis set nominal = sisa hutang. Tidak bisa bayar lebih | Misal: sisa Rp200.000, driver mau bayar Rp500.000 → otomatis jadi Rp200.000 |
| Sisa hutang jadi Rp0 setelah bayar | Status berubah: "✅ Lunas" + tanggal pelunasan. Pindah ke section "Lunas" | Hutang tidak dihapus, tetap bisa dilihat di riwayat |
| Driver edit sisa hutang manual | Sisa hutang berubah sesuai input driver | Untuk kasus: driver bayar di luar app, atau ada penyesuaian dari pihak peminjam |
| Driver hapus hutang | Konfirmasi: "Yakin hapus?" → soft delete | Hutang hilang dari list aktif, tapi data tetap ada di database |
| Belum ada hutang sama sekali | Halaman kosong + ilustrasi: "Belum ada hutang 🎉" + tombol "➕ Tambah Hutang" | Bukan hal buruk — bisa jadi memang sudah bebas hutang |
| Hutang personal tanpa jatuh tempo | Status: "🟢 Tidak ada deadline" | Tidak ada peringatan jatuh tempo |
| Hutang personal mendekati jatuh tempo | Status: "🟡 X hari lagi" | Jadi pengingat visual, tapi tidak ada notifikasi (itu F012 — SHOULD, bukan MUST) |

---

## 5. Info Teknis dari User

- 3 tipe hutang: Cicilan Tetap, Pinjol/Paylater, Pinjaman Personal
- 2 jenis denda: per hari (pinjol) dan per bulan (paylater) — dihitung otomatis oleh app
- Bayar cicilan tetap/pinjol → TIDAK masuk pengeluaran harian (sudah di target F007)
- Bayar denda → MASUK pengeluaran harian (tidak direncanakan)
- Bayar hutang personal → app tanya driver apakah masuk pengeluaran hari ini
- Hutang lunas tetap tampil sebagai riwayat (✅ Lunas)
- Hutang bersifat dinamis — bisa diedit kapan saja
- Pembayaran hutang yang tidak masuk pengeluaran harian tetap diperhitungkan oleh F007 untuk profit tracking

---

> Bagian di bawah ini adalah terjemahan teknis dari semua yang sudah kita diskusikan. Bagian ini untuk AI Builder — kamu tidak perlu membaca atau memahaminya.

---

# BAGIAN TEKNIS

---

## 6. Technical Implementation

### 6.1 API Endpoints

Tidak ada API — semua lokal di device.

### 6.2 Database Changes

#### Tabel baru: `debts`

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| name | TEXT | Nama hutang (misal: "Cicilan Motor Honda BeAT") |
| debt_type | TEXT | FIXED_INSTALLMENT, PINJOL_PAYLATER, PERSONAL |
| original_amount | INTEGER | Total hutang awal dalam Rupiah |
| remaining_amount | INTEGER | Sisa hutang saat ini dalam Rupiah |
| monthly_installment | INTEGER | Cicilan per bulan dalam Rupiah. NULL jika tidak ada (personal fleksibel) |
| due_date_day | INTEGER | Tanggal jatuh tempo per bulan (1-31). NULL jika tidak ada |
| penalty_type | TEXT | DAILY, MONTHLY, NONE. Default NONE |
| penalty_amount | INTEGER | Nominal denda per hari/bulan dalam Rupiah. 0 jika tidak ada |
| note | TEXT | Catatan opsional. NULL jika kosong |
| status | TEXT | ACTIVE, PAID_OFF, DELETED |
| paid_off_at | TEXT (ISO 8601) | Tanggal lunas. NULL jika belum lunas |
| created_at | TEXT (ISO 8601) | Waktu dibuat |
| updated_at | TEXT (ISO 8601) | Waktu terakhir diedit |

#### Tabel baru: `debt_payments`

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| debt_id | TEXT (FK → debts.id) | Hutang yang dibayar |
| amount | INTEGER | Nominal pembayaran dalam Rupiah |
| payment_type | TEXT | INSTALLMENT (cicilan), PENALTY (denda), PARTIAL (personal) |
| include_as_expense | INTEGER | 1 = masuk pengeluaran harian, 0 = tidak |
| linked_expense_id | TEXT (FK → quick_entries.id) | Jika include_as_expense = 1, link ke record pengeluaran. NULL jika tidak |
| payment_date | TEXT (YYYY-MM-DD) | Tanggal pembayaran |
| note | TEXT | Catatan opsional |
| created_at | TEXT (ISO 8601) | Waktu dibuat |

### 6.3 Business Logic

1. **Perhitungan Denda Otomatis:**
   - Denda hanya berlaku untuk hutang tipe PINJOL_PAYLATER dengan penalty_type = DAILY atau MONTHLY
   - Titik awal hitung denda = **mana yang lebih baru** antara:
     - `due_date_day` bulan ini (tanggal jatuh tempo), ATAU
     - Tanggal terakhir bayar denda: MAX(payment_date) FROM `debt_payments` WHERE debt_id = hutang ini AND payment_type = PENALTY
   - Untuk DAILY penalty: Denda = penalty_amount × jumlah_hari sejak titik awal
   - Untuk MONTHLY penalty: Denda = penalty_amount × jumlah_bulan sejak titik awal
   - Denda dihitung real-time (tidak disimpan di database) — karena berubah setiap hari
   - Denda berhenti (= 0) jika cicilan bulan ini sudah dibayar
   - **Sisa Hutang Real** = remaining_amount + denda berjalan

2. **Status Hutang:**
   - ACTIVE + belum lewat jatuh tempo → 🟢 On-track
   - ACTIVE + mendekati jatuh tempo (≤ 7 hari) → 🟡 X hari lagi
   - ACTIVE + lewat jatuh tempo → 🔴 Telat X hari/bulan
   - ACTIVE + tanpa jatuh tempo (personal) → 🟢 Tidak ada deadline
   - PAID_OFF → ✅ Lunas · tanggal
   - remaining_amount = 0 dan belum di-set PAID_OFF → otomatis set PAID_OFF + paid_off_at = NOW

3. **Bayar Cicilan (FIXED_INSTALLMENT, PINJOL_PAYLATER):**
   - INSERT ke `debt_payments` dengan payment_type = INSTALLMENT
   - UPDATE `debts` SET remaining_amount = remaining_amount - amount
   - include_as_expense = 0 (TIDAK masuk pengeluaran harian)
   - Jika remaining_amount <= 0 → set status = PAID_OFF, paid_off_at = NOW
   - **Post-payment check:** Setelah status berubah ke PAID_OFF, trigger pengecekan mode ambisius F009 — lihat poin 11
   - **Catatan:** Walaupun tidak INSERT ke `quick_entries`, pembayaran ini tetap di-query oleh F007 dari tabel `debt_payments` untuk mengurangi profit tersedia

<!-- P7-FIX: Tambah post-payment check ke F009 setelah PAID_OFF -->

4. **Bayar Denda:**
   - INSERT ke `debt_payments` dengan payment_type = PENALTY
   - include_as_expense = 1 (MASUK pengeluaran harian)
   - INSERT ke `quick_entries` dengan:
     - type = EXPENSE
     - category_id = **(SELECT id FROM quick_entry_categories WHERE name = 'Denda Hutang' AND is_system = 1)**
     - amount = nominal denda
     - note = "Denda [nama hutang]" (auto-generated)
     - entry_date = TODAY
   - Link: debt_payments.linked_expense_id = quick_entries.id
   - **Reset denda:** Setelah record ini tersimpan, perhitungan denda di #1 otomatis berubah karena titik awal hitung denda sekarang = tanggal pembayaran ini (bukan dari jatuh tempo lagi). Denda kembali ke Rp0 dan mulai berjalan lagi keesokan harinya selama cicilan belum dibayar

<!-- P5-FIX: Ganti kategori text "Denda Hutang" → category_id FK ke system category di F004 -->

5. **Bayar Hutang Personal:**
   - INSERT ke `debt_payments` dengan payment_type = PARTIAL
   - UPDATE remaining_amount
   - include_as_expense = nilai yang dipilih driver (0 atau 1)
   - Jika include_as_expense = 1 → INSERT ke `quick_entries` dengan:
     - type = EXPENSE
     - category_id = **(SELECT id FROM quick_entry_categories WHERE name = 'Pembayaran Hutang' AND is_system = 1)**
     - amount = nominal pembayaran
     - note = "Bayar [nama hutang]" (auto-generated)
     - entry_date = TODAY
   - Link: debt_payments.linked_expense_id = quick_entries.id
   - Jika remaining_amount <= 0 → set status = PAID_OFF, paid_off_at = NOW
   - **Post-payment check:** Setelah status berubah ke PAID_OFF, trigger pengecekan mode ambisius F009 — lihat poin 11
   - **Catatan:** Jika include_as_expense = 0, pembayaran ini tetap di-query oleh F007 dari tabel `debt_payments` untuk mengurangi profit tersedia

<!-- P5-FIX: Ganti insert generic → category_id FK ke system category "Pembayaran Hutang" di F004 -->
<!-- P7-FIX: Tambah post-payment check ke F009 setelah PAID_OFF -->

6. **Validasi Pembayaran:**
   - Nominal harus > 0
   - Nominal tidak boleh > remaining_amount (auto-cap ke remaining_amount)
   - Jika nominal = remaining_amount → hutang otomatis lunas

7. **Total untuk Dashboard & Target:**
   - Total sisa hutang: SUM(remaining_amount) WHERE status = ACTIVE
   - Total cicilan per bulan: SUM(monthly_installment) WHERE status = ACTIVE AND monthly_installment IS NOT NULL
   - Data ini dipakai F007 untuk hitung target harian

8. **Edit Hutang:**
   - Semua field bisa diedit kapan saja
   - Jika remaining_amount diedit manual → catat di log (tapi tidak perlu INSERT ke debt_payments)

9. **Hapus Hutang:**
   - Soft delete: SET status = DELETED
   - Hutang dengan status DELETED tidak tampil di list dan tidak dihitung
   - Data tetap ada di database
   - **Post-delete check:** Setelah hapus, trigger pengecekan mode ambisius F009 — lihat poin 11 (karena jumlah hutang aktif berkurang)

<!-- P7-FIX: Tambah post-delete check ke F009 -->

10. **Profit Tracking untuk F007:**
    Setiap pembayaran hutang yang **tidak masuk pengeluaran harian** tetap harus diperhitungkan oleh F007 dalam kalkulasi profit tersedia:

    | Jenis Pembayaran | Masuk Pengeluaran Harian (F004)? | Mengurangi Profit Tersedia (F007)? |
    |---|---|---|
    | Bayar cicilan (Cicilan Tetap) | ❌ Tidak | ✅ Ya |
    | Bayar cicilan (Pinjol/Paylater) | ❌ Tidak | ✅ Ya |
    | Bayar denda | ✅ Ya (via F004) | ✅ Ya (otomatis via F004) |
    | Bayar hutang personal (pilih "Ya") | ✅ Ya (via F004) | ✅ Ya (otomatis via F004) |
    | Bayar hutang personal (pilih "Tidak") | ❌ Tidak | ✅ Ya |

    F007 menghitung profit tersedia dengan query:
    ```
    Pembayaran Hutang Non-Expense = SUM(amount)
      FROM debt_payments
      WHERE include_as_expense = 0
      AND payment_date dalam bulan berjalan

    Profit Tersedia = Profit Terkumpul Bulan Ini − Pembayaran Hutang Non-Expense
    ```

11. **Trigger Pengecekan Mode Ambisius (F009):**

    <!-- P7-FIX: Poin baru — mekanisme trigger dari F006 ke F009 -->

    Setiap kali ada perubahan pada hutang yang bisa memengaruhi jumlah hutang aktif, F006 HARUS memicu pengecekan mode ambisius di F009:

    **Kapan trigger dipanggil:**
    - Setelah pembayaran yang menyebabkan status berubah ke PAID_OFF (poin 3 dan 5)
    - Setelah hutang dihapus / soft delete (poin 9)

    **Apa yang dilakukan trigger:**
    - Hitung: `COUNT(*) FROM debts WHERE status = 'ACTIVE'`
    - Jika hasilnya = 0 (tidak ada hutang aktif lagi):
      - UPDATE `ambitious_mode` SET is_active = 0, updated_at = NOW
      - Tampilkan notifikasi ke driver: "🎉 Semua hutang lunas! Mode ambisius otomatis dinonaktifkan."
    - Jika hasilnya > 0: tidak ada aksi (mode ambisius tetap berjalan dengan data hutang yang tersisa)

    **Implementasi:** Bisa berupa function call langsung (F006 memanggil service/helper milik F009) atau reactive listener (F009 observe tabel `debts` dan bereaksi saat ada perubahan status). Yang penting: AI Builder HARUS memastikan pengecekan ini terjadi.

### 6.4 External Dependencies

Tidak ada. Semua lokal di device.

---

## 7. Acceptance Criteria

| # | Kriteria | Test Method |
|---|----------|-------------|
| 1 | Driver bisa tambah hutang tipe Cicilan Tetap dengan semua field yang relevan | Manual |
| 2 | Driver bisa tambah hutang tipe Pinjol/Paylater dengan pilihan denda per hari atau per bulan | Manual |
| 3 | Driver bisa tambah hutang tipe Personal dengan jatuh tempo opsional | Manual |
| 4 | Denda per hari dihitung otomatis berdasarkan jumlah hari telat | Integration |
| 5 | Denda per bulan dihitung otomatis berdasarkan jumlah bulan telat | Integration |
| 6 | Denda dihitung sejak jatuh tempo ATAU sejak terakhir bayar denda (mana yang lebih baru) | Integration |
| 7 | Denda berhenti jika cicilan bulan ini sudah dibayar | Integration |
| 8 | Sisa Hutang Real = remaining_amount + denda berjalan (tampil di detail) | Integration |
| 9 | Bayar cicilan: sisa hutang berkurang, TIDAK masuk pengeluaran harian | Integration |
| 10 | Bayar denda: masuk pengeluaran harian via kategori sistem "Denda Hutang" (linked ke quick_entries dengan category_id FK), denda reset | Integration |
| 11 | Bayar hutang personal: app tanya "masuk pengeluaran?", jika Ya → INSERT ke quick_entries via kategori sistem "Pembayaran Hutang" | Manual |
| 12 | Nominal bayar tidak bisa melebihi sisa hutang (auto-cap) | Unit |
| 13 | Saat sisa hutang = 0 → status otomatis berubah ke PAID_OFF | Integration |
| 14 | Hutang lunas tetap tampil di section "Lunas" dengan tanggal pelunasan | Manual |
| 15 | Status visual: 🟢 On-track, 🟡 mendekati jatuh tempo, 🔴 telat | Manual |
| 16 | Total sisa hutang dan cicilan per bulan tampil di atas list | Manual |
| 17 | Riwayat pembayaran per hutang tampil dengan tanggal dan nominal | Manual |
| 18 | Driver bisa edit semua data hutang kapan saja | Manual |
| 19 | Driver bisa hapus hutang (soft delete) | Manual |
| 20 | Pembayaran hutang non-expense bisa di-query oleh F007 untuk profit tracking | Integration |
| 21 | Data hutang berfungsi offline | Manual |
| 22 | Setelah hutang terakhir lunas/dihapus, mode ambisius F009 otomatis nonaktif | Integration |
| 23 | Setelah hutang PAID_OFF, trigger pengecekan mode ambisius F009 terpanggil | Integration |

<!-- P5-FIX: Criteria #10 dan #11 dipertegas dengan category_id FK ke system category -->
<!-- P7-FIX: Criteria #22 dan #23 baru untuk validasi trigger ke F009 -->

---

## 8. Dependencies

<!-- P6-FIX: Tambah F004 sebagai dependency (write ke quick_entries) -->

- **Bergantung pada:**
  - **F004 (Input Cepat):** F006 melakukan INSERT ke tabel `quick_entries` (milik F004) saat driver bayar denda hutang atau bayar hutang personal yang dipilih masuk pengeluaran. F006 menggunakan `category_id` dari kategori sistem "Denda Hutang" dan "Pembayaran Hutang" yang di-seed oleh F004. **F004 dan tabel-tabelnya HARUS sudah ada sebelum F006 dibangun.**
- **Integrasi dengan F009:**
  - F006 trigger pengecekan mode ambisius F009 setiap kali hutang berubah status ke PAID_OFF atau DELETED. Lihat Business Logic poin 11.
- **Dibutuhkan oleh:**
  - **F007 (Target Harian):** Total cicilan per bulan + data jatuh tempo per hutang untuk deadline-aware calculation + data pembayaran hutang untuk profit tracking
  - **F008 (AI Chat):** Data hutang untuk analisa dan rekomendasi
  - **F009 (Kewajiban & Jadwal):** Data cicilan hutang untuk perhitungan kewajiban dan mode ambisius
