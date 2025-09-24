# Simple Weather App - Setup Guide

## 📱 Aplikasi Cuaca Real-time Android

Aplikasi cuaca real-time untuk Android yang dibangun menggunakan Jetpack Compose dengan arsitektur MVVM.

## ✨ Fitur

- 🔍 Pencarian cuaca berdasarkan nama kota
- 🌡️ Tampilan suhu dalam Celsius
- 🌤️ Ikon cuaca yang dinamis
- 💧 Detail cuaca: Kelembaban, Kecepatan angin, UV Index, Curah hujan
- ⏰ Waktu lokal
- 🔄 Auto-retry dan error handling
- 📱 Desain yang responsif dan modern

## 🏗️ Arsitektur

- **MVVM (Model-View-ViewModel)** pattern
- **Jetpack Compose** untuk UI
- **Retrofit** untuk networking
- **StateFlow** untuk state management
- **Repository pattern** untuk data abstraction
- **Coroutines** untuk asynchronous operations

## 🔧 Setup yang Diperlukan

### 1. API Key Setup
Aplikasi ini menggunakan **WeatherAPI.com** untuk mendapatkan data cuaca.

**LANGKAH WAJIB:**
1. Kunjungi [https://www.weatherapi.com/](https://www.weatherapi.com/)
2. Daftar akun gratis (free tier menyediakan 1 juta request/bulan)
3. Setelah login, dapatkan API key dari dashboard
4. Buka file `app/src/main/java/com/kelasxi/simpleweatherapp/data/api/WeatherApiService.kt`
5. Ganti `YOUR_API_KEY_HERE` dengan API key yang Anda dapatkan:

```kotlin
companion object {
    const val BASE_URL = "https://api.weatherapi.com/v1/"
    const val API_KEY = "masukkan_api_key_anda_disini" // 👈 Ganti ini!
}
```

### 2. Build Project
Setelah setup API key:
1. Sync project dengan Gradle
2. Build project
3. Run aplikasi di emulator atau device

## 📱 Cara Menggunakan

1. **Pencarian Kota**: Ketik nama kota di search field dan tekan tombol search atau Enter
2. **Melihat Detail**: Scroll ke bawah untuk melihat detail cuaca
3. **Retry**: Jika terjadi error, tekan tombol "Try Again"

## 🔍 Troubleshooting

### Error "Unauthorized" atau "API key invalid"
- Pastikan API key sudah dimasukkan dengan benar
- Verifikasi API key masih aktif di dashboard WeatherAPI.com

### Error "No internet connection"
- Pastikan device/emulator memiliki koneksi internet
- Cek permission INTERNET sudah ditambahkan (sudah otomatis)

### Error "City not found"
- Coba gunakan nama kota dalam bahasa Inggris
- Pastikan ejaan nama kota benar

## 📂 Struktur Project

```
app/src/main/java/com/kelasxi/simpleweatherapp/
├── data/
│   ├── api/
│   │   ├── ApiClient.kt           # Retrofit configuration
│   │   └── WeatherApiService.kt   # API interface
│   ├── model/
│   │   └── WeatherModels.kt       # Data classes
│   └── repository/
│       └── WeatherRepository.kt   # Data abstraction
├── presentation/
│   ├── components/
│   │   ├── SearchField.kt         # Search component
│   │   ├── WeatherDisplay.kt      # Main weather display
│   │   ├── WeatherDetailsCard.kt  # Details card
│   │   └── StateComponents.kt     # Loading & Error states
│   └── viewmodel/
│       └── WeatherViewModel.kt    # ViewModel with StateFlow
└── MainActivity.kt                # Main UI
```

## 🛠️ Dependencies yang Digunakan

- **Jetpack Compose** - Modern UI toolkit
- **ViewModel & Lifecycle** - State management
- **Retrofit & OkHttp** - Networking
- **Gson** - JSON parsing
- **Coil** - Image loading untuk weather icons
- **Coroutines** - Asynchronous programming

## 🔄 Flow Aplikasi

1. **Start** → ViewModel load default city (Jakarta)
2. **User Search** → ViewModel call Repository
3. **Repository** → Call API via Retrofit
4. **API Response** → Update StateFlow
5. **UI Update** → Compose recomposes UI automatically

## 📝 Notes

- Aplikasi default akan load cuaca Jakarta saat pertama kali dibuka
- Menggunakan WeatherAPI.com dengan free tier (1M requests/month)
- Semua networking operation menggunakan Coroutines
- Error handling sudah diimplementasi untuk berbagai skenario
- UI responsive untuk berbagai ukuran layar

## 🚀 Next Steps (Opsional)

Untuk pengembangan lebih lanjut, bisa ditambahkan:
- Database local untuk offline caching
- Weather forecast 7 hari
- Geolocation untuk detect lokasi otomatis
- Weather alerts/notifications
- Multiple cities favorites
- Dark/Light theme toggle

---

**Happy Coding! 🌟**