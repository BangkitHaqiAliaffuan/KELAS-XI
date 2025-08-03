# 🚀 WaveOfFood Admin Setup Guide

## 📋 Overview
Aplikasi admin dashboard untuk mengelola WaveOfFood dengan fitur-fitur lengkap yang terintegrasi dengan data user app.

## 🏗️ Arsitektur Admin Dashboard
```
WaveOfFoodAdmin/
├── 🔐 Authentication (Admin Login)
├── 📊 Dashboard Overview (Statistics)
├── 📦 Order Management (Status Updates)
├── 🍽️ Menu Management (CRUD Operations)
├── 👥 User Management (Customer Data)
└── 📈 Analytics & Reports
```

## ✨ Fitur Utama yang Sudah Diimplementasi

### 1. 🔐 **Admin Authentication**
- Login khusus admin dengan validasi email
- Session management dengan Firebase Auth
- Security check untuk akses admin

### 2. 📊 **Dashboard Overview**
- **Real-time Statistics:**
  - Total Orders (dari collection `orders`)
  - Total Users (dari collection `users`)
  - Total Menu Items (dari collection `menu`/`foods`)
  - Total Revenue (dari orders dengan status completed)

### 3. 📦 **Order Management**
- **View All Orders:** List semua pesanan dengan real-time updates
- **Order Status Management:**
  - Pending → Confirmed → Preparing → Delivering → Completed
  - Update status dengan satu klik
- **Order Details:** Informasi lengkap setiap pesanan
- **Customer Information:** Nama, alamat, phone customer

### 4. 🎨 **Professional UI Design**
- **Green Theme:** Sesuai dengan WaveOfFood user app (#4CAF50)
- **Material Design 3:** Modern card-based interface
- **Responsive Layout:** Optimal untuk berbagai ukuran layar
- **Status Color Coding:** Visual feedback yang jelas

## 🔧 Setup Instructions

### Step 1: Firebase Configuration
1. **Copy Firebase Config dari User App:**
   ```bash
   # Copy google-services.json dari WaveOfFood user app ke admin app
   cp "WaveOfFood/app/google-services.json" "WaveOfFoodAdmin/app/"
   ```

2. **Atau buat project Firebase terpisah** (opsional):
   - Buat project baru di Firebase Console
   - Enable Authentication & Firestore
   - Download google-services.json
   - Replace template file

### Step 2: Admin User Setup
1. **Buat Admin Account:**
   ```bash
   # Di Firebase Console > Authentication
   # Tambah user dengan email: admin@waveoffood.com
   # Password: (atur password yang kuat)
   ```

2. **Update Admin Email** (optional):
   ```java
   // Di LoginActivity.java, line 28
   private static final String ADMIN_EMAIL = "your-admin@email.com";
   ```

### Step 3: Build & Run
```bash
# Sync project
./gradlew clean build

# Install ke device/emulator
./gradlew installDebug
```

## 📱 Cara Penggunaan

### 1. **Login Admin**
- Buka app WaveOfFood Admin
- Masukkan email admin (admin@waveoffood.com)
- Masukkan password
- Klik LOGIN

### 2. **Dashboard Management**
- **View Statistics:** Lihat ringkasan data real-time
- **Refresh Data:** Pull-to-refresh atau menu refresh
- **Navigate:** Klik card untuk masuk ke management section

### 3. **Order Management**
- **View Orders:** Semua pesanan ditampilkan berdasarkan waktu terbaru
- **Update Status:** Klik tombol "Update Status" untuk mengubah status
- **Order Flow:** Pending → Confirmed → Preparing → Delivering → Completed
- **View Details:** Klik card order untuk detail lengkap

## 🔄 Data Integration dengan User App

### Compatible Data Models:
```
✅ OrderModel - Kompatibel 100% dengan user app
✅ UserModel - Sesuai struktur Firestore users collection  
✅ FoodModel - Mendukung collection 'menu' dan 'foods'
✅ CartItemModel - Parsing items dalam orders
```

### Firebase Collections yang Digunakan:
- `orders` - Management pesanan customer
- `users` - Data customer dan statistik
- `menu` - Management food items (primary)
- `foods` - Fallback collection untuk food items

## 🎯 Status Updates Flow

```
📱 Customer App → 📦 Place Order → Status: "pending"
                      ↓
🔧 Admin App → 📋 View Order → Update to "confirmed"
                      ↓  
🔧 Admin App → 🍳 Preparing → Update to "preparing"
                      ↓
🔧 Admin App → 🚚 Delivery → Update to "delivering"
                      ↓
🔧 Admin App → ✅ Complete → Update to "completed"
```

## 🚀 Fitur Lanjutan (Ready for Implementation)

### Order Detail Activity:
- Breakdown item pesanan
- Customer delivery address
- Payment method information
- Order timeline tracking

### Menu Management:
- Add/Edit/Delete food items
- Category management
- Price updates
- Availability toggle

### User Management:
- Customer profiles
- Order history per user
- User activity analytics

### Analytics Dashboard:
- Sales charts (daily/weekly/monthly)
- Popular items analysis
- Revenue trends
- Customer insights

## 🔧 Troubleshooting

### Common Issues:

1. **Login Access Denied:**
   ```
   Issue: Email tidak recognized sebagai admin
   Solution: Update ADMIN_EMAIL constant atau gunakan email yang sesuai
   ```

2. **No Orders Showing:**
   ```
   Issue: Collection 'orders' kosong
   Solution: Pastikan user app sudah membuat pesanan test
   ```

3. **Firebase Connection:**
   ```
   Issue: google-services.json not found
   Solution: Copy file dari user app atau download dari Firebase Console
   ```

## 📞 Admin Credentials Default

```
Email: admin@waveoffood.com
Password: (set via Firebase Console)

Atau gunakan email admin custom dengan format:
- Contains "admin" keyword
- Example: youradmin@domain.com
```

## 🎨 UI/UX Features

### Visual Design:
- ✅ Green primary color (#4CAF50) - matching user app
- ✅ Card-based modern interface
- ✅ Status color coding (Orange→Blue→Purple→Grey→Green→Red)
- ✅ Professional typography
- ✅ Responsive grid layout

### User Experience:
- ✅ Real-time data updates
- ✅ Pull-to-refresh
- ✅ Loading states
- ✅ Empty states with helpful messages
- ✅ One-click status updates
- ✅ Intuitive navigation

## 📊 Data Compatibility Matrix

| Feature | User App | Admin App | Status |
|---------|----------|-----------|---------|
| Orders | ✅ Create | ✅ Manage | Compatible |
| Users | ✅ Register | ✅ View | Compatible |
| Menu | ✅ Browse | 🔄 CRUD | Planned |
| Analytics | ❌ | ✅ View | Admin Only |

---

**🎉 WaveOfFood Admin siap digunakan!**

Sekarang Anda dapat mengelola restaurant dengan interface admin yang professional dan terintegrasi penuh dengan user app.
