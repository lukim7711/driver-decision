# DECISIONS.md — Driver Financial Intelligence App

---

## D-001: Topik — Mode Kerja
- **Keputusan**: Mode STANDARD (9 fitur MUST).
- **Alasan**: App punya 9 fitur MUST (F001–F009). Sesuai panduan, 4–8 fitur = STANDARD. Meskipun 9 fitur sedikit di atas batas, kompleksitas per fitur masih terkelola karena semuanya lokal (tidak ada backend).
- **Alternatif yang ditolak**: COMPLEX — overkill karena tidak ada multi-service architecture.
- **Dampak ke dokumen**: Semua dokumen mengikuti full flow STANDARD.
- **Tanggal**: 2026-02-10

---

## D-002: Topik — Pemisahan F009 dari F007
- **Keputusan**: Fitur "Kewajiban & Jadwal" dipecah dari F007 menjadi F009 tersendiri.
- **Alasan**: F007 awalnya terlalu besar (hitung target + kelola biaya tetap + kelola jadwal + mode ambisius). Pemecahan membuat setiap fitur punya 1 tanggung jawab jelas: F007 = mesin hitung, F009 = kelola data kewajiban & jadwal.
- **Alternatif yang ditolak**: Gabung semua di F007 — terlalu kompleks, sulit di-test.
- **Dampak ke dokumen**: PRD.md (tambah F009), feature specs F007 dan F009.
- **Tanggal**: 2026-02-12

---

## D-003: Topik — F013 Dimerge ke F009
- **Keputusan**: F013 (Biaya Tetap Bulanan) tidak dibuat terpisah, sudah termasuk dalam F009.
- **Alasan**: Biaya tetap bulanan secara natural bagian dari "kewajiban" yang dikelola F009. Memisahkan hanya menambah file tanpa manfaat.
- **Dampak ke dokumen**: PRD.md (F013 ditandai "Sudah termasuk dalam F009").
- **Tanggal**: 2026-02-12

---

## D-004: Topik — F004 Mencakup Pengeluaran DAN Pemasukan
- **Keputusan**: F004 "Input Cepat" mencakup pencatatan pengeluaran DAN pemasukan lain (tip cash, transfer masuk, kerja sampingan), bukan hanya pengeluaran.
- **Alasan**: User (driver) kadang dapat pemasukan di luar Shopee. Mekanisme input-nya identik (tap kategori → tap nominal), jadi digabung dalam 1 fitur dengan tab Pengeluaran/Pemasukan.
- **Alternatif yang ditolak**: Fitur terpisah untuk pemasukan — UX fragmentasi, driver harus belajar 2 tempat.
- **Dampak ke dokumen**: PRD.md (nama F004 di PRD masih "Input Pengeluaran Cepat" — minor, tidak diubah), F004 feature spec, F005 (dashboard menampilkan "Pemasukan Lain").
- **Tanggal**: 2026-02-13

---

## D-005: Topik — Accessibility Service vs MediaProjection untuk Screen Capture
- **Keputusan**: Menggunakan Accessibility Service sebagai metode utama untuk membaca layar Shopee (F001 + F002).
- **Alasan**: Accessibility Service bisa membaca node tree UI tanpa screenshot — lebih akurat, lebih ringan. MediaProjection (screen recording) sebagai fallback manual ("Mulai Rekam").
- **Alternatif yang ditolak**: MediaProjection only — butuh konfirmasi user setiap sesi, tidak bisa jalan otomatis di belakang layar.
- **Dampak ke dokumen**: F001, F002, ARCHITECTURE.md.
- **Tanggal**: 2026-02-13

---

## D-006: Topik — Pembayaran Hutang dan Pengeluaran Harian
- **Keputusan**: Bayar cicilan (tetap/pinjol) TIDAK masuk pengeluaran harian. Bayar denda MASUK pengeluaran harian. Bayar hutang personal = driver pilih.
- **Alasan**: Cicilan sudah diperhitungkan di target harian (F007) — jika masuk pengeluaran juga, akan double counting. Denda adalah pengeluaran tak terduga yang harus mengurangi profit harian. Personal = fleksibel sesuai situasi driver.
- **Alternatif yang ditolak**: Semua pembayaran masuk pengeluaran — target harian jadi tidak akurat.
- **Dampak ke dokumen**: F006 feature spec (Section 6.3 business logic), F007 (profit tracking).
- **Tanggal**: 2026-02-14

---

## REV-001: Revisi — Audit Silang Feature Specs (P4–P7)
- **Apa yang berubah**: 4 perbaikan konsistensi antar feature specs:
  - **P4**: F005 sumber data pendapatan ditegaskan dari F002 (bukan "F001 ATAU F002").
  - **P5**: F004 tambah field `is_system` + 2 system categories ("Cicilan Hutang", "Denda Hutang"). F006 tambah `category_id` FK.
  - **P6**: F006 dependencies diupdate — sekarang bergantung pada F004 (tabel `quick_entries` + `quick_entry_categories`).
  - **P7**: F009 Mode Ambisius auto-off diperjelas — dual mechanism (event-driven dari F006 + reactive fallback). Tambah field `deactivated_reason`. F006 tambah trigger ke F009.
- **Alasan**: Audit silang menemukan inkonsistensi yang bisa membuat AI Builder salah build.
- **Dokumen yang diupdate**: F004, F005, F006, F009.
- **Tanggal**: 2026-02-17

---

## D-007: Topik — Tech Stack
- **Keputusan**: Kotlin + Jetpack Compose, Room/SQLite, Google ML Kit (on-device OCR), Groq API (LLM).
- **Alasan**: (1) Fitur inti (Accessibility Service) hanya bisa Android native → Kotlin. (2) Semua data lokal → Room/SQLite. (3) OCR harus di-device (bukan cloud) → ML Kit. (4) LLM gratis dan cepat → Groq.
- **Alternatif yang ditolak**: Flutter (tidak bisa Accessibility Service), Firebase (app 100% lokal, tidak butuh cloud), Google Cloud (dilarang PRD), Tesseract OCR (akurasi lebih rendah).
- **Dampak ke dokumen**: ARCHITECTURE.md, CONSTITUTION.md.
- **Tanggal**: 2026-02-17

---

## D-008: Topik — F007 Tidak Punya Tabel Database
- **Keputusan**: F007 (Target Harian) adalah computation engine — tidak menyimpan data ke tabel sendiri. Target dihitung real-time via `TargetRepository.calculateDailyTarget()`.
- **Alasan**: Target berubah setiap kali ada data baru (profit masuk, hutang dibayar, jadwal diubah). Menyimpan di tabel berarti harus sync terus — lebih rumit dan rawan stale data. Hitung real-time lebih sederhana dan selalu akurat.
- **Alternatif yang ditolak**: Tabel `daily_targets` — menambah kompleksitas sync tanpa manfaat.
- **Dampak ke dokumen**: ARCHITECTURE.md (Section 4 Data Model, catatan F007), F005 (query sumber data dashboard).
- **Tanggal**: 2026-02-17
