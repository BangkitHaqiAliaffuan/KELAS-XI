# 🔧 Fix CORS Error - Hospital Navigator

## ❌ Error yang Anda Alami:

```
Access to XMLHttpRequest at 'https://hospital-navigator-backend.vercel.app/api/v1/rooms' 
from origin 'https://hospitalnavigator-lake.vercel.app' 
has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

---

## 🎯 Penyebab:

Backend belum mengizinkan frontend Anda (`https://hospitalnavigator-lake.vercel.app`) untuk mengakses API.

---

## ✅ Solusi: Set Environment Variable di Vercel

### **Cara 1: Via Vercel Dashboard (Recommended)**

1. **Buka Vercel Dashboard:**
   - Go to: https://vercel.com/dashboard
   - Pilih project backend Anda: `hospital-navigator-backend`

2. **Masuk ke Settings:**
   - Klik tab **"Settings"**
   - Klik **"Environment Variables"** di sidebar

3. **Tambahkan Variable Baru:**
   
   **Variable 1:**
   ```
   Name: ALLOWED_ORIGINS
   Value: https://hospitalnavigator-lake.vercel.app
   ```
   
   **Pilih Environment:**
   - ✅ Production
   - ✅ Preview
   - ✅ Development
   
   **Klik "Save"**

4. **Redeploy Backend:**
   - Go to tab **"Deployments"**
   - Klik titik tiga (...) pada deployment terakhir
   - Klik **"Redeploy"**
   - Tunggu sampai selesai

5. **Test:**
   ```bash
   curl -H "Origin: https://hospitalnavigator-lake.vercel.app" \
        -H "Access-Control-Request-Method: GET" \
        -X OPTIONS \
        https://hospital-navigator-backend.vercel.app/api/v1/rooms
   ```

---

### **Cara 2: Via Vercel CLI**

```bash
# Install Vercel CLI (jika belum)
npm install -g vercel

# Login
vercel login

# Set environment variable
vercel env add ALLOWED_ORIGINS

# Ketik value:
https://hospitalnavigator-lake.vercel.app

# Pilih environment: Production, Preview, Development (pilih semua)

# Redeploy
vercel --prod
```

---

### **Cara 3: Multiple Frontend URLs**

Jika Anda punya beberapa frontend URL (production, staging, preview):

```
Name: ALLOWED_ORIGINS
Value: https://hospitalnavigator-lake.vercel.app,https://hospital-navigator-preview.vercel.app,https://hospital-navigator-staging.vercel.app
```

**PENTING:** Pisahkan dengan koma, **TANPA SPASI**!

---

### **Cara 4: Wildcard Vercel Domains (Kurang Aman)**

Jika Anda ingin mengizinkan semua subdomain Vercel:

⚠️ **Not Recommended for Production!**

Anda perlu update CORS config di `server/src/index.js` untuk support wildcard.

---

## 🔍 Verifikasi CORS Settings

### **1. Check Environment Variables:**

```bash
# Via Vercel CLI
vercel env ls

# Atau cek di Vercel Dashboard → Settings → Environment Variables
```

### **2. Check Backend Logs:**

Setelah redeploy, cek logs di Vercel:

```
Vercel Dashboard → Your Project → Deployments → Latest → Logs
```

Cari log seperti ini:
```
[CORS] Allowed origins: https://hospitalnavigator-lake.vercel.app
```

### **3. Test CORS dengan cURL:**

```bash
# Test OPTIONS request (preflight)
curl -i -X OPTIONS \
  -H "Origin: https://hospitalnavigator-lake.vercel.app" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Content-Type" \
  https://hospital-navigator-backend.vercel.app/api/v1/rooms

# Expected response headers:
# Access-Control-Allow-Origin: https://hospitalnavigator-lake.vercel.app
# Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
# Access-Control-Allow-Headers: Content-Type, Authorization
```

### **4. Test dari Browser Console:**

Buka frontend Anda, tekan F12, paste di Console:

```javascript
fetch('https://hospital-navigator-backend.vercel.app/api/v1/health')
  .then(res => res.json())
  .then(data => console.log('✅ CORS OK:', data))
  .catch(err => console.error('❌ CORS Error:', err));
```

---

## 🚀 Quick Fix (Temporary)

Jika Anda butuh fix cepat untuk testing, saya sudah menambahkan **fallback default** di `server/src/config/index.js`:

```javascript
const defaultProductionOrigins = [
  'https://hospitalnavigator-lake.vercel.app',
  'https://hospital-navigator-frontend.vercel.app',
].join(',');
```

Ini akan otomatis mengizinkan URL tersebut jika `ALLOWED_ORIGINS` tidak diset.

**Tapi tetap recommended untuk set environment variable!**

---

## 📝 Checklist

- [ ] Buka Vercel Dashboard
- [ ] Pilih project backend
- [ ] Go to Settings → Environment Variables
- [ ] Add `ALLOWED_ORIGINS` dengan value frontend URL
- [ ] Pilih Production, Preview, Development
- [ ] Save
- [ ] Redeploy backend
- [ ] Test dengan curl atau browser
- [ ] Check logs untuk konfirmasi
- [ ] Test dari frontend

---

## 🔄 Setelah Set Environment Variable

1. **Commit dan Push Code Baru:**
   ```bash
   cd server
   git add .
   git commit -m "Add CORS fallback for production"
   git push
   ```

2. **Atau Redeploy Manual:**
   - Vercel Dashboard → Deployments → Redeploy

3. **Wait for Deployment:**
   - Tunggu sampai status "Ready"

4. **Test:**
   - Buka frontend Anda
   - Coba fetch data dari backend
   - Check browser console (F12)

---

## 🐛 Troubleshooting

### **Error Masih Muncul Setelah Set Env Var?**

1. **Pastikan sudah redeploy:**
   - Environment variables hanya apply setelah redeploy
   - Cek deployment timestamp

2. **Cek typo di URL:**
   ```
   ✅ https://hospitalnavigator-lake.vercel.app
   ❌ https://hospitalnavigator-lake.vercel.app/  (trailing slash)
   ❌ http://hospitalnavigator-lake.vercel.app   (http instead of https)
   ```

3. **Cek logs:**
   ```
   [CORS] Request from origin: https://hospitalnavigator-lake.vercel.app
   [CORS] Origin allowed: https://hospitalnavigator-lake.vercel.app
   ```
   
   Jika muncul:
   ```
   [CORS] Blocked request from origin: https://hospitalnavigator-lake.vercel.app
   ```
   
   Berarti environment variable belum apply.

4. **Clear browser cache:**
   - Hard refresh: Ctrl+Shift+R (Windows) atau Cmd+Shift+R (Mac)
   - Atau buka Incognito/Private window

5. **Check Network tab:**
   - F12 → Network tab
   - Cari request yang failed
   - Klik request → Headers tab
   - Cek "Response Headers" ada `Access-Control-Allow-Origin` atau tidak

---

## 💡 Best Practices

### **Development:**
```env
ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000,http://127.0.0.1:5173
```

### **Production:**
```env
ALLOWED_ORIGINS=https://hospitalnavigator-lake.vercel.app
```

### **Multiple Environments:**
```env
# Production
ALLOWED_ORIGINS=https://hospitalnavigator-lake.vercel.app

# Staging
ALLOWED_ORIGINS=https://hospitalnavigator-staging.vercel.app

# Preview (all preview deployments)
ALLOWED_ORIGINS=https://hospitalnavigator-lake.vercel.app,https://hospitalnavigator-*.vercel.app
```

---

## 🔐 Security Notes

1. **Jangan gunakan wildcard `*` di production:**
   ```javascript
   // ❌ BAD - allows any origin
   cors({ origin: '*' })
   
   // ✅ GOOD - specific origins only
   cors({ origin: 'https://hospitalnavigator-lake.vercel.app' })
   ```

2. **Gunakan HTTPS:**
   - Vercel otomatis provide HTTPS
   - Jangan allow HTTP origins di production

3. **Limit origins:**
   - Hanya tambahkan origin yang benar-benar dibutuhkan
   - Review regularly

---

## 📞 Still Having Issues?

1. **Check backend logs:**
   - Vercel Dashboard → Deployments → Logs

2. **Check frontend console:**
   - F12 → Console tab
   - Look for CORS errors

3. **Test backend directly:**
   ```bash
   curl https://hospital-navigator-backend.vercel.app/api/v1/health
   ```

4. **Verify environment variables:**
   ```bash
   vercel env ls
   ```

5. **Contact support:**
   - Provide error message
   - Provide backend URL
   - Provide frontend URL
   - Provide screenshot of error

---

## ✅ Expected Result

Setelah fix, Anda akan melihat di browser console:

```javascript
// ✅ Success
{
  success: true,
  data: [...],
  count: 75
}
```

Dan di Network tab:
```
Status: 200 OK
Access-Control-Allow-Origin: https://hospitalnavigator-lake.vercel.app
```

---

**Good luck! 🚀**

Jika masih error setelah mengikuti langkah di atas, screenshot error dan environment variables Anda, lalu tanyakan lagi.
