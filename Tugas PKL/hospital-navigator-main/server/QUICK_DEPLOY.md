# ⚡ Quick Deploy ke Vercel (5 Menit)

## ⚠️ Penting: Path Repository

Jika path repository Anda mengandung **spasi** (contoh: `Tugas PKL/...`), Vercel akan error. 

**Solusi sudah diterapkan:** File `vercel.json` menggunakan `api/index.js` sebagai entry point yang lebih pendek.

---

## Langkah Singkat

### 1️⃣ Buka Vercel
- Buka [vercel.com](https://vercel.com)
- Login dengan GitHub

### 2️⃣ Import Project
- Klik **"Add New..."** → **"Project"**
- Pilih repository Anda
- **Root Directory:** Pilih folder `server` ✅
- **Centang:** "Include source files outside of the Root Directory"

### 3️⃣ Environment Variables
Tambahkan 3 variables ini:

```
NODE_ENV = production
PORT = 3001
ALLOWED_ORIGINS = https://hospitalnavigator-lake.vercel.app,https://*.vercel.app
```

### 4️⃣ Deploy
- Klik **"Deploy"**
- Tunggu 1-2 menit
- Copy URL backend Anda (contoh: `https://xxx.vercel.app`)

### 5️⃣ Update Frontend
- Buka project frontend di Vercel
- Settings → Environment Variables
- Edit `VITE_API_URL` = `https://xxx.vercel.app/api/v1`
- Redeploy frontend

### 6️⃣ Test
Buka: `https://xxx.vercel.app/api/v1/rooms`

Harus muncul data JSON ✅

---

## 🔧 Troubleshooting

**Error: "Serverless Function has an invalid name"?**
→ Sudah fixed! File `api/index.js` sudah dibuat sebagai entry point.

**CORS Error?**
→ Cek `ALLOWED_ORIGINS` include URL frontend Anda

**404 Error?**
→ Pastikan Root Directory = `server`

**Data tidak muncul?**
→ Cek `VITE_API_URL` di frontend sudah benar

---

## Selesai! 🎉

Website Anda sekarang:
- ✅ Frontend di Vercel
- ✅ Backend di Vercel
- ✅ Tidak ada CORS error
- ✅ Gratis selamanya (Vercel Free Tier)

---

Untuk troubleshooting lengkap, baca: [VERCEL_TROUBLESHOOTING.md](./VERCEL_TROUBLESHOOTING.md)

Untuk panduan lengkap, baca: [VERCEL_DEPLOYMENT.md](./VERCEL_DEPLOYMENT.md)
