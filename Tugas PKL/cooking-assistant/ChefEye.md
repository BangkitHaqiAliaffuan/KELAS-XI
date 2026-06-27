**ChefEye AI**

Product Requirements Document

| Versi    | **1.0 - MVP**              |
| -------- | -------------------------- |
| Status   | **In Development**         |
| Tanggal  | **Mei 2026**               |
| Platform | **Web PWA (Mobile-first)** |

# **1\. Executive Summary**

ChefEye AI adalah asisten memasak multimodal berbasis web yang dirancang untuk memberikan bimbingan memasak secara real-time dan hands-free. Produk ini menjembatani celah antara resep teks statis dengan eksekusi dinamis di dapur menggunakan integrasi Computer Vision dan AI Voice.

| **Proposisi Nilai Utama**                                                                |
| ---------------------------------------------------------------------------------------- |
| Pengguna dapat memasak tanpa menyentuh layar, seluruhnya dikendalikan melalui suara.     |
| AI mengidentifikasi bahan secara visual dan memberikan instruksi kontekstual berikutnya. |
| Konteks percakapan dipertahankan sepanjang sesi memasak untuk pengalaman yang kohesif.   |
| Dibangun dengan filosofi zero-budget - tidak ada biaya operasional untuk skala MVP.      |
|                                                                                          |

# **2\. Problem Statement**

## **2.1 Masalah Utama Pengguna**

- Memasak sambil melihat layar ponsel sangat merepotkan karena tangan kotor atau basah.
- Pemula kesulitan mengidentifikasi bahan atau teknik memotong yang benar tanpa bimbingan visual.
- Resep teks statis tidak responsif terhadap kondisi aktual di dapur pengguna.
- Tidak ada solusi yang menggabungkan vision, voice, dan context dalam satu pengalaman seamless.

## **2.2 Dampak Masalah**

| **Segmen Pengguna**                | **Pain Point**                               | **Dampak**                               |
| ---------------------------------- | -------------------------------------------- | ---------------------------------------- |
| Pemula memasak                     | Tidak bisa identifikasi bahan segar vs busuk | Makanan gagal, bahan terbuang            |
| Koki rumahan aktif                 | Harus menyentuh layar berkali-kali           | Layar kotor, alur masak terganggu        |
| Pengguna dengan keterbatasan fisik | Sulit mengoperasikan app konvensional        | Eksklusi dari pengalaman memasak mandiri |

# **3\. Goals & Success Metrics**

## **3.1 Tujuan Bisnis**

- Meluncurkan MVP yang fungsional dalam 8 minggu pertama pengembangan.
- Mendapatkan 500 pengguna aktif bulanan dalam 3 bulan pertama pasca-launch.
- Mempertahankan zero operational cost hingga pengguna mencapai 1.000 MAU.
- Membangun fondasi teknis yang dapat di-scale ke fitur premium di masa depan.

## **3.2 Key Success Metrics**

| **Metrik**                 | **Definisi**                                | **Target MVP** | **Target v1.1** |
| -------------------------- | ------------------------------------------- | -------------- | --------------- |
| **Vision Latency**         | Waktu dari snapshot ke respons AI           | **< 2 detik**  | **< 1 detik**   |
| **Ingredient Accuracy**    | % identifikasi bahan yang benar             | **\> 85%**     | **\> 93%**      |
| **Session Completion**     | % sesi yang selesai sampai langkah terakhir | **\> 60%**     | **\> 75%**      |
| **Voice Recognition Rate** | % perintah suara yang dipahami dengan benar | **\> 80%**     | **\> 90%**      |

# **4\. User Personas**

## **4.1 Persona Utama - Pemula Antusias**

<div class="joplin-table-wrapper"><table><tbody><tr><th><p><strong>Rina, 24 tahun - Karyawan Kantoran</strong></p><p>Baru belajar masak sejak kos. Sering gagal karena tidak tahu takaran yang benar atau bahan yang sudah tidak segar.</p></th><th><p><strong>Goals:</strong></p><ul><li>Masak tanpa takut gagal</li><li>Tahu bahan masih layak pakai atau tidak</li></ul><p><strong>Pain Points:</strong></p><ul><li>Tidak bisa pegang HP saat tangan berminyak</li><li>Resep di YouTube tidak bisa di-pause mudah</li></ul></th></tr></tbody></table></div>

## **4.2 Persona Sekunder - Koki Rumahan Aktif**

<div class="joplin-table-wrapper"><table><tbody><tr><th><p><strong>Budi, 38 tahun - Memasak untuk Keluarga</strong></p><p>Rutin memasak 5x seminggu. Mahir tapi ingin eksplorasi resep baru dengan bahan yang ada di kulkas.</p></th><th><p><strong>Goals:</strong></p><ul><li>Generate resep dari bahan yang tersedia</li><li>Bimbingan hands-free tanpa gangguan</li></ul><p><strong>Pain Points:</strong></p><ul><li>App resep tidak fleksibel dengan bahan improvisasi</li><li>Tidak ada yang ingat progres memasak sebelumnya</li></ul></th></tr></tbody></table></div>

# **5\. Feature Requirements**

## **5.1 Feature Registry**

Prioritas: P0 = Must Have (MVP blocker), P1 = Should Have, P2 = Nice to Have

| **ID**   | **Fitur**                 | **Deskripsi**                                                                                                                                               | **Prior.** | **Target** |
| -------- | ------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------- | ---------- |
| **F-01** | **Real-time Vision ID**   | Kamera mengidentifikasi bahan makanan secara instan menggunakan Groq Vision API. Menampilkan nama bahan dan confidence score sebagai overlay di viewfinder. | **P0**     | **MVP**    |
| **F-02** | **Voice Wake Word**       | Pengguna mengaktifkan asisten dengan kata kunci 'Hey Chef' tanpa menyentuh layar. Sistem selalu mendengarkan di background saat sesi aktif.                 | **P0**     | **MVP**    |
| **F-03** | **Step-by-Step Guidance** | AI memberikan instruksi memasak per langkah secara terurut. Setiap instruksi disesuaikan dengan konteks visual yang terdeteksi di kamera.                   | **P0**     | **MVP**    |
| **F-04** | **Contextual Memory**     | AI mengingat bahan yang sudah diproses dalam sesi berjalan. State disimpan di Zustand dan diinjeksikan ke setiap API call sebagai system context.           | **P0**     | **MVP**    |
| **F-05** | **TTS Response**          | Seluruh instruksi AI disuarakan melalui Web Speech API (speechSynthesis). Pengguna tidak perlu membaca layar selama memasak.                                | **P0**     | **MVP**    |
| **F-06** | **Recipe Browser**        | Pengguna dapat mencari dan memilih resep dari TheMealDB API. Tersedia filter berdasarkan kategori dan bahan utama.                                          | **P0**     | **MVP**    |
| **F-07** | **Ingredient Scanner**    | Mode scan khusus untuk mengidentifikasi semua bahan sebelum memasak. Hasil ditampilkan sebagai card dengan info kesegaran.                                  | **P1**     | **MVP**    |
| **F-08** | **Local Recipe Cache**    | Resep yang pernah dibuka di-cache di IndexedDB untuk akses offline. 20-30 resep Indonesia populer dibundle sebagai static JSON.                             | **P1**     | **MVP**    |
| **F-09** | **Improvisation Mode**    | User mengarahkan kamera ke semua bahan di kulkas, AI generate resep on-the-fly berdasarkan visual input tanpa teks.                                         | **P1**     | **v1.1**   |
| **F-10** | **Freshness Detective**   | AI menilai kesegaran visual bahan (layu, busuk, segar) dan memberikan rekomendasi apakah bahan layak digunakan.                                             | **P1**     | **v1.1**   |
| **F-11** | **Cooking Session Log**   | Setiap sesi memasak tersimpan dengan timestamp dan foto bahan. User dapat review sesi sebelumnya untuk analisis.                                            | **P2**     | **v1.1**   |
| **F-12** | **Barcode Scanner**       | Integrasi Open Food Facts API untuk identifikasi produk kemasan melalui scan barcode. Menampilkan komposisi dan info nutrisi.                               | **P2**     | **v2.0**   |
| **F-13** | **Regional Dialect**      | Dukungan perintah suara dalam bahasa daerah (Jawa, Sunda) sebagai diferensiasi pasar Indonesia.                                                             | **P2**     | **v2.0**   |

# **6\. Technical Architecture**

## **6.1 Tech Stack**

| **Layer**                | **Teknologi**                             | **Justifikasi**                                                       |
| ------------------------ | ----------------------------------------- | --------------------------------------------------------------------- |
| **Frontend**             | Next.js (App Router) + Tailwind CSS       | SSR + PWA support out-of-the-box, mobile-first                        |
| **State Management**     | Zustand + IndexedDB (via zustand/persist) | Local-first, zero backend, works offline                              |
| **Vision AI (Primary)**  | Groq API + Llama 4 Scout 17b              | Inferensi visual tercepat (~500 tok/s), free tier generous            |
| **Vision AI (Fallback)** | Google Gemini 1.5 Flash                   | Digunakan hanya saat Groq rate limit tercapai                         |
| **Voice Engine**         | Web Speech API (STT + TTS)                | Native browser API, zero cost, no latency overhead                    |
| **Recipe Data**          | TheMealDB API + Local JSON                | TheMealDB 100% gratis tanpa API key, JSON lokal untuk resep Indonesia |
| **Deployment**           | Vercel (Free Tier, PWA enabled)           | Serverless, edge network global, CI/CD otomatis dari GitHub           |

## **6.2 Data Architecture**

ChefEye AI menggunakan pendekatan local-first. Seluruh state runtime dikelola di client menggunakan Zustand, dengan persistence ke IndexedDB untuk data yang perlu bertahan antar sesi (inventory bahan). Session data bersifat volatile dan direset setiap sesi baru dimulai.

| **Prinsip Local-First Architecture**                                                                        |
| ----------------------------------------------------------------------------------------------------------- |
| Session state (Zustand): volatile, reset setiap sesi - currentStep, processedIngredients, lastVisualContext |
| Inventory (IndexedDB): persisten antar sesi - daftar bahan yang pernah dideteksi dan dikonfirmasi           |
| Recipe cache (IndexedDB): resep yang pernah dibuka di-cache untuk akses offline                             |
| Semua API call ke Groq menyertakan session context sebagai system prompt injection                          |
| Upgrade ke Supabase sync hanya dilakukan jika ada kebutuhan multi-device atau social features               |
|                                                                                                             |

## **6.3 Context Management Strategy**

Setiap API call ke Groq menyertakan objek cookingSession sebagai bagian dari system prompt. Ini memastikan AI selalu sadar konteks sesi tanpa memerlukan conversation history yang panjang.

## **6.4 Vision Optimization**

- Pixel diff detection: frame dikirim ke API hanya jika terdapat perubahan visual > 15% dari frame sebelumnya.
- Frame compression: snapshot diresize ke 512x512 sebelum dikirim untuk meminimalkan bandwidth.
- Snapshot interval: minimum 3 detik antara dua API call vision untuk menghindari rate limit.
- Bounding box rendering dilakukan di frontend menggunakan Canvas API, bukan dari API response.

# **7\. UX & Accessibility Requirements**

## **7.1 Core UX Principles**

- **Setiap aksi utama harus dapat dilakukan tanpa menyentuh layar.** Hands-free first:
- **Informasi kritis harus dapat dibaca dalam 1 detik dari jarak lengan.** Glanceability:
- **Pesan error harus actionable dan dalam bahasa yang ramah (bukan kode teknis).** Error recovery:
- **App harus tetap berfungsi minimal untuk resep yang sudah di-cache meskipun tanpa koneksi.** Offline resilience:

## **7.2 Screen Hierarchy**

- Home Screen - Entry point, CTA tunggal, 3 feature highlight.
- Active Cooking Screen - Layar utama. Camera viewfinder mendominasi 60% area. Step card floating di bawah.
- Ingredient Scanner Screen - Full-screen camera mode dengan hasil identifikasi sliding dari bawah.
- Recipe Browser Screen - Grid resep dengan search dan filter kategori.
- Recipe Detail Screen - Hero image, ingredient checklist, step timeline, sticky CTA.

## **7.3 Accessibility Standards**

| **Standar**             | **Implementasi**                                                           |
| ----------------------- | -------------------------------------------------------------------------- |
| **Ukuran touch target** | Minimum 44x44px untuk semua elemen interaktif, 56px untuk CTA utama        |
| **Kontras warna**       | Minimum 4.5:1 untuk body text (WCAG AA), 3:1 untuk large text              |
| **Ukuran font minimum** | 16px untuk body text, 14px untuk label. Step instruction menggunakan 20px+ |
| **Screen reader**       | Semua overlay kamera harus memiliki aria-label yang deskriptif             |
| **Reduced motion**      | Semua animasi harus menghormati prefers-reduced-motion media query         |
| **Focus indicator**     | 2px solid amber (#F5A623), 2px offset untuk navigasi keyboard yang visible |

# **8\. Development Roadmap**

## **8.1 Sprint Plan - MVP (8 Minggu)**

| **Sprint** | **Durasi** | **Deliverable**                                                                                   | **Fitur (ID)** |
| ---------- | ---------- | ------------------------------------------------------------------------------------------------- | -------------- |
| **S1**     | Minggu 1-2 | Foundation: Next.js setup, Zustand store, session context object, UI shell semua screen           | F-04, F-08     |
| **S2**     | Minggu 3-4 | Vision Core: Groq API integration, pixel diff detection, camera overlay, ingredient chip          | F-01, F-07     |
| **S3**     | Minggu 5-6 | Voice Engine: Wake word, STT intent parsing, TTS response, voice state machine                    | F-02, F-05     |
| **S4**     | Minggu 7-8 | Recipe & Polish: TheMealDB integration, step-by-step guidance, testing, PWA config, Vercel deploy | F-03, F-06     |

# **9\. Risks & Mitigations**

| **Risiko**                                        | **Dampak** | **Mitigasi**                                                                                                          |
| ------------------------------------------------- | ---------- | --------------------------------------------------------------------------------------------------------------------- |
| Groq rate limit tercapai saat jam sibuk           | **Tinggi** | Request queue + automatic fallback ke Gemini. Pixel diff mengurangi frekuensi API call 70%.                           |
| Web Speech API tidak konsisten di Firefox/Android | **Sedang** | Fallback ke whisper.cpp via WASM untuk offline STT. Tampilkan warning jika browser tidak support.                     |
| Akurasi rendah di dapur berasap/gelap             | **Sedang** | Tampilkan indikator kualitas cahaya. Saran verbal jika kondisi buruk: 'Cahaya kurang, coba dekatkan bahan ke cahaya.' |
| TheMealDB tidak memiliki resep Indonesia          | **Rendah** | Bundle 25-30 resep Indonesia populer sebagai static JSON lokal. TheMealDB untuk resep internasional.                  |
| Vercel bandwidth limit 100GB/bulan                | **Rendah** | Kompresi gambar agresif sebelum upload. PWA caching mengurangi request berulang.                                      |

# **10\. Open Questions & Decisions Pending**

| **Pertanyaan yang Perlu Dijawab Sebelum Sprint 3**                                                                |
| ----------------------------------------------------------------------------------------------------------------- |
| Q1: Apakah wake word 'Hey Chef' diproses sepenuhnya di client (whisper.cpp WASM) atau tetap via Web Speech API?   |
| Q2: Bahasa default instruksi AI - Bahasa Indonesia penuh, atau mixed dengan English untuk istilah teknis memasak? |
| Q3: Apakah sesi memasak perlu PIN atau passcode untuk mencegah reset tidak sengaja saat cooking mode aktif?       |
| Q4: Batas jumlah resep lokal Indonesia yang di-bundle - apakah 25 resep cukup untuk validasi pasar awal?          |
| Q5: Kapan threshold upgrade dari local-first ke Supabase hybrid? Apakah di 500 MAU atau ada trigger lain?         |
|                                                                                                                   |

# **11\. Appendix**

## **11.1 API Reference**

- TheMealDB: <https://www.themealdb.com/api.php> - Gratis, tanpa API key, 300+ resep
- Groq API: <https://console.groq.com> - Llama 4 Scout 17b, free tier 30 req/menit
- Gemini API: <https://ai.google.dev> - Gemini 1.5 Flash, fallback vision
- Open Food Facts: <https://world.openfoodfacts.org/api> - Barcode lookup, 3 juta+ produk

## **11.2 Design References**

- Figma/Stitch Design System: Dark theme, Amber #F5A623 accent, DM Sans typography
- Accessibility: WCAG 2.1 Level AA compliance target
- Responsive breakpoints: 375px (mobile primary), 768px (tablet), 1280px (desktop)

_- End of Document -_