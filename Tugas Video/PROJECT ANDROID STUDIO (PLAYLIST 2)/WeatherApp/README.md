# 🌤️ Smart Weather App (Aplikasi Cuaca Cerdas)

Aplikasi cuaca modern yang dibangun dengan Android (Kotlin) dan menggunakan OpenWeatherMap API.

## 📱 Fitur Utama

- **🎨 UI Modern**: Desain yang clean dan responsif dengan Material Design 3
- **📍 Lokasi Otomatis**: Deteksi lokasi pengguna dengan GPS
- **🔍 Pencarian Kota**: Cari cuaca di kota mana saja
- **🌡️ Informasi Lengkap**: Suhu, kelembaban, kecepatan angin, dan waktu matahari terbit/terbenam
- **🎭 Animasi Cuaca**: Animasi Lottie yang indah sesuai kondisi cuaca
- **🎨 Background Dinamis**: Latar belakang berubah sesuai kondisi cuaca
- **🔄 Pull to Refresh**: Refresh data dengan mudah
- **🌓 Dark/Light Theme**: Otomatis menyesuaikan tema sistem

## 🛠️ Teknologi yang Digunakan

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI**: View Binding, Material Design 3
- **Network**: Retrofit 2, OkHttp
- **Location**: Google Play Services Location
- **Animation**: Lottie
- **Async**: Kotlin Coroutines
- **Lifecycle**: Android Architecture Components

## 🏗️ Setup Proyek

### 1. Clone Repository
```bash
git clone <repository-url>
cd WeatherApp
```

### 2. Mendapatkan API Key
1. Kunjungi [OpenWeatherMap](https://openweathermap.org/api)
2. Buat akun gratis
3. Pergi ke "My API Keys"
4. Copy API Key Anda

### 3. Konfigurasi API Key
Buka file `app/src/main/java/com/kelasxi/weatherapp/utils/Constants.kt` dan ganti:
```kotlin
const val WEATHER_API_KEY = "YOUR_API_KEY_HERE"
```
dengan API key Anda:
```kotlin
const val WEATHER_API_KEY = "your_actual_api_key_here"
```

### 4. Build dan Run
```bash
./gradlew assembleDebug
```
atau jalankan langsung dari Android Studio.

## 📂 Struktur Proyek

```
app/
├── src/main/
│   ├── java/com/kelasxi/weatherapp/
│   │   ├── model/          # Data models
│   │   ├── network/        # API services & Retrofit setup
│   │   ├── repository/     # Data repository
│   │   ├── utils/          # Utility classes
│   │   ├── viewmodel/      # ViewModels
│   │   ├── MainActivity.kt # Main Activity
│   │   └── SplashActivity.kt # Splash Screen
│   ├── res/
│   │   ├── drawable/       # Icons & backgrounds
│   │   ├── font/          # Custom fonts (Merriweather Sans)
│   │   ├── layout/        # XML layouts
│   │   ├── raw/           # Lottie animations
│   │   └── values/        # Colors, strings, themes
│   └── AndroidManifest.xml
```

## 🎨 Assets yang Digunakan

### Fonts
- **Merriweather Sans**: Font modern untuk tampilan yang elegan
  - `merriweathersans_regular.ttf`
  - `merriweathersans_medium.ttf`
  - `merriweathersans_semibold.ttf`
  - `merriweathersans_bold.ttf`

### Animations (Lottie)
- `sun.json` - Animasi cuaca cerah
- `cloud.json` - Animasi cuaca berawan
- `rain.json` - Animasi cuaca hujan
- `snow.json` - Animasi cuaca salju

### Icons & Backgrounds
- Weather icons: sunny, cloudy, rain, snow, etc.
- Dynamic backgrounds sesuai kondisi cuaca
- Modern UI icons untuk humidity, wind, temperature, etc.

## 🚀 Mode Demo

Aplikasi dapat berjalan dalam mode demo tanpa API key dengan data dummy. Ini berguna untuk testing UI dan development awal.

## 📱 Cara Penggunaan

1. **Splash Screen**: Aplikasi dimulai dengan splash screen yang elegant
2. **Permission**: Izinkan akses lokasi untuk deteksi otomatis
3. **Data Cuaca**: Lihat informasi cuaca lengkap untuk lokasi Anda
4. **Search**: Tap icon search untuk mencari cuaca di kota lain
5. **Refresh**: Swipe down untuk memperbarui data cuaca

## 🎯 Fitur Advanced

### 1. Location Management
- Auto-detect lokasi pengguna
- Fallback ke pencarian manual jika GPS tidak tersedia
- Permission handling yang smooth

### 2. Error Handling
- Network error handling
- API error responses
- User-friendly error messages

### 3. Performance
- Efficient API calls dengan caching
- Smooth animations
- Memory-friendly image loading

## 🔧 Customization

### Menambah Kondisi Cuaca Baru
1. Tambahkan icon di `res/drawable/`
2. Tambahkan animasi Lottie di `res/raw/`
3. Update `WeatherUtils.kt` untuk mapping kondisi baru

### Mengubah Tema
Edit file `res/values/colors.xml` dan `res/values/themes.xml`

## 🐛 Troubleshooting

### API Key Issues
- Pastikan API key valid dan aktif
- Periksa quota API (free tier: 1000 calls/day)
- Gunakan mode demo untuk testing tanpa API

### Location Issues
- Pastikan permission diberikan
- Aktifkan GPS di perangkat
- Test di perangkat fisik (emulator bisa bermasalah dengan GPS)

### Build Issues
- Pastikan Android SDK terbaru
- Sync Gradle files
- Clean & rebuild project

## 📄 License

Proyek ini dibuat untuk tujuan edukasi dan pembelajaran pengembangan Android.

## 👨‍💻 Developer

Dibuat dengan ❤️ untuk pembelajaran Android Development

---

### 📝 Catatan Pengembangan

**Langkah Implementasi:**
1. ✅ Setup project dengan dependensi
2. ✅ Buat model data untuk API response
3. ✅ Implementasi network layer dengan Retrofit
4. ✅ Buat repository pattern
5. ✅ Implementasi MVVM dengan ViewModel
6. ✅ Desain UI modern dengan Material Design
7. ✅ Implementasi location services
8. ✅ Tambahkan animasi dan dynamic backgrounds
9. ✅ Error handling dan permission management
10. ✅ Testing dan optimisasi

**Best Practices yang Diterapkan:**
- Clean Architecture
- Separation of Concerns
- Error Handling
- Memory Management
- User Experience
- Modern Android Development practices
