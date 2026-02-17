# CONSTITUTION.md — Driver Financial Intelligence App

Aturan yang HARUS dipatuhi AI Builder saat menulis kode. Setiap item adalah constraint.

---

## 1. Code Rules

- HARUS menggunakan Kotlin dengan target Android API 26+ (Android 8.0).
- HARUS menggunakan Jetpack Compose untuk semua UI — TIDAK BOLEH menggunakan XML layout.
- HARUS menggunakan Kotlin Coroutines + Flow untuk semua operasi asynchronous.
- HARUS menggunakan Hilt untuk dependency injection.
- TIDAK BOLEH menggunakan `var` untuk state — gunakan `MutableStateFlow` atau `mutableStateOf`.
- TIDAK BOLEH meninggalkan `println()` atau `Log.d()` debug di production code — gunakan `Timber` jika perlu logging.
- TIDAK BOLEH menggunakan `!!` (force unwrap) — gunakan `?.let {}`, `?: default`, atau `requireNotNull()` dengan pesan jelas.
- HARUS menggunakan `sealed class` atau `sealed interface` untuk UI state (Loading, Success, Error).

---

## 2. Frontend Rules

- HARUS menggunakan functional composables — TIDAK BOLEH class-based.
- HARUS menggunakan Material 3 (Material You) design components.
- HARUS menamai composable dengan PascalCase (misal `DashboardScreen`, `DebtCard`).
- HARUS menamai file screen dengan kebab-case per fitur (misal `dashboard/DashboardScreen.kt`).
- HARUS menggunakan `ViewModel` per screen — 1 ViewModel per screen utama.
- TIDAK BOLEH melakukan database query langsung di composable — selalu via ViewModel → UseCase → Repository → DAO.
- HARUS menggunakan `Navigation Compose` untuk navigasi antar screen.
- HARUS menempatkan semua warna, typography, dan spacing di `theme/` — TIDAK BOLEH hardcode warna di composable.
- HARUS memastikan semua elemen interaktif bisa dijangkau jempol (minimum touch target 48dp).

---

## 3. Backend Rules

Tidak ada backend server. App ini 100% lokal. Satu-satunya external call:

- HARUS menggunakan Retrofit + OkHttp untuk Groq API call.
- HARUS menyimpan API key di `BuildConfig` — TIDAK BOLEH hardcode di source code.
- HARUS menambahkan timeout 30 detik untuk Groq API call.
- HARUS menangani semua HTTP error (timeout, 429 rate limit, 500 server error) dengan pesan user-friendly.
- TIDAK BOLEH menambahkan backend/server/cloud database tanpa keputusan eksplisit di DECISIONS.md.

---

## 4. Database Rules

- HARUS menggunakan Room sebagai abstraction layer di atas SQLite.
- HARUS menyimpan semua nominal uang sebagai INTEGER dalam satuan Rupiah tanpa desimal. Input `Rp 50.000` → simpan `50000`.
- HARUS menggunakan soft delete (`status = 'DELETED'` atau `is_active = 0`) — TIDAK BOLEH hard delete data user.
- HARUS menyimpan timestamp dalam format ISO 8601 dengan timezone (`2026-02-17T21:09:00+07:00`).
- HARUS menyimpan tanggal dalam format `YYYY-MM-DD`.
- HARUS menamai tabel dengan snake_case plural (`debts`, `quick_entries`, `history_trips`).
- HARUS menamai field dengan snake_case (`created_at`, `remaining_amount`, `is_active`).
- HARUS menggunakan TEXT UUID sebagai primary key — TIDAK BOLEH auto-increment integer.
- HARUS membuat Room Migration untuk setiap perubahan schema — TIDAK BOLEH `fallbackToDestructiveMigration()`.
- TIDAK BOLEH menyimpan data computed (misal profit, target harian) di database — hitung real-time dari data sumber.
- HARUS menjalankan semua database query di background thread (Dispatchers.IO).

---

## 5. Git Workflow

- HARUS menggunakan branch naming: `feat/F001-auto-capture`, `fix/F006-penalty-calculation`.
- HARUS menggunakan commit message convention: `feat: add quick input grid`, `fix: penalty calculation off by one`.
- HARUS membuat 1 PR per fitur — TIDAK BOLEH menggabung >1 fitur dalam 1 PR.
- HARUS squash merge ke `main`.
- TIDAK BOLEH push langsung ke `main` kecuali hotfix kritis.

---

## 6. Security Rules

- HARUS menyimpan Groq API key di `local.properties` (development) atau `BuildConfig` (production) — TIDAK BOLEH hardcode di source code.
- TIDAK BOLEH meng-expose API key di Git — tambahkan `local.properties` ke `.gitignore`.
- HARUS memvalidasi semua input dari user sebelum simpan ke database (nominal > 0, teks tidak kosong untuk field wajib).
- TIDAK BOLEH menyimpan data sensitif user di SharedPreferences tanpa enkripsi — gunakan EncryptedSharedPreferences jika perlu.
- HARUS meminta izin Android secara eksplisit (Accessibility Service, Notification Access) dan menjelaskan kenapa ke user saat setup.

---

## 7. Testing Rules

- HARUS menamai file test dengan suffix `Test.kt` (misal `CalculateDailyTargetUseCaseTest.kt`).
- HARUS menulis unit test untuk setiap business logic di feature spec Section 6.3 — khususnya:
  - F003: Confidence tier system (threshold 85/60/30).
  - F006: Perhitungan denda otomatis (daily/monthly).
  - F007: Algoritma target harian deadline-aware (ini yang paling kritis).
  - F009: Mode ambisius perhitungan + auto-off logic.
- HARUS menulis integration test untuk setiap Repository.
- HARUS menggunakan JUnit 5 + Mockk untuk unit test.
- HARUS menggunakan Room in-memory database untuk test database.
- TIDAK BOLEH merge PR jika ada test yang gagal.
