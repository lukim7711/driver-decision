# ARCHITECTURE.md

---

## 1. Tech Stack

| Layer | Teknologi | Kenapa Dipilih |
|-------|-----------|----------------|
| **Bahasa & Framework** | Kotlin + Jetpack Compose | Fitur inti app (baca layar Shopee di belakang layar, auto-capture notifikasi) hanya bisa dibangun dengan Android native. Jetpack Compose adalah cara modern membangun tampilan Android — lebih ringkas dan mudah di-maintain. |
| **Database** | Room (SQLite) | Semua data tersimpan di HP. Room adalah library resmi Android untuk database lokal — gratis, cepat, bisa dipakai tanpa internet. Mendukung query kompleks yang dibutuhkan F007 (target harian). |
| **Screen Reading** | Android Accessibility Service | Fitur F001/F002 butuh kemampuan membaca tampilan app Shopee di belakang layar. Accessibility Service adalah satu-satunya cara resmi Android untuk ini. Tidak perlu root, tapi butuh izin khusus dari user saat setup. |
| **OCR (Baca Tulisan dari Gambar)** | Google ML Kit (On-Device) | Membaca teks dari screenshot Shopee (F003). Jalan 100% di HP — bukan Google Cloud, jadi tidak melanggar constraint PRD. Gratis tanpa batas. Akurasi tinggi untuk teks Indonesia dan angka. Fallback: jika ML Kit gagal, gunakan regex/pattern matching dari parsing_patterns. |
| **LLM (AI Chat)** | Groq API (Llama 3.3 70B) | Untuk fitur AI Chat (F008) dan opsional membantu generate parsing patterns (F003). Groq dipilih karena: gratis (free tier 30 req/menit), sangat cepat (<1 detik response), model Llama 3.3 70B cukup pintar untuk analisa keuangan sederhana. Alternatif ditolak: OpenAI (berbayar), Google Gemini (bagus tapi free tier lebih ketat). |
| **Background Processing** | WorkManager + Foreground Service | Auto-capture (F001) butuh jalan terus di belakang layar. Foreground Service menjaga app tetap hidup. WorkManager untuk tugas terjadwal (pengingat F002 jam 23:00, notifikasi F003). |
| **Dependency Injection** | Hilt | Standard Android untuk mengelola dependency antar komponen. Memudahkan testing. |
| **Image Processing** | Android Bitmap API | Untuk memproses screenshot sebelum di-OCR. Crop, resize, enhance — semua pakai API bawaan Android, tidak perlu library tambahan. |

### Teknologi yang TIDAK Dipakai

| Teknologi | Kenapa Tidak |
|-----------|-------------|
| Flutter / React Native | Tidak bisa akses Accessibility Service secara native. Fitur inti app (baca layar Shopee) tidak mungkin dibangun. |
| Firebase | Free tier cukup, tapi app ini 100% lokal — tidak butuh cloud database. Menambah kompleksitas tanpa manfaat. |
| Google Cloud | Dilarang oleh PRD (sulit pendaftaran bagi user). |
| Tesseract OCR | Akurasi lebih rendah dari ML Kit untuk teks campuran Indonesia + angka. Setup lebih rumit. |

### Estimasi Biaya

| Komponen | Biaya |
|----------|-------|
| Kotlin + Jetpack Compose | Gratis |
| Room (SQLite) | Gratis |
| Google ML Kit On-Device | Gratis |
| Groq API Free Tier | Gratis (30 request/menit, cukup untuk 1 user) |
| Android Accessibility Service | Gratis |
| Google Play Store (jika publish) | Rp400.000 sekali bayar (opsional) |
| **Total** | **Gratis** (atau Rp400.000 jika publish ke Play Store) |

---

## 2. Project Structure

```
driver-financial-intelligence/
├── app/
│   ├── src/main/
│   │   ├── java/com/driverfinance/
│   │   │   ├── di/                          # Dependency injection modules
│   │   │   ├── data/                        # Data layer
│   │   │   │   ├── local/                   # Room database
│   │   │   │   │   ├── AppDatabase.kt       # Database class + migrations
│   │   │   │   │   ├── dao/                 # DAO interfaces per tabel
│   │   │   │   │   │   ├── ScreenSnapshotDao.kt
│   │   │   │   │   │   ├── ParsingPatternDao.kt
│   │   │   │   │   │   ├── TripDao.kt
│   │   │   │   │   │   ├── CapturedOrderDao.kt
│   │   │   │   │   │   ├── HistoryTripDao.kt
│   │   │   │   │   │   ├── HistoryDetailDao.kt
│   │   │   │   │   │   ├── MatchedOrderDao.kt
│   │   │   │   │   │   ├── DataReviewDao.kt
│   │   │   │   │   │   ├── AiCorrectionLogDao.kt
│   │   │   │   │   │   ├── QuickEntryCategoryDao.kt
│   │   │   │   │   │   ├── QuickEntryDao.kt
│   │   │   │   │   │   ├── DebtDao.kt
│   │   │   │   │   │   ├── DebtPaymentDao.kt
│   │   │   │   │   │   ├── FixedExpenseDao.kt
│   │   │   │   │   │   ├── WorkScheduleDao.kt
│   │   │   │   │   │   └── AmbitiousModeDao.kt
│   │   │   │   │   └── entity/              # Room entity classes per tabel
│   │   │   │   ├── remote/                  # External API
│   │   │   │   │   └── GroqApiService.kt    # Groq LLM API interface
│   │   │   │   └── repository/              # Repository implementations
│   │   │   │       ├── CaptureRepository.kt         # F001 + F002
│   │   │   │       ├── ExtractionRepository.kt      # F003
│   │   │   │       ├── QuickEntryRepository.kt      # F004
│   │   │   │       ├── DashboardRepository.kt       # F005
│   │   │   │       ├── DebtRepository.kt            # F006
│   │   │   │       ├── TargetRepository.kt          # F007
│   │   │   │       ├── ChatRepository.kt            # F008
│   │   │   │       └── ObligationRepository.kt      # F009
│   │   │   ├── domain/                      # Business logic layer
│   │   │   │   ├── usecase/                 # Use cases per fitur
│   │   │   │   │   ├── capture/             # F001 + F002 use cases
│   │   │   │   │   ├── extraction/          # F003 use cases
│   │   │   │   │   ├── quickentry/          # F004 use cases
│   │   │   │   │   ├── dashboard/           # F005 use cases
│   │   │   │   │   ├── debt/                # F006 use cases
│   │   │   │   │   ├── target/              # F007 use cases (CalculateDailyTargetUseCase)
│   │   │   │   │   ├── chat/                # F008 use cases
│   │   │   │   │   └── obligation/          # F009 use cases
│   │   │   │   └── model/                   # Domain models
│   │   │   ├── ui/                          # Presentation layer (Jetpack Compose)
│   │   │   │   ├── theme/                   # App theme, colors, typography
│   │   │   │   ├── components/              # Shared UI components
│   │   │   │   │   ├── TabBar.kt            # Tab bar 5 tab (Home, Order, AI, Input, Lain)
│   │   │   │   │   ├── HeroNumber.kt        # Hero number + progress bar
│   │   │   │   │   ├── ConfirmDialog.kt     # Dialog konfirmasi standar
│   │   │   │   │   ├── NotificationBell.kt  # Bell + dropdown notifikasi
│   │   │   │   │   └── PresetGrid.kt        # Grid tombol preset (nominal, kategori)
│   │   │   │   ├── screen/                  # Screens per fitur
│   │   │   │   │   ├── dashboard/           # F005 Dashboard
│   │   │   │   │   ├── order/               # Tab Order (list order hari ini)
│   │   │   │   │   ├── chat/                # F008 AI Chat
│   │   │   │   │   ├── quickentry/          # F004 Input Cepat
│   │   │   │   │   ├── debt/                # F006 Manajemen Hutang
│   │   │   │   │   ├── obligation/          # F009 Biaya Tetap + Jadwal
│   │   │   │   │   ├── capture/             # Capture Manager (F001 + F002)
│   │   │   │   │   ├── review/              # F003 Data Perlu Dicek
│   │   │   │   │   ├── target/              # F007 Detail Target
│   │   │   │   │   └── setup/               # Setup Awal (onboarding)
│   │   │   │   └── navigation/              # Navigation graph
│   │   │   └── service/                     # Android Services
│   │   │       ├── CaptureAccessibilityService.kt  # F001+F002 Accessibility Service
│   │   │       ├── CaptureWorker.kt                # Background processing worker
│   │   │       └── ReminderWorker.kt               # Scheduled notifications
│   │   ├── res/                             # Resources (strings, drawables, etc.)
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── docs/                                    # Dokumen spesifikasi (file ini)
│   ├── PRD.md
│   ├── ARCHITECTURE.md
│   ├── CONSTITUTION.md
│   ├── AI-BRIEFING.md
│   ├── PROGRESS.md
│   ├── DECISIONS.md
│   └── features/
│       ├── F001-auto-capture-order-masuk.md
│       ├── F002-capture-riwayat-rincian-pesanan.md
│       ├── F003-ai-ekstraksi-data.md
│       ├── F004-input-cepat.md
│       ├── F005-dashboard-harian.md
│       ├── F006-manajemen-hutang.md
│       ├── F007-target-harian-otomatis.md
│       ├── F008-ai-chat.md
│       └── F009-kewajiban-jadwal.md
└── tests/                                   # Test files (mirror app structure)
```

### Penjelasan Layer

| Layer | Tanggung Jawab |
|-------|---------------|
| **`data/local/`** | Semua yang berhubungan dengan database — entity, DAO, migrations |
| **`data/remote/`** | Hanya Groq API. Satu file saja. |
| **`data/repository/`** | Menghubungkan data layer dengan business logic. Satu repository per fitur. |
| **`domain/usecase/`** | Business logic murni — kalkulasi, validasi, flow data. Setiap fitur punya folder sendiri. |
| **`ui/screen/`** | Tampilan per fitur. Setiap screen baca data via ViewModel → UseCase → Repository → DAO. |
| **`service/`** | Android services untuk background processing (Accessibility Service, WorkManager). |

---

## 3. API Contract

### 3.1 External API (Groq LLM)

Satu-satunya external API yang dipakai.

| Method | Endpoint | Dipakai Oleh | Deskripsi |
|--------|----------|-------------|-----------|
| POST | `https://api.groq.com/openai/v1/chat/completions` | F008 (AI Chat), F003 (Pattern Generation, opsional) | Kirim pesan + context data driver, terima jawaban AI |

**Request Format:**
```json
{
  "model": "llama-3.3-70b-versatile",
  "messages": [
    {"role": "system", "content": "<system_prompt + driver_data_context>"},
    {"role": "user", "content": "<pesan_driver>"},
    {"role": "assistant", "content": "<jawaban_sebelumnya>"},
    ...
  ],
  "max_tokens": 300,
  "temperature": 0.7
}
```

**Response Format:**
```json
{
  "choices": [
    {
      "message": {
        "role": "assistant",
        "content": "<jawaban_AI>"
      }
    }
  ],
  "usage": {
    "prompt_tokens": 1200,
    "completion_tokens": 250,
    "total_tokens": 1450
  }
}
```

**Rate Limit:** 30 requests/menit (free tier). Cukup untuk 1 user.

**Auth:** API key via header `Authorization: Bearer <GROQ_API_KEY>`.

### 3.2 Internal Interfaces (Repository Pattern)

App ini 100% lokal — tidak ada REST API. Semua data diakses via Repository → DAO.

| Repository | Fitur | Fungsi Utama |
|-----------|-------|-------------|
| `CaptureRepository` | F001 + F002 | `saveSnapshot()`, `saveTrip()`, `saveCapturedOrder()`, `saveHistoryTrip()`, `saveHistoryDetail()`, `getSnapshotsByType()`, `getTodayTrips()` |
| `ExtractionRepository` | F003 | `matchOrders()`, `getUnmatchedOrders()`, `getPendingReviews()`, `submitCorrection()`, `getPatterns()`, `savePattern()` |
| `QuickEntryRepository` | F004 | `addEntry()`, `getTodayExpenses()`, `getTodayIncome()`, `getCategories()`, `getCategoryById()` |
| `DashboardRepository` | F005 | `getTodaySummary()` → computed dari F002 + F004 + F007 data |
| `DebtRepository` | F006 | `addDebt()`, `updateDebt()`, `deleteDebt()`, `getActiveDebts()`, `addPayment()`, `calculatePenalty()`, `getTotalMonthlyInstallment()` |
| `TargetRepository` | F007 | `calculateDailyTarget()` → mesin hitung, baca data F004 + F006 + F009 |
| `ChatRepository` | F008 | `sendMessage()` → compile context + call Groq API |
| `ObligationRepository` | F009 | `getFixedExpenses()`, `addFixedExpense()`, `toggleWorkDay()`, `getWorkSchedule()`, `toggleAmbitiousMode()`, `checkAndDeactivateAmbitiousMode()` |

**Catatan penting tentang F007:** F007 TIDAK punya tabel sendiri. `TargetRepository.calculateDailyTarget()` adalah fungsi hitung yang membaca data dari tabel milik F004, F006, dan F009, lalu mengembalikan hasil perhitungan. F005 memanggil fungsi ini untuk mendapatkan angka target harian.

---

## 4. Data Model

Single source of truth untuk semua tabel database. Di-merge dari semua feature specs.

### Tabel dari F001 (Auto Capture Order Masuk)

**Tabel `screen_snapshots`** — Menyimpan semua screenshot/rekaman layar

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| screen_type | TEXT | `ORDER_DETAIL`, `HISTORY_LIST`, `HISTORY_DETAIL` |
| image_path | TEXT | Path file gambar di storage lokal |
| raw_text | TEXT | Teks hasil OCR (nullable) |
| node_tree_json | TEXT | Hierarchy UI dari Accessibility Service (nullable) |
| is_processed | INTEGER | 1 = sudah diproses, 0 = belum |
| captured_at | TEXT (ISO 8601) | Waktu capture |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

**Tabel `parsing_patterns`** — Pola untuk mengenali field di layar Shopee

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| screen_type | TEXT | `ORDER_DETAIL`, `HISTORY_LIST`, `HISTORY_DETAIL` |
| field_name | TEXT | Nama field target (misal `pickup_address`, `total_earning`) |
| pattern_type | TEXT | `REGEX`, `KEYWORD`, `NODE_POSITION` |
| pattern_value | TEXT | Regex string, keyword, atau JSON posisi node |
| accuracy | REAL | 0.0–1.0, dihitung dari validasi |
| is_active | INTEGER | 1 = aktif dipakai, 0 = nonaktif |
| created_at | TEXT (ISO 8601) | Waktu dibuat |
| updated_at | TEXT (ISO 8601) | Waktu terakhir diedit |

**Tabel `trips`** — Trip yang ter-detect dari notifikasi order masuk

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| trip_date | TEXT (YYYY-MM-DD) | Tanggal trip |
| trip_code | TEXT | Kode trip (misal `6VUS`). Nullable |
| service_type | TEXT | `SPX_INSTANT`, `SPX_SAMEDAY`, `SHOPEEFOOD`, `UNKNOWN` |
| source_snapshot_id | TEXT | FK → `screen_snapshots.id` |
| captured_at | TEXT (ISO 8601) | Waktu capture |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

**Tabel `captured_orders`** — Detail order yang ter-capture dari Order Detail screen

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| trip_id | TEXT | FK → `trips.id` |
| order_sn | TEXT | Serial number pesanan (misal `260210BVSAY2V2`) |
| order_id | TEXT | ID panjang pesanan. Nullable |
| pickup_address | TEXT | Alamat pickup lengkap. Nullable |
| delivery_address | TEXT | Alamat delivery lengkap. Nullable |
| seller_name | TEXT | Nama seller/restoran. Nullable |
| parcel_weight | TEXT | Berat paket. Nullable |
| parcel_dimensions | TEXT | Dimensi paket. Nullable |
| payment_method | TEXT | `COD`, `NON_COD`. Nullable |
| payment_amount | INTEGER | Nominal COD dalam Rupiah. Nullable |
| raw_text | TEXT | Teks mentah dari screenshot |
| parse_confidence | REAL | 0.0–1.0 |
| source_snapshot_id | TEXT | FK → `screen_snapshots.id` |
| captured_at | TEXT (ISO 8601) | Waktu capture |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

### Tabel dari F002 (Capture Riwayat Rincian Pesanan)

**Tabel `history_trips`** — Data dari list Riwayat Pesanan

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| linked_trip_id | TEXT | FK → `trips.id`. NULL jika belum tercocokkan |
| trip_date | TEXT (YYYY-MM-DD) | Tanggal trip |
| trip_time | TEXT (HH:MM) | Waktu trip dari list riwayat |
| service_type | TEXT | `SPX_INSTANT`, `SPX_SAMEDAY`, `SHOPEEFOOD`, `UNKNOWN` |
| total_earning | INTEGER | Total pendapatan trip dalam Rupiah |
| is_combined | INTEGER | 1 = Pesanan Gabungan, 0 = tunggal |
| restaurant_name | TEXT | Nama restoran (ShopeeFood). NULL untuk tipe lain |
| raw_text | TEXT | Teks mentah dari list item |
| source_snapshot_id | TEXT | FK → `screen_snapshots.id` |
| captured_at | TEXT (ISO 8601) | Waktu capture |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

**Tabel `history_details`** — Data dari halaman Rincian Pesanan

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| history_trip_id | TEXT | FK → `history_trips.id` |
| linked_order_id | TEXT | FK → `captured_orders.id`. NULL jika belum tercocokkan |
| order_sn | TEXT | Serial number pesanan |
| order_id_long | TEXT | ID panjang pesanan. Nullable |
| delivery_fee | INTEGER | Biaya pengantaran dalam Rupiah |
| total_earning | INTEGER | Total pendapatan dalam Rupiah |
| bonus_type | TEXT | Tipe bonus (misal `Bonus Harian Jabodetabek`). Nullable |
| bonus_points | INTEGER | Jumlah poin bonus |
| cash_compensation | INTEGER | Kompensasi Bayar Tunai dalam Rupiah |
| cash_collected | INTEGER | Tagih Tunai yang Diterima dalam Rupiah |
| order_adjustment | INTEGER | Penyesuaian Pesanan dalam Rupiah |
| time_accepted | TEXT (HH:MM) | Waktu status Diterima |
| time_arrived | TEXT (HH:MM) | Waktu status Tiba |
| time_picked_up | TEXT (HH:MM) | Waktu status Diambil |
| time_completed | TEXT (HH:MM) | Waktu status Selesai |
| raw_text | TEXT | Teks mentah dari halaman rincian |
| parse_confidence | REAL | 0.0–1.0 |
| source_snapshot_id | TEXT | FK → `screen_snapshots.id` |
| captured_at | TEXT (ISO 8601) | Waktu capture |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

### Tabel dari F003 (AI Ekstraksi Data)

**Tabel `matched_orders`** — Hasil pencocokan F001 + F002

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| captured_order_id | TEXT | FK → `captured_orders.id`. NULL jika hanya punya data F002 |
| history_detail_id | TEXT | FK → `history_details.id`. NULL jika hanya punya data F001 |
| match_status | TEXT | `MATCHED`, `F001_ONLY`, `F002_ONLY`, `MISMATCH` |
| match_confidence | REAL | 0.0–1.0 |
| matched_at | TEXT (ISO 8601) | Waktu matching dilakukan |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

**Tabel `data_reviews`** — Data yang perlu dicek driver

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| source_table | TEXT | `captured_orders`, `history_details`, atau `matched_orders` |
| source_id | TEXT | ID record yang bermasalah |
| field_name | TEXT | Nama field yang kurang yakin |
| original_value | TEXT | Nilai yang terbaca AI |
| suggested_value | TEXT | Saran koreksi dari AI. Nullable |
| corrected_value | TEXT | Nilai koreksi dari driver. Nullable |
| confidence | REAL | 0.0–1.0 |
| review_status | TEXT | `PENDING`, `CONFIRMED`, `CORRECTED`, `DISMISSED` |
| reviewed_at | TEXT (ISO 8601) | Waktu driver review. Nullable |
| created_at | TEXT (ISO 8601) | Waktu record dibuat |

**Tabel `ai_corrections_log`** — Log koreksi untuk AI learning

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| data_review_id | TEXT | FK → `data_reviews.id` |
| pattern_id | TEXT | FK → `parsing_patterns.id`. Nullable |
| original_value | TEXT | Nilai sebelum koreksi |
| corrected_value | TEXT | Nilai setelah koreksi |
| correction_type | TEXT | `AMOUNT`, `ADDRESS`, `SERVICE_TYPE`, `ORDER_CODE`, `TIMELINE`, `OTHER` |
| applied_to_pattern | INTEGER | 1 = sudah dipakai improve pattern, 0 = belum |
| created_at | TEXT (ISO 8601) | Waktu koreksi |

### Tabel dari F004 (Input Cepat)

**Tabel `quick_entry_categories`** — Kategori pengeluaran/pemasukan

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| name | TEXT | Nama kategori (misal `Bensin`, `Makan`) |
| emoji | TEXT | Emoji kategori (misal ⛽, 🍔) |
| type | TEXT | `EXPENSE` atau `INCOME` |
| is_system | INTEGER | 1 = kategori sistem (tidak bisa diedit/hapus), 0 = kategori user |
| sort_order | INTEGER | Urutan tampil di grid |
| is_active | INTEGER | 1 = aktif, 0 = nonaktif (soft delete) |
| created_at | TEXT (ISO 8601) | Waktu dibuat |
| updated_at | TEXT (ISO 8601) | Waktu terakhir diedit |

**Pre-loaded categories (di-insert saat pertama kali install):**

| Name | Emoji | Type | is_system |
|------|-------|------|-----------|
| Bensin | ⛽ | EXPENSE | 0 |
| Makan | 🍔 | EXPENSE | 0 |
| Rokok | 🚬 | EXPENSE | 0 |
| Parkir | 🅿️ | EXPENSE | 0 |
| Pulsa | 📱 | EXPENSE | 0 |
| Lainnya | 📦 | EXPENSE | 0 |
| Tip Cash | 💵 | INCOME | 0 |
| Transfer Masuk | 🏦 | INCOME | 0 |
| Kerja Sampingan | 🔨 | INCOME | 0 |
| Lainnya | 💰 | INCOME | 0 |
| Cicilan Hutang | 🏧 | EXPENSE | 1 |
| Denda Hutang | ⚠️ | EXPENSE | 1 |

**Tabel `quick_entries`** — Pencatatan pengeluaran/pemasukan

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| category_id | TEXT | FK → `quick_entry_categories.id` |
| type | TEXT | `EXPENSE` atau `INCOME` |
| amount | INTEGER | Nominal dalam Rupiah |
| note | TEXT | Catatan opsional. Nullable |
| entry_date | TEXT (YYYY-MM-DD) | Tanggal pencatatan |
| entry_time | TEXT (HH:MM) | Waktu pencatatan |
| created_at | TEXT (ISO 8601) | Waktu dibuat |

### Tabel dari F006 (Manajemen Hutang)

**Tabel `debts`** — Data hutang aktif

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| name | TEXT | Nama hutang (misal `Cicilan Motor Honda BeAT`) |
| debt_type | TEXT | `FIXED_INSTALLMENT`, `PINJOL_PAYLATER`, `PERSONAL` |
| original_amount | INTEGER | Total hutang awal dalam Rupiah |
| remaining_amount | INTEGER | Sisa hutang saat ini dalam Rupiah |
| monthly_installment | INTEGER | Cicilan per bulan dalam Rupiah. NULL jika personal (fleksibel) |
| due_date_day | INTEGER | Tanggal jatuh tempo per bulan (1-31). NULL jika tidak ada |
| penalty_type | TEXT | `DAILY`, `MONTHLY`, `NONE`. Default `NONE` |
| penalty_amount | INTEGER | Nominal denda per hari/bulan dalam Rupiah. 0 jika tidak ada |
| note | TEXT | Catatan opsional. Nullable |
| status | TEXT | `ACTIVE`, `PAID_OFF`, `DELETED` |
| paid_off_at | TEXT (ISO 8601) | Tanggal lunas. Nullable |
| created_at | TEXT (ISO 8601) | Waktu dibuat |
| updated_at | TEXT (ISO 8601) | Waktu terakhir diedit |

**Tabel `debt_payments`** — Riwayat pembayaran hutang

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| debt_id | TEXT | FK → `debts.id` |
| amount | INTEGER | Nominal pembayaran dalam Rupiah |
| payment_type | TEXT | `INSTALLMENT`, `PENALTY`, `PARTIAL` |
| include_as_expense | INTEGER | 1 = masuk pengeluaran harian, 0 = tidak |
| category_id | TEXT | FK → `quick_entry_categories.id`. Kategori saat INSERT ke quick_entries |
| linked_expense_id | TEXT | FK → `quick_entries.id`. NULL jika include_as_expense = 0 |
| payment_date | TEXT (YYYY-MM-DD) | Tanggal pembayaran |
| note | TEXT | Catatan opsional. Nullable |
| created_at | TEXT (ISO 8601) | Waktu dibuat |

### Tabel dari F009 (Kewajiban & Jadwal)

**Tabel `fixed_expenses`** — Biaya tetap bulanan

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| emoji | TEXT | Emoji yang dipilih driver |
| name | TEXT | Nama biaya tetap (misal `Pulsa Paket Data`) |
| amount | INTEGER | Nominal per bulan dalam Rupiah |
| note | TEXT | Catatan opsional. Nullable |
| is_active | INTEGER | 1 = aktif, 0 = nonaktif (soft delete) |
| created_at | TEXT (ISO 8601) | Waktu dibuat |
| updated_at | TEXT (ISO 8601) | Waktu terakhir diedit |

**Tabel `work_schedules`** — Jadwal kerja per hari

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| date | TEXT (YYYY-MM-DD) | Tanggal spesifik |
| is_working | INTEGER | 1 = narik, 0 = libur |
| created_at | TEXT (ISO 8601) | Waktu dibuat |
| updated_at | TEXT (ISO 8601) | Waktu terakhir diedit |

**Tabel `ambitious_mode`** — Mode Ambisius

| Field | Type | Deskripsi |
|-------|------|-----------|
| id | TEXT (UUID) | Primary key |
| is_active | INTEGER | 1 = aktif, 0 = nonaktif |
| target_months | INTEGER | Target lunas dalam X bulan |
| activated_at | TEXT (ISO 8601) | Waktu diaktifkan. Nullable |
| deactivated_reason | TEXT | `MANUAL`, `ALL_DEBTS_PAID`, `NO_ACTIVE_DEBTS`. Nullable |
| updated_at | TEXT (ISO 8601) | Waktu terakhir diedit |

### Catatan tentang F005 dan F007

- **F005 (Dashboard)** tidak punya tabel sendiri. Dashboard menampilkan data computed dari tabel milik F001, F002, F003, F004, F007, F009.
- **F007 (Target Harian)** tidak punya tabel sendiri. F007 adalah mesin hitung (computation engine) yang membaca data dari F004, F006, F009, lalu mengembalikan angka target via `TargetRepository.calculateDailyTarget()`.
- **F008 (AI Chat)** tidak punya tabel sendiri. Chat history hanya disimpan di memory selama sesi aktif, tidak di database.

### Relasi Antar Tabel

```
screen_snapshots ←── trips ←── captured_orders ──→ matched_orders ←── history_details ──→ history_trips
       ↑                              ↓                    ↓                                     ↑
  parsing_patterns              data_reviews ──→ ai_corrections_log                        screen_snapshots

quick_entry_categories ←── quick_entries
                       ←── debt_payments ──→ debts

fixed_expenses (standalone)
work_schedules (standalone)
ambitious_mode (standalone, 1 record)
```

---

## 5. Deployment Architecture

### Runtime Environment

| Komponen | Di mana |
|----------|---------|
| App | HP Android driver (Android 8.0+, API 26+) |
| Database | SQLite di storage internal HP |
| Screenshot storage | Storage internal HP (`/data/data/<package>/files/captures/`) |
| AI Processing (LLM) | Groq cloud API (saat butuh AI Chat atau generate pattern) |
| OCR Processing | On-device via Google ML Kit (offline) |

### Distribusi App

- **Development**: APK sideload (install langsung tanpa Play Store)
- **Production (opsional)**: Google Play Store (biaya Rp400.000 sekali bayar)

### Environment Variables

| Variable | Deskripsi |
|----------|-----------|
| `GROQ_API_KEY` | API key untuk Groq LLM. Disimpan di `local.properties` (development) atau BuildConfig (production) |

### Minimum Device Requirements

| Spec | Minimum |
|------|---------|
| Android version | 8.0 (API 26) |
| RAM | 3 GB |
| Storage | 100 MB untuk app + data grows over time |
| Internet | Diperlukan HANYA untuk AI Chat (F008) dan opsional generate pattern (F003) |

---

## 6. Cross-Cutting Patterns

Pola-pola berikut berlaku di SEMUA fitur. Feature spec boleh merujuk ke section ini: "Mengikuti Cross-Cutting Patterns di ARCHITECTURE.md".

### 6.1 Error Handling

- **Database error**: Tampilkan toast `"Gagal menyimpan data. Coba lagi."` + log error.
- **Network error** (hanya F008 + F003 LLM): Tampilkan inline message `"Butuh koneksi internet"` + nonaktifkan input.
- **API error** (Groq timeout/500/rate limit): Tampilkan `"Ada gangguan, coba beberapa saat lagi"` + tombol retry.
- **Parsing error** (F001/F002 gagal baca layar): Data tetap disimpan sebagai raw snapshot. Tandai `is_processed = 0`.
- Error TIDAK BOLEH crash app. Semua error di-catch dan ditampilkan sebagai toast atau inline message.

### 6.2 Offline Behavior

- **Default: Offline-first.** Semua fitur berfungsi tanpa internet KECUALI F008 (AI Chat) dan F003 (opsional LLM pattern generation).
- Data SELALU disimpan ke database lokal terlebih dahulu.
- Tidak ada sync ke cloud — app ini 100% lokal.
- Jika user buka AI Chat tanpa internet: tampilkan `"Butuh koneksi internet untuk chat dengan AI"`, kolom input nonaktif.

### 6.3 Loading & Empty States

- **Loading**: Tampilkan shimmer/skeleton loading untuk data yang butuh query. BUKAN spinner berputar.
- **Empty state**: Pesan ramah + CTA (call to action).
  - Dashboard kosong: "Belum ada data hari ini" + target tetap tampil.
  - Hutang kosong: ilustrasi + "Belum ada hutang" + tombol Tambah Hutang.
  - Chat kosong: Greeting AI + contoh topik.
- **Timeout**: AI Chat timeout 30 detik → pesan gagal + tombol retry.

### 6.4 Shared UI Components

| Komponen | Dipakai Di | Behavior |
|----------|-----------|----------|
| **Tab Bar** | Semua halaman utama | 5 tab: Home, Order, AI, Input (elevated), Lain. Tab aktif = warna primer. Tab Input lebih besar 8dp, shadow, hijau. Selalu visible kecuali halaman detail/overlay. |
| **Hero Number** | F005 Dashboard | Angka besar di atas. Warna berubah sesuai status (kuning = progress, hijau = tercapai, merah = minus). |
| **Confirm Dialog** | F006 (bayar hutang, hapus), F009 (hapus biaya tetap) | Selalu 2 tombol: "Batal" (kiri, abu) + "Aksi" (kanan, warna primer). |
| **Notification Bell** | F005 Dashboard (header) | Badge angka jika ada notifikasi. Tap → dropdown list. Sumber: F001 (capture mati), F002 (pengingat riwayat), F003 (data perlu dicek). |
| **Preset Grid** | F004 (kategori + nominal), F009 (template biaya tetap) | Grid tombol tap-able. Ukuran besar, mudah dijangkau jempol. |
| **Status Badge** | F006 (On-track/Telat/Lunas), F003 (confidence level) | Warna: hijau = baik, merah = urgent, abu = netral. |

### 6.5 Data Refresh Strategy

- **Event-driven refresh**: Dashboard auto-refresh saat data baru masuk dari F001, F002, F003, atau F004. BUKAN polling/timer (hemat baterai).
- **onResume refresh**: Dashboard refresh setiap kali app dibuka/kembali ke foreground.
- **Day reset**: Saat hari berganti (00:00), Dashboard reset ke data hari baru. Data kemarin tetap tersimpan.
- **Month reset**: Setiap tanggal 1, profit terkumpul reset ke 0, kewajiban bulan baru dihitung ulang.

### 6.6 Number & Currency Formatting

- Semua nominal uang disimpan sebagai **INTEGER dalam Rupiah** (tanpa desimal). Input Rp50.000 → simpan `50000`.
- Tampilan ke user selalu dengan format Rupiah: `Rp50.000`, `Rp1.575.000`.
- Persentase ditampilkan tanpa desimal: `85%`, `100%`.
