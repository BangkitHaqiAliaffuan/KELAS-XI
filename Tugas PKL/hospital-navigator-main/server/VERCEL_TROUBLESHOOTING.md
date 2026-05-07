# 🔧 Vercel Deployment Troubleshooting

## Error: "Serverless Function has an invalid name"

### Error Message:
```
A Serverless Function has an invalid name: "'Tugas PKL/hospital-navigator-main/server/src/index.js'". 
They must be less than 128 characters long and must not contain any space.
```

### Penyebab:
Path repository Anda mengandung **spasi** (contoh: `Tugas PKL/...`). Vercel tidak bisa membuat serverless function dengan nama yang mengandung spasi.

### ✅ Solusi (Sudah Diterapkan):

File `vercel.json` sudah diupdate untuk menggunakan entry point `api/index.js` yang lebih pendek dan kompatibel dengan Vercel.

**Struktur baru:**
```
server/
├── api/
│   └── index.js          # ✅ Entry point untuk Vercel (pendek, tanpa spasi)
├── src/
│   └── index.js          # Express app utama
└── vercel.json           # ✅ Updated - gunakan api/index.js
```

### 🚀 Cara Deploy Ulang:

1. **Commit perubahan:**
   ```bash
   git add .
   git commit -m "Fix Vercel serverless function name"
   git push
   ```

2. **Redeploy di Vercel:**
   - Vercel akan auto-deploy dari GitHub
   - Atau manual: Vercel Dashboard → Deployments → Redeploy

3. **Test:**
   ```
   https://your-backend.vercel.app/api/v1/rooms
   ```

---

## Error Lainnya

### 1. "Module not found"

**Error:**
```
Error: Cannot find module 'express'
```

**Solusi:**
- Pastikan `package.json` ada di root folder `server/`
- Pastikan semua dependencies terinstall
- Redeploy

### 2. "CORS Error"

**Error:**
```
Access to XMLHttpRequest blocked by CORS policy
```

**Solusi:**
1. Cek environment variable `ALLOWED_ORIGINS` di Vercel
2. Pastikan include URL frontend:
   ```
   ALLOWED_ORIGINS=https://hospitalnavigator-lake.vercel.app,https://*.vercel.app
   ```
3. Redeploy backend

### 3. "404 Not Found"

**Error:**
```
404 - This page could not be found
```

**Solusi:**
1. Pastikan Root Directory = `server` di Vercel settings
2. Cek `vercel.json` ada di folder `server/`
3. Cek routes di `vercel.json`:
   ```json
   "routes": [
     {
       "src": "/(.*)",
       "dest": "api/index.js"
     }
   ]
   ```
4. Redeploy

### 4. "Function Execution Timeout"

**Error:**
```
Task timed out after 10.00 seconds
```

**Solusi:**
- Vercel Free Tier: max 10 detik per request
- Optimasi query/logic di backend
- Atau upgrade ke Vercel Pro (60 detik)

### 5. "Build Failed"

**Error:**
```
Error: Build failed with exit code 1
```

**Solusi:**
1. Cek Vercel build logs untuk detail error
2. Pastikan `"type": "module"` ada di `package.json`
3. Pastikan semua import menggunakan `.js` extension:
   ```javascript
   import config from './config/index.js';  // ✅ Good
   import config from './config/index';     // ❌ Bad
   ```
4. Redeploy

### 6. "Environment Variable Not Found"

**Error:**
```
process.env.ALLOWED_ORIGINS is undefined
```

**Solusi:**
1. Vercel Dashboard → Project → Settings → Environment Variables
2. Tambahkan variable yang hilang
3. Pilih environment: Production, Preview, Development
4. Redeploy (penting!)

---

## Checklist Deployment

Sebelum deploy, pastikan:

- [ ] `vercel.json` ada di folder `server/`
- [ ] `api/index.js` ada dan export app
- [ ] `package.json` ada `"type": "module"`
- [ ] Semua import menggunakan `.js` extension
- [ ] Environment variables sudah diset di Vercel
- [ ] Root Directory = `server` di Vercel settings
- [ ] "Include source files outside Root Directory" dicentang

---

## Testing Deployment

### 1. Test Root Endpoint
```bash
curl https://your-backend.vercel.app/
```

Expected response:
```json
{
  "success": true,
  "message": "Hospital Navigator API",
  "version": "1.0.0",
  "endpoints": {
    "health": "/api/v1/health",
    "rooms": "/api/v1/rooms",
    "qrAnchors": "/api/v1/qr-anchors"
  }
}
```

### 2. Test API Endpoints
```bash
# Health check
curl https://your-backend.vercel.app/api/v1/health

# Get rooms
curl https://your-backend.vercel.app/api/v1/rooms

# Get QR anchors
curl https://your-backend.vercel.app/api/v1/qr-anchors
```

### 3. Test CORS
```bash
curl -H "Origin: https://hospitalnavigator-lake.vercel.app" \
     -H "Access-Control-Request-Method: GET" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS \
     https://your-backend.vercel.app/api/v1/rooms
```

Expected: Status 200 dengan CORS headers

---

## Monitoring

### View Logs
1. Vercel Dashboard → Project Backend
2. Klik deployment terakhir
3. Tab **"Logs"** untuk console output
4. Tab **"Functions"** untuk performance metrics

### Common Log Messages

**✅ Success:**
```
[CORS] Allowed origins: https://hospitalnavigator-lake.vercel.app
[CORS] Request from origin: https://hospitalnavigator-lake.vercel.app
[CORS] Origin allowed: https://hospitalnavigator-lake.vercel.app
```

**❌ CORS Blocked:**
```
[CORS] Blocked request from origin: https://unknown-site.com
```

**❌ Error:**
```
Error: Cannot find module 'express'
```

---

## Rollback Deployment

Jika deployment baru bermasalah:

1. Vercel Dashboard → Deployments
2. Pilih deployment sebelumnya yang working
3. Klik titik tiga (...) → **"Promote to Production"**

---

## Support

Jika masih ada masalah:

1. **Cek Vercel Logs** untuk error details
2. **Cek Browser DevTools** Network tab untuk CORS errors
3. **Test API** langsung dengan curl/Postman
4. **Baca dokumentasi:**
   - `QUICK_DEPLOY.md` - Quick start
   - `VERCEL_DEPLOYMENT.md` - Full guide
   - `VERCEL_TROUBLESHOOTING.md` - This file

---

## Vercel CLI Commands

```bash
# Login
vercel login

# Deploy to preview
vercel

# Deploy to production
vercel --prod

# View logs
vercel logs

# List deployments
vercel ls

# Remove deployment
vercel rm <deployment-url>

# View environment variables
vercel env ls

# Add environment variable
vercel env add ALLOWED_ORIGINS
```

---

**Happy Deploying! 🚀**
