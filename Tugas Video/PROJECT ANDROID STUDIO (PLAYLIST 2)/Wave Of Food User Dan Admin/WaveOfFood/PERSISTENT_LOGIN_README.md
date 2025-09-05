# Persistent Login Implementation untuk Wave Of Food

Implementasi ini menyediakan sistem login yang dapat menyimpan data user secara lokal sehingga user tidak perlu login ulang ketika aplikasi ditutup atau di-install ulang.

## Fitur yang Disediakan

### 1. **UserPreferencesManager**
- Menggunakan DataStore Preferences untuk menyimpan data user
- Menyimpan status login, profil user, dan preferensi remember login
- Data tersimpan secara persistent di device

### 2. **AuthRepository**
- Mengelola autentikasi Firebase dan local storage
- Auto login menggunakan data tersimpan
- Sinkronisasi data antara Firebase dan local storage
- Logout dengan pembersihan data lokal

### 3. **AuthViewModel**
- State management untuk UI
- Observasi perubahan status login
- Handle loading states dan error messages

## Cara Penggunaan

### 1. Setup Dependencies

Pastikan dependencies berikut sudah ditambahkan di `build.gradle.kts`:

```kotlin
implementation(libs.androidx.datastore.preferences)
implementation(platform(libs.firebase.bom))
implementation(libs.firebase.firestore)
implementation(libs.firebase.auth)
```

### 2. Implementasi di Screen

```kotlin
@Composable
fun YourLoginScreen() {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )
    
    val uiState by viewModel.uiState.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    
    // Auto login check
    if (uiState.isAutoLoginSuccess == null) {
        // Show loading screen
        LoadingScreen()
        return
    }
    
    // Jika auto login berhasil, navigasi ke home
    if (uiState.isAutoLoginSuccess == true) {
        LaunchedEffect(Unit) {
            navigateToHome()
        }
        return
    }
    
    // Tampilkan form login jika auto login gagal
    LoginForm(
        onLogin = { email, password, rememberMe ->
            viewModel.signIn(email, password, rememberMe)
        }
    )
}
```

### 3. Implementasi di MainActivity

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            YourTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreenWithPersistence(
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }
                    
                    composable("home") {
                        MainAppScreen(
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
```

## Data Yang Disimpan

### User Profile
- `userId`: ID user dari Firebase Auth
- `email`: Email user
- `name`: Nama user
- `phone`: Nomor telepon user
- `address`: Alamat user
- `avatarUrl`: URL foto profil user

### Preferences
- `isLoggedIn`: Status login user
- `rememberLogin`: Apakah user memilih remember me

## Flow Aplikasi

1. **Aplikasi Dibuka**
   - Cek auto login dari local storage
   - Jika berhasil, langsung ke home screen
   - Jika gagal, tampilkan login screen

2. **User Login**
   - Validasi dengan Firebase Auth
   - Ambil data user dari Firestore
   - Simpan ke local storage jika remember me aktif
   - Navigasi ke home screen

3. **User Logout**
   - Sign out dari Firebase Auth
   - Hapus semua data dari local storage
   - Navigasi ke login screen

4. **Sinkronisasi Data**
   - Ambil data terbaru dari Firestore
   - Update local storage
   - Refresh UI dengan data terbaru

## Error Handling

- Connection timeout: Gunakan data local yang tersimpan
- Firebase Auth error: Clear local data dan minta login ulang
- Firestore error: Tetap gunakan data local
- Data corruption: Reset semua data dan minta login ulang

## Security Considerations

- Data disimpan menggunakan DataStore yang aman
- Tidak menyimpan password di local storage
- Session validation dengan Firebase Auth
- Auto logout jika session expired

## Testing

Untuk test fitur persistent login:

1. Login dengan "Remember me" aktif
2. Tutup aplikasi
3. Buka aplikasi lagi - seharusnya langsung masuk
4. Install ulang aplikasi - data akan hilang (expected behavior untuk security)

## Migration dari SharedPreferences

Jika sebelumnya menggunakan SharedPreferences, data perlu dimigrasikan:

```kotlin
// Tambahkan di UserPreferencesManager
suspend fun migrateFromSharedPreferences(context: Context) {
    val sharedPref = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)
    
    if (isLoggedIn) {
        val email = sharedPref.getString("email", "") ?: ""
        val name = sharedPref.getString("name", "") ?: ""
        // ... migrate other data
        
        saveUserLogin(userId, email, name, ...)
        
        // Clear old data
        sharedPref.edit().clear().apply()
    }
}
```
