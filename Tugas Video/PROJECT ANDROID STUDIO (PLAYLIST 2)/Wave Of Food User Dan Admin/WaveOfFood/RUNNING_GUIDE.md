# 🚀 **PANDUAN LENGKAP MENJALANKAN WAVEOFFOOD**

## **📱 LANGKAH-LANGKAH MENJALANKAN APLIKASI**

### **✅ CHECKLIST PERSIAPAN**

**SEBELUM MEMULAI, PASTIKAN SUDAH ADA:**
- [x] Android Studio terbaru (Hedgehog 2023.1.1+)
- [x] Java JDK 17+ atau JDK 23
- [x] Android SDK API 35
- [x] Koneksi internet stabil
- [x] Akun Google (untuk Firebase)

---

## **🔥 LANGKAH 1: SETUP FIREBASE LENGKAP**

### **1.1 Buat Project Firebase**
1. **Buka [Firebase Console](https://console.firebase.google.com/)**
2. **Klik "Add project"** atau **"Create a project"**
3. **Project name:** `WaveOfFood`
4. **Project ID:** `waveoffood-[random]` (otomatis)
5. **Enable Google Analytics:** ✅ (Recommended)
6. **Klik "Create project"**

### **1.2 Tambahkan Android App**
1. **Klik ikon Android** di Firebase Console
2. **Android package name:** `com.kelasxi.waveoffood` ⚠️ **HARUS SAMA PERSIS**
3. **App nickname:** `WaveOfFood Android`
4. **Klik "Register app"**

### **1.3 Download google-services.json**
1. **Download** file `google-services.json`
2. **Copy** file ke `app/google-services.json` ⚠️ **POSISI HARUS TEPAT**
   ```
   WaveOfFood/
   ├── app/
   │   ├── google-services.json ← DI SINI
   │   ├── build.gradle.kts
   │   └── src/
   ```
3. **Jangan** taruh di folder lain!

---

## **🔐 LANGKAH 2: ENABLE AUTHENTICATION**

### **2.1 Setup Authentication**
1. **Firebase Console → Authentication**
2. **Klik "Get started"**
3. **Tab "Sign-in method"**
4. **Enable "Email/Password"** ✅
5. **Klik "Save"**

### **2.2 (Opsional) Setup Email Templates**
1. **Tab "Templates"**
2. **Customize** email verification dan reset password
3. **Set Action URL** sesuai domain Anda

---

## **💾 LANGKAH 3: SETUP FIRESTORE DATABASE**

### **3.1 Buat Database**
1. **Firebase Console → Firestore Database**
2. **Klik "Create database"**
3. **Mode:** "Start in test mode" (untuk development)
4. **Location:** `asia-southeast1 (Singapore)` ⚠️ **RECOMMENDED untuk Indonesia**
5. **Klik "Done"**

### **3.2 Setup Security Rules**
1. **Tab "Rules"** di Firestore Console
2. **Replace** rules dengan:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection - hanya owner yang bisa akses
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      
      // UserCart subcollection
      match /userCart/{cartItem} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }
    
    // Menu collection - semua user bisa baca, tidak bisa tulis
    match /menu/{menuItem} {
      allow read: if request.auth != null;
      allow write: if false; // Only admin
    }
    
    // Orders collection - hanya owner yang bisa akses
    match /orders/{orderId} {
      allow read, write: if request.auth != null && 
        request.auth.uid == resource.data.userId;
    }
  }
}
```

3. **Klik "Publish"**

### **3.3 Tambahkan Data Sample Menu**
1. **Firestore Console → Start collection**
2. **Collection ID:** `menu`
3. **Tambahkan 5 dokumen berikut:**

**Document 1: `nasi-gudeg`**
```json
{
  "foodName": "Nasi Gudeg",
  "foodPrice": "25000",
  "foodDescription": "Nasi gudeg khas Yogyakarta dengan ayam kampung, telur, dan sambal krecek yang pedas manis",
  "foodImage": "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400",
  "foodCategory": "Indonesian Food",
  "isPopular": true,
  "rating": 4.5
}
```

**Document 2: `rendang-daging`**
```json
{
  "foodName": "Rendang Daging",
  "foodPrice": "35000",
  "foodDescription": "Rendang daging sapi autentik Padang dengan bumbu rempah yang kaya dan santan yang gurih",
  "foodImage": "https://images.unsplash.com/photo-1604521037225-0448dc7c9e31?w=400",
  "foodCategory": "Indonesian Food",
  "isPopular": true,
  "rating": 4.8
}
```

**Document 3: `gado-gado`**
```json
{
  "foodName": "Gado-Gado",
  "foodPrice": "20000",
  "foodDescription": "Salad Indonesia dengan sayuran rebus, tahu, tempe, dan saus kacang yang lezat",
  "foodImage": "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400",
  "foodCategory": "Indonesian Food",
  "isPopular": false,
  "rating": 4.2
}
```

**Document 4: `sate-ayam`**
```json
{
  "foodName": "Sate Ayam",
  "foodPrice": "30000",
  "foodDescription": "Sate ayam bakar dengan bumbu kacang yang gurih dan lontong sebagai pelengkap",
  "foodImage": "https://images.unsplash.com/photo-1529042410759-befb1204b468?w=400",
  "foodCategory": "Indonesian Food",
  "isPopular": true,
  "rating": 4.6
}
```

**Document 5: `bakso-malang`**
```json
{
  "foodName": "Bakso Malang",
  "foodPrice": "18000",
  "foodDescription": "Bakso daging sapi dengan kuah kaldu yang gurih, mie, dan aneka pelengkap khas Malang",
  "foodImage": "https://images.unsplash.com/photo-1575669090474-5d35b4b96ba1?w=400",
  "foodCategory": "Indonesian Food",
  "isPopular": false,
  "rating": 4.3
}
```

---

## **📱 LANGKAH 4: MENJALANKAN APLIKASI**

### **4.1 Buka Android Studio**
1. **Open** project `WaveOfFood`
2. **Wait** for Gradle sync to complete
3. **Check** file `google-services.json` ada di `app/` folder

### **4.2 Build Project**
1. **Menu → Build → Clean Project**
2. **Menu → Build → Rebuild Project**
3. **Tunggu** hingga build selesai

### **4.3 Run pada Device/Emulator**
1. **Setup Android device** atau **AVD emulator**
2. **Klik Run** ▶️ atau **Shift+F10**
3. **Pilih device** target
4. **Tunggu** aplikasi ter-install dan berjalan

---

## **🧪 LANGKAH 5: TESTING APLIKASI**

### **5.1 Test Flow Registrasi**
1. **Buka aplikasi** → Klik **"Daftar"**
2. **Isi form:**
   - Nama: Test User
   - Email: test@example.com  
   - Password: test123 (min 6 karakter)
3. **Klik "Daftar"**
4. **Cek** apakah user berhasil dibuat di Firebase Auth

### **5.2 Test Flow Login**
1. **Klik "Masuk"**
2. **Login** dengan akun yang sudah dibuat
3. **Verify** masuk ke MainActivity dengan bottom navigation

### **5.3 Test Home Fragment**
1. **Cek** apakah data menu muncul
2. **Scroll** horizontal di "Popular Food"
3. **Scroll** vertical di "All Menu"
4. **Klik** salah satu item makanan

### **5.4 Test Detail Activity**
1. **Dari HomeFragment** → Klik item makanan
2. **Cek** detail makanan muncul
3. **Test** quantity increase/decrease
4. **Klik "Add to Cart"**
5. **Verify** item masuk ke cart

### **5.5 Test Cart Fragment**
1. **Bottom Navigation** → Klik **Cart** 🛒
2. **Cek** item yang ditambahkan muncul
3. **Test** delete item dari cart
4. **Cek** total price calculation

### **5.6 Test Profile Fragment**
1. **Bottom Navigation** → Klik **Profile** 👤
2. **Cek** data user muncul (nama, email)
3. **Test** logout functionality
4. **Verify** kembali ke LoginActivity

---

## **🛠️ TROUBLESHOOTING**

### **❌ "google-services.json not found"**
**Solusi:**
- Pastikan file `google-services.json` ada di `app/` folder
- Restart Android Studio
- Clean & Rebuild project

### **❌ "App crashes on startup"**
**Solusi:**
1. Check logcat untuk error message
2. Verify Firebase project setup
3. Check internet connection
4. Verify package name sama persis

### **❌ "Authentication failed"**  
**Solusi:**
1. Check Firebase Auth is enabled
2. Verify Email/Password method enabled
3. Check internet connection
4. Verify google-services.json up to date

### **❌ "No data in RecyclerView"**
**Solusi:**
1. Check Firestore rules
2. Verify data sample sudah ditambahkan
3. Check internet connection
4. Debug Firestore queries

### **❌ "Images not loading"**
**Solusi:**
1. Check internet connection
2. Verify image URLs valid
3. Add internet permission di AndroidManifest.xml
4. Check Glide dependencies

---

## **📋 FINAL CHECKLIST**

**✅ SEBELUM DEPLOY:**
- [ ] Firebase project setup completed
- [ ] google-services.json in correct location
- [ ] Authentication working
- [ ] Firestore data populated
- [ ] Security rules configured
- [ ] All fragments navigating correctly
- [ ] Cart functionality working
- [ ] User registration/login working
- [ ] Data persistence working
- [ ] Images loading properly

**🎉 SELAMAT! Aplikasi WaveOfFood siap digunakan!**

---

## **🔄 MAINTENANCE NOTES**

### **Security Rules - Production**
Untuk production, ganti Firestore rules dengan:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && 
        request.auth.uid == userId &&
        request.auth.token.email_verified == true;
    }
    // ... rules lainnya dengan validasi lebih ketat
  }
}
```

### **Performance Optimization**
1. Implement **DiffUtil** di RecyclerView adapters
2. Add **image caching** strategy
3. Implement **data pagination**
4. Add **offline support** dengan Room database

### **Monitoring**
1. Setup **Firebase Analytics**
2. Enable **Crashlytics**
3. Monitor **Performance**
4. Setup **Remote Config**
