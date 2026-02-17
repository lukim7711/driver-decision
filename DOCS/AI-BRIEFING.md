# AI-BRIEFING.md — Driver Financial Intelligence App

---

## 1. Ringkasan Proyek

Aplikasi mobile Android untuk Shopee Driver yang otomatis menangkap data orderan harian, mencatat pengeluaran dengan cepat, mengelola hutang/cicilan, dan menghitung target harian berdasarkan kewajiban bulanan. Dilengkapi AI Chat sebagai financial advisor pribadi.

---

## 2. Peta Dokumen

| Jika kamu perlu... | Baca file ini |
|---------------------|--------------|
| Memahami APA yang dibangun | `docs/PRD.md` |
| Detail fitur Auto Capture Order Masuk | `docs/features/F001-auto-capture-order-masuk.md` |
| Detail fitur Capture Riwayat Rincian | `docs/features/F002-capture-riwayat-rincian-pesanan.md` |
| Detail fitur AI Ekstraksi Data | `docs/features/F003-ai-ekstraksi-data.md` |
| Detail fitur Input Cepat | `docs/features/F004-input-cepat.md` |
| Detail fitur Dashboard Harian | `docs/features/F005-dashboard-harian.md` |
| Detail fitur Manajemen Hutang | `docs/features/F006-manajemen-hutang.md` |
| Detail fitur Target Harian Otomatis | `docs/features/F007-target-harian-otomatis.md` |
| Detail fitur AI Chat | `docs/features/F008-ai-chat.md` |
| Detail fitur Kewajiban & Jadwal | `docs/features/F009-kewajiban-jadwal.md` |
| Teknologi, data model, API contract | `docs/ARCHITECTURE.md` |
| Aturan coding | `docs/CONSTITUTION.md` |
| Konteks keputusan desain | `docs/DECISIONS.md` |
| Status build terkini | `docs/PROGRESS.md` |

---

## 3. Peta File Proyek

Akan dilengkapi saat fase build dimulai. Lihat `ARCHITECTURE.md` Section 2 (Project Structure) untuk folder tree yang direncanakan.

---

## 4. Aturan yang Sering Dilanggar

- Nominal uang HARUS INTEGER (`50000`, bukan `"Rp 50.000"`).
- Semua timestamp HARUS ISO 8601 dengan timezone (`+07:00`).
- TIDAK BOLEH ada `Log.d()` atau `println()` di production code.
- 1 PR per fitur — TIDAK BOLEH gabung >1 fitur dalam 1 PR.
- Soft delete — JANGAN hard delete data user.
- TIDAK BOLEH `!!` (force unwrap) — gunakan safe calls.
- F007 TIDAK punya tabel sendiri — ini mesin hitung, bukan storage.
- Data computed (profit, target, progress) dihitung real-time, TIDAK disimpan di database.

---

## 5. Update Protocol

- **PROGRESS.md**: Update status setiap kali fitur selesai di-build atau PR dibuat.
- **AI-BRIEFING.md Section 3**: Lengkapi Peta File Proyek saat folder/file baru dibuat.
- **DECISIONS.md**: Tambah entri jika ada keputusan teknis baru saat build.
- **Feature specs**: JANGAN diubah saat build — jika ada perubahan, catat di DECISIONS.md.
- **ARCHITECTURE.md**: JANGAN diubah kecuali ada perubahan signifikan — catat alasannya.
