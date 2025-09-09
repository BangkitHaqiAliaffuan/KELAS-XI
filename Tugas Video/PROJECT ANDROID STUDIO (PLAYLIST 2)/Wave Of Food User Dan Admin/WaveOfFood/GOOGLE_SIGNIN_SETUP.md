# 🚀 Google Sign-In dengan Credential Manager API (2025)

## ⚠️ PENTING: API Lama Sudah Deprecated!

**Google Sign-In API lama sudah deprecated dan akan dihapus pada 2025.** Implementasi ini menggunakan **Credential Manager API** yang baru, sesuai dengan rekomendasi Google terbaru.

## 📦 Dependencies yang Diperlukan

Tambahkan di `app/build.gradle.kts`:

```kotlin
dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    
    // Credential Manager (New Google Sign-In approach for 2025)
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
}
```

## 🛠️ Setup yang Diperlukan

### 1. **Google Console Configuration**
1. Buka [Google Cloud Console](https://console.cloud.google.com/)
2. Pilih atau buat project baru
3. Enable **Google Sign-In API**
4. Buat **OAuth 2.0 Client ID** untuk Android

### 2. **SHA-1 Fingerprint**
Dapatkan SHA-1 fingerprint:
```bash
# Untuk debug
./gradlew signingReport

# Untuk Windows
gradlew signingReport
```

### 3. **OAuth Client Setup**
- Application type: **Android**
- Package name: `com.kelasxi.waveoffood`
- SHA-1 certificate fingerprint: [paste dari step 2]

### 4. **Update strings.xml**
```xml
<resources>
    <string name="default_web_client_id">YOUR_GOOGLE_WEB_CLIENT_ID_HERE</string>
</resources>
```

### 5. **Firebase Project Setup**
1. Tambahkan app Android ke Firebase project
2. Download `google-services.json` terbaru
3. Letakkan di folder `app/`
4. Pastikan Web Client ID di `google-services.json` sama dengan di `strings.xml`

## 🏗️ Implementasi

### AuthRepository.kt
```kotlin
suspend fun signInWithGoogle(googleToken: String): User? {
    return try {
        val credential = GoogleAuthProvider.getCredential(googleToken, null)
        val result = auth.signInWithCredential(credential).await()
        
        result.user?.let { user ->
            // Handle user data in Firestore
            val userDoc = firestore.collection("users").document(user.uid).get().await()
            
            if (userDoc.exists()) {
                // Update existing user
                userPreferencesManager.saveUserLogin(...)
            } else {
                // Create new user document
                val userData = hashMapOf(...)
                firestore.collection("users").document(user.uid).set(userData).await()
                userPreferencesManager.saveUserLogin(...)
            }
        }
        
        result.user
    } catch (e: Exception) {
        throw Exception("Google Sign-In failed: ${e.message}")
    }
}
```

### GoogleSignInViewModel.kt
```kotlin
class GoogleSignInViewModel(
    private val authRepository: AuthRepository,
    private val context: Context
) : ViewModel() {
    
    private val credentialManager = CredentialManager.create(context)
    
    fun signInWithGoogle(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setAutoSelectEnabled(false)
                    .build()
                
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                
                handleSignIn(result, onSuccess, onFailure)
                
            } catch (e: GetCredentialException) {
                onFailure("Sign-in failed: ${e.message}")
            }
        }
    }
}
```

### UI Implementation
```kotlin
// Login/Register Screen
SocialLoginButton(
    text = "Google",
    icon = "🔍",
    backgroundColor = Color(0xFFDB4437),
    isLoading = googleSignInViewModel.isLoading,
    onClick = { 
        googleSignInViewModel.signInWithGoogle(
            onSuccess = { onNavigateToHome() },
            onFailure = { error -> 
                errorMessage = error
            }
        )
    }
)
```

## ✅ Keunggulan Credential Manager API

1. **Modern & Future-proof**: API terbaru yang akan didukung jangka panjang
2. **Unified Experience**: Mendukung multiple sign-in methods (Google, Passkeys, dll)
3. **Better Security**: Keamanan yang lebih baik dengan enkripsi modern
4. **Consistent UX**: User experience yang konsisten di seluruh platform
5. **Passkey Support**: Mendukung Passkey untuk authentication tanpa password

## 🔧 Troubleshooting

### Error: "No credentials available"
- Pastikan SHA-1 fingerprint benar
- Cek Web Client ID di strings.xml
- Pastikan google-services.json ter-update

### Error: "DEVELOPER_ERROR"
- Package name tidak cocok di Google Console
- SHA-1 fingerprint tidak terdaftar
- google-services.json tidak sesuai

### Error: "NETWORK_ERROR"
- Cek koneksi internet
- Pastikan Google Play Services ter-update

## 📱 Minimum Requirements

- **minSdk**: 23 (Android 6.0)
- **targetSdk**: 34+
- **Google Play Services**: Versi terbaru
- **AndroidX Credentials**: 1.3.0+

## 🎯 Migration dari Legacy API

Jika sebelumnya menggunakan `com.google.android.gms:play-services-auth`:

1. **Hapus dependencies lama**:
   ```kotlin
   // HAPUS INI:
   implementation("com.google.android.gms:play-services-auth:xx.x.x")
   ```

2. **Tambah dependencies baru**:
   ```kotlin
   // TAMBAH INI:
   implementation("androidx.credentials:credentials:1.3.0")
   implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
   implementation("com.google.android.libraries.identity.googleid:1.1.1")
   ```

3. **Update implementasi** sesuai dengan contoh di atas

## 🚀 Status Implementasi

✅ **Completed:**
- Credential Manager integration
- Modern API implementation
- Firebase Auth compatibility
- Error handling
- Loading states
- UI components

✅ **Ready for Production** setelah setup Google Console selesai!

---

**Note**: Implementasi ini menggunakan teknologi terbaru 2025 dan akan didukung jangka panjang oleh Google.
