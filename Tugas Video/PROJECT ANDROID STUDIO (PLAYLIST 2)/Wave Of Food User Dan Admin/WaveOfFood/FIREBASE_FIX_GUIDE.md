# ⚠️ SOLUSI: Default FirebaseApp failed to initialize

## 🔥 Error yang Anda alami:
```
Default FirebaseApp failed to initialize because no default options were found. 
This usually means that com.google.gms:google-services was not applied to your gradle project.
```

## 🛠️ LANGKAH PERBAIKAN (MUDAH & CEPAT)

### **1. Restart Android Studio**
- Tutup Android Studio sepenuhnya
- Buka Task Manager → End all processes `android*` dan `java*`
- Buka kembali Android Studio

### **2. Sync Project dengan Firebase**
1. **Buka Android Studio**
2. **Tools** → **Firebase**
3. **Authentication** → **Connect to Firebase**
4. **Pilih project WaveOfFood** yang sudah ada
5. **Add Firebase Authentication to your app** → **Accept Changes**

### **3. Manual Sync (Jika langkah 2 gagal)**
1. **File** → **Sync Project with Gradle Files**
2. **Build** → **Clean Project**
3. **Build** → **Rebuild Project**

### **4. Verifikasi File Konfigurasi**

**Pastikan file ini ada dan benar:**

#### `app/build.gradle.kts` - Plugin harus seperti ini:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}
```

#### `app/build.gradle.kts` - Dependencies harus seperti ini:
```kotlin
dependencies {
    // Firebase BOM - pastikan versi terbaru
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-analytics")
    
    // Dependencies lainnya...
}
```

#### `build.gradle.kts` (project level) - Plugin harus seperti ini:
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
}
```

### **5. Pastikan `google-services.json` ada**
File harus ada di: `app/google-services.json`

**Jika tidak ada:**
1. Buka [Firebase Console](https://console.firebase.google.com)
2. Pilih project **WaveOfFood**
3. **Project Settings** → **General**
4. **Your apps** → **Android app**
5. **Download** `google-services.json`
6. **Copy** ke folder `app/` di project Android Studio Anda

### **6. Test Koneksi Firebase**

Tambahkan kode test ini di `MainActivity.kt`:

```kotlin
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    
    // Test Firebase initialization
    try {
        FirebaseApp.initializeApp(this)
        Log.d("Firebase", "✅ Firebase initialized successfully!")
        
        // Test Firestore connection
        FirebaseFirestore.getInstance()
            .collection("test")
            .document("connection")
            .set(mapOf("status" to "connected", "timestamp" to System.currentTimeMillis()))
            .addOnSuccessListener {
                Log.d("Firebase", "✅ Firestore connection successful!")
                // Sekarang bisa panggil import data
                FirestoreSampleData.importSampleMenuData(this)
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "❌ Firestore connection failed", e)
            }
            
    } catch (e: Exception) {
        Log.e("Firebase", "❌ Firebase initialization failed", e)
    }
    
    // ... rest of your code
}
```

## 🔍 **CARA CEK APAKAH BERHASIL:**

### **Logcat Filter:**
- **Package:** `com.kelasxi.waveoffood`
- **Tag:** `Firebase`
- **Log Level:** `Debug`

### **Log yang Diharapkan:**
```
✅ Firebase initialized successfully!
✅ Firestore connection successful!
🚀 MULAI IMPORT DATA - Function dipanggil!
✅ Document 1 added with ID: xyz123 - Nasi Gudeg
✅ Document 2 added with ID: abc456 - Rendang Daging
...
```

### **Toast Notification:**
- "Memulai import 10 data menu..."
- "Import selesai! Berhasil: 10, Gagal: 0"

## 🆘 **TROUBLESHOOTING LANJUTAN**

### **Jika masih error:**

1. **Delete build folder manually:**
   - Tutup Android Studio
   - Delete folder: `WaveOfFood/app/build`
   - Buka Android Studio lagi
   - Build → Rebuild Project

2. **Reset Firebase connection:**
   - Tools → Firebase
   - Authentication → Remove Firebase Authentication
   - Ulangi langkah setup dari awal

3. **Cek internet connection:**
   - Pastikan emulator/device terhubung internet
   - Test buka browser di emulator

4. **Cek Firebase Console:**
   - Authentication → Sign-in method → Email/Password → Enable
   - Firestore Database → Create database → Test mode
   - Firestore Database → Rules:
     ```
     rules_version = '2';
     service cloud.firestore {
       match /databases/{database}/documents {
         match /{document=**} {
           allow read, write: if true;
         }
       }
     }
     ```

## 💡 **TIPS:**
- Restart Android Studio setelah setiap perubahan konfigurasi
- Gunakan Build → Clean Project sebelum Build → Rebuild Project
- Pastikan internet connection stabil
- Cek Firebase Console untuk melihat data yang masuk

---
**🚀 Setelah mengikuti langkah ini, aplikasi Anda akan berhasil terhubung ke Firebase dan import data akan berfungsi!**
