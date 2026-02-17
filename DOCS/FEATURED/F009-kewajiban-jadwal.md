# F009: Kewajiban & Jadwal

## Deskripsi
Fitur untuk mengelola data kewajiban bulanan (biaya tetap) dan jadwal kerja driver. Biaya tetap adalah pengeluaran rutin bulanan seperti pulsa, listrik, kontrakan, dll. Jadwal kerja menentukan hari mana driver narik dan hari mana libur. Semua data di sini dipakai F007 untuk menghitung target harian. Juga tersedia Mode Ambisius untuk mempercepat pelunasan hutang.

## User Story
> Sebagai driver, saya ingin mencatat semua biaya tetap bulanan dan mengatur jadwal kerja mingguan saya, agar app bisa menghitung target harian yang akurat sesuai kewajiban dan waktu kerja saya.

## Referensi
- Dipecah dari F007 (Target Harian Otomatis). Data dari F009 dipakai oleh F007 untuk perhitungan target.

---

# BAGIAN USER

---

## 1. Tentang Fitur Ini

Fitur ini untuk **catat biaya tetap bulanan** (pulsa, listrik, kontrakan, dll) dan **atur jadwal kerja** (hari mana narik, hari mana libur). Semua data ini dipakai untuk menghitung target harian yang akurat. Ada juga **Mode Ambisius** untuk percepat lunasi hutang.

---

## 2. Alur Penggunaan

### Set Biaya Tetap Bulanan
1. Driver buka tab "Lain" → tap "📋 Biaya Tetap Bulanan"
2. Muncul list biaya tetap yang sudah di-set
3. Tap "➕ Tambah" → isi emoji, nama, nominal
4. Biaya tetap yang sudah ada bisa diedit (emoji, nama, nominal) atau dihapus
5. Default: app kasih template (Pulsa, Listrik, Air, Kontrakan, Internet, Sekolah Anak, Servis Kendaraan) — driver pilih yang relevan
6. Total biaya tetap otomatis masuk perhitungan target (F007)

### Set Jadwal Mingguan
1. Driver buka tab "Lain" → tap "📅 Jadwal Kerja", ATAU buka Detail Target (F007) → scroll ke section "Jadwal Minggu Ini"
2. Muncul 7 hari (Minggu–Sabtu) dengan toggle per hari
3. Default: semua hari = narik (✅)
4. Driver tap hari tertentu → toggle jadi libur (❌)
5. Sisa hari kerja berubah → target harian di F007 ikut berubah langsung
6. Driver bisa ubah kapan saja (tidak ada pop-up otomatis)

### Aktifkan Mode Ambisius
1. Driver buka Detail Target (F007) → scroll ke bawah
2. Tap toggle "🚀 Mode Ambisius"
3. Muncul: "Mau lunas semua hutang dalam berapa bulan?"
4. Driver pilih angka (misal: 6 bulan)
5. App hitung: total sisa hutang ÷ X bulan = cicilan per bulan yang lebih tinggi
6. Target harian otomatis naik (dihitung oleh F007)
7. Bisa dimatikan kapan saja → kembali ke mode normal (cicilan minimum)

---

## 3. Tampilan Layar

### A. Halaman Biaya Tetap Bulanan

```
┌──────────────────────────────────────┐
│  [←]  Biaya Tetap Bulanan           │
├──────────────────────────────────────┤
│                                      │
│  Total: Rp250.000/bulan             │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 📱  Pulsa / Paket Data        │  │
│  │     Rp50.000/bulan            │  │
│  └────────────────────────────────┘  │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ ⚡  Listrik                    │  │
│  │     Rp200.000/bulan           │  │
│  └────────────────────────────────┘  │
│                                      │
│  [  ➕ Tambah Biaya Tetap  ]        │
│                                      │
└──────────────────────────────────────┘
```

### B. Form Tambah/Edit Biaya Tetap

```
┌──────────────────────────────────────┐
│  [←]  Tambah Biaya Tetap            │
├──────────────────────────────────────┤
│                                      │
│  Emoji:                              │
│  [ 📱 ] ← tap untuk pilih           │
│                                      │
│  Nama:                               │
│  ┌──────────────────────────────────┐│
│  │ Pulsa / Paket Data              ││
│  └──────────────────────────────────┘│
│                                      │
│  Nominal Per Bulan:                  │
│  ┌──────────────────────────────────┐│
│  │ Rp 50.000                       ││
│  └──────────────────────────────────┘│
│                                      │
│  Catatan (opsional):                 │
│  ┌──────────────────────────────────┐│
│  │ Telkomsel + paket data 15GB     ││
│  └──────────────────────────────────┘│
│                                      │
├──────────────────────────────────────┤
│  [         💾 Simpan         ]      │
└──────────────────────────────────────┘
```

### C. Template Default Biaya Tetap (saat pertama kali)

```
┌──────────────────────────────────────┐
│  Pilih biaya tetap yang kamu punya: │
├──────────────────────────────────────┤
│                                      │
│  [✅] 📱  Pulsa / Paket Data        │
│  [✅] ⚡  Listrik                    │
│  [  ] 💧  Air (PDAM)                │
│  [  ] 🏠  Kontrakan / Kos           │
│  [  ] 📺  Internet / WiFi           │
│  [  ] 🎓  Uang Sekolah Anak         │
│  [  ] 🔧  Servis Kendaraan Rutin    │
│                                      │
│  Driver centang yang relevan, lalu  │
│  isi nominal masing-masing.         │
│                                      │
├──────────────────────────────────────┤
│  [       Lanjut →        ]          │
└──────────────────────────────────────┘
```

### D. Jadwal Kerja Mingguan

```
┌──────────────────────────────────────┐
│  [←]  Jadwal Kerja                   │
├──────────────────────────────────────┤
│                                      │
│  ──────── Minggu Ini ─────────────  │
│  16-22 Feb 2026                      │
│                                      │
│  Min  Sen  Sel  Rab  Kam  Jum  Sab  │
│  [✅] [✅] [✅] [❌] [✅] [✅] [✅] │
│                                      │
│  6 hari narik · 1 hari libur        │
│  Tap hari untuk toggle narik/libur  │
│                                      │
│  ──────── Minggu Depan ───────────  │
│  23 Feb - 1 Mar 2026                │
│                                      │
│  Min  Sen  Sel  Rab  Kam  Jum  Sab  │
│  [✅] [✅] [✅] [✅] [✅] [✅] [✅] │
│                                      │
│  7 hari narik · 0 hari libur        │
│                                      │
│  ℹ️ Jadwal mempengaruhi target      │
│     harian di Dashboard              │
│                                      │
└──────────────────────────────────────┘
```

### E. Mode Ambisius (tampil di halaman Detail Target milik F007)

```
┌──────────────────────────────────────┐
│  ──────── 🚀 Mode Ambisius ─────── │
│                                      │
│  [ ON ──────○ ]                      │
│                                      │
│  Total sisa hutang: Rp13.850.000    │
│                                      │
│  Lunas dalam:                        │
│  [ 3 ] [ 6 ✦] [ 9 ] [ 12 ] bulan   │
│  atau ketik: [___] bulan            │
│                                      │
│  Cicilan normal:   Rp1.400.000/bln  │
│  Mode ambisius:    Rp2.308.333/bln  │
│  Tambahan/bulan:   +Rp908.333       │
│                                      │
│  Target harian (normal):  Rp75.000  │
│  Target harian (ambisius):Rp116.288 │
│                                      │
│  ℹ️ Mode ini menambah target harian │
│     untuk percepat pelunasan hutang. │
│     Biaya tetap tidak berubah.      │
│                                      │
└──────────────────────────────────────┘
```

---

## 4. Kasus Khusus

| Situasi | Apa yang User Lihat | Apa yang Terjadi |
|---------|---------------------|------------------|
| Belum set biaya tetap sama sekali | Di F007: target hanya dari cicilan hutang. Info: "Tambahkan biaya tetap untuk target lebih akurat" | Fitur tetap jalan, tapi kurang akurat |
| Biaya tetap kosong (tidak punya) | List kosong + tombol "➕ Tambah Biaya Tetap" | Driver mungkin memang tidak punya — bukan error |
| Pertama kali buka Biaya Tetap | Muncul template default (wireframe C) — driver centang yang relevan | Mempercepat setup awal |
| Driver hapus semua biaya tetap | Total jadi Rp0. Target harian hanya dari cicilan hutang | Tidak ada peringatan berlebihan |
| Jadwal belum di-set (default) | Semua hari = narik (✅) | Default paling aman — driver adjust sendiri |
| Driver libur → hari kerja berkurang | Target per hari di F007 naik otomatis | Langsung terlihat efeknya |
| Driver toggle narik di hari yang tadinya libur | Target per hari di F007 turun | Hari kerja bertambah → beban per hari lebih ringan |
| Ganti bulan (tgl 1) | Jadwal mingguan yang belum di-set → default narik semua | Fresh start setiap bulan |
| Mode ambisius aktif + hari libur | Target naik lebih tinggi lagi (double effect) | Driver harus sadar konsekuensinya |
| Mode ambisius — hutang sudah lunas semua | App notifikasi: "🎉 Semua hutang lunas!" + mode ambisius otomatis off | Kembali ke mode normal — trigger dari F006 |
| Mode ambisius aktif tapi tidak ada hutang | Tidak bisa diaktifkan. Pesan: "Tidak ada hutang aktif untuk dipercepat" | Guard — mode ambisius butuh data hutang |

---

## 5. Info Teknis dari User

- Biaya tetap: editable (emoji, nama, nominal, catatan opsional), default template disediakan
- Biaya tetap = angka statis, tidak perlu tracking "sudah bayar atau belum" per item
- Hari kerja: toggle per hari, set per minggu (Minggu–Sabtu), default semua narik
- Tidak ada pop-up otomatis untuk set jadwal — driver buka sendiri
- Mode ambisius (opsional): percepat pelunasan hutang dalam X bulan
- Yang dipercepat hanya hutang (bukan biaya tetap)
- Semua data di F009 dipakai oleh F007 untuk menghitung target harian
- Diakses dari tab "Lain"

---

> Bagian di bawah ini adalah terjemahan teknis dari semua yang sudah kita diskusikan. Bagian ini untuk AI Builder — kamu tidak perlu membaca atau memahaminya.

---

# BAGIAN TEKNIS

---

## 6. Technical Implementation

### 6.1 API Endpoints

Tidak ada API — semua lokal di device.

### 6.2 Database Changes

#### Tabel: `fixed_expenses`

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| emoji | TEXT | Emoji yang dipilih driver (misal: 📱, ⚡) |
| name | TEXT | Nama biaya tetap (misal: "Pulsa / Paket Data") |
| amount | INTEGER | Nominal per bulan dalam Rupiah |
| note | TEXT | Catatan opsional. NULL jika kosong |
| is_active | INTEGER | 1 = aktif, 0 = nonaktif (soft delete) |
| created_at | TEXT (ISO 8601) | Waktu dibuat |
| updated_at | TEXT (ISO 8601) | Waktu terakhir diedit |

#### Tabel: `work_schedules`

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| date | TEXT (YYYY-MM-DD) | Tanggal spesifik |
| is_working | INTEGER | 1 = narik, 0 = libur |
| created_at | TEXT (ISO 8601) | Waktu dibuat |
| updated_at | TEXT (ISO 8601) | Waktu terakhir diedit |

#### Tabel: `ambitious_mode`

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key (singleton — hanya 1 record) |
| is_active | INTEGER | 1 = aktif, 0 = nonaktif |
| target_months | INTEGER | Target lunas dalam X bulan |
| activated_at | TEXT (ISO 8601) | Waktu diaktifkan. NULL jika nonaktif |
| deactivated_reason | TEXT | MANUAL (driver matikan sendiri), AUTO_ALL_PAID_OFF (semua hutang lunas — trigger dari F006), NULL jika aktif |
| updated_at | TEXT (ISO 8601) | Waktu terakhir diedit |

<!-- P7-FIX: Tambah field deactivated_reason untuk membedakan auto-off vs manual off -->

### 6.3 Business Logic

1. **Biaya Tetap — CRUD:**
   - Tambah: INSERT ke `fixed_expenses` dengan is_active = 1
   - Edit: UPDATE emoji, name, amount, note, updated_at
   - Hapus: soft delete → SET is_active = 0
   - List: SELECT WHERE is_active = 1 ORDER BY created_at ASC
   - Total: SUM(amount) FROM fixed_expenses WHERE is_active = 1

2. **Biaya Tetap — Template Default:**
   - Saat pertama kali buka (belum ada record di `fixed_expenses`), tampilkan template
   - Template berisi 7 item default: Pulsa, Listrik, Air, Kontrakan, Internet, Sekolah Anak, Servis Kendaraan
   - Driver centang yang relevan → app INSERT ke `fixed_expenses` untuk setiap item yang dicentang
   - Setelah template dipakai, tidak tampil lagi (cek: COUNT(*) FROM fixed_expenses > 0, termasuk yang is_active = 0)

3. **Biaya Tetap — Kapan Dianggap "Dibayar":**
   - Biaya tetap tidak punya mekanisme "bayar" seperti hutang
   - **Pendekatan implementasi: sederhana** — biaya tetap = angka statis yang masuk kewajiban bulanan
   - Ketika driver bayar listrik/pulsa/dll, itu tercatat sebagai pengeluaran harian (F004) → mengurangi profit → secara alami mengurangi kemampuan bayar kewajiban lain
   - Tidak perlu tracking "sudah bayar atau belum" per item biaya tetap

4. **Jadwal Kerja — Toggle:**
   - Toggle hari: INSERT OR REPLACE ke `work_schedules` dengan date = tanggal spesifik
   - Jika tidak ada record untuk suatu tanggal → default is_working = 1 (narik)
   - Tampilkan 2 minggu: minggu ini + minggu depan
   - Sisa hari kerja (dipakai F007): COUNT hari dari besok sampai akhir bulan WHERE is_working = 1 (atau tidak ada record = default narik)

5. **Jadwal Kerja — Reset Bulanan:**
   - Setiap tanggal 1: jadwal yang belum di-set → default narik semua
   - Record bulan lalu tetap tersimpan (tidak dihapus), tapi tidak dipakai untuk perhitungan bulan ini

6. **Mode Ambisius — Aktivasi:**
   - Saat diaktifkan: INSERT OR UPDATE `ambitious_mode` SET is_active = 1, target_months = X, activated_at = NOW, deactivated_reason = NULL
   - Saat dinonaktifkan manual: UPDATE SET is_active = 0, deactivated_reason = 'MANUAL'
   - Guard: tidak bisa diaktifkan jika tidak ada hutang aktif (COUNT(*) FROM debts WHERE status = 'ACTIVE' = 0) → tampilkan pesan: "Tidak ada hutang aktif untuk dipercepat"

7. **Mode Ambisius — Perhitungan:**
   - Cicilan Ambisius = SUM(remaining_amount) FROM debts WHERE status = 'ACTIVE' ÷ target_months
   - Jika Cicilan Ambisius < Total Cicilan Normal → gunakan Total Cicilan Normal (tidak boleh lebih rendah)
   - Kewajiban Ambisius = MAX(Cicilan Ambisius, Total Cicilan Normal) + Total Biaya Tetap
   - Data ini dibaca oleh F007 untuk menghitung target harian

8. **Mode Ambisius — Auto-off (Dual Mechanism):**

   <!-- P7-FIX: Mekanisme auto-off sekarang dual: event-driven dari F006 + reactive saat data dibaca -->

   Mode ambisius HARUS otomatis nonaktif saat semua hutang sudah lunas. Ada **2 mekanisme** yang bekerja bersama (defense in depth):

   **Mekanisme A — Event-Driven Trigger dari F006 (PRIMARY):**
   - F006 memanggil function/helper milik F009 setiap kali ada hutang yang berubah status ke PAID_OFF atau DELETED (lihat F006 6.3 poin 11)
   - Function ini mengecek: `COUNT(*) FROM debts WHERE status = 'ACTIVE'`
   - Jika = 0 → UPDATE `ambitious_mode` SET is_active = 0, deactivated_reason = 'AUTO_ALL_PAID_OFF', updated_at = NOW
   - Tampilkan notifikasi: "🎉 Semua hutang lunas! Mode ambisius otomatis dinonaktifkan."

   **Mekanisme B — Reactive Check saat F007 Baca Data (FALLBACK):**
   - Setiap kali F007 membaca data dari F009 untuk hitung target harian (saat dashboard refresh), F007 juga mengecek konsistensi:
     - Jika `ambitious_mode.is_active = 1` DAN `COUNT(*) FROM debts WHERE status = 'ACTIVE' = 0` → auto-fix: SET is_active = 0, deactivated_reason = 'AUTO_ALL_PAID_OFF'
   - Ini safety net jika trigger F006 gagal (misal: race condition, crash di tengah proses)
   - Tidak menampilkan notifikasi (karena mungkin sudah ditampilkan oleh Mekanisme A)

   **Kenapa perlu 2 mekanisme:**
   - Mekanisme A: responsif, langsung tereksekusi saat hutang lunas → driver langsung tahu
   - Mekanisme B: safety net, memastikan state selalu konsisten meskipun trigger gagal

   **Function signature yang harus di-expose oleh F009:**
   ```
   checkAndDeactivateAmbitiousMode(): boolean
   // Returns true jika mode ambisius berhasil dinonaktifkan
   // Returns false jika masih ada hutang aktif (mode tetap jalan)
   // Dipanggil oleh F006 (Mekanisme A) dan F007 (Mekanisme B)
   ```

9. **Data yang Disediakan untuk F007:**
   - Total Biaya Tetap: SUM(amount) FROM fixed_expenses WHERE is_active = 1
   - Sisa Hari Kerja: COUNT hari kerja dari besok sampai akhir bulan (dari `work_schedules`)
   - Mode Ambisius: is_active, target_months (dari `ambitious_mode`)
   - Definisi Kewajiban Bulanan:
     - Kewajiban Normal = Total Cicilan Hutang (dari F006) + Total Biaya Tetap
     - Kewajiban Ambisius = MAX(Cicilan Ambisius, Total Cicilan Hutang) + Total Biaya Tetap

### 6.4 External Dependencies

Tidak ada. Semua lokal di device.

---

## 7. Acceptance Criteria

| # | Kriteria | Test Method |
|---|----------|-------------|
| 1 | Driver bisa tambah biaya tetap dengan emoji, nama, nominal, catatan | Manual |
| 2 | Driver bisa edit biaya tetap (emoji, nama, nominal, catatan) | Manual |
| 3 | Driver bisa hapus biaya tetap (soft delete) | Manual |
| 4 | Template default tersedia saat pertama kali buka Biaya Tetap | Manual |
| 5 | Template tidak muncul lagi setelah pernah dipakai | Manual |
| 6 | Total biaya tetap dihitung otomatis dari semua item aktif | Unit |
| 7 | Driver bisa toggle hari kerja/libur per hari (Minggu–Sabtu) | Manual |
| 8 | Default semua hari = narik | Manual |
| 9 | Jadwal menampilkan 2 minggu (minggu ini + minggu depan) | Manual |
| 10 | Libur → sisa hari kerja berkurang → target F007 naik | Integration |
| 11 | Toggle narik di hari libur → sisa hari kerja bertambah → target F007 turun | Integration |
| 12 | Reset bulanan: jadwal yang belum di-set default narik semua | Integration |
| 13 | Mode ambisius: driver bisa set target lunas dalam X bulan | Manual |
| 14 | Mode ambisius: hanya hutang yang dipercepat, biaya tetap tidak berubah | Unit |
| 15 | Mode ambisius: tidak bisa diaktifkan jika tidak ada hutang aktif | Unit |
| 16 | Mode ambisius: otomatis off saat semua hutang lunas (via trigger F006) | Integration |
| 17 | Mode ambisius: otomatis off via reactive check saat F007 baca data (fallback) | Integration |
| 18 | Mode ambisius: deactivated_reason tercatat dengan benar (MANUAL vs AUTO_ALL_PAID_OFF) | Unit |
| 19 | Function `checkAndDeactivateAmbitiousMode()` bisa dipanggil oleh F006 dan F007 | Integration |
| 20 | Data biaya tetap, jadwal kerja, dan mode ambisius bisa dibaca oleh F007 | Integration |
| 21 | Semua data berfungsi offline | Manual |

<!-- P7-FIX: Criteria #16 dipertegas (via trigger F006), #17-19 baru untuk dual mechanism -->

---

## 8. Dependencies

- **Bergantung pada:**
  - **F006 (Manajemen Hutang):** Data cicilan hutang (total cicilan per bulan, sisa hutang) untuk perhitungan kewajiban dan mode ambisius. F006 juga memanggil `checkAndDeactivateAmbitiousMode()` milik F009 saat hutang PAID_OFF atau DELETED (lihat F006 6.3 poin 11).
- **Meng-expose function untuk dipanggil oleh:**
  - **F006:** `checkAndDeactivateAmbitiousMode()` — dipanggil setiap kali hutang berubah status (event-driven auto-off)
  - **F007:** `checkAndDeactivateAmbitiousMode()` — dipanggil saat baca data sebagai safety net (reactive auto-off)
- **Dibutuhkan oleh:**
  - **F007 (Target Harian):** Data biaya tetap, jadwal kerja, dan mode ambisius untuk menghitung target harian
  - **F008 (AI Chat):** Data biaya tetap, jadwal kerja, dan mode ambisius untuk analisa dan rekomendasi AI

<!-- P7-FIX: Section 8 sekarang mencatat function yang di-expose dan siapa pemanggilnya -->
"""

with open("F009-kewajiban-jadwal.md", "w", encoding="utf-8") as f:
    f.write(f009_content)

print(f"✅ F009-kewajiban-jadwal.md berhasil digenerate ({len(f009_content):,} karakter)")
print()
print("=== RINGKASAN PERUBAHAN P7 di F009 ===")
print()
print("1. [6.2] ambitious_mode: Tambah field `deactivated_reason`")
print("   → Membedakan MANUAL (driver matikan) vs AUTO_ALL_PAID_OFF (semua hutang lunas)")
print()
print("2. [6.3 poin 6] Mode Ambisius Aktivasi:")
print("   → Saat aktif: deactivated_reason = NULL")
print("   → Saat manual off: deactivated_reason = 'MANUAL'")
print()
print("3. [6.3 poin 8] Mode Ambisius Auto-off — REWRITE TOTAL:")
print("   → Mekanisme A (PRIMARY): Event-driven trigger dari F006")
print("   → Mekanisme B (FALLBACK): Reactive check saat F007 baca data")
print("   → Defense in depth — jika trigger gagal, safety net tetap jaga konsistensi")
print("   → Function signature: checkAndDeactivateAmbitiousMode(): boolean")
print()
print("4. [7] Acceptance Criteria:")
print("   → #16 dipertegas: via trigger F006")
print("   → #17 BARU: reactive check via F007 (fallback)")
print("   → #18 BARU: deactivated_reason tercatat benar")
print("   → #19 BARU: function bisa dipanggil oleh F006 dan F007")
print()
print("5. [8] Dependencies:")
print("   → Tambah section 'Meng-expose function': siapa memanggil apa")
print("   → F006 dan F007 sebagai caller checkAndDeactivateAmbitiousMode()")
