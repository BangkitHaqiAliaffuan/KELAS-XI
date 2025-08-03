# Panduan Import Data Firestore - WaveOfFood

## 🚀 3 Cara Import Data Sample ke Firestore

### 1. 📱 Manual Melalui Aplikasi Android (TERCEPAT & TERMUDAH)

**Langkah-langkah:**
1. Buka file `MainActivity.kt`
2. Tambahkan kode ini di method `onCreate()` setelah `setupBottomNavigation()`:

```kotlin
// Import data sample (jalankan sekali saja)
FirestoreSampleData.importSampleMenuData(this)
```

3. Import class yang diperlukan di bagian atas file:
```kotlin
import com.kelasxi.waveoffood.utils.FirestoreSampleData
```

4. Build dan jalankan aplikasi
5. Anda akan melihat Toast notification "Memulai import 10 data menu..."
6. Setelah selesai akan muncul "Import selesai! Berhasil: 10, Gagal: 0"
7. Cek Firebase Console - data akan bertambah otomatis
8. **PENTING:** Hapus kode import setelah data berhasil masuk!

### 2. 🌐 Manual Melalui Firebase Console

1. Buka [Firebase Console](https://console.firebase.google.com)
2. Pilih project WaveOfFood Anda
3. Klik "Firestore Database" → "Data"
4. Klik "Start collection" → Nama collection: `menu`
5. Tambahkan dokumen dengan field berikut:

**Contoh Dokumen 1:**
```
foodName: "Nasi Gudeg" (string)
foodPrice: "25000" (string)  
foodDescription: "Nasi gudeg khas Yogyakarta dengan ayam kampung, telur, dan sambal krecek yang pedas manis" (string)
foodImage: "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400" (string)
foodCategory: "Indonesian Food" (string)
```

6. Ulangi untuk 9 item lainnya dari file `firestore-data.json`

### 3. ⚡ Otomatis dengan Node.js Script

**Prasyarat:**
- Install Node.js dari [nodejs.org](https://nodejs.org)
- Install Firebase CLI: `npm install -g firebase-tools`

**Langkah-langkah:**
1. Buka Terminal/Command Prompt di folder project
2. Login ke Firebase:
```bash
firebase login
```

3. Inisialisasi Firebase di project:
```bash
firebase init firestore
```

4. Install dependencies:
```bash
npm install firebase-admin
```

5. Jalankan script import:
```bash
node firestore-import.js
```

## 📊 Struktur Data yang Akan Diimport

### Collection: menu
- **foodName** (string): Nama makanan
- **foodPrice** (string): Harga dalam rupiah
- **foodDescription** (string): Deskripsi makanan
- **foodImage** (string): URL gambar dari Unsplash
- **foodCategory** (string): Kategori makanan

### Sample Data (10 Items):
1. Nasi Gudeg - Rp 25,000
2. Rendang Daging - Rp 35,000  
3. Gado-Gado - Rp 20,000
4. Sate Ayam - Rp 30,000
5. Bakso Malang - Rp 18,000
6. Ayam Geprek - Rp 22,000
7. Nasi Padang - Rp 28,000
8. Mie Ayam - Rp 15,000
9. Es Teh Manis - Rp 5,000
10. Jus Alpukat - Rp 12,000

## 🔥 Tips Optimasi Query Firestore

### 1. Menggunakan Index untuk Query Cepat
```kotlin
// Query berdasarkan kategori (sudah terindex otomatis)
firestore.collection("menu")
    .whereEqualTo("foodCategory", "Indonesian Food")
    .get()

// Query berdasarkan range harga
firestore.collection("menu")
    .whereGreaterThan("foodPrice", "20000")
    .whereLessThan("foodPrice", "30000")
    .get()
```

### 2. Limit Query untuk Performa
```kotlin
// Batasi hasil query untuk loading yang cepat
firestore.collection("menu")
    .limit(10)
    .get()
```

### 3. Menggunakan Pagination
```kotlin
// Untuk pagination yang efisien
firestore.collection("menu")
    .orderBy("foodName")
    .startAfter(lastDocument)
    .limit(10)
    .get()
```

### 4. Cache Data untuk Offline
```kotlin
// Enable offline persistence (panggil sekali di Application class)
FirebaseFirestore.getInstance().firestoreSettings = 
    FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build()
```

## ⚠️ Penting untuk Performa

1. **Gunakan Cara #1 (Android App)** - Paling mudah dan cepat
2. **Jangan lupa hapus kode import** setelah data masuk
3. **Gunakan cache offline** untuk performa aplikasi yang lebih baik
4. **Limit query results** untuk menghindari penggunaan bandwidth berlebih

## 🆘 Troubleshooting

**Problem:** Data tidak muncul di aplikasi
- **Solution:** Periksa koneksi internet dan Firebase rules

**Problem:** Import gagal dari aplikasi  
- **Solution:** Pastikan Firebase sudah terkonfigurasi dengan benar

**Problem:** Tidak ada Toast notification muncul
- **Solution:** Pastikan kode `FirestoreSampleData.importSampleMenuData(this)` sudah ditambahkan

**Problem:** Toast muncul tapi data tidak masuk ke Firestore
- **Solution:** 
  1. Cek Android Studio Logcat untuk melihat error "FirestoreImport"
  2. Pastikan Firestore Database Rules sudah di-set ke test mode:
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
  3. Pastikan aplikasi memiliki permission INTERNET

**Problem:** Query lambat
- **Solution:** Gunakan index dan limit query results

**Problem:** Data duplikat saat menjalankan aplikasi berkali-kali
- **Solution:** Hapus kode import setelah berhasil, atau tambahkan pengecekan data sudah ada

---
💡 **Rekomendasi:** Gunakan metode #1 (import melalui aplikasi Android) karena paling mudah dan terintegrasi langsung dengan kode aplikasi Anda!
