# PRD — Driver Financial Intelligence App

---

## §1. Product Vision

### Ringkasan
Aplikasi mobile Android yang membantu Shopee Driver mengubah data orderan harian menjadi insight kerja efektif melalui AI. Aplikasi menangkap data order secara otomatis, mencatat pengeluaran dengan cepat, mengelola hutang/cicilan, dan memberikan strategi kerja berdasarkan analisa ratusan data personal driver.

### Problem Statement
- Shopee Driver menghasilkan **20-30 orderan per hari** (600-900 data point per bulan) yang berisi data berharga: alamat pickup/delivery, pendapatan, waktu kerja, area, bonus, COD — namun **data ini terbuang** karena tidak ada tools untuk mengolahnya.
- Driver **tidak mencatat** penghasilan dan pengeluaran karena proses pencatatan yang merepotkan — hanya menganalisa sekilas di akhir hari kerja.
- Driver menganggap pekerjaan harian sebagai **rutinitas biasa** tanpa menganalisa pola, sehingga **tidak ada perkembangan** — penghasilan stagnan, pengeluaran tidak terkontrol, hutang tidak terkelola.
- Data order detail (termasuk alamat) **hanya tersedia sebelum pesanan diterima** dan hilang setelah selesai. Rincian pesanan di riwayat harus dibuka satu per satu. Tidak ada cara mudah untuk mengumpulkan data secara menyeluruh.
- Driver bekerja berdasarkan alamat — **data area sangat krusial** untuk strategi kerja, namun tidak ada tools yang menganalisa pola area personal driver.

### Value Proposition
- **Input tanpa effort**: data order ter-capture otomatis di belakang layar saat orderan masuk, tanpa mengganggu workflow driver.
- **Pengeluaran tercatat < 3 detik**: tap kategori → tap nominal → selesai.
- **AI sebagai partner kerja**: dari ratusan data, AI menemukan pola dan memberikan rekomendasi strategi kerja yang actionable — bukan sekadar angka dan grafik.
- **Hutang terkelola**: target harian dihitung otomatis berdasarkan kewajiban bulanan, sehingga driver punya arah finansial yang jelas.

---

## §2. Studi Kasus

### Data Referensi dari Shopee Driver (Aktual)

**Contoh Pendapatan Per Trip (10 Feb 2026):**

| Waktu | Tipe Layanan | Jumlah Pesanan | Pendapatan | Bonus Poin |
|-------|-------------|----------------|------------|------------|
| 16:42 | SPX Instant (Marketplace) | Gabungan (2+) | Rp15.200 | - |
| 18:13 | SPX Instant (Marketplace) | 4 pesanan | Rp39.200 | 250 Poin |
| 20:43 | ShopeeFood | 2 pesanan | Rp19.200 | 150 Poin |

**Contoh Order Detail (Sebelum Diterima):**

| Field | Data |
|-------|------|
| Trip Code | #6VUS |
| Pickup | Komplek Taman Duta Mas, Jl. Kusuma IV C Blok D6 No. 14, Grogol Petamburan, Jakarta Barat |
| Delivery | Jl. Kampung Norogtok, RT.8/RW.5, Nerogtog, Kota Tangerang, Banten |
| Parcel | 1.2 kg, 30x25x4 cm |
| Payment | Cash (COD) |

**Contoh COD Flow:**

| Field | Nilai |
|-------|-------|
| Tagih Tunai dari Pelanggan | Rp49.800 |
| Penyesuaian (dipotong dari saldo driver) | -Rp49.800 |
| Total Pendapatan Driver (biaya antar) | Rp23.200 (terpisah dari COD) |

**Kapasitas Pesanan Per Tipe Layanan:**

| Tipe Layanan | Max Pesanan per Trip |
|-------------|---------------------|
| SPX Instant (Marketplace/Warehouse) | 1-5 pesanan |
| ShopeeFood | 1-2 pesanan |
| SPX Sameday (Marketplace) | 8-10 pesanan |

**Sistem Bonus Harian Jabodetabek:**

| Komponen | Poin |
|----------|------|
| Poin Dasar (pesanan pertama dalam trip) | +100 |
| Poin Tambahan Gabungan (pesanan ke-2 dst) | +50 per pesanan |
| Volume Sameday 2-3 pesanan/trip | +50 |
| Volume Sameday 4-6 pesanan/trip | +100 |
| Volume Sameday 7-8 pesanan/trip | +300 |
| Volume Sameday 9-10 pesanan/trip | +550 |
| Volume Sameday 11-12 pesanan/trip | +700 |

Kriteria kualifikasi: Rating ≥ 4.70, Tingkat Pencapaian ≥ 75%. Bonus masuk saldo keesokan hari.

**Contoh Perhitungan: 1 Trip SPX Instant Gabungan (3 pesanan):**
- Poin Dasar (pesanan pertama): +100
- Poin Tambahan (pesanan ke-2 dan ke-3): +50 × 2 = +100
- Total: 200 poin

### Pola Data Penting

- **Pesanan Gabungan** = 2+ pesanan dalam 1 trip (ditandai badge kuning di riwayat)
- **1 pesanan** = tanpa label (polos)
- **Alamat lengkap** hanya tersedia saat order masuk (sebelum diterima), hilang setelah selesai
- **Riwayat pesanan** menyembunyikan alamat, tapi nama restoran (ShopeeFood) tetap terlihat
- **Timeline per pesanan**: Diterima → Tiba → Diambil → Selesai (masing-masing dengan timestamp)
- **Algoritma Shopee trust-based**: driver yang sering pickup di seller/area tertentu lebih diprioritaskan mendapat order di area tersebut

---

## §3. User Persona

- **Nama (fiktif):** Adi
- **Usia:** 28 tahun
- **Pekerjaan:** Shopee Driver full-time (SPX Instant, ShopeeFood, SPX Sameday)
- **Area kerja:** Jabodetabek (khususnya Jakarta Barat dan sekitarnya)
- **Device:** HP Android mid-range
- **Jam kerja:** Tidak tentu, biasanya mulai pagi tapi bisa berubah-ubah
- **Tech literacy:** Sedang — familiar dengan HP Android, menggunakan Shopee Driver App setiap hari, bisa install dan setup aplikasi dengan panduan

**Kebiasaan kerja:**
- Setelah 1 trip selesai, langsung terima trip berikutnya jika area akhir ramai
- Tidak punya jadwal kerja tetap — fleksibel
- Di akhir hari kerja, biasanya hanya cek penghasilan sekilas di riwayat Shopee
- Tidak mencatat penghasilan/pengeluaran karena merepotkan

**Kebiasaan keuangan:**
- Punya beberapa hutang aktif: cicilan motor, pinjol (berbunga + denda harian), pinjaman dari kerabat/teman
- Pengeluaran rutin saat kerja: bensin, makan, rokok, parkir
- Pengeluaran tetap bulanan: pulsa, listrik, dll
- Hutang bersifat dinamis — bisa berubah, bertambah, berkurang

**Pain point utama:**
- Data orderan harian yang berharga terbuang karena tidak ada cara mudah mengolahnya
- Tidak tahu apakah penghasilan hari ini cukup untuk menutup semua kewajiban
- Tidak punya strategi kerja — hanya menjalankan rutinitas tanpa arah
- Merasa tidak ada kemajuan dalam hidup meskipun bekerja setiap hari

---

## §4. Fitur

### §4.1 MUST — Harus Ada di Versi Pertama

| ID | Nama | Deskripsi |
|----|------|-----------|
| F001 | Auto Capture Order Masuk | Aplikasi otomatis capture layar saat notifikasi order Shopee masuk. Data alamat pickup, delivery, seller, customer, parcel, payment tersimpan otomatis di belakang layar tanpa mengganggu driver |
| F002 | Capture Riwayat & Rincian Pesanan | Di akhir hari, driver bisa rekam layar saat scroll riwayat + rincian pesanan, ATAU aplikasi auto-capture lewat izin melihat layar app lain. Data pendapatan, poin, COD, timeline tersimpan |
| F003 | AI Ekstraksi Data dari Capture | AI membaca semua screenshot/rekaman layar dan mengekstrak data terstruktur: alamat, nominal, waktu, kode pesanan, tipe layanan. Lalu mencocokkan data order masuk (F001) dengan rincian selesai (F002) berdasarkan kode order |
| F004 | Input Pengeluaran Cepat | Tap kategori (bensin, makan, rokok, parkir, pulsa, dll) → tap nominal preset (10rb, 15rb, 20rb, 25rb, dst) → tersimpan. Target: di bawah 3 detik per pencatatan |
| F005 | Dashboard Harian | Ringkasan hari ini: total pemasukan, total pengeluaran, profit bersih, jumlah trip, jumlah pesanan, poin bonus, pencapaian target harian |
| F006 | Manajemen Hutang | Catat, edit, hapus hutang (cicilan motor, pinjol, pinjaman personal). Support denda harian untuk pinjol/cicilan. Sifat dinamis — bisa diubah kapan saja |
| F007 | Target Harian Otomatis | Menghitung target harian berdasarkan total kewajiban bulanan (cicilan hutang dari F006 + biaya tetap dari F009) ÷ sisa hari kerja (dari F009). Memperhitungkan deadline terdekat. Tampil di dashboard: "Hari ini minimal harus dapat Rp XXX" |
| F008 | AI Chat — Tanya Kapan Saja | Driver bisa tanya langsung ke AI dalam bahasa sehari-hari: "Gimana performa hari ini?", "Minggu ini udah cukup belum buat bayar cicilan?", dsb. AI jawab berdasarkan data yang sudah terkumpul |
| F009 | Kewajiban & Jadwal | Kelola data kewajiban bulanan (biaya tetap seperti pulsa, listrik, kontrakan) dan jadwal kerja mingguan driver. Termasuk Mode Ambisius untuk percepat pelunasan hutang. Data di sini dipakai F007 untuk hitung target harian. Dipecah dari F007 |

### §4.2 SHOULD — Penting Tapi Bisa Ditunda

| ID | Nama | Deskripsi |
|----|------|-----------|
| F010 | Laporan Mingguan & Bulanan | Rekap pendapatan, pengeluaran, profit, jumlah trip per minggu/bulan. Breakdown per kategori pengeluaran. Tren naik/turun |
| F011 | Analisa Area & Waktu | AI analisa dari data historis: area mana paling menguntungkan (rata-rata per trip), jam berapa paling produktif, hari apa paling ramai — berdasarkan data personal driver |
| F012 | Peringatan Jatuh Tempo Hutang | Notifikasi otomatis saat cicilan/pinjol mendekati jatuh tempo. Prioritas level: sudah lewat (darurat), besok (peringatan), minggu ini (info) |
| F013 | ~~Biaya Tetap Bulanan~~ | ~~Sudah termasuk dalam F009 (Kewajiban & Jadwal)~~ |
| F014 | Analisa Poin & Bonus | AI menghitung dan menampilkan: total poin hari ini, estimasi bonus harian, seberapa dekat dengan target pencapaian bonus Shopee |

### §4.3 FUTURE — Ide Pengembangan ke Depan

| ID | Nama | Deskripsi |
|----|------|-----------|
| F015 | Analisa Prioritas Area (Algoritma Shopee) | AI mempelajari pola: seller/area mana yang sering memberi order berulang (trust-based). Rekomendasi: "Pertahankan area X, kamu sudah dipercaya di sana" |
| F016 | Prediksi Penghasilan | AI prediksi penghasilan minggu/bulan depan berdasarkan tren historis. "Jika pola kerja sama, bulan depan estimasi Rp X" |
| F017 | Multi-Driver (Multi-User) | Aplikasi bisa digunakan oleh banyak driver, masing-masing punya akun dan data sendiri |
| F018 | Export Data | Unduh semua data ke CSV/Excel untuk analisa lebih lanjut |
| F019 | Simulasi "What If" | Driver bisa tanya: "Kalau aku kerja 2 jam lebih lama per hari, kapan hutang lunas?" — AI hitung dan jawab |
| F020 | AI Insight Otomatis | AI otomatis kirim rangkuman di akhir hari/minggu tanpa diminta: pola produktivitas, perbandingan dengan minggu lalu, peringatan pengeluaran naik, progress hutang |

### §4.4 NON-Goals

- Bukan aplikasi navigasi/peta — sudah ada di Shopee & Google Maps
- Bukan pengganti dashboard Shopee Driver — mengambil data dari sana, bukan menggantikan
- Bukan aplikasi untuk terima/tolak order — itu tetap di Shopee
- Bukan aplikasi sosial media atau komunitas driver
- Bukan aplikasi investasi atau trading
- Bukan duplikasi fitur "area ramai" Shopee — fokus ke analisa personal

---

## §5. User Interface

### §5.1 Prinsip Desain

1. **Satu tangan, satu jempol** — driver sering pegang HP dengan 1 tangan. Semua tombol penting harus bisa dijangkau jempol (bagian bawah layar).
2. **Tap > Ketik** — minimalkan keyboard. Gunakan preset button, kategori grid, dan pilihan tap.
3. **Glanceable** — informasi penting (target harian, profit) harus terbaca dalam 2 detik tanpa scroll.
4. **Background first** — fitur utama (auto capture) berjalan di belakang layar tanpa interaksi driver.
5. **Bahasa sehari-hari** — tidak ada jargon keuangan atau teknis. Semua teks ditulis seperti ngobrol.

### §5.2 Daftar Layar

| # | Nama Layar | Fungsi |
|---|-----------|--------|
| 1 | Setup Awal | Aktivasi izin (notifikasi, melihat layar app lain, rekam layar) + input data hutang (F006), biaya tetap dan hari kerja (F009) pertama kali |
| 2 | Dashboard (Home) | Ringkasan harian: pemasukan, pengeluaran, profit, target, jumlah trip, poin bonus |
| 3 | Input Pengeluaran | Grid kategori + preset nominal untuk catat pengeluaran cepat |
| 4 | Data Order | List semua order hari ini yang ter-capture, bisa lihat detail per order |
| 5 | AI Chat | Halaman chat dengan AI — tanya apa saja tentang performa dan strategi |
| 6 | Hutang | List semua hutang aktif, progress, tambah/edit/hapus |
| 8 | Kewajiban & Jadwal | Kelola biaya tetap bulanan, jadwal kerja mingguan, mode ambisius |
| 7 | Capture Manager | Status auto-capture, list screenshot yang belum diproses, trigger manual rekam layar |

### §5.3 User Flow Per Fitur Utama

**F001 — Auto Capture Order Masuk:**
1. Driver mengaktifkan izin saat setup awal (1x saja)
2. Aplikasi berjalan di belakang layar
3. Notifikasi order Shopee masuk
4. Driver buka Shopee dan lihat order detail seperti biasa
5. Aplikasi otomatis capture layar di belakang layar
6. Driver terima/tolak order seperti biasa
7. Data tersimpan di HP
Total interaksi driver: 0 (sepenuhnya otomatis setelah setup)

**F002 — Capture Riwayat & Rincian:**
1. Di akhir hari, driver buka Shopee seperti biasa
2. Driver buka riwayat pesanan dan scroll/klik rincian
3. Aplikasi auto-capture lewat izin melihat layar di belakang layar
4. ATAU: driver tap "Mulai Rekam" di aplikasi, lalu scroll riwayat Shopee, lalu tap "Selesai"
5. Data tersimpan di HP, AI proses di belakang layar
Total interaksi driver: 0 (auto) atau 2 tap (manual record)

**F003 — AI Ekstraksi Data:**
1. AI membaca semua capture yang tersimpan
2. AI membaca tulisan dari screenshot dan mengekstrak: alamat, nominal, waktu, kode pesanan, tipe layanan
3. AI mencocokkan data F001 (alamat) dengan F002 (pendapatan) berdasarkan kode order
4. Data terstruktur tersimpan di HP
5. Jika ada data yang ambigu, AI tandai untuk dikonfirmasi driver
Total interaksi driver: 0 (atau minimal jika ada konfirmasi)

**F004 — Input Pengeluaran Cepat:**
1. Driver tap tombol "+" atau "Catat Pengeluaran" di dashboard
2. Muncul grid kategori: ⛽ Bensin, 🍜 Makan, 🚬 Rokok, 🅿️ Parkir, dll
3. Driver tap 1 kategori
4. Muncul deretan tombol nominal: 10rb, 15rb, 20rb, 25rb, 30rb, 50rb, custom
5. Driver tap 1 nominal → tersimpan, kembali ke dashboard
Total interaksi: 3 tap, < 3 detik

**F005 — Dashboard Harian:**
1. Driver buka app → langsung masuk Dashboard (tanpa splash/halaman perantara)
2. Dashboard menampilkan: total pemasukan, total pengeluaran, profit bersih, jumlah trip, jumlah pesanan, poin bonus, pencapaian target harian
3. Jika belum ada data hari ini → tetap tampil target harian + info ringkasan data kemarin sebagai pembanding
4. Driver tap salah satu angka (misal "Pemasukan") → masuk ke halaman detail (list order hari ini)
5. Driver tap "Pengeluaran" → masuk ke list pengeluaran hari ini
6. Driver tap "Hutang" atau progress target → masuk ke halaman Hutang
Total interaksi: 1 tap untuk lihat detail, atau 0 jika cuma glance

**F006 — Manajemen Hutang:**
1. Driver masuk ke halaman Hutang (dari dashboard atau navigasi bawah)
2. Tampil list kartu hutang aktif — tiap kartu menampilkan: nama hutang, progress bar pelunasan (misal "Rp 3.600.000 / Rp 12.000.000 — 30%"), jumlah cicilan berikutnya, tanggal jatuh tempo. Untuk pinjol: ada tanda ⚠️ dan info denda per hari
3. Driver tap "Tambah Hutang" → muncul form langkah per langkah: pilih jenis (Cicilan Motor / Pinjol / Pinjaman Personal / Custom) → isi nama → isi total hutang → isi cicilan per bulan → isi tanggal jatuh tempo → (jika pinjol) isi denda per hari → selesai
4. Driver tap kartu hutang yang sudah ada → masuk halaman detail hutang (info lengkap + riwayat pembayaran)
5. Di halaman detail, driver bisa tap "Edit" untuk ubah data atau "Hapus" untuk menghapus hutang
6. Jika hutang dihapus/diubah → target harian otomatis diperbarui
Total interaksi: tap kartu untuk detail, tap tombol untuk tambah/edit/hapus

**F007 — Target Harian Otomatis:**
1. Target dihitung otomatis dari: cicilan hutang (F006) + biaya tetap (F009) + jadwal kerja (F009). Driver tidak perlu input manual — semua data sudah ada dari fitur lain
2. App menghitung: sisa kewajiban bulan ini ÷ sisa hari kerja = target per hari, disesuaikan deadline terdekat
3. Target tampil di Dashboard: "Hari ini minimal harus dapat Rp XXX"
4. Jika profit hari ini melewati target → angka berubah hijau + muncul info: "✅ Target tercapai!"
5. Jika data hutang berubah (F006) atau biaya tetap/jadwal berubah (F009) → target otomatis ikut berubah
6. Driver bisa aktifkan Mode Ambisius (F009) untuk percepat pelunasan hutang → target harian naik
Total interaksi: 0 (otomatis setelah setup), atau ketik di AI Chat jika mau ubah

**F008 — AI Chat:**
1. Driver tap tab "AI" di navigasi bawah
2. Muncul layar chat
3. Driver ketik atau voice: "Gimana performa hari ini?"
4. AI jawab berdasarkan data: "Hari ini 8 trip, dapat Rp 156.000, target Rp 180.000. Kurang Rp 24.000 lagi."
5. Driver bisa lanjut tanya follow-up
Total interaksi: ketik/bicara, seperti chat biasa

**F009 — Kewajiban & Jadwal:**
1. Driver buka tab "Lain" → tap "Biaya Tetap Bulanan" → set biaya rutin (pulsa, listrik, kontrakan, dll) dengan template atau manual
2. Driver buka tab "Lain" → tap "Jadwal Kerja" → toggle hari mana narik dan mana libur (default: semua narik)
3. Opsional: di halaman Detail Target (F007), driver bisa aktifkan Mode Ambisius → pilih mau lunas hutang dalam X bulan → target harian otomatis naik
4. Semua data di F009 otomatis dipakai F007 untuk hitung target harian

Total interaksi: tap untuk set data, lalu tidak perlu buka lagi kecuali ada perubahan

---

## §6. Technical Constraints

- **Platform:** Android only (HP)
- **Budget:** Sebisa mungkin gratis. API LLM berbayar diperbolehkan.
- **Larangan:** TIDAK menggunakan Google Cloud (sulit pendaftaran bagi user)
- **Performa:**
  - App terbuka < 2 detik
  - Auto capture di belakang layar tidak mengganggu performa HP dan Shopee Driver App
  - AI Chat merespons < 5 detik
  - AI membaca tulisan dari screenshot selesai dalam < 10 detik per gambar
- **Penyimpanan:** Data utama tersimpan di HP (bisa dipakai tanpa internet)
- **AI Processing:** Bisa di HP atau lewat internet, yang penting cepat dan tidak mahal

---

## §7. Success Metrics

| # | Metrik | Target |
|---|--------|--------|
| 1 | Auto capture berhasil menangkap data order | > 90% orderan ter-capture tanpa intervensi manual |
| 2 | Waktu input pengeluaran | < 3 detik per pencatatan |
| 3 | Data order terstruktur akurat | > 85% data terekstrak benar oleh AI |
| 4 | Penggunaan harian | Driver buka app minimal 4 dari 5 hari kerja (retention > 80%) |
| 5 | Target harian terpantau | Driver melihat dashboard > 3x per hari |
| 6 | Hutang terkelola | Semua hutang aktif tercatat dan progress terpantau |

---

## §8. Assumptions & Risks

### Assumptions
- Driver menggunakan HP Android dengan RAM cukup untuk menjalankan app di belakang layar
- Driver bersedia memberikan izin melihat layar app lain dan akses notifikasi saat setup
- Tampilan Shopee Driver App relatif konsisten (tidak drastis berubah layout setiap minggu)
- Driver bekerja minimal 5 hari per minggu sehingga data cukup untuk analisa AI
- Koneksi internet tersedia untuk proses AI (meskipun input bisa tanpa internet)

### Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| Shopee update tampilan drastis → auto capture gagal baca | Tinggi — data tidak masuk | AI harus adaptif. Sediakan fallback manual (rekam layar). Monitoring ketat saat Shopee update |
| Android membatasi app di belakang layar (battery optimization) | Tinggi — capture tidak jalan | Panduan setup yang jelas untuk whitelist app dari battery optimization. Deteksi dan notifikasi jika service mati |
| Izin melihat layar app lain ditolak/dicabut user | Tinggi — fitur utama tidak berfungsi | Onboarding yang jelas menjelaskan kenapa izin dibutuhkan. Fallback ke screenshot manual |
| API LLM berbayar makin mahal | Sedang — biaya operasional naik | Pilih provider dengan pricing transparan. Optimasi prompt agar hemat token. Pertimbangkan model lokal di masa depan |
| AI salah baca tulisan dari screenshot | Sedang — data tidak reliable | Mekanisme konfirmasi untuk data ambigu. Continuous improvement model AI |
| Google Play reject karena izin melihat layar app lain | Sedang — tidak bisa publish | Pastikan penggunaan sesuai policy. Sediakan opsi distribusi APK langsung jika perlu |
