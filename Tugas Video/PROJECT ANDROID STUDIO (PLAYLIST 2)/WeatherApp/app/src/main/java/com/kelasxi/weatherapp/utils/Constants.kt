package com.kelasxi.weatherapp.utils

object Constants {
    // PENTING: Ganti dengan API Key Anda dari OpenWeatherMap
    // Cara mendapatkan API Key:
    // 1. Daftar di https://openweathermap.org/api
    // 2. Buat akun gratis
    // 3. Pergi ke My API Keys
    // 4. Copy API Key dan paste di bawah ini
    const val WEATHER_API_KEY = "YOUR_API_KEY_HERE"
    
    // Untuk keperluan demo, Anda bisa menggunakan API Key demo (terbatas):
    // const val WEATHER_API_KEY = "demo_key"
    
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    
    // Default coordinates (Jakarta)
    const val DEFAULT_LATITUDE = -6.2088
    const val DEFAULT_LONGITUDE = 106.8456
    
    // Function untuk check apakah API key sudah diset
    fun isApiKeySet(): Boolean {
        return WEATHER_API_KEY != "YOUR_API_KEY_HERE"
    }
}
