# F008 — AI Chat (Tanya Kapan Saja)

---

## BAGIAN USER

### 1. Tentang Fitur Ini

**Deskripsi:** Fitur chat khusus di mana driver bisa bertanya apa saja seputar data kerja dan keuangannya. AI menjawab berdasarkan seluruh data yang sudah terkumpul di app (order, pendapatan, pengeluaran, hutang, target). AI berfungsi sebagai Financial Advisor pribadi — memberikan insight, analisa, dan membantu decision making. Bukan chat umum — AI hanya menjawab yang berkaitan dengan data driver.

**User Story:** Sebagai driver, saya ingin bisa tanya langsung ke AI tentang kondisi keuangan dan performa kerja saya dalam bahasa sehari-hari, agar saya bisa dapat insight dan saran yang actionable tanpa harus baca grafik atau angka-angka sendiri.

**Referensi:** Implements F008 dari PRD.md

---

### 2. Alur Penggunaan

**Buka AI Chat:**
1. Driver tap tab "AI" di navigasi bawah
2. Muncul layar chat kosong (fresh — tidak ada riwayat)
3. Di atas: greeting singkat dari AI
4. Di bawah: kolom ketik + tombol kirim

**Tanya ke AI:**
1. Driver ketik pertanyaan: "Gimana performa hari ini?"
2. Tap tombol kirim
3. AI loading sebentar (1-3 detik)
4. Jawaban muncul: teks + angka penting di-highlight (bold/warna)
5. Driver bisa lanjut tanya follow-up

**AI Menjawab di Luar Scope:**
1. Driver tanya: "Cuaca besok gimana?"
2. AI jawab: "Saya cuma bisa bantu soal data kerja dan keuangan kamu ya 😊 Coba tanya tentang performa, hutang, atau target harian!"

**AI Menjawab Fitur Belum Tersedia:**
1. Driver tanya: "Area mana yang paling menguntungkan?"
2. AI jawab: "Fitur analisa area belum tersedia sekarang, tapi sedang direncanakan ya 😊"

**Tidak Ada Internet:**
1. Driver buka tab AI saat offline
2. Muncul pesan: "Butuh koneksi internet untuk chat dengan AI"
3. Tombol kirim nonaktif

---

### 3. Tampilan Layar

**A. Chat Kosong (Baru Dibuka):**
```
┌──────────────────────────────────────┐
│  [←]     AI Chat          🤖        │
├──────────────────────────────────────┤
│                                      │
│                                      │
│                                      │
│         🤖                           │
│         Halo! Saya AI asisten       │
│         keuangan kamu.               │
│                                      │
│         Tanya apa saja soal          │
│         performa kerja, hutang,      │
│         target, atau keuangan kamu.  │
│                                      │
│                                      │
│                                      │
│                                      │
│                                      │
│                                      │
├──────────────────────────────────────┤
│  ┌────────────────────────────┐ [➤] │
│  │ Ketik pertanyaan...        │      │
│  └────────────────────────────┘      │
└──────────────────────────────────────┘
```

**B. Percakapan Aktif:**
```
┌──────────────────────────────────────┐
│  [←]     AI Chat          🤖        │
├──────────────────────────────────────┤
│                                      │
│         🤖 Halo! Saya AI asisten    │
│         keuangan kamu.               │
│                                      │
│                        ┌───────────┐ │
│                        │ Gimana    │ │
│                        │ hari ini? │ │
│                        └───────────┘ │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 🤖 Hari ini lumayan!          │  │
│  │                                │  │
│  │ • 8 trip selesai               │  │
│  │ • Pendapatan: **Rp156.000**   │  │
│  │ • Pengeluaran: **Rp54.000**   │  │
│  │ • Profit: **Rp102.000**       │  │
│  │                                │  │
│  │ Target hari ini **Rp126.000** │  │
│  │ — kurang **Rp24.000** lagi.   │  │
│  │                                │  │
│  │ Kalau dapat 1-2 trip lagi     │  │
│  │ harusnya cukup! 💪             │  │
│  └────────────────────────────────┘  │
│                                      │
│                     ┌──────────────┐ │
│                     │ Hutang gue   │ │
│                     │ gimana?      │ │
│                     └──────────────┘ │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 🤖 Kamu punya 3 hutang aktif: │  │
│  │                                │  │
│  │ 1. 🏍️ Motor Honda BeAT        │  │
│  │    Sisa: **Rp12.350.000**     │  │
│  │    Cicilan: Rp650.000/bln     │  │
│  │    🟢 On-track (tgl 15)       │  │
│  │                                │  │
│  │ 2. 📱 Pinjol Akulaku          │  │
│  │    Sisa: **Rp1.575.000**      │  │
│  │    🔴 Telat 5 hari!           │  │
│  │    Denda berjalan: Rp75.000   │  │
│  │                                │  │
│  │ 3. 👤 Hutang ke Budi          │  │
│  │    Sisa: **Rp700.000**        │  │
│  │    🟡 11 hari lagi             │  │
│  │                                │  │
│  │ ⚠️ Prioritas: bayar **Pinjol  │  │
│  │ Akulaku** dulu! Denda jalan   │  │
│  │ Rp15.000/hari.                │  │
│  └────────────────────────────────┘  │
│                                      │
├──────────────────────────────────────┤
│  ┌────────────────────────────┐ [➤] │
│  │ Ketik pertanyaan...        │      │
│  └────────────────────────────┘      │
└──────────────────────────────────────┘
```

**C. AI Menjawab di Luar Scope:**
```
┌──────────────────────────────────────┐
│                                      │
│                     ┌──────────────┐ │
│                     │ Cuaca besok  │ │
│                     │ gimana?      │ │
│                     └──────────────┘ │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 🤖 Saya cuma bisa bantu soal  │  │
│  │ data kerja dan keuangan kamu  │  │
│  │ ya 😊                          │  │
│  │                                │  │
│  │ Coba tanya tentang:           │  │
│  │ • Performa hari/minggu/bulan  │  │
│  │ • Progress hutang             │  │
│  │ • Target harian               │  │
│  │ • Pengeluaran                 │  │
│  │ • Saran keuangan              │  │
│  └────────────────────────────────┘  │
│                                      │
└──────────────────────────────────────┘
```

**D. Tidak Ada Internet:**
```
┌──────────────────────────────────────┐
│  [←]     AI Chat          🤖        │
├──────────────────────────────────────┤
│                                      │
│                                      │
│                                      │
│              📡                      │
│                                      │
│    Butuh koneksi internet            │
│    untuk chat dengan AI              │
│                                      │
│    Fitur lain tetap bisa             │
│    dipakai secara offline            │
│                                      │
│                                      │
│                                      │
│                                      │
├──────────────────────────────────────┤
│  ┌────────────────────────────┐ [➤] │
│  │ Ketik pertanyaan...        │      │
│  └────────────────────────────┘      │
│          (nonaktif)                  │
└──────────────────────────────────────┘
```

**E. AI Sedang Loading:**
```
┌──────────────────────────────────────┐
│                                      │
│                     ┌──────────────┐ │
│                     │ Kapan hutang │ │
│                     │ gue lunas?   │ │
│                     └──────────────┘ │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ 🤖 ●●●                        │  │
│  │    Sedang menganalisa data...  │  │
│  └────────────────────────────────┘  │
│                                      │
└──────────────────────────────────────┘
```

---

### 4. Kasus Khusus

| Situasi | Apa yang User Lihat | Apa yang Terjadi |
|---------|---------------------|------------------|
| Tidak ada internet | "Butuh koneksi internet untuk chat dengan AI" + kolom input nonaktif | AI butuh cloud LLM untuk memproses |
| Internet putus di tengah chat | Jawaban AI gagal muncul: "Koneksi terputus. Coba kirim lagi." + tombol retry | Pesan driver tetap tampil, jawaban AI yang gagal bisa di-retry |
| Driver tanya di luar scope | AI jawab sopan + kasih contoh topik yang bisa ditanya | AI tidak menjawab pertanyaan umum |
| Driver tanya fitur SHOULD/FUTURE | "Fitur ini belum tersedia sekarang, tapi sedang direncanakan ya 😊" | Misal: analisa area, prediksi penghasilan, simulasi what-if |
| Belum ada data sama sekali (baru install) | AI jawab: "Belum ada data yang bisa saya analisa. Mulai narik dulu, nanti saya bantu analisa!" | AI butuh minimal data untuk menjawab |
| Data hari ini kosong (belum narik) | AI jawab berdasarkan data terakhir yang ada: "Hari ini belum ada data. Kemarin kamu profit Rp95.000..." | AI tetap bisa berguna walau hari ini kosong |
| Driver tanya ambigu tanpa waktu | AI tentukan sendiri dari konteks: jam berapa sekarang, hari apa, apakah ada data hari ini | Misal: tanya siang hari → jawab hari ini. Tanya hari libur → jawab minggu lalu |
| Pertanyaan follow-up | AI memahami konteks percakapan sebelumnya dalam sesi ini | Misal: "Gimana hutang gue?" → jawab → "Yang pinjol bisa dilunasi kapan?" → AI tahu "pinjol" dari jawaban sebelumnya |
| Driver kirim pesan sangat panjang | AI tetap coba jawab sebaik mungkin | Tidak ada batasan panjang input |
| Driver kirim pesan kosong atau karakter random | AI jawab: "Hmm, saya kurang mengerti. Coba tanya tentang performa, hutang, atau target kamu ya" | Validasi input minimal |
| AI response terlalu lama (>10 detik) | Tampil: "Maaf, agak lama nih. Tunggu sebentar ya..." | Timeout setelah 30 detik → "Gagal memproses. Coba lagi." |
| Tutup dan buka lagi tab AI | Chat kosong (fresh) — tidak ada riwayat | Setiap sesi baru = fresh chat |

---

### 5. Info Teknis dari User

- AI **khusus data driver** — bukan chat umum, tidak cari info di luar app
- Scope: analisa performa, insight, decision making, progress hutang/target, perbandingan waktu
- Input: **ketik saja** (voice tidak untuk versi pertama)
- Riwayat chat: **tidak ada** — setiap buka = fresh
- Suggestion chips: **tidak untuk versi pertama**
- Rentang waktu jawaban: **AI tentukan sendiri** dari konteks
- AI model: **Cloud-based** (butuh internet)
- Format jawaban: teks + angka di-highlight (bold/warna)
- Data yang diakses: F001 (order), F002 (riwayat), F003 (data matching), F004 (pengeluaran/pemasukan lain), F006 (hutang), F007 (target), F009 (biaya tetap/jadwal)
- Di luar scope → tolak sopan + kasih contoh topik yang bisa ditanya
- Fitur belum tersedia → info bahwa sedang direncanakan

---

## BAGIAN TEKNIS

> Bagian di bawah ini adalah terjemahan teknis dari semua yang sudah kita diskusikan. Bagian ini untuk AI Builder — kamu tidak perlu membaca atau memahaminya.

### 6. Technical Implementation

#### 6.1 API Endpoints

**Endpoint yang dibutuhkan:**

| Method | Endpoint | Deskripsi |
|--------|----------|-----------|
| POST | /chat/completions (LLM Provider) | Kirim pesan + konteks data driver, terima jawaban AI |

Menggunakan API dari LLM provider (misal: OpenAI, Anthropic, Google Gemini, atau alternatif lain). Pilihan provider ditentukan oleh AI Builder berdasarkan harga, kecepatan, dan kualitas.

#### 6.2 Database Changes

**Tidak ada tabel baru** — chat tidak disimpan (setiap sesi fresh).

Namun AI perlu **membaca** data dari tabel berikut untuk menyusun context yang dikirim ke LLM:

| Tabel | Pemilik | Data yang Diambil | Untuk Menjawab |
|-------|---------|-------------------|----------------|
| `history_trips` | F002 | Pendapatan Shopee per trip: total_earning, service_type, trip_date | "Berapa pendapatan hari ini?", performa harian/mingguan/bulanan |
| `history_details` | F002 | Detail per pesanan: bonus_points, timeline, COD info | "Berapa poin hari ini?", detail trip |
| `captured_orders` | F001 | Data alamat, seller, customer, tipe layanan | "Trip mana yang paling jauh?", info pesanan |
| `matched_orders` | F003 | Data gabungan F001+F002 (order lengkap dengan alamat + pendapatan) | Analisa yang butuh data alamat + pendapatan sekaligus |
| `quick_entries` | F004 | Pengeluaran harian (type=EXPENSE) + pemasukan lain non-Shopee (type=INCOME) | "Berapa pengeluaran hari ini?", analisa profit |
| `debts` | F006 | Daftar hutang aktif: nama, remaining_amount, monthly_installment, due_date_day, status, has_penalty | "Gimana hutang gue?", prioritas bayar |
| `debt_payments` | F006 | Riwayat pembayaran hutang: amount, payment_date, debt_id | "Bulan ini udah bayar berapa?", progress pelunasan |
| `daily_target_cache` | F007 | Target harian + breakdown kewajiban + status + deadline urgent | "Target hari ini berapa?", sisa target, progress kewajiban |
| `fixed_expenses` | F009 | Biaya tetap bulanan: nama, amount | "Kewajiban bulan ini apa aja?" |
| `work_schedules` | F009 | Jadwal kerja: schedule_date, is_working | "Minggu ini kerja berapa hari?" |
| `ambitious_mode` | F009 | Status mode ambisius: is_active, target_months | "Mode ambisius aktif nggak?" |

#### 6.3 Business Logic

1. **System Prompt — Identitas AI:**
   ```
   Kamu adalah AI Financial Advisor untuk Shopee Driver.
   Kamu HANYA menjawab pertanyaan seputar data kerja dan keuangan driver.
   Kamu TIDAK menjawab pertanyaan umum, tidak cari info di internet.
   Jawab dalam bahasa Indonesia sehari-hari, singkat, dan actionable.
   Gunakan emoji secukupnya.
   Angka penting ditulis bold (**Rp150.000**).
   ```

2. **Context Injection — Data Driver:**
   - Setiap kali driver kirim pesan, app mengumpulkan data terkini dari database lokal
   - Data di-compile menjadi context string yang dikirim bersama pesan ke LLM
   - Context berisi:
     ```
     DATA HARI INI:
     - Tanggal: [tanggal]
     - Jam sekarang: [jam]
     - Trip selesai: [jumlah] (dari history_trips WHERE trip_date = TODAY)
     - Pendapatan Shopee: [nominal] (dari history_trips SUM total_earning WHERE trip_date = TODAY)
     - Pemasukan Lain: [nominal] (dari quick_entries WHERE type = INCOME AND entry_date = TODAY)
     - Total Pendapatan: [nominal] (Shopee + Pemasukan Lain)
     - Pengeluaran: [nominal] (detail per kategori dari quick_entries WHERE type = EXPENSE AND entry_date = TODAY)
     - Profit: [nominal] (Total Pendapatan − Pengeluaran)
     - Target harian: [nominal] (dari daily_target_cache WHERE target_date = TODAY)
     - Sisa target: [nominal]
     - Poin bonus: [jumlah] (dari history_details SUM bonus_points WHERE date = TODAY)

     DATA MINGGU INI:
     - Hari kerja: [jumlah]/[total] (dari work_schedules)
     - Profit terkumpul: [nominal]

     DATA BULAN INI:
     - Profit terkumpul: [nominal]
     - Profit tersedia (setelah bayar cicilan): [nominal] (dari daily_target_cache profit_available)
     - Kewajiban bulan ini: [nominal] (dari daily_target_cache total_obligation)
     - Sisa kewajiban: [nominal] (dari daily_target_cache remaining_obligation)

     HUTANG AKTIF:
     - [nama]: sisa [nominal], cicilan [nominal]/bln, jatuh tempo tgl [X], status [status], denda [jika ada]
       (dari debts WHERE status = ACTIVE)
     - ...

     BIAYA TETAP:
     - [nama]: [nominal]/bln
       (dari fixed_expenses WHERE is_active = 1)
     - ...

     MODE AMBISIUS: [aktif/nonaktif], target [X] bulan
       (dari ambitious_mode)
     ```

3. **Conversation Memory (Sesi Saja):**
   - Dalam 1 sesi chat, semua pesan (driver + AI) disimpan di memory lokal (variabel, bukan database)
   - Dikirim ke LLM sebagai conversation history untuk konteks follow-up
   - Saat driver tutup tab AI atau navigasi ke tab lain → memory di-clear
   - Batas conversation history: 20 pesan terakhir (10 pasang tanya-jawab) untuk hemat token

4. **Scope Guard — Tolak Pertanyaan di Luar Scope:**
   - System prompt sudah instruksikan AI untuk menolak pertanyaan di luar scope
   - Tambahan: app bisa cek keyword di response AI. Jika AI "bocor" dan menjawab di luar scope, app bisa filter (opsional, tergantung implementasi)
   - Response template untuk di luar scope:
     ```
     Saya cuma bisa bantu soal data kerja dan keuangan kamu ya 😊
     Coba tanya tentang:
     • Performa hari/minggu/bulan
     • Progress hutang
     • Target harian
     • Pengeluaran
     • Saran keuangan
     ```

5. **Future Feature Guard:**
   - Keyword detection: jika pertanyaan mengandung kata "area", "lokasi", "prediksi", "estimasi bulan depan", "simulasi", "kalau aku..."
   - Response template:
     ```
     Fitur ini belum tersedia sekarang, tapi sedang direncanakan ya 😊
     ```
   - Catatan: ini heuristic sederhana. AI juga bisa mendeteksi sendiri dari system prompt

6. **Waktu Konteks Otomatis:**
   - AI menentukan rentang waktu jawaban berdasarkan:
     - Jam sekarang (pagi/siang/malam)
     - Hari sekarang (hari kerja/libur)
     - Ketersediaan data hari ini
     - Kata kunci di pertanyaan ("hari ini", "minggu ini", "bulan ini", "kemarin", dll)
   - Instruksi di system prompt:
     ```
     Jika driver tidak menyebut waktu spesifik:
     - Jika ada data hari ini → default jawab hari ini
     - Jika belum ada data hari ini → jawab berdasarkan data terakhir yang tersedia
     - Untuk pertanyaan tentang tren/perbandingan → gunakan minggu ini
     - Untuk pertanyaan tentang hutang/target → gunakan bulan ini
     ```

7. **Format Response:**
   - Teks bahasa Indonesia sehari-hari
   - Angka nominal ditulis bold: **Rp150.000**
   - Emoji secukupnya (tidak berlebihan)
   - Jawaban singkat dan to-the-point (max ~150 kata per response)
   - Untuk data list (misal daftar hutang), gunakan numbered list
   - Selalu akhiri dengan insight/saran actionable jika relevan

8. **Error Handling:**
   - Tidak ada internet → tampilkan pesan offline, input nonaktif
   - Timeout (>30 detik) → "Gagal memproses. Coba lagi." + tombol retry
   - API error (500, rate limit) → "Ada gangguan, coba beberapa saat lagi"
   - Internet putus saat menunggu response → "Koneksi terputus. Coba kirim lagi." + tombol retry

9. **Token & Cost Management:**
   - Context data driver dikirim setiap request → estimasi ~500-800 token per request untuk context
   - Conversation history → estimasi ~100-200 token per pasang tanya-jawab
   - Batas 20 pesan history untuk cap token usage
   - Response AI dibatasi max ~300 token (~150 kata)
   - Estimasi total per request: ~1,500-2,000 token (input + output)

#### 6.4 External Dependencies

| Dependency | Deskripsi | Fallback |
|------------|-----------|----------|
| LLM API (cloud) | Untuk memproses chat dan generate jawaban | Tidak ada fallback — fitur butuh internet |
| Internet connection | Untuk menghubungi LLM API | Tampilkan pesan offline |

---

### 7. Acceptance Criteria

| # | Kriteria | Test Method |
|---|----------|-------------|
| 1 | Driver bisa buka AI Chat dari tab "AI" di navigasi bawah | Manual |
| 2 | Chat selalu fresh saat dibuka (tidak ada riwayat) | Manual |
| 3 | Driver bisa ketik pertanyaan dan kirim | Manual |
| 4 | AI menjawab berdasarkan data aktual driver (bukan karangan) | Integration |
| 5 | Angka penting di jawaban AI tampil bold/highlight | Manual |
| 6 | AI menjawab dalam bahasa Indonesia sehari-hari | Manual |
| 7 | Follow-up pertanyaan dipahami dalam konteks sesi | Integration |
| 8 | Pertanyaan di luar scope → ditolak sopan + kasih contoh topik | Integration |
| 9 | Pertanyaan fitur belum tersedia → info sedang direncanakan | Integration |
| 10 | Tidak ada internet → pesan offline + input nonaktif | Manual |
| 11 | Internet putus saat chat → pesan error + tombol retry | Manual |
| 12 | Timeout >30 detik → pesan gagal + tombol retry | Integration |
| 13 | AI bisa menjawab soal performa harian (trip, pendapatan, pengeluaran, profit) | Integration |
| 14 | AI bisa menjawab soal progress hutang (sisa, status, prioritas bayar) | Integration |
| 15 | AI bisa menjawab soal target harian dan kewajiban bulanan | Integration |
| 16 | AI bisa memberi saran/rekomendasi actionable | Integration |
| 17 | AI menentukan rentang waktu jawaban secara otomatis dari konteks | Integration |
| 18 | Context data driver dikirim dengan benar ke LLM (sumber tabel presisi) | Unit |
| 19 | Conversation history di-clear saat navigasi keluar dari tab AI | Unit |
| 20 | Batas 20 pesan history per sesi | Unit |

---

### 8. Dependencies

- **Bergantung pada:**
  - **F001 (Auto Capture Order):** Data alamat, seller, customer dari `captured_orders` — untuk konteks lokasi pesanan
  - **F002 (Capture Riwayat):** Data pendapatan Shopee dari `history_trips` + detail per pesanan dari `history_details` — SUMBER UTAMA data performa
  - **F003 (AI Ekstraksi):** Data gabungan dari `matched_orders` — untuk analisa yang butuh data alamat + pendapatan sekaligus
  - **F004 (Input Cepat):** Data pengeluaran harian dan pemasukan lain (non-Shopee) dari `quick_entries`
  - **F006 (Manajemen Hutang):** Data hutang aktif dari `debts`, riwayat pembayaran dari `debt_payments` — untuk progress hutang dan prioritas bayar
  - **F007 (Target Harian):** Target harian dan breakdown kewajiban dari `daily_target_cache` — untuk data target dan progress
  - **F009 (Kewajiban & Jadwal):** Biaya tetap dari `fixed_expenses`, jadwal kerja dari `work_schedules`, mode ambisius dari `ambitious_mode`
  - **Internet + LLM API:** Wajib untuk fitur ini berfungsi
- **Dibutuhkan oleh:**
  - Tidak ada fitur lain yang bergantung pada F008 (fitur ini berdiri sebagai consumer data)