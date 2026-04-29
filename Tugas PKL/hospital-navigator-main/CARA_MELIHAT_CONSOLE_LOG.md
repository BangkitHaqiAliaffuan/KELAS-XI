# 🔍 Cara Melihat Console Log Backend Status

## ✅ Update Terbaru

Saya telah menambahkan **automatic backend detection** yang akan menampilkan console log saat aplikasi dimulai.

---

## 📝 Yang Telah Ditambahkan

### 1. **Hook untuk Backend Status** (`src/hooks/useBackendStatus.ts`)
- Otomatis check backend saat aplikasi dimulai
- Menampilkan console log yang jelas

### 2. **Update App.tsx**
- Menggunakan `useBackendStatus` hook
- Otomatis check backend saat aplikasi load

### 3. **Enhanced Console Logging** (`src/lib/api.ts`)
- Console log yang lebih detail dan jelas
- Visual indicators (✅ ❌ 🔍 📡 📁)
- Informasi lengkap tentang status backend

---

## 🚀 Cara Melihat Console Log

### Step 1: Restart Development Server

**PENTING**: Anda harus **restart** frontend development server agar perubahan `.env` dan kode baru ter-load.

```bash
# Stop frontend (Ctrl+C)
# Kemudian start lagi
npm run dev
```

### Step 2: Buka Browser Console

1. Buka aplikasi di browser: `http://localhost:5173`
2. Tekan **F12** atau **Ctrl+Shift+I** (Windows/Linux) atau **Cmd+Option+I** (Mac)
3. Klik tab **Console**

### Step 3: Lihat Console Log

Anda akan melihat log seperti ini:

#### **Jika Backend TIDAK Running:**

```
═══════════════════════════════════════════════════════
🏥 Hospital Navigator - API Configuration
═══════════════════════════════════════════════════════
API Base URL: http://localhost:3001/api/v1
Environment: development
VITE_API_URL: http://localhost:3001/api/v1
═══════════════════════════════════════════════════════

[useBackendStatus] Hook initialized, checking backend status...
🔍 [API] Checking backend health...
🔍 [API] Health check URL: http://localhost:3001/api/v1/health
❌ [API] Cannot connect to backend - Network error
❌ [API] Make sure backend is running at: http://localhost:3001/api/v1

═══════════════════════════════════════════════════════
❌ Backend is NOT AVAILABLE - Using static data
📁 Data source: Static files (Fallback)
💡 To use backend: cd server && npm run dev
═══════════════════════════════════════════════════════

[useBackendStatus] Backend is NOT AVAILABLE ❌
[useBackendStatus] Data source: Static (Fallback)
[useBackendStatus] ⚠️ Using static data (backend not running)
```

#### **Jika Backend RUNNING:**

```
═══════════════════════════════════════════════════════
🏥 Hospital Navigator - API Configuration
═══════════════════════════════════════════════════════
API Base URL: http://localhost:3001/api/v1
Environment: development
VITE_API_URL: http://localhost:3001/api/v1
═══════════════════════════════════════════════════════

[useBackendStatus] Hook initialized, checking backend status...
🔍 [API] Checking backend health...
🔍 [API] Health check URL: http://localhost:3001/api/v1/health
✅ [API] Backend is HEALTHY and AVAILABLE
✅ [API] Response: { success: true, message: "Hospital Navigator API is running", ... }

═══════════════════════════════════════════════════════
✅ Backend is AVAILABLE - Using API data
📡 Data source: Backend API (Real-time)
═══════════════════════════════════════════════════════

[useBackendStatus] Backend is AVAILABLE ✅
[useBackendStatus] Data source: API (Real-time)
[useBackendStatus] 🎉 Using backend API for data
```

---

## 🧪 Testing

### Test 1: Tanpa Backend

```bash
# Pastikan backend TIDAK running
# Start frontend
npm run dev

# Buka browser console
# Expected: Console log menunjukkan "Backend is NOT AVAILABLE"
```

### Test 2: Dengan Backend

```bash
# Terminal 1 - Start backend
cd server
npm install  # Jika belum install
npm run dev

# Terminal 2 - Start frontend (RESTART jika sudah running)
npm run dev

# Buka browser console
# Expected: Console log menunjukkan "Backend is AVAILABLE"
```

### Test 3: Switch Backend On/Off

```bash
# Start dengan backend running
# Console: "Backend is AVAILABLE"

# Stop backend (Ctrl+C di terminal backend)
# Refresh browser (F5)
# Console: "Backend is NOT AVAILABLE"

# Start backend lagi
# Refresh browser (F5)
# Console: "Backend is AVAILABLE"
```

---

## 🐛 Troubleshooting

### Problem: Console tidak menampilkan log apapun

**Solution**:
1. **Hard refresh** browser:
   - Windows/Linux: `Ctrl + Shift + R`
   - Mac: `Cmd + Shift + R`

2. **Clear cache dan restart**:
   ```bash
   # Stop frontend (Ctrl+C)
   # Clear browser cache
   # Start frontend
   npm run dev
   ```

3. **Check console filter**:
   - Pastikan filter di console tidak menyembunyikan log
   - Set filter ke "All levels" atau "Verbose"

### Problem: Console menunjukkan "VITE_API_URL: Not set"

**Solution**:
1. Check file `.env` ada di root directory
2. Isi file `.env`:
   ```env
   VITE_API_URL=http://localhost:3001/api/v1
   ```
3. **RESTART** frontend:
   ```bash
   # Stop (Ctrl+C)
   npm run dev
   ```

### Problem: Console menunjukkan "Cannot connect to backend"

**Solution**:
1. Check backend running:
   ```bash
   curl http://localhost:3001/api/v1/health
   ```

2. Jika error, start backend:
   ```bash
   cd server
   npm run dev
   ```

3. Check port tidak bentrok (backend harus di port 3001)

### Problem: CORS Error di console

**Solution**:
1. Check `server/.env`:
   ```env
   CORS_ORIGIN=http://localhost:5173
   ```

2. Restart backend:
   ```bash
   cd server
   npm run dev
   ```

---

## 📊 Console Log Explanation

### Symbols Meaning:
- ✅ = Success / Available
- ❌ = Error / Not Available
- 🔍 = Checking / Searching
- 📡 = API Data (Real-time)
- 📁 = Static Data (Fallback)
- 💡 = Tip / Suggestion
- 🔄 = Resetting / Refreshing
- ⚠️ = Warning
- 🎉 = Success message

### Log Sections:

1. **API Configuration** (Startup)
   - Shows API URL and environment
   - Displayed once when app loads

2. **Backend Health Check** (Automatic)
   - Shows health check process
   - Displays result (available or not)

3. **Data Source Selection** (Result)
   - Shows which data source is being used
   - API (backend) or Static (fallback)

---

## ✅ Checklist

Untuk memastikan console log muncul:

- [ ] File `.env` ada di root directory
- [ ] `.env` berisi `VITE_API_URL=http://localhost:3001/api/v1`
- [ ] Frontend di-**restart** setelah update `.env`
- [ ] Browser console terbuka (F12)
- [ ] Console filter set ke "All levels"
- [ ] Hard refresh browser (Ctrl+Shift+R)

---

## 🎯 Expected Behavior

### Scenario 1: Backend OFF
```
Console shows:
❌ Backend is NOT AVAILABLE
📁 Using static data
💡 To use backend: cd server && npm run dev
```

### Scenario 2: Backend ON
```
Console shows:
✅ Backend is AVAILABLE
📡 Using API data
🎉 Using backend API for data
```

### Scenario 3: Backend Error
```
Console shows:
❌ Cannot connect to backend
⚠️ Network error
📁 Falling back to static data
```

---

## 📞 Still Not Working?

Jika setelah mengikuti semua langkah di atas console log masih tidak muncul:

1. **Check browser console filter**:
   - Klik icon filter di console
   - Pastikan "Info", "Warnings", "Errors" semua dicentang

2. **Try different browser**:
   - Chrome, Firefox, atau Edge
   - Buka incognito/private mode

3. **Check for JavaScript errors**:
   - Lihat apakah ada error merah di console
   - Error bisa mencegah log muncul

4. **Verify files exist**:
   ```bash
   ls -la src/lib/api.ts
   ls -la src/hooks/useBackendStatus.ts
   ls -la .env
   ```

5. **Check build**:
   ```bash
   npm run build
   # Jika ada error, fix dulu
   ```

---

## 🎉 Summary

Setelah restart frontend, Anda **PASTI** akan melihat console log yang menunjukkan:
- ✅ API Configuration
- ✅ Backend health check process
- ✅ Backend status (available or not)
- ✅ Data source being used

Jika tidak muncul, kemungkinan besar:
1. Frontend belum di-restart
2. Browser cache belum di-clear
3. Console filter menyembunyikan log

**Solusi**: Hard refresh (Ctrl+Shift+R) dan restart frontend! 🚀
