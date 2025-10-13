# Maps SDK Configuration Guide

## 📋 Overview
Proyek ini telah diupdate untuk mendukung **dua alternatif SDK Maps**:
1. **Google Maps SDK** (Rekomendasi) - Lebih mudah setup dan stabil
2. **TomTom Maps SDK** (Alternatif) - Memerlukan setup repository khusus

## 🆓 Free Tier Comparison

### Google Maps (Recommended)
✅ **Keuntungan:**
- Repository publik (tidak perlu setup khusus)
- 25,000 Dynamic Maps requests/day (gratis)
- Dokumentasi lengkap dan komunitas besar
- Terintegrasi dengan Google Services
- Places API yang powerful

⚠️ **Batasan:**
- Dynamic Maps: $7/1000 requests setelah free tier
- Geocoding: $5/1000 requests setelah free tier
- Places API: $17/1000 requests setelah free tier

### TomTom Maps
✅ **Keuntungan:**
- 50,000 tile requests/day (gratis)
- 2,500 non-tile requests/day (gratis)
- Fitur navigation yang bagus

❌ **Kekurangan:**
- Memerlukan repository authentication
- Setup lebih kompleks
- Dokumentasi terbatas
- Community support terbatas

## 🔧 Dependencies Configuration

### Option 1: Google Maps SDK (Recommended)
```gradle
dependencies {
    // Google Maps SDK - Stable dan mudah digunakan
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.libraries.places:places:3.3.0")
    
    // Maps utilities
    implementation("com.google.maps.android:android-maps-utils:3.4.0")
}
```

### Option 2: TomTom SDK (Advanced Setup Required)
```gradle
// Repository tambahan diperlukan di settings.gradle.kts
maven {
    url = uri("https://repositories.tomtom.com/artifactory/maven")
    // Mungkin memerlukan credentials
}

dependencies {
    val tomtomVersion = "1.26.0"
    
    // Core TomTom dependencies
    implementation("com.tomtom.sdk.maps:map-display:$tomtomVersion")
    implementation("com.tomtom.sdk.search:search-online:$tomtomVersion")
    implementation("com.tomtom.sdk.geocoding:geocoding-online:$tomtomVersion")
}
```

## 🛠️ Configuration Files

### 1. settings.gradle.kts
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://repositories.tomtom.com/artifactory/maven")
        }
    }
}
```

### 2. app/build.gradle.kts
```kotlin
// API Key configuration
val tomtomApiKey: String by project

android {
    defaultConfig {
        minSdk = 26  // Minimum required by TomTom SDK
        
        // Required ABI filters
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }
    
    buildFeatures {
        buildConfig = true
    }
    
    // Build config untuk API key
    buildTypes.configureEach {
        buildConfigField("String", "TOMTOM_API_KEY", "\"$tomtomApiKey\"")
    }
}
```

### 3. gradle.properties
```properties
tomtomApiKey=YOUR_TOMTOM_API_KEY_HERE
```

## 🗝️ API Key Setup

1. **Dapatkan API Key Gratis:**
   - Kunjungi [TomTom Developer Portal](https://developer.tomtom.com)
   - Buat akun gratis
   - Buat aplikasi baru
   - Copy API key

2. **Konfigurasi API Key:**
   ```properties
   # gradle.properties
   tomtomApiKey=l8waPMgsQ2VEEOKMeEOJPi8RG7Ri4kAf
   ```

3. **Gunakan di Kode:**
   ```kotlin
   val apiKey = BuildConfig.TOMTOM_API_KEY
   
   val mapOptions = MapOptions(
       mapKey = apiKey
   )
   ```

## 📱 Implementation Example

### Basic Map Display
```kotlin
class MapActivity : AppCompatActivity() {
    
    private lateinit var tomTomMap: TomTomMap
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup MapFragment
        val mapOptions = MapOptions(mapKey = BuildConfig.TOMTOM_API_KEY)
        val mapFragment = MapFragment.newInstance(mapOptions)
        
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()
            
        mapFragment.getMapAsync { map ->
            this.tomTomMap = map
            setupMap()
        }
    }
    
    private fun setupMap() {
        // Pindah kamera ke lokasi
        tomTomMap.moveCamera(
            CameraOptions(
                position = Position(-6.2088, 106.8456), // Jakarta
                zoom = 12.0
            )
        )
    }
}
```

### Search Implementation (Free Tier)
```kotlin
private fun performSearch(query: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val searchQuery = SearchQuery.SearchQuery(query)
            val options = SearchOptions(limit = 5) // Batasi untuk hemat quota
            
            val response = searchService.search(searchQuery, options)
            
            response.onSuccess { result ->
                // Handle search results
                val firstResult = result.results.firstOrNull()
                // Process result...
            }
            
            response.onFailure { error ->
                // Handle error
                Log.e("Search", "Error: ${error.message}")
            }
            
        } catch (e: Exception) {
            Log.e("Search", "Exception: ${e.message}")
        }
    }
}
```

### Geocoding Implementation (Free Tier)
```kotlin
private fun reverseGeocode(position: Position) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val query = GeocodingQuery.ReverseGeocodingQuery(position)
            val response = geocodingService.reverseGeocoding(query)
            
            response.onSuccess { result ->
                val address = result.addresses.firstOrNull()
                val addressString = address?.let { buildAddressString(it) }
                // Update UI dengan alamat
            }
            
        } catch (e: Exception) {
            Log.e("Geocoding", "Error: ${e.message}")
        }
    }
}
```

## 🚀 Best Practices untuk Free Tier

### 1. Optimasi Request
- **Cache hasil search** untuk menghindari request berulang
- **Batch requests** ketika memungkinkan  
- **Implementasi debouncing** untuk search input
- **Limit hasil search** (max 10-20 results)

### 2. Error Handling
```kotlin
// Implementasi retry mechanism
private suspend fun safeApiCall(apiCall: suspend () -> Unit) {
    try {
        apiCall()
    } catch (e: Exception) {
        when {
            e.message?.contains("quota exceeded") == true -> {
                // Handle quota exceeded
                showQuotaExceededMessage()
            }
            e.message?.contains("unauthorized") == true -> {
                // Handle invalid API key
                showApiKeyError()
            }
            else -> {
                // Handle other errors
                showGenericError()
            }
        }
    }
}
```

### 3. UI Feedback
```kotlin
// Show loading state saat API call
private fun showLoadingState() {
    binding.progressBar.visibility = View.VISIBLE
    binding.btnSearch.isEnabled = false
}

private fun hideLoadingState() {
    binding.progressBar.visibility = View.GONE
    binding.btnSearch.isEnabled = true
}
```

## ⚠️ Important Notes

### Requirements
- **Minimum Android API:** 26 (Android 8.0)
- **NDK Version:** 26 (other versions may not be compatible)
- **Supported ABIs:** arm64-v8a, x86_64 only
- **OpenGL ES:** 3.0 support required

### Limitations
- **Turn-by-turn navigation:** Premium feature
- **Real-time traffic:** Requires subscription
- **Offline maps:** Premium feature
- **Advanced routing:** Limited in free tier

### Monitoring Usage
- Monitor API usage di [TomTom Developer Console](https://developer.tomtom.com/user/me/apps)
- Set up alerts untuk quota limit
- Implementasi fallback untuk quota exceeded

## 🔗 Useful Links
- [TomTom Android SDK Documentation](https://developer.tomtom.com/maps/android)
- [API Reference](https://developer.tomtom.com/maps/android/api-reference)
- [Sample Projects](https://github.com/tomtom-international/tomtom-sdk-android-examples)
- [Developer Portal](https://developer.tomtom.com)

## 📞 Support
Untuk masalah teknis:
- [TomTom Developer Community](https://developer.tomtom.com/community)
- [GitHub Issues](https://github.com/tomtom-international/tomtom-sdk-android-examples/issues)
- [Documentation](https://developer.tomtom.com/maps/android/getting-started)