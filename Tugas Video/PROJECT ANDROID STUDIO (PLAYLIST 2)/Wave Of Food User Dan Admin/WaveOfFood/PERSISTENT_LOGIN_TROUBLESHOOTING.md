# Troubleshooting Persistent Login

## Masalah: User Harus Login Ulang Setelah Menutup Aplikasi

### Diagnosis
Aplikasi tidak mengingat status login user setelah aplikasi ditutup dan dibuka kembali.

### Penyebab Potensial
1. **Auto login tidak berjalan** - checkAutoLogin() tidak dipanggil atau gagal
2. **Data tidak tersimpan** - UserPreferencesManager tidak menyimpan data dengan benar
3. **Firebase session expired** - Session Firebase Auth tidak persisten
4. **Navigation logic** - Logic navigasi tidak mendeteksi auto login success

### Perbaikan Yang Telah Dilakukan

#### 1. **Perbaikan AutoLogin Logic**
```kotlin
// Sebelumnya: Terlalu ketat dengan Firebase session
if (currentUser != null && currentUser.uid == userProfile.userId) {
    Result.success(userProfile)
} else {
    signOut() // Terlalu agresif clear data
}

// Sekarang: Lebih toleran terhadap Firebase session
if (userProfile.userId.isNotEmpty() && userProfile.email.isNotEmpty()) {
    // Trust local data jika valid
    Result.success(userProfile)
}
```

#### 2. **Perbaikan UI State Handling**
```kotlin
// Tambahan observasi untuk auto login
LaunchedEffect(uiState.isAutoLoginSuccess) {
    if (uiState.isAutoLoginSuccess == true) {
        onNavigateToHome()
    }
}

// Loading screen untuk auto login check
if (uiState.isAutoLoginSuccess == null) {
    // Show loading "Checking login status..."
}
```

#### 3. **Debug Logging**
```kotlin
LaunchedEffect(Unit) {
    // Log status login saat app start
    val isLoggedInLocal = prefsManager.isLoggedIn.first()
    Log.d("LoginScreen", "App started - isLoggedIn: $isLoggedInLocal")
}
```

### Cara Testing

#### 1. **Test Normal Flow**
1. Login dengan credentials yang valid
2. Tutup aplikasi completely (kill from background)
3. Buka aplikasi lagi
4. **Expected:** Langsung masuk ke home screen tanpa login

#### 2. **Test Debug Logging**
Buka Logcat dan filter dengan "LoginScreen" atau "AuthViewModel":
```
D/LoginScreen: App started - isLoggedIn: true, rememberLogin: true
D/AuthViewModel: Auto login success - navigating to home
```

#### 3. **Test Manual DataStore**
Untuk debug lebih lanjut, tambahkan di MainActivity:
```kotlin
// Tambahkan button debug (hapus di production)
Button(onClick = {
    lifecycleScope.launch {
        LoginDebugUtils.logCurrentLoginStatus(this@MainActivity)
    }
}) {
    Text("Debug Login Status")
}
```

### Debugging Steps

#### Step 1: Verify Data Storage
```kotlin
// Setelah login berhasil, cek apakah data tersimpan
LoginDebugUtils.logCurrentLoginStatus(context)
```

Expected output:
```
D/LoginDebug: isLoggedIn: true
D/LoginDebug: rememberLogin: true
D/LoginDebug: userProfile.userId: NaE7XShQjoaE2LQbMq57O1vQWLD2
D/LoginDebug: userProfile.email: user@example.com
```

#### Step 2: Verify Auto Login Check
```kotlin
// Di AuthViewModel, tambahkan logging
Log.d("AuthViewModel", "checkAutoLogin started")
Log.d("AuthViewModel", "isLoggedIn: $isLoggedIn, rememberLogin: $rememberLogin")
```

#### Step 3: Verify Navigation
```kotlin
// Di LoginScreen, pastikan navigation dipanggil
LaunchedEffect(uiState.isAutoLoginSuccess) {
    if (uiState.isAutoLoginSuccess == true) {
        Log.d("LoginScreen", "Auto login success - navigating to home")
        onNavigateToHome()
    }
}
```

### Common Issues & Solutions

#### Issue 1: "Auto login keeps failing"
**Solution:** Check if Firebase session persists
```kotlin
// Di autoLogin(), log Firebase current user
val currentUser = firebaseAuth.currentUser
Log.d("AutoLogin", "Firebase user: ${currentUser?.uid}")
```

#### Issue 2: "Data not persisting after app restart"
**Solution:** Verify DataStore implementation
```kotlin
// Test DataStore directly
context.dataStore.data.collect { preferences ->
    Log.d("DataStore", "IS_LOGGED_IN: ${preferences[IS_LOGGED_IN]}")
}
```

#### Issue 3: "Navigation not working"
**Solution:** Ensure navigation callback is correct
```kotlin
// Di MainActivity/NavGraph, pastikan callback benar
LoginScreen(
    onNavigateToHome = {
        navController.navigate("home") {
            popUpTo("login") { inclusive = true }
        }
    }
)
```

### Testing Checklist

- [ ] Login dengan credentials valid
- [ ] Verify data tersimpan (check logs)
- [ ] Kill aplikasi dari background
- [ ] Buka aplikasi lagi
- [ ] Verify auto login berjalan (check logs)
- [ ] Verify navigasi ke home screen
- [ ] Test logout dan login ulang
- [ ] Test dengan device restart

### Expected Behavior

✅ **Setelah login sekali:**
- Data user tersimpan di DataStore
- isLoggedIn = true, rememberLogin = true
- userProfile terisi dengan data valid

✅ **Saat buka aplikasi lagi:**
- checkAutoLogin() berjalan otomatis
- Data diambil dari DataStore
- Auto login berhasil
- Navigate langsung ke home screen

✅ **Tidak perlu input credentials lagi**

### Production Notes

1. **Remove debug logging** - Hapus semua Log.d() di production
2. **Remove debug utilities** - Hapus LoginDebugUtils dan FirebaseDebugScreen
3. **Test on multiple devices** - Pastikan consistent di berbagai device
4. **Test storage limits** - DataStore aman untuk data user basic

Persistent login sekarang seharusnya bekerja dengan reliable! 🚀
