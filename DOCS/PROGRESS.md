# PROGRESS.md — Driver Financial Intelligence App

---

## Sesi Terakhir
- **Tanggal**: 2026-02-18
- **Fase**: MVP Build
- **Status**: F009 Kewajiban & Jadwal selesai

---

## Feature Status

### SETUP

| ID | Nama | Status | PR |
|----|------|--------|----|
| — | Project Setup | ✅ | #14 |
| — | Database + Room Setup | ✅ | #14 |
| — | Accessibility Service Setup | ✅ | #14 |
| — | Groq API Integration | ✅ | #16 |
| — | Navigation + Tab Bar | ✅ | #15 |

### MVP Features (MUST)

| ID | Nama | Status | PR |
|----|------|--------|----|
| F001 | Auto Capture Order Masuk | ✅ | #17 |
| F002 | Capture Riwayat Rincian Pesanan | ✅ | #18 |
| F003 | AI Ekstraksi Data dari Capture | ✅ | #19 |
| F004 | Input Cepat | ✅ | #20 |
| F005 | Dashboard Harian | ✅ | #21 |
| F006 | Manajemen Hutang | ✅ | #22 |
| F007 | Target Harian Otomatis | ⬜ | — |
| F008 | AI Chat Tanya Kapan Saja | ⬜ | — |
| F009 | Kewajiban & Jadwal | ✅ | #23 |

### Post-MVP (SHOULD)

| ID | Nama | Status | PR |
|----|------|--------|----|
| F010 | Laporan Mingguan/Bulanan | ⬜ Backlog | — |
| F011 | Analisa Area & Waktu | ⬜ Backlog | — |
| F012 | Peringatan Jatuh Tempo Hutang | ⬜ Backlog | — |
| F014 | Analisa Poin Bonus | ⬜ Backlog | — |

### Backlog (FUTURE)

| ID | Nama | Status |
|----|------|--------|
| F015 | Analisa Prioritas Area & Algoritma Shopee | ⬜ Backlog |
| F016 | Prediksi Penghasilan | ⬜ Backlog |
| F017 | Multi-Driver (Multi-User) | ⬜ Backlog |
| F018 | Export Data | ⬜ Backlog |
| F019 | Simulasi What-If | ⬜ Backlog |
| F020 | AI Insight Otomatis | ⬜ Backlog |

### Bug Fixes

| ID | Nama | Status | PR |
|----|------|--------|----|
| (kosong — akan diisi saat build) | | | |

---

## Build Order Notes
- F009 dibangun sebelum F007 karena F007 membaca tabel milik F009 (fixed_expenses, work_schedules, ambitious_mode)
