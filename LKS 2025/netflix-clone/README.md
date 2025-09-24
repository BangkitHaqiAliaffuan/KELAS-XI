# Netflix Clone

Netflix clone yang dibangun menggunakan React.js, Firebase Authentication, dan TMDB API.

## 🚀 Fitur

- **Autentikasi Pengguna**: Login dan Sign Up menggunakan Firebase
- **Homepage**: Tampilan film berdasarkan kategori yang dapat digulir
- **Hero Banner**: Film unggulan dengan trailer
- **Detail Film**: Halaman detail dengan pemutaran trailer
- **Responsive Design**: Desain yang responsif untuk berbagai ukuran layar
- **Netflix-like UI**: Antarmuka yang mirip dengan Netflix

## 🛠️ Teknologi yang Digunakan

- **React.js**: Framework frontend
- **Firebase**: Autentikasi pengguna
- **TMDB API**: Data film dan trailer
- **React Router**: Navigasi antar halaman
- **CSS**: Styling dengan desain Netflix-like

## 📋 Prasyarat

Sebelum menjalankan aplikasi, pastikan Anda memiliki:

1. **Node.js** (versi 16 atau lebih baru)
2. **NPM** atau **Yarn**
3. **Akun Firebase** (untuk autentikasi)
4. **API Key TMDB** (untuk data film)

## ⚙️ Setup

### 1. Clone Repository

```bash
git clone <repository-url>
cd netflix-clone
```

### 2. Install Dependencies

```bash
npm install
```

### 3. Setup Firebase

1. Buat project baru di [Firebase Console](https://console.firebase.google.com/)
2. Enable **Authentication** dengan **Email/Password**
3. Dapatkan konfigurasi Firebase dari Project Settings

### 4. Setup TMDB API

1. Daftar di [The Movie Database (TMDB)](https://www.themoviedb.org/)
2. Dapatkan API Key dari [API Settings](https://www.themoviedb.org/settings/api)

### 5. Environment Variables

1. Copy file `.env.example` menjadi `.env`:
```bash
cp .env.example .env
```

2. Isi nilai yang diperlukan di file `.env`:
```env
# TMDB API Configuration
VITE_TMDB_API_KEY=your_tmdb_api_key_here
VITE_TMDB_BASE_URL=https://api.themoviedb.org/3

# Firebase Configuration
VITE_FIREBASE_API_KEY=your_firebase_api_key_here
VITE_FIREBASE_AUTH_DOMAIN=your_firebase_auth_domain_here
VITE_FIREBASE_PROJECT_ID=your_firebase_project_id_here
VITE_FIREBASE_STORAGE_BUCKET=your_firebase_storage_bucket_here
VITE_FIREBASE_MESSAGING_SENDER_ID=your_firebase_messaging_sender_id_here
VITE_FIREBASE_APP_ID=your_firebase_app_id_here
```

### 6. Jalankan Aplikasi

```bash
npm run dev
```

Aplikasi akan berjalan di `http://localhost:5173`

## 📂 Struktur Proyek

```
netflix-clone/
├── public/
│   └── vite.svg
├── src/
│   ├── assets/           # Gambar dan aset
│   │   ├── assets.js     # Exports semua aset
│   │   └── ...
│   ├── components/       # Komponen React
│   │   ├── Auth.css
│   │   ├── Hero.jsx
│   │   ├── Hero.css
│   │   ├── LoadingSpinner.jsx
│   │   ├── LoadingSpinner.css
│   │   ├── Login.jsx
│   │   ├── MovieRow.jsx
│   │   ├── MovieRow.css
│   │   ├── Navbar.jsx
│   │   ├── Navbar.css
│   │   └── SignUp.jsx
│   ├── context/          # Context providers
│   │   └── AuthContext.jsx
│   ├── pages/            # Halaman utama
│   │   ├── Home.jsx
│   │   ├── Home.css
│   │   ├── MovieDetail.jsx
│   │   └── MovieDetail.css
│   ├── utils/            # Utilities dan konfigurasi
│   │   ├── firebase.js   # Konfigurasi Firebase
│   │   └── tmdb.js       # API TMDB
│   ├── App.jsx           # Komponen utama dengan routing
│   ├── App.css
│   ├── index.css         # Global styles
│   └── main.jsx
├── .env                  # Environment variables
├── .env.example          # Template environment variables
└── package.json
```

## 🎬 Penggunaan

1. **Sign Up/Login**: Buat akun baru atau login dengan akun yang sudah ada
2. **Browse Movies**: Jelajahi film berdasarkan kategori di homepage
3. **Watch Trailers**: Klik film untuk melihat detail dan menonton trailer
4. **Navigation**: Gunakan navbar untuk navigasi dan logout

## 🌟 Fitur Utama

### Autentikasi
- Firebase Authentication dengan email/password
- Persistent login state
- Protected routes

### Homepage
- Hero banner dengan film unggulan
- Multiple movie categories:
  - Trending Now
  - Netflix Originals
  - Top Rated
  - Action Movies
  - Comedy Movies
  - Horror Movies
  - Romance Movies
  - Documentaries

### Movie Detail Page
- Detail lengkap film
- Pemutaran trailer YouTube
- Navigasi kembali ke homepage

### Responsive Design
- Mobile-first approach
- Breakpoints untuk tablet dan desktop
- Netflix-like styling dengan dark theme

## 🚀 Build untuk Production

```bash
npm run build
```

File build akan tersedia di folder `dist/`

## 📝 Notes

- Pastikan API keys disimpan dengan aman dan tidak di-commit ke repository
- Gunakan `.env.local` untuk development lokal
- Untuk production, set environment variables di hosting platform

## 🤝 Contributing

1. Fork repository
2. Buat feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push ke branch (`git push origin feature/AmazingFeature`)
5. Buat Pull Request

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.+ Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## React Compiler

The React Compiler is not enabled on this template. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.
