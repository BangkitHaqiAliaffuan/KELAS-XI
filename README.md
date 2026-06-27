# Kelas XI RPL — Portfolio Pembelajaran

Repository ini berisi **50+ project** yang dikerjakan selama kelas XI SMK jurusan **RPL (Rekayasa Perangkat Lunak) / Software Engineering**. Setiap project independen dan memiliki tech stack masing-masing.

## Struktur Project

| Direktori | Deskripsi | Teknologi |
|-----------|-----------|-----------|
| `LKS 2025/` | Latihan Lomba Kompetensi Siswa (LKS) — 20+ mini project | React, Vite, Laravel |
| `Tugas Akhir Semester 1/` | Aplikasi manajemen sampah "TrashCare" dengan klasifikasi AI | Laravel 12, React, Android, TensorFlow |
| `Tugas PKL/` | Project Praktik Kerja Lapangan (internship) — portal pemerintah, navigator rumah sakit, dll | React, Next.js 16, Laravel, Three.js |
| `Tugas PPKN/` | Project Pendidikan Pancasila dan Kewarganegaraan | Next.js 16, shadcn/ui, Tailwind v4 |
| `Tugas Luar RPL/` | Project ekstrakurikuler dan tugas luar jurusan | Laravel 13, React, Next.js |
| `Tugas Video/` | Project tutorial dari video — Android & Jetpack Compose | Kotlin, Android Studio |

## Tech Stack

| Area | Teknologi |
|------|-----------|
| **Frontend** | React 18/19, Next.js 16, Vite, TypeScript, Tailwind CSS, shadcn/ui |
| **Backend** | Laravel 8.2–13, PHP, Filament Admin 3.3–5.4, Sanctum |
| **Mobile** | Android (Kotlin), Jetpack Compose |
| **AI/ML** | TensorFlow Python (klasifikasi sampah) |
| **Lainnya** | Three.js, GSAP, Firebase, WeatherAPI |

## Cara Menjalankan

Setiap project berdiri sendiri. Masuk ke direktori masing-masing, lalu:

```bash
# React / Vite / Next.js
cd LKS\ 2025/nama-project
npm install     # atau pnpm install jika ada pnpm-lock.yaml
npm run dev

# Laravel
cd Tugas\ PKL/nama-project
composer install
cp .env.example .env
php artisan key:generate
php artisan serve

# Android
# Buka dengan Android Studio, biarkan Gradle sync, lalu run
```

## Catatan

- Package manager bervariasi (`npm` / `pnpm`). Cek lockfile sebelum install.
- Versi Laravel dan Vite berbeda di tiap project. Selalu cek `composer.json` / `package.json`.
- Repository ini adalah **portofolio pembelajaran** — dokumentasi perkembangan skill selama kelas XI.
