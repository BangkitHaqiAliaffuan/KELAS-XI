# 🚀 Panduan Deploy Backend ke Vercel (GRATIS)

## Persiapan

### 1. Install Vercel CLI (Opsional)
```bash
npm install -g vercel
```

## Cara 1: Deploy via Vercel Dashboard (Paling Mudah)

### Step 1: Buat Akun Vercel
1. Buka [vercel.com](https://vercel.com)
2. Sign up dengan GitHub account Anda
3. Vercel akan otomatis connect ke GitHub

### Step 2: Import Project
1. Di Vercel Dashboard, klik **"Add New..."** → **"Project"**
2. Pilih repository GitHub Anda (hospital-navigator)
3. Vercel akan detect repository Anda

### Step 3: Konfigurasi Project
Pada halaman konfigurasi:

**Framework Preset:** Other

**Root Directory:** 
- Klik **"Edit"** 
- Pilih folder `server`
- ✅ Centang "Include source files outside of the Root Directory in the Build Step"

**Build Settings:**
- Build Command: (kosongkan atau `npm install`)
- Output Directory: (kosongkan)
- Install Command: `npm install`

### Step 4: Environment Variables
Tambahkan environment variables berikut:

| Key | Value | Keterangan |
|-----|-------|------------|
| `NODE_ENV` | `production` | Environment mode |
| `PORT` | `3001  ` | Port (tidak digunakan di Vercel, tapi tetap perlu) |
| `ALLOWED_ORIGINS` | `https://hospitalnavigator-lake.vercel.app,https://*.vercel.app` | URL frontend Anda |

**Cara menambahkan:**
1. Scroll ke bawah ke bagian **"Environment Variables"**
2. Klik **"Add"** untuk setiap variable
3. Masukkan Key dan Value
4. Pilih environment: **Production**, **Preview**, **Development** (pilih semua)

### Step 5: Deploy
1. Klik **"Deploy"**
2. Tunggu proses build (1-2 menit)
3. Setelah selesai, Anda akan mendapat URL seperti:
   ```
   https://hospital-navigator-backend.vercel.app
   ```

### Step 6: Test Backend
Buka di browser:
```
https://your-backend-url.vercel.app/api/v1/rooms
```

Harus menampilkan JSON data ruangan.

---

## Cara 2: Deploy via Vercel CLI

### Step 1: Login
```bash
cd server
vercel login
```

### Step 2: Deploy
```bash
vercel
```

Jawab pertanyaan:
- Set up and deploy? **Y**
- Which scope? (pilih account Anda)
- Link to existing project? **N**
- What's your project's name? `hospital-navigator-backend`
- In which directory is your code located? `./`

### Step 3: Set Environment Variables
```bash
vercel env add ALLOWED_ORIGINS
```
Masukkan value: `https://hospitalnavigator-lake.vercel.app,https://*.vercel.app`

Pilih environment: **Production**, **Preview**, **Development**

### Step 4: Deploy ke Production
```bash
vercel --prod
```

---

## Konfigurasi Frontend

Setelah backend berhasil di-deploy, update frontend:

### Step 1: Update Environment Variable di Vercel (Frontend)
1. Buka Vercel Dashboard → Project Frontend Anda
2. Settings → Environment Variables
3. Edit atau tambahkan:
   - **Key:** `VITE_API_URL`
   - **Value:** `https://your-backend-url.vercel.app/api/v1`
   - **Environment:** Production, Preview, Development

### Step 2: Redeploy Frontend
1. Di Vercel Dashboard → Deployments
2. Klik titik tiga (...) pada deployment terakhir
3. Klik **"Redeploy"**

Atau push commit baru ke GitHub untuk trigger auto-deploy.

---

## Update CORS di Backend

Setelah tahu URL backend Anda, update `ALLOWED_ORIGINS`:

### Via Vercel Dashboard:
1. Backend Project → Settings → Environment Variables
2. Edit `ALLOWED_ORIGINS`
3. Tambahkan URL backend Anda sendiri:
   ```
   https://hospitalnavigator-lake.vercel.app,https://*.vercel.app,https://your-backend-url.vercel.app
   ```
4. Redeploy backend

---

## Testing

### 1. Test Backend Endpoints
```bash
# Health check
curl https://your-backend-url.vercel.app/api/v1/health

# Get rooms
curl https://your-backend-url.vercel.app/api/v1/rooms

# Get QR anchors
curl https://your-backend-url.vercel.app/api/v1/qr-anchors
```

### 2. Test Frontend
1. Buka website frontend Anda
2. Buka DevTools → Network tab
3. Refresh halaman
4. Cek request ke `/api/v1/rooms` dan `/api/v1/qr-anchors`
5. Status harus **200 OK**

---

## Troubleshooting

### Error: "Not allowed by CORS"
**Solusi:**
1. Cek `ALLOWED_ORIGINS` di backend environment variables
2. Pastikan URL frontend Anda sudah ada di list
3. Redeploy backend setelah update

### Error: "404 Not Found"
**Solusi:**
1. Cek `vercel.json` sudah benar
2. Pastikan routes mengarah ke `src/index.js`
3. Redeploy

### Error: "Module not found"
**Solusi:**
1. Pastikan semua dependencies ada di `package.json`
2. Cek `"type": "module"` ada di `package.json`
3. Redeploy

### Backend tidak update setelah push
**Solusi:**
1. Vercel auto-deploy hanya untuk branch `main` atau `master`
2. Pastikan Anda push ke branch yang benar
3. Atau deploy manual via CLI: `vercel --prod`

---

## Monitoring

### View Logs
1. Vercel Dashboard → Project Backend
2. Klik deployment terakhir
3. Tab **"Logs"** untuk melihat console logs
4. Tab **"Functions"** untuk melihat serverless function performance

### Check Performance
1. Tab **"Analytics"** untuk melihat request metrics
2. Tab **"Speed Insights"** untuk performance monitoring

---

## Batasan Vercel Free Tier

| Resource | Limit |
|----------|-------|
| Bandwidth | 100 GB/bulan |
| Serverless Function Execution | 100 GB-Hours/bulan |
| Serverless Function Duration | 10 detik max |
| Deployments | Unlimited |
| Team Members | 1 (hobby plan) |

Untuk aplikasi hospital navigator, ini lebih dari cukup!

---

## Custom Domain (Opsional)

Jika Anda punya domain sendiri:

1. Vercel Dashboard → Project → Settings → Domains
2. Klik **"Add"**
3. Masukkan domain Anda (contoh: `api.yourdomain.com`)
4. Ikuti instruksi untuk update DNS records
5. Update `VITE_API_URL` di frontend dengan domain baru

---

## Checklist Deployment

- [ ] Backend di-deploy ke Vercel
- [ ] Environment variables `ALLOWED_ORIGINS` sudah diset
- [ ] Test endpoint `/api/v1/rooms` return data
- [ ] Test endpoint `/api/v1/qr-anchors` return data
- [ ] Frontend environment variable `VITE_API_URL` sudah diupdate
- [ ] Frontend di-redeploy
- [ ] Test frontend bisa fetch data dari backend
- [ ] Tidak ada CORS error di browser console

---

## Struktur URL Final

**Backend:**
```
https://hospital-navigator-backend.vercel.app/api/v1/rooms
https://hospital-navigator-backend.vercel.app/api/v1/qr-anchors
```

**Frontend:**
```
https://hospitalnavigator-lake.vercel.app
```

**Environment Variables:**

Backend `.env` (di Vercel):
```env
NODE_ENV=production
PORT=3001
ALLOWED_ORIGINS=https://hospitalnavigator-lake.vercel.app,https://*.vercel.app
```

Frontend `.env` (di Vercel):
```env
VITE_API_URL=https://hospital-navigator-backend.vercel.app/api/v1
```

---

## Support

Jika ada masalah:
1. Cek Vercel logs untuk error messages
2. Cek browser DevTools Network tab untuk CORS errors
3. Pastikan semua environment variables sudah benar
4. Coba redeploy backend dan frontend

**Selamat! Backend Anda sekarang online 24/7 gratis! 🎉**
