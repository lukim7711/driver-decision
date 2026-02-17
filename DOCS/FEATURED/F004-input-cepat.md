# F004: Input Cepat

## Deskripsi
Fitur untuk mencatat pengeluaran dan pemasukan di luar Shopee dengan cepat — cukup tap kategori, tap nominal, tersimpan. Target: di bawah 3 detik per pencatatan. Ada 2 tab: **Pengeluaran** (bensin, makan, rokok, dll) dan **Pemasukan** (tip cash, transfer masuk, kerja sampingan, dll). Kategori dan nominal preset bisa diedit sesuai kebiasaan driver. Mendukung pencatatan beruntun untuk catat banyak transaksi sekaligus di akhir hari.

## User Story
> Sebagai driver, saya ingin mencatat pengeluaran dan pemasukan di luar Shopee secepat mungkin tanpa mengetik, agar saya tahu persis berapa uang yang keluar dan masuk setiap hari.

## Referensi
- Implements **F004** dari PRD.md

---

# BAGIAN USER

---

## 1. Tentang Fitur Ini

Fitur ini gunanya untuk **catat semua pengeluaran dan pemasukan di luar Shopee** — kayak bensin, makan, rokok, parkir, tip cash, dll.

Caranya simpel: **tap kategori → tap nominal → tersimpan**. Tidak perlu ketik apa-apa (kecuali mau kasih catatan).

---

## 2. Alur Penggunaan

### Setup Kategori & Nominal (Opsional — 1x atau kapan saja)
1. Driver buka app → masuk **Settings/Pengaturan → Kategori Input Cepat**
2. Muncul 2 tab: **Pengeluaran** dan **Pemasukan**
3. Masing-masing tab punya list kategori default
4. Driver bisa **tambah** kategori baru, **hapus** yang tidak dipakai, **rename**, **ganti icon/emoji**
5. Tap salah satu kategori → muncul deretan nominal preset-nya
6. Driver bisa **edit nominal preset** per kategori
7. Simpan → selesai

### Pencatatan Pengeluaran Harian (Beruntun)
1. Driver tap tombol ➕ atau "Catat" di dashboard
2. Muncul layar **Input Cepat** dengan 2 tab: **Pengeluaran** dan **Pemasukan**
3. Default aktif di tab **Pengeluaran** → muncul grid kategori pengeluaran
4. Driver tap 1 kategori (misal: 🍚 **Makan**)
5. Muncul deretan nominal preset untuk kategori Makan (misal: 10rb, 15rb, 20rb, 25rb) + tombol "Ketik nominal lain"
6. Driver tap 1 nominal (misal: Rp15.000)
7. Muncul field catatan kecil (opsional) — driver bisa ketik "Warteg Citra Garden" atau langsung skip
8. Tersimpan → muncul konfirmasi singkat: ✅ *Makan Rp15.000*
9. **Langsung muncul grid kategori lagi** → driver bisa catat yang berikutnya
10. Tap ⛽ Bensin → tap Rp30.000 → skip catatan → tersimpan
11. Tap 🚬 Rokok → tap Rp25.000 → tersimpan
12. Sudah selesai? Tap ← atau "Selesai" di pojok atas → balik ke dashboard

### Pencatatan Pemasukan di Luar Shopee
1. Driver tap tombol ➕ di dashboard → masuk layar Input Cepat
2. Tap tab **Pemasukan** → muncul grid kategori pemasukan
3. Driver tap 💵 **Tip Cash** → tap Rp20.000 → catatan "Pelanggan Tangerang" → tersimpan
4. Bisa lanjut catat pemasukan lain, atau pindah ke tab Pengeluaran

### Pencatatan dengan Nominal Custom
1. Driver tap kategori (misal: Bensin)
2. Nominal yang diinginkan tidak ada di preset
3. Tap "Ketik nominal lain"
4. Muncul numpad besar (tombol angka + tombol "000" + tombol hapus)
5. Driver ketik: 3 → 7 → 000 → muncul "Rp37.000"
6. Tap Simpan → tersimpan

**Total interaksi per pencatatan:** 3 tap (kategori → nominal → auto-simpan) = di bawah 3 detik. Dengan catatan: 3 tap + ketik singkat ≈ 5 detik. Dengan custom nominal: 4-5 tap numpad ≈ 6 detik.

---

## 3. Tampilan Layar

### A. Tab Pengeluaran (default saat buka Input Cepat)

```
┌─────────────────────────────────┐
│  Input Cepat                    │
│  [Pengeluaran] [Pemasukan]      │
│                                 │
│  ⛽         🍚        🚬       │
│  Bensin    Makan     Rokok      │
│                                 │
│  🅿️        🥤        📦       │
│  Parkir    Minum     Lainnya    │
│                                 │
│  Hari ini: 3 pengeluaran        │
│  Rp70.000                       │
└─────────────────────────────────┘
```

### B. Tab Pemasukan

```
┌─────────────────────────────────┐
│  Input Cepat                    │
│  [Pengeluaran] [Pemasukan]      │
│                                 │
│  💵         💸        🔧       │
│  Tip Cash  Transfer   Kerja     │
│            Masuk      Samping-  │
│                       an        │
│  📦                             │
│  Lainnya                        │
│                                 │
│  Hari ini: 1 pemasukan          │
│  Rp20.000                       │
└─────────────────────────────────┘
```

### C. Pilih Nominal (setelah tap kategori — sama untuk pengeluaran & pemasukan)

```
┌─────────────────────────────────┐
│  🍚 Makan — Berapa?             │
│                                 │
│  [10.000] [15.000] [20.000]     │
│  [25.000] [30.000] [50.000]     │
│                                 │
│  [Ketik nominal lain]           │
│                                 │
│  Catatan (opsional): _______    │
└─────────────────────────────────┘
```

### D. Numpad Custom Nominal (setelah tap "Ketik nominal lain")

```
┌─────────────────────────────────┐
│  🍚 Makan — Berapa?             │
│  Rp 37.000                      │
│                                 │
│  [1] [2] [3]                    │
│  [4] [5] [6]                    │
│  [7] [8] [9]                    │
│  [000] [0] [⌫]                 │
│                                 │
│  Catatan (opsional): _______    │
│  [Simpan]                       │
└─────────────────────────────────┘
```

### E. Konfirmasi tersimpan (muncul singkat, lalu kembali ke grid)

```
✅ Makan Rp15.000 — Warteg Citra Garden
```

### F. Settings: Kelola Kategori (2 tab)

```
┌─────────────────────────────────┐
│  Kategori Input Cepat           │
│  [Pengeluaran] [Pemasukan]      │
│                                 │
│  ⛽ Bensin        [Edit]        │
│     Preset: 20rb 30rb 50rb 100rb│
│                                 │
│  🍚 Makan         [Edit]        │
│     Preset: 10rb 15rb 20rb 25rb │
│                                 │
│  🚬 Rokok         [Edit]        │
│     Preset: 15rb 20rb 25rb 30rb │
│                                 │
│  🅿️ Parkir        [Edit]        │
│     Preset: 2rb 3rb 5rb 10rb    │
│                                 │
│  🥤 Minum         [Edit]        │
│     Preset: 5rb 8rb 10rb 15rb   │
│                                 │
│  📦 Lainnya       [Edit]        │
│     Preset: 5rb 10rb 20rb 50rb  │
│                                 │
│  [+ Tambah Kategori Baru]       │
└─────────────────────────────────┘
```

> **Catatan:** Kategori bawaan sistem (🔒 Denda Hutang, 🔒 Pembayaran Hutang) **tidak muncul** di grid input maupun settings. Kategori ini hanya dipakai otomatis oleh fitur Hutang (F006) dan tampilannya hanya terlihat di riwayat transaksi.

### G. Settings: Edit 1 Kategori

```
┌─────────────────────────────────┐
│  Edit Kategori                  │
│                                 │
│  Icon: ⛽ (tap untuk ganti)     │
│  Nama: [Bensin          ]       │
│  Nominal Preset:                │
│  [20.000] [30.000] [50.000]     │
│  [100.000]                      │
│  Tap angka untuk edit, + tambah │
│                                 │
│  [Simpan]  [Hapus]              │
└─────────────────────────────────┘
```

---

## 4. Kasus Khusus

| Situasi | Apa yang User Lihat | Apa yang Terjadi |
|---------|---------------------|------------------|
| Driver tap nominal preset → langsung tersimpan (tanpa catatan) | Konfirmasi tersimpan → kembali ke grid kategori | Data tersimpan tanpa catatan (catatan opsional) |
| Driver ketik nominal 0 di numpad | Tombol "Simpan" disabled (abu-abu) | Tidak bisa simpan nominal 0 |
| Driver tap tombol Simpan 2x cepat (numpad) | Tombol disabled setelah tap pertama, muncul loading singkat | Mencegah duplikat pencatatan |
| Driver tidak catat apa pun hari ini | Tidak ada peringatan atau paksaan | Di dashboard, pengeluaran dan pemasukan lain = Rp0 |
| Driver hapus semua kategori di 1 tab | Minimal 1 kategori harus ada per tab. Muncul pesan "Minimal 1 kategori" | Kategori terakhir tidak bisa dihapus |
| Driver tambah kategori baru tanpa preset nominal | Default preset 5rb, 10rb, 20rb, 50rb otomatis ditambahkan | Driver bisa edit setelahnya |
| Driver ingin edit/hapus transaksi yang sudah tercatat | Di dashboard atau halaman riwayat → tap item → pilih Edit atau Hapus | Data bisa diubah atau dihapus kapan saja |
| Internet mati saat catat | Tidak ada pengaruh — semua lokal di HP | Data tersimpan di HP |
| Driver pindah tab Pengeluaran → Pemasukan saat sedang input | Grid berganti sesuai tab yang aktif. Data yang belum di-simpan tidak hilang (jika sedang di numpad) | Smooth transition antar tab |
| Ada transaksi dari fitur lain (denda hutang, bayar hutang) di riwayat | Muncul dengan label kategori yang sesuai (🔒 Denda Hutang / 🔒 Pembayaran Hutang) | Transaksi ini dibuat otomatis oleh F006 — driver tidak input manual, tapi terlihat di riwayat |

---

## 5. Info Teknis dari User

- Driver biasanya catat di akhir hari (kumpulin dulu, catat sekaligus) → alur beruntun penting
- Kategori dan nominal preset harus bisa diedit oleh driver (baik pengeluaran maupun pemasukan)
- Target: di bawah 3 detik per pencatatan (preset), 6 detik untuk custom nominal
- Catatan per transaksi bersifat opsional
- Pemasukan di luar Shopee yang umum: tip cash, transfer masuk, kerja sampingan

---

> Bagian di bawah ini adalah terjemahan teknis dari semua yang sudah kita diskusikan. Bagian ini untuk AI Builder — kamu tidak perlu membaca atau memahaminya.

---

# BAGIAN TEKNIS

---

## 6. Technical Implementation

### 6.1 API Endpoints

Tidak ada API — semua lokal di device

### 6.2 Database Changes

#### Tabel baru: `quick_entries`

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| type | TEXT | `EXPENSE` atau `INCOME` |
| category_id | TEXT | FK → quick_entry_categories.id — Kategori transaksi |
| amount | INTEGER | Nominal dalam Rupiah (tanpa desimal). Input "Rp 30.000" → simpan `30000` |
| note | TEXT | Catatan opsional (misal: "Shell Daan Mogot"). NULL jika kosong |
| entry_date | TEXT | YYYY-MM-DD — Tanggal transaksi (default: hari ini) |
| entry_time | TEXT | HH:MM — Waktu pencatatan |
| is_deleted | INTEGER | 0 = aktif, 1 = soft deleted |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |
| updated_at | TEXT (ISO 8601) | Waktu terakhir diedit |

#### Tabel baru: `quick_entry_categories`

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| type | TEXT | `EXPENSE` atau `INCOME` |
| name | TEXT | Nama kategori (misal: "Bensin", "Tip Cash") |
| icon | TEXT | Emoji/icon kategori (misal: ⛽, 💵) |
| sort_order | INTEGER | Urutan tampil di grid (0, 1, 2, ...) |
| is_default | INTEGER | 1 = kategori bawaan app, 0 = buatan driver |
| is_system | INTEGER | 1 = kategori sistem (dibuat oleh fitur lain, tidak muncul di grid input, tidak bisa diedit/hapus driver), 0 = kategori biasa |
| is_active | INTEGER | 1 = aktif (tampil di grid), 0 = dihapus (soft delete) |
| created_at | TEXT (ISO 8601) | Waktu dibuat |
| updated_at | TEXT (ISO 8601) | Waktu terakhir diedit |

<!-- P5-FIX: Tambah field is_system untuk membedakan kategori sistem (Denda Hutang, Pembayaran Hutang) yang dibuat F006 dan tidak boleh diedit/dihapus driver -->

#### Tabel baru: `quick_entry_presets`

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| category_id | TEXT | FK → quick_entry_categories.id — Kategori yang memiliki preset ini |
| amount | INTEGER | Nominal preset dalam Rupiah |
| sort_order | INTEGER | Urutan tampil (0, 1, 2, ...) |
| created_at | TEXT (ISO 8601) | Waktu dibuat |

#### Data seed default saat install

**Kategori pengeluaran default (`is_default = 1, is_system = 0`):**

| Nama | Icon | Preset Nominal |
|------|------|----------------|
| Bensin | ⛽ | 5000, 10000, 15000, 20000, 25000, 30000, 50000 |
| Makan | 🍚 | 5000, 10000, 15000, 20000, 25000, 30000, 50000 |
| Rokok | 🚬 | 5000, 10000, 15000, 20000, 25000, 30000, 50000 |
| Parkir | 🅿️ | 5000, 10000, 15000, 20000, 25000, 30000, 50000 |
| Minum | 🥤 | 5000, 10000, 15000, 20000, 25000, 30000, 50000 |
| Lainnya | 📦 | 5000, 10000, 15000, 20000, 25000, 30000, 50000 |

**Kategori pemasukan default (`is_default = 1, is_system = 0`):**

| Nama | Icon | Preset Nominal |
|------|------|----------------|
| Tip Cash | 💵 | 5000, 10000, 15000, 20000, 25000, 30000, 50000 |
| Transfer Masuk | 💸 | 50000, 100000, 200000, 500000, 1000000 |
| Kerja Sampingan | 🔧 | 50000, 100000, 150000, 200000, 300000, 500000 |
| Lainnya | 📦 | 5000, 10000, 20000, 50000, 100000 |

**Kategori sistem (`is_system = 1, is_default = 0`) — tidak muncul di grid, tidak bisa diedit/dihapus driver:**

<!-- P5-FIX: Kategori sistem baru untuk integrasi dengan F006 Manajemen Hutang -->

| Nama | Icon | Type | Preset | Keterangan |
|------|------|------|--------|------------|
| Denda Hutang | 🔒 | EXPENSE | *(tidak ada)* | Dipakai oleh F006 saat driver bayar denda pinjol/paylater. INSERT ke quick_entries otomatis. |
| Pembayaran Hutang | 🔒 | EXPENSE | *(tidak ada)* | Dipakai oleh F006 saat driver bayar hutang personal dan pilih "masuk pengeluaran". INSERT ke quick_entries otomatis. |

### 6.3 Business Logic

1. **Pencatatan Beruntun**
   - Setelah entry tersimpan, UI kembali ke grid kategori (tetap di tab yang sama)
   - Counter di bawah grid update real-time
     - Tab Pengeluaran: "Hari ini: X pengeluaran — RpYYY"
     - Tab Pemasukan: "Hari ini: X pemasukan — RpYYY"
   - Tombol ← atau "Selesai" untuk keluar ke dashboard

2. **Penyimpanan**
   - Saat driver tap nominal preset → langsung INSERT ke `quick_entries` tanpa konfirmasi tambahan
   - Saat driver pakai numpad → INSERT setelah tap "Simpan"
   - Nominal disimpan sebagai INTEGER dalam Rupiah (input "Rp 30.000" → simpan `30000`)
   - Field `type` diisi berdasarkan tab aktif: `EXPENSE` atau `INCOME`
   - Catatan bersifat opsional — jika driver tidak isi, simpan NULL

3. **Validasi**
   - Nominal harus > 0. Jika 0 → tombol "Simpan" disabled
   - Kategori minimal 1 harus ada per tab (EXPENSE dan INCOME masing-masing minimal 1) — yang dihitung hanya kategori non-sistem (`is_system = 0`)
   - Nama kategori tidak boleh kosong
   - Preset nominal harus > 0 dan tidak boleh duplikat per kategori

4. **Edit/Hapus Transaksi**
   - Driver bisa edit nominal, kategori, catatan, atau tanggal dari riwayat
   - Driver bisa hapus transaksi → soft delete (`is_deleted = 1`)
   - Edit/hapus langsung update perhitungan di dashboard

5. **Kategori Management**
   - **Kategori biasa** (`is_system = 0`):
     - Tambah → driver isi nama → pilih icon → set preset nominal → pilih type (EXPENSE/INCOME)
     - Edit → ubah nama, icon, atau preset nominal
     - Hapus → soft delete (`is_active = 0`). Transaksi yang sudah tercatat tetap ada
     - Urutan bisa diubah per tab
   - **Kategori sistem** (`is_system = 1`):
     - TIDAK muncul di grid input F004 — query grid: `SELECT ... WHERE is_active = 1 AND is_system = 0`
     - TIDAK muncul di halaman Settings Kelola Kategori
     - TIDAK bisa diedit, dihapus, atau di-rename oleh driver
     - Transaksi yang menggunakan kategori sistem TETAP muncul di riwayat pengeluaran dengan label kategori yang sesuai (misal: "🔒 Denda Hutang — Rp75.000")

<!-- P5-FIX: Aturan baru untuk kategori sistem agar F006 bisa INSERT ke quick_entries dengan category_id yang valid tanpa konflik dengan kategori manual driver -->

6. **Double-Tap Prevention**
   - Setelah tap nominal preset atau tombol "Simpan", disable interaksi selama 500ms
   - Mencegah INSERT duplikat

7. **Dashboard Integration**
   - Pengeluaran dari F004 (`type = EXPENSE`) ditampilkan sebagai **Pengeluaran** di dashboard
   - Pemasukan dari F004 (`type = INCOME`) ditampilkan sebagai **Pemasukan Lain** di dashboard, terpisah dari pendapatan Shopee (F001/F002)
   - Total profit = Pendapatan Shopee + Pemasukan Lain − Pengeluaran
   - Catatan: pengeluaran mencakup SEMUA quick_entries bertipe EXPENSE (termasuk yang di-insert oleh F006 via kategori sistem)

### 6.4 External Dependencies

Tidak ada. Semua lokal di device.

---

## 7. Acceptance Criteria

| # | Kriteria | Test Method |
|---|----------|-------------|
| 1 | Driver bisa catat pengeluaran dalam 3 tap (kategori → nominal → auto-simpan) | Manual |
| 2 | Driver bisa catat pemasukan dalam 3 tap (pindah tab → kategori → nominal) | Manual |
| 3 | Waktu pencatatan dengan preset < 3 detik | Manual |
| 4 | Setelah simpan, langsung kembali ke grid kategori di tab yang sama (alur beruntun) | Manual |
| 5 | Tab Pengeluaran ↔ Pemasukan bisa dipindah dengan smooth | Manual |
| 6 | Numpad custom dengan tombol "000" berfungsi | Manual |
| 7 | Catatan opsional — bisa diisi atau di-skip | Manual |
| 8 | Kategori pengeluaran bisa ditambah, dihapus, di-rename, dan ganti icon | Manual |
| 9 | Kategori pemasukan bisa ditambah, dihapus, di-rename, dan ganti icon | Manual |
| 10 | Preset nominal per kategori bisa diedit (pengeluaran dan pemasukan) | Manual |
| 11 | Minimal 1 kategori non-sistem per tab harus ada (tidak bisa hapus semua) | Unit |
| 12 | Nominal 0 atau negatif ditolak | Unit |
| 13 | Transaksi yang sudah tercatat bisa diedit dan dihapus | Manual |
| 14 | Double-tap tidak menghasilkan duplikat | Integration |
| 15 | Counter di grid update real-time setelah simpan | Manual |
| 16 | Data tersimpan tanpa internet (offline) | Manual |
| 17 | Default kategori + preset (pengeluaran & pemasukan) muncul saat install | Integration |
| 18 | Dashboard: pengeluaran dari F004 masuk "Pengeluaran", pemasukan masuk "Pemasukan Lain" | Integration |
| 19 | Kategori sistem (Denda Hutang, Pembayaran Hutang) ada di database saat install | Integration |
| 20 | Kategori sistem TIDAK muncul di grid input dan Settings | Manual |
| 21 | Kategori sistem TIDAK bisa diedit/dihapus oleh driver | Unit |
| 22 | Transaksi dari kategori sistem muncul di riwayat pengeluaran dengan label yang benar | Integration |

<!-- P5-FIX: Acceptance criteria #19-22 baru untuk validasi kategori sistem -->

---

## 8. Dependencies

- **Tidak bergantung pada fitur lain** (fitur ini berdiri sendiri untuk input data).
- **Tabel `quick_entries` ditulis oleh F006:**
  - F006 (Manajemen Hutang) melakukan INSERT ke `quick_entries` saat driver bayar denda hutang atau bayar hutang personal yang dipilih masuk pengeluaran. F006 menggunakan `category_id` dari kategori sistem (Denda Hutang / Pembayaran Hutang) yang di-seed oleh F004.
- **Dibutuhkan oleh:**
  - **F005 (Dashboard)** — total pengeluaran & pemasukan lain hari ini
  - **F006 (Manajemen Hutang)** — INSERT ke quick_entries untuk pembayaran denda & hutang personal; menggunakan kategori sistem
  - **F007 (Target Harian)** — pengeluaran mempengaruhi profit bersih
  - **F008 (AI Chat)** — data pengeluaran/pemasukan untuk analisa

<!-- P5-FIX: Section 8 sekarang mencatat bahwa F006 menulis ke tabel quick_entries (write-dependency) -->
