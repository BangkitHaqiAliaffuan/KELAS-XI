## 🔥 **SETUP FIREBASE LENGKAP UNTUK WAVE OF FOOD**

### **🎯 LANGKAH 1: BUAT PROJECT FIREBASE**

1. **Kunjungi Firebase Console:**
   - Buka [https://console.firebase.google.com/](https://console.firebase.google.com/)
   - Login dengan akun Google Anda

2. **Buat Project Baru:**
   - Klik **"Add project"** atau **"Tambah project"**
   - Nama project: **"WaveOfFood"** 
   - Project ID akan otomatis dibuat: `waveoffood-xxxxx`
   - ✅ **Enable Google Analytics** (Recommended)
   - Pilih Analytics account atau buat baru
   - Klik **"Create project"**

### **🎯 LANGKAH 2: TAMBAHKAN ANDROID APP**

1. **Tambah Android App:**
   - Di Firebase Console, klik ikon **Android** 
   - **Package name:** `com.kelasxi.waveoffood` (**HARUS SAMA PERSIS**)
   - **App nickname:** WaveOfFood Android
   - **Debug signing certificate SHA-1:** (Optional untuk development)

2. **Download google-services.json:**
   - Download file `google-services.json`
   - **PENTING:** Letakkan di `app/google-services.json` (sejajar dengan `app/build.gradle.kts`)
   - **JANGAN** letakkan di folder lain!

### **🎯 LANGKAH 3: ENABLE AUTHENTICATION**

1. **Setup Authentication:**
   - Di Firebase Console → **"Authentication"**
   - Klik tab **"Sign-in method"**
   - **Enable "Email/Password"**
   - ✅ Centang **"Email/Password"**
   - ✅ Centang **"Email link (passwordless sign-in)"** (Optional)
   - Klik **"Save"**

2. **Atur Email Templates:**
   - Tab **"Templates"** → Customize email templates
   - Set **"Action URL"** jika diperlukan

### **🎯 LANGKAH 4: SETUP FIRESTORE DATABASE**

1. **Buat Database:**
   - Firebase Console → **"Firestore Database"** 
   - Klik **"Create database"**
   - **Mode:** "Start in test mode" (untuk development)
   - **Location:** asia-southeast1 (Singapore) - **RECOMMENDED untuk Indonesia**
   - Klik **"Done"**

2. **Setup Security Rules:**
   - Tab **"Rules"** → Replace dengan rules berikut:

### **~~🎯 LANGKAH 5: SETUP STORAGE~~ (OPSIONAL - TIDAK WAJIB)**

**⚠️ CATATAN PENTING:** Firebase Storage **TIDAK DIPERLUKAN** untuk aplikasi ini!

**Mengapa Storage tidak wajib:**
- ✅ **Gambar makanan** menggunakan URL eksternal (Unsplash)
- ✅ **Tidak ada upload gambar** dari user
- ✅ **Semua assets** sudah tersedia online
- ✅ **Free tier Firebase** sudah cukup

**Jika ingin tetap setup (opsional):**
1. **Enable Storage:**
   - Firebase Console → **"Storage"**
   - Klik **"Get started"**
   - **Mode:** "Start in test mode"
   - **Location:** asia-southeast1 (Singapore)
   - Klik **"Done"**

**⚠️ SKIP LANGKAH INI jika tidak ingin menggunakan billing plan!**

### Structure Firestore Database (ENHANCED DESIGN):

```
📁 categories (BARU - untuk Enhanced UI)
  📄 {categoryId}
    - id: string
    - name: string (Pizza, Burger, Dessert, dll)
    - imageUrl: string (URL gambar kategori)
    - isActive: boolean
    - createdAt: timestamp

📁 foods (UPDATE dari "menu")
  📄 {foodItemId}
    - id: string
    - name: string (foodName -> name)
    - price: number (foodPrice string -> price number) 
    - description: string
    - imageUrl: string (foodImage -> imageUrl)
    - categoryId: string (reference ke categories)
    - isPopular: boolean
    - rating: number
    - preparationTime: number (menit)
    - isAvailable: boolean
    - ingredients: array of strings
    - nutritionInfo: object
      - calories: number
      - protein: number
      - carbs: number
      - fat: number
    - createdAt: timestamp
    - updatedAt: timestamp

📁 users (UPDATE)
  📄 {userUID}
    - uid: string
    - name: string
    - email: string
    - address: string
    - phone: string
    - profileImageUrl: string (URL eksternal)
    - favoriteCategories: array of strings
    - totalOrders: number
    - loyaltyPoints: number
    - createdAt: timestamp
    - updatedAt: timestamp
    
    📁 cart (subcollection - UPDATE dari userCart)
      📄 {cartItemId}
        - id: string
        - foodId: string (reference ke foods)
        - name: string
        - price: number
        - imageUrl: string
        - quantity: number
        - selectedSize: string (Small, Medium, Large)
        - selectedExtras: array of objects
          - name: string
          - price: number
        - subtotal: number
        - addedAt: timestamp

    📁 favorites (subcollection - BARU)
      📄 {foodId}
        - foodId: string
        - addedAt: timestamp

    📁 addresses (subcollection - BARU)
      📄 {addressId}
        - id: string
        - label: string (Home, Office, dll)
        - address: string
        - coordinates: geopoint
        - isDefault: boolean
        - createdAt: timestamp

📁 orders (UPDATE)
  📄 {orderId}
    - orderId: string
    - userId: string
    - userName: string
    - userPhone: string
    - deliveryAddress: object
      - address: string
      - coordinates: geopoint
      - instructions: string
    - items: array of enhanced CartItemModel
    - subtotal: number
    - deliveryFee: number
    - serviceFee: number
    - discount: number
    - totalAmount: number
    - paymentMethod: string
    - orderStatus: string (pending, confirmed, preparing, delivering, completed, cancelled)
    - estimatedDelivery: timestamp
    - actualDelivery: timestamp (optional)
    - driverInfo: object (optional)
      - name: string
      - phone: string
      - vehicleInfo: string
    - rating: number (optional)
    - review: string (optional)
    - createdAt: timestamp
    - updatedAt: timestamp

📁 promotions (BARU)
  📄 {promoId}
    - id: string
    - title: string
    - description: string
    - imageUrl: string
    - discountType: string (percentage, fixed)
    - discountValue: number
    - minOrder: number
    - maxDiscount: number (untuk percentage)
    - validFrom: timestamp
    - validUntil: timestamp
    - isActive: boolean
    - applicableCategories: array of strings
    - usageLimit: number
    - usedCount: number

📁 reviews (BARU)
  📄 {reviewId}
    - id: string
    - userId: string
    - userName: string
    - foodId: string
    - orderId: string
    - rating: number (1-5)
    - comment: string
    - images: array of strings (URLs)
    - isVerified: boolean
    - createdAt: timestamp
```

### Menambahkan Data Sample Menu:

Setelah setup Firebase selesai, tambahkan beberapa data sample ke collection "menu":

```json
{
  "foodName": "Nasi Gudeg",
  "foodPrice": "25000",
  "foodDescription": "Nasi gudeg khas Yogyakarta dengan ayam kampung dan telur",
  "foodImage": "URL_GAMBAR_DISINI",
  "foodCategory": "Indonesian Food"
}
```

### **🔒 LANGKAH 6: FIRESTORE SECURITY RULES**

**Buka Firestore Console → Rules Tab → Replace dengan:**

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users dapat read/write data mereka sendiri
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      
      // UserCart subcollection - hanya owner yang bisa akses
      match /userCart/{cartItem} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }
    
    // Menu dapat dibaca oleh semua user yang terautentikasi
    match /menu/{menuItem} {
      allow read: if request.auth != null;
      allow write: if false; // Only admin can write
    }
    
    // Orders dapat dibaca/ditulis oleh owner
    match /orders/{orderId} {
      allow read, write: if request.auth != null && 
        request.auth.uid == resource.data.userId;
    }
  }
}
```

### **~~🔒 LANGKAH 7: STORAGE SECURITY RULES~~ (TIDAK DIPERLUKAN)**

**❌ SKIP LANGKAH INI** - Storage tidak digunakan dalam aplikasi ini.

---

## **📊 STRUKTUR DATABASE FIRESTORE**

**⚠️ CATATAN:** Semua gambar menggunakan URL eksternal, **TIDAK PERLU Firebase Storage**

### **📁 Collection: `users`**
```json
{
  "uid": "user123",
  "name": "John Doe",
  "email": "john@example.com", 
  "address": "Jl. Example No. 123",
  "phone": "081234567890",
  "profileImage": "https://via.placeholder.com/150", // URL eksternal
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

**📁 SubCollection: `users/{uid}/userCart`**
```json
{
  "id": "food123",
  "foodName": "Nasi Gudeg",
  "foodPrice": "25000",
  "foodDescription": "Nasi gudeg khas Yogyakarta...",
  "foodImage": "https://images.unsplash.com/...", // URL eksternal
  "foodCategory": "Indonesian Food",
  "quantity": 2,
  "addedAt": "timestamp"
}
```

### **📁 Collection: `menu`**
```json
{
  "id": "food123",
  "foodName": "Nasi Gudeg",
  "foodPrice": "25000", 
  "foodDescription": "Nasi gudeg khas Yogyakarta dengan ayam kampung dan telur",
  "foodImage": "https://images.unsplash.com/...", // URL eksternal - GRATIS
  "foodCategory": "Indonesian Food",
  "isPopular": true,
  "rating": 4.5,
  "createdAt": "timestamp"
}
```

### **📁 Collection: `orders`**
```json
{
  "orderId": "order123",
  "userId": "user123", 
  "userName": "John Doe",
  "userEmail": "john@example.com",
  "userAddress": "Jl. Example No. 123",
  "userPhone": "081234567890",
  "items": [
    {
      "foodName": "Nasi Gudeg",
      "foodPrice": "25000",
      "quantity": 2,
      "subtotal": "50000"
    }
  ],
  "totalPrice": "52000",
  "deliveryFee": "2000", 
  "orderStatus": "pending", // pending, confirmed, preparing, delivering, completed
  "orderTimestamp": "timestamp",
  "estimatedDelivery": "timestamp"
}
```

---

## **🍽️ LANGKAH 7: MENAMBAHKAN DATA SAMPLE (DESIGN TERBARU)**

**⚠️ UPDATE UNTUK DESIGN ENHANCED:**
Dengan design terbaru yang menggunakan professional green theme dan enhanced fragments, struktur data Firebase perlu disesuaikan.

**3 CARA MEMASUKKAN DATA KE FIRESTORE:**

### **🚀 CARA 1: MENGGUNAKAN FIREBASE CONSOLE (MANUAL - 10 MENIT)**

**Langkah-langkah untuk Enhanced Design:**
1. **Buka Firestore Console:**
   - [https://console.firebase.google.com/](https://console.firebase.google.com/)
   - Pilih project "WaveOfFood"
   - Klik **"Firestore Database"**

2. **Buat Collection "categories" (BARU):**
   - Klik **"Start collection"**
   - **Collection ID:** `categories`
   - Klik **"Next"**

3. **Tambah Categories untuk Enhanced Design:**
   - **Document ID:** `pizza` 
   - **Field:** `id` | **Type:** string | **Value:** `pizza`
   - **Field:** `name` | **Type:** string | **Value:** `Pizza`
   - **Field:** `imageUrl` | **Type:** string | **Value:** `https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400`
   - **Field:** `isActive` | **Type:** boolean | **Value:** `true`
   - Klik **"Save"**

4. **Buat Collection "foods" (UPDATE):**
   - Klik **"Start collection"**
   - **Collection ID:** `foods`
   - Klik **"Next"**

5. **Tambah Enhanced Food Data:**
   - **Document ID:** `nasi-gudeg` (atau "Auto-ID")
   - **Field:** `foodName` | **Type:** string | **Value:** `Nasi Gudeg`
   - **Field:** `foodPrice` | **Type:** string | **Value:** `25000`
   - **Field:** `foodDescription` | **Type:** string | **Value:** `Nasi gudeg khas Yogyakarta...`
   - **Field:** `foodImage` | **Type:** string | **Value:** `https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400`
   - **Field:** `foodCategory` | **Type:** string | **Value:** `Indonesian Food`
   - **Field:** `isPopular` | **Type:** boolean | **Value:** `true`
   - **Field:** `rating` | **Type:** number | **Value:** `4.5`
   - Klik **"Save"**

4. **Ulangi untuk 4 documents lainnya** (lihat data sample di bawah)

### **⚡ CARA 2: IMPORT JSON ENHANCED (SUPER CEPAT - 2 MENIT)**

**File: enhanced-data.json** (Simpan di root project)

### **Categories Data (BARU):**
```json
{
  "categories": {
    "pizza": {
      "id": "pizza",
      "name": "Pizza", 
      "imageUrl": "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400",
      "isActive": true,
      "createdAt": "2025-07-29T00:00:00Z"
    },
    "burger": {
      "id": "burger", 
      "name": "Burger",
      "imageUrl": "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400",
      "isActive": true,
      "createdAt": "2025-07-29T00:00:00Z"
    },
    "indonesian": {
      "id": "indonesian",
      "name": "Indonesian Food", 
      "imageUrl": "https://images.unsplash.com/photo-1604521037225-0448dc7c9e31?w=400",
      "isActive": true,
      "createdAt": "2025-07-29T00:00:00Z"
    },
    "dessert": {
      "id": "dessert",
      "name": "Dessert",
      "imageUrl": "https://images.unsplash.com/photo-1551024506-0bccd828d307?w=400", 
      "isActive": true,
      "createdAt": "2025-07-29T00:00:00Z"
    },
    "drinks": {
      "id": "drinks",
      "name": "Drinks",
      "imageUrl": "https://images.unsplash.com/photo-1544145945-f90425340c7e?w=400",
      "isActive": true, 
      "createdAt": "2025-07-29T00:00:00Z"
    }
  }
}
```

### **Enhanced Foods Data:**
```json
{
  "foods": {
    "nasi-gudeg": {
      "id": "nasi-gudeg",
      "name": "Nasi Gudeg",
      "price": 25000,
      "description": "Nasi gudeg khas Yogyakarta dengan ayam kampung, telur, dan sambal krecek yang pedas manis",
      "imageUrl": "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400",
      "categoryId": "indonesian", 
      "isPopular": true,
      "rating": 4.5,
      "preparationTime": 15,
      "isAvailable": true,
      "ingredients": ["Nasi", "Gudeg", "Ayam Kampung", "Telur", "Sambal Krecek"],
      "nutritionInfo": {
        "calories": 450,
        "protein": 25,
        "carbs": 65,
        "fat": 12
      },
      "createdAt": "2025-07-29T00:00:00Z",
      "updatedAt": "2025-07-29T00:00:00Z"
    },
    "rendang-daging": {
      "id": "rendang-daging", 
      "name": "Rendang Daging",
      "price": 35000,
      "description": "Rendang daging sapi autentik Padang dengan bumbu rempah yang kaya dan santan yang gurih",
      "imageUrl": "https://images.unsplash.com/photo-1604521037225-0448dc7c9e31?w=400",
      "categoryId": "indonesian",
      "isPopular": true,
      "rating": 4.8,
      "preparationTime": 20,
      "isAvailable": true,
      "ingredients": ["Daging Sapi", "Santan", "Cabai", "Bawang", "Rempah"],
      "nutritionInfo": {
        "calories": 520,
        "protein": 35,
        "carbs": 8,
        "fat": 38
      },
      "createdAt": "2025-07-29T00:00:00Z", 
      "updatedAt": "2025-07-29T00:00:00Z"
    },
    "margherita-pizza": {
      "id": "margherita-pizza",
      "name": "Margherita Pizza",
      "price": 45000,
      "description": "Pizza klasik dengan saus tomat, mozzarella segar, dan daun basil yang harum",
      "imageUrl": "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400",
      "categoryId": "pizza",
      "isPopular": true, 
      "rating": 4.6,
      "preparationTime": 25,
      "isAvailable": true,
      "ingredients": ["Pizza Dough", "Tomato Sauce", "Mozzarella", "Basil", "Olive Oil"],
      "nutritionInfo": {
        "calories": 680,
        "protein": 28,
        "carbs": 78, 
        "fat": 28
      },
      "createdAt": "2025-07-29T00:00:00Z",
      "updatedAt": "2025-07-29T00:00:00Z"
    },
    "cheeseburger": {
      "id": "cheeseburger",
      "name": "Classic Cheeseburger", 
      "price": 38000,
      "description": "Burger daging sapi juicy dengan keju cheddar, lettuce, tomat, dan saus spesial",
      "imageUrl": "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400",
      "categoryId": "burger",
      "isPopular": true,
      "rating": 4.4,
      "preparationTime": 18,
      "isAvailable": true,
      "ingredients": ["Beef Patty", "Cheddar Cheese", "Lettuce", "Tomato", "Onion", "Special Sauce"],
      "nutritionInfo": {
        "calories": 620,
        "protein": 32,
        "carbs": 45,
        "fat": 35
      },
      "createdAt": "2025-07-29T00:00:00Z",
      "updatedAt": "2025-07-29T00:00:00Z" 
    },
    "chocolate-cake": {
      "id": "chocolate-cake",
      "name": "Chocolate Fudge Cake",
      "price": 22000,
      "description": "Kue cokelat lembut dengan lapisan fudge yang kaya dan topping whipped cream",
      "imageUrl": "https://images.unsplash.com/photo-1551024506-0bccd828d307?w=400",
      "categoryId": "dessert",
      "isPopular": false,
      "rating": 4.7,
      "preparationTime": 10,
      "isAvailable": true,
      "ingredients": ["Chocolate", "Flour", "Eggs", "Butter", "Sugar", "Whipped Cream"],
      "nutritionInfo": {
        "calories": 380,
        "protein": 6,
        "carbs": 52,
        "fat": 18
      },
      "createdAt": "2025-07-29T00:00:00Z",
      "updatedAt": "2025-07-29T00:00:00Z"
    },
    "iced-coffee": {
      "id": "iced-coffee",
      "name": "Iced Coffee Latte",
      "price": 15000,
      "description": "Kopi susu dingin dengan espresso premium dan susu segar yang creamy",
      "imageUrl": "https://images.unsplash.com/photo-1544145945-f90425340c7e?w=400",
      "categoryId": "drinks",
      "isPopular": false,
      "rating": 4.3,
      "preparationTime": 5,
      "isAvailable": true,
      "ingredients": ["Espresso", "Milk", "Ice", "Sugar"],
      "nutritionInfo": {
        "calories": 120,
        "protein": 8,
        "carbs": 12,
        "fat": 5
      },
      "createdAt": "2025-07-29T00:00:00Z",
      "updatedAt": "2025-07-29T00:00:00Z"
    }
  }
}
```

### **Promotions Data (BARU):**
```json
{
  "promotions": {
    "welcome-promo": {
      "id": "welcome-promo",
      "title": "Welcome Discount 20%",
      "description": "Dapatkan diskon 20% untuk pembelian pertama Anda!",
      "imageUrl": "https://images.unsplash.com/photo-1607082348824-0a96f2a4b9ba?w=400", 
      "discountType": "percentage",
      "discountValue": 20,
      "minOrder": 50000,
      "maxDiscount": 15000,
      "validFrom": "2025-07-29T00:00:00Z",
      "validUntil": "2025-12-31T23:59:59Z",
      "isActive": true,
      "applicableCategories": ["all"],
      "usageLimit": 1,
      "usedCount": 0
    },
    "weekend-special": {
      "id": "weekend-special",
      "title": "Weekend Special 15%",
      "description": "Diskon spesial weekend untuk semua makanan Indonesia",
      "imageUrl": "https://images.unsplash.com/photo-1607082349566-187342175e2f?w=400",
      "discountType": "percentage", 
      "discountValue": 15,
      "minOrder": 30000,
      "maxDiscount": 10000,
      "validFrom": "2025-07-26T00:00:00Z",
      "validUntil": "2025-07-27T23:59:59Z",
      "isActive": true,
      "applicableCategories": ["indonesian"],
      "usageLimit": 100,
      "usedCount": 0
    }
  }
}
```

### **Document 1: Nasi Gudeg**
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

### **Document 2: Rendang Daging**  
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

### **Document 3: Gado-Gado**
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

### **Document 4: Sate Ayam**
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

### **Document 5: Bakso Malang**
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

## **🚀 LANGKAH 8: TEST KONEKSI FIREBASE**

**✅ CHECKLIST FIREBASE SETUP TANPA STORAGE:**
- [ ] ✅ Firebase project dibuat
- [ ] ✅ Android app ditambahkan dengan package name yang benar
- [ ] ✅ `google-services.json` didownload dan ditaruh di `app/`
- [ ] ✅ Authentication enabled (Email/Password)
- [ ] ✅ Firestore database dibuat dengan test mode
- [ ] ✅ Firestore security rules diupdate
- [ ] ✅ Sample menu data ditambahkan
- [ ] ❌ **Firebase Storage TIDAK diperlukan**

**⚠️ PENTING:** 
- Pastikan file `google-services.json` sudah ada di folder `app/`
- **Firebase Storage TIDAK WAJIB** - aplikasi menggunakan gambar eksternal
- **Free tier Firebase** sudah cukup untuk authentication + Firestore

## **💰 BIAYA FIREBASE (FREE TIER)**

**Yang Anda gunakan (GRATIS):**
- ✅ **Authentication:** 10,000 users/month
- ✅ **Firestore:** 20,000 writes, 50,000 reads/day
- ✅ **Hosting:** 10GB bandwidth/month
- ❌ **Storage:** TIDAK DIGUNAKAN

**Total biaya:** **$0 (GRATIS)** untuk development dan testing!

## **🎯 ALTERNATIF GAMBAR GRATIS**

**Sumber gambar yang bisa digunakan:**
1. **Unsplash:** https://images.unsplash.com/ (sudah digunakan)
2. **Lorem Picsum:** https://picsum.photos/400/300
3. **Pexels:** https://images.pexels.com/
4. **Placeholder:** https://via.placeholder.com/400x300

**Contoh URL yang bisa digunakan:**
```json
"foodImage": "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400"
```

**⚠️ PENTING:** Pastikan file `google-services.json` sudah ada di folder `app/` sebelum menjalankan aplikasi!

---

## **🚨 LANGKAH EKSTERNAL YANG HARUS DILAKUKAN MANUAL**

### **1. 📱 SETUP FIREBASE PROJECT (EKSTERNAL)**
**❌ Tidak bisa dilakukan otomatis - HARUS MANUAL:**

1. **Buat Firebase Project:**
   - Buka [https://console.firebase.google.com/](https://console.firebase.google.com/)
   - Klik **"Add project"**
   - Nama: **"WaveOfFood"**
   - Enable Google Analytics
   - **⚠️ WAJIB:** Catat Project ID yang dibuat

2. **Tambah Android App:**
   - Klik ikon Android di Firebase Console
   - **Package name:** `com.kelasxi.waveoffood` (HARUS SAMA PERSIS)
   - **App nickname:** WaveOfFood Android
   - Download `google-services.json`
   - **⚠️ CRITICAL:** Letakkan file di `app/google-services.json`

3. **Enable Authentication:**
   - Firebase Console → Authentication → Sign-in method
   - Enable **"Email/Password"**
   - **⚠️ WAJIB:** Aktifkan kedua opsi (Email/Password + Email link)

4. **Setup Firestore Database:**
   - Firebase Console → Firestore Database
   - **"Create database"** → **"Start in test mode"**
   - **Location:** asia-southeast1 (Singapore)
   - **⚠️ CRITICAL:** Salin Security Rules yang sudah disediakan

### **2. 🔐 SETUP SECURITY RULES (EKSTERNAL)**
**❌ Harus copy-paste manual ke Firebase Console:**

**Buka:** Firebase Console → Firestore Database → Rules Tab
**Replace dengan rules ini:**

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Categories - public read
    match /categories/{categoryId} {
      allow read: if true;
      allow write: if false; // Admin only
    }
    
    // Foods - public read  
    match /foods/{foodId} {
      allow read: if true;
      allow write: if false; // Admin only
    }
    
    // Promotions - public read
    match /promotions/{promoId} {
      allow read: if true;
      allow write: if false; // Admin only
    }
    
    // Reviews - authenticated read, owner write
    match /reviews/{reviewId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && 
        request.auth.uid == resource.data.userId;
    }
    
    // Users - owner access only
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      
      // Cart subcollection
      match /cart/{cartItem} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
      
      // Favorites subcollection  
      match /favorites/{foodId} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
      
      // Addresses subcollection
      match /addresses/{addressId} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }
    
    // Orders - owner access only
    match /orders/{orderId} {
      allow read, write: if request.auth != null && 
        request.auth.uid == resource.data.userId;
    }
  }
}
```

### **3. 📊 IMPORT DATA SAMPLE (EKSTERNAL)**
**❌ Pilih salah satu cara - HARUS MANUAL:**

**CARA A: Manual via Firebase Console (10 menit)**
- Login ke Firebase Console
- Buka Firestore Database  
- Buat collection: `categories`, `foods`, `promotions`
- Copy-paste data JSON yang sudah disediakan

**CARA B: Firebase CLI (2 menit)**
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login ke Firebase
firebase login

# Init project (pilih Firestore)
firebase init firestore

# Import data (setelah buat file enhanced-data.json)
firebase firestore:import enhanced-data.json
```

### **4. 🔧 TESTING & DEBUGGING (EKSTERNAL)**
**❌ Harus test manual di device/emulator:**

1. **Test Authentication:**
   - Jalankan app di Android Studio
   - Test register/login dengan email
   - Cek di Firebase Console → Authentication → Users

2. **Test Firestore Connection:**
   - Buka Logcat di Android Studio
   - Cari log "Firebase initialized successfully"
   - Test loading categories & foods

3. **Test Cart Functionality:**
   - Add items to cart
   - Cek Firestore Console → users/{uid}/cart

4. **Test Order Flow:**
   - Complete checkout process
   - Verify order in Firestore Console → orders

### **5. 📱 PRODUCTION SETUP (EKSTERNAL)**
**❌ Untuk deploy production - MANUAL:**

1. **Generate Release SHA-1:**
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

2. **Add SHA-1 to Firebase:**
   - Firebase Console → Project Settings → Your apps
   - Add SHA-1 certificate fingerprint

3. **Update Security Rules:**
   - Change test mode rules to production
   - Remove test mode permissions

4. **Setup Domain Verification:**
   - Add authorized domains in Firebase Console
   - Setup custom email templates

### **6. 🚀 OPTIONAL ENHANCEMENTS (EKSTERNAL)**
**❌ Advanced features - MANUAL IMPLEMENTATION:**

1. **Push Notifications:**
   - Firebase Console → Cloud Messaging
   - Generate server key
   - Implement FCM in app

2. **Analytics Dashboard:**
   - Firebase Console → Analytics
   - Setup custom events
   - Track user behavior

3. **Performance Monitoring:**
   - Add Performance SDK
   - Monitor app performance
   - Track crashes

4. **A/B Testing:**
   - Firebase Console → Remote Config
   - Setup experiments
   - Monitor conversion rates

---

## **✅ CHECKLIST SETUP FIREBASE ENHANCED**

**Manual Steps (EKSTERNAL):**
- [ ] ✅ Firebase project dibuat dengan nama "WaveOfFood"
- [ ] ✅ Android app ditambahkan dengan package `com.kelasxi.waveoffood`
- [ ] ✅ File `google-services.json` didownload dan ditaruh di `app/`
- [ ] ✅ Authentication enabled (Email/Password)
- [ ] ✅ Firestore database dibuat dengan test mode di asia-southeast1
- [ ] ✅ Enhanced security rules di-copy ke Firebase Console
- [ ] ✅ Data sample (categories, foods, promotions) diimport
- [ ] ✅ Test authentication berfungsi
- [ ] ✅ Test Firestore connection berhasil
- [ ] ✅ Test cart & order functionality

**Automatic Steps (SUDAH SELESAI):**
- [x] ✅ Enhanced fragments dibuat (HomeFragmentEnhanced, dll)
- [x] ✅ Firebase dependencies ditambahkan ke build.gradle
- [x] ✅ Enhanced models dibuat (CategoryModel, FoodModel, dll) 
- [x] ✅ CartManager singleton implemented
- [x] ✅ Professional green design system applied
- [x] ✅ Enhanced adapters created (CategoryAdapter, FoodAdapter, CartAdapter)
- [x] ✅ Build successful tanpa error

**⚠️ CRITICAL:** 
- File `google-services.json` HARUS ada di `app/` folder
- Package name HARUS `com.kelasxi.waveoffood` (sama persis)
- Security rules HARUS di-copy manual ke Firebase Console
- Data sample HARUS diimport manual (pilih cara manual atau CLI)

**🎯 STATUS:** 
- **App Code:** ✅ SIAP (BUILD SUCCESSFUL)
- **Firebase Setup:** ❌ PERLU MANUAL STEPS
- **Total Time:** ~15 menit untuk complete setup

---

## **🔧 CARA SUPER CEPAT IMPORT DATA (NODE.JS)**

**Prerequisites:**
1. Download Service Account Key dari Firebase Console
2. Install Node.js di komputer

**Langkah Singkat:**
```bash
# 1. Install dependencies
npm install

# 2. Rename service account key menjadi 'serviceAccountKey.json'
# 3. Letakkan file serviceAccountKey.json di root project

# 4. Import semua data enhanced (30 detik)
npm run import
```

**File yang sudah dibuat:**
- ✅ `enhanced-data.json` - Data lengkap categories, foods, promotions
- ✅ `firebase-import-enhanced.js` - Script import otomatis
- ✅ `package.json` - Dependencies dan scripts
- ✅ `FirebaseRepository.kt` - Repository lengkap untuk enhanced design
- ✅ `Models.kt` - Enhanced models dengan Firestore compatibility

---

## **🚨 TROUBLESHOOTING COMMON ISSUES**

### **❌ Error: "Default FirebaseApp failed to initialize"**
**Solusi:**
1. ✅ Pastikan `google-services.json` ada di folder `app/`
2. ✅ Pastikan package name sama: `com.kelasxi.waveoffood`
3. ✅ Clean & Rebuild project: Build → Clean Project → Rebuild Project
4. ✅ Cek app-level `build.gradle.kts` ada: `id("com.google.gms.google-services")`

### **❌ Error: "Permission denied" saat akses Firestore**
**Solusi:**
1. ✅ Copy security rules yang sudah disediakan ke Firebase Console
2. ✅ Pastikan user sudah login sebelum akses data
3. ✅ Test dengan anonymous authentication jika perlu

### **❌ Error: "Network error" atau "Connection failed"**
**Solusi:**
1. ✅ Pastikan internet connection stabil
2. ✅ Cek Firebase project ID di `google-services.json`
3. ✅ Restart app atau clear app data

### **❌ Build Error: "Unresolved reference"**
**Solusi:**
1. ✅ Sync project: File → Sync Project with Gradle Files
2. ✅ Invalidate caches: File → Invalidate Caches and Restart
3. ✅ Check import statements di adapter dan fragment files

---

## **📋 FINAL CHECKLIST LENGKAP**

### **Firebase Console Setup:**
- [ ] ✅ Project "WaveOfFood" dibuat
- [ ] ✅ Android app dengan package `com.kelasxi.waveoffood` ditambahkan
- [ ] ✅ `google-services.json` didownload dan ditaruh di `app/`
- [ ] ✅ Authentication enabled (Email/Password)
- [ ] ✅ Firestore database dibuat di asia-southeast1
- [ ] ✅ Security rules enhanced di-copy ke Rules tab
- [ ] ✅ Data sample diimport (manual atau script)

### **Android Studio Setup:**
- [x] ✅ Firebase dependencies di `build.gradle.kts`
- [x] ✅ Enhanced models (`CategoryModel`, `FoodModel`, dll)
- [x] ✅ `FirebaseRepository.kt` created
- [x] ✅ Enhanced fragments ready (`HomeFragmentEnhanced`, dll)
- [x] ✅ Enhanced adapters ready (`CategoryAdapter`, `FoodAdapter`, dll)
- [x] ✅ Professional design system applied
- [x] ✅ Build successful tanpa error

### **Testing Checklist:**
- [ ] ✅ App runs without crashes
- [ ] ✅ Firebase connection successful (check logs)
- [ ] ✅ Categories loading from Firestore
- [ ] ✅ Foods loading by category
- [ ] ✅ Add to cart functionality
- [ ] ✅ User registration/login
- [ ] ✅ Order creation process

---

## **🚀 NEXT STEPS AFTER SETUP**

### **1. Immediate Testing (5 menit)**
```kotlin
// Test Firebase connection in MainActivity
private fun testFirebaseConnection() {
    Log.d("Firebase", "Testing connection...")
    FirebaseFirestore.getInstance()
        .collection("categories")
        .limit(1)
        .get()
        .addOnSuccessListener { documents ->
            Log.d("Firebase", "✅ Connection successful! Found ${documents.size()} categories")
        }
        .addOnFailureListener { exception ->
            Log.e("Firebase", "❌ Connection failed", exception)
        }
}
```

### **2. Enhanced Features to Add:**
- 🔔 Push notifications untuk order updates
- 📍 GPS location untuk delivery tracking
- 💳 Payment gateway integration (Midtrans, dll)
- ⭐ Review & rating system
- 🎯 Loyalty program dengan points
- 📊 Analytics tracking
- 🔍 Advanced search dengan filters

### **3. Production Deployment:**
- 🔐 Update security rules untuk production
- 🔑 Generate release keystore dan SHA-1
- 🌐 Setup custom domain untuk deep links
- 📱 Test di multiple devices & Android versions
- 🚀 Publish ke Google Play Store

**🎉 SELAMAT! Firebase Enhanced Integration Complete!**

**📞 Support:**
- Jika ada error, cek Logcat di Android Studio
- Firebase Console memiliki debugging tools
- StackOverflow untuk troubleshooting spesifik
