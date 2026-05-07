# Panduan Deployment Hospital Navigator

## Masalah CORS dengan Localhost

Ketika frontend di-hosting di Vercel tetapi backend masih di `localhost:3001`, browser akan memblokir request dengan error:

```
Access to XMLHttpRequest at 'http://localhost:3001/api/v1/qr-anchors' from origin 'https://hospitalnavigator-lake.vercel.app' has been blocked by CORS policy: Permission was denied for this request to access the `loopback` address space.
```

**Penyebab:**
- `localhost` hanya bisa diakses dari komputer lokal
- Browser memblokir akses ke loopback address dari situs publik (fitur keamanan)

## Solusi: Deploy Backend

Anda perlu hosting backend Express.js Anda. Berikut pilihan hosting gratis/murah:

### Option 1: Railway (Recommended - Gratis untuk start)
1. Buat akun di [Railway.app](https://railway.app)
2. Install Railway CLI atau gunakan GitHub integration
3. Deploy folder `server/`:
   ```bash
   cd server
   railway login
   railway init
   railway up
   ```
4. Set environment variables di Railway dashboard:
   - `PORT` (Railway akan set otomatis)
   - `ALLOWED_ORIGINS` (tambahkan URL Vercel Anda)
5. Railway akan memberikan URL publik seperti: `https://your-app.railway.app`

### Option 2: Render (Gratis dengan batasan)
1. Buat akun di [Render.com](https://render.com)
2. Create New > Web Service
3. Connect repository GitHub Anda
4. Konfigurasi:
   - **Root Directory**: `server`
   - **Build Command**: `npm install`
   - **Start Command**: `npm start`
5. Set environment variables:
   - `NODE_ENV=production`
   - `ALLOWED_ORIGINS=https://hospitalnavigator-lake.vercel.app`
6. Render akan memberikan URL seperti: `https://your-app.onrender.com`

### Option 3: Vercel (untuk Node.js API)
1. Buat file `server/vercel.json`:
   ```json
   {
     "version": 2,
     "builds": [
       {
         "src": "src/index.js",
         "use": "@vercel/node"
       }
     ],
     "routes": [
       {
         "src": "/(.*)",
         "dest": "src/index.js"
       }
     ]
   }
   ```
2. Deploy:
   ```bash
   cd server
   vercel
   ```

### Option 4: Heroku (Berbayar mulai Nov 2022)
1. Install Heroku CLI
2. Deploy:
   ```bash
   cd server
   heroku create your-app-name
   git push heroku main
   ```

## Konfigurasi Frontend di Vercel

Setelah backend di-deploy, update environment variable di Vercel:

1. Buka Vercel Dashboard > Your Project > Settings > Environment Variables
2. Tambahkan variable:
   - **Key**: `VITE_API_URL`
   - **Value**: `https://your-backend-url.com/api/v1` (URL dari Railway/Render/dll)
   - **Environment**: Production, Preview, Development (pilih semua)
3. Redeploy frontend Anda

## Konfigurasi Backend CORS

Pastikan backend Anda mengizinkan origin dari Vercel. Update `server/.env`:

```env
PORT=3001
NODE_ENV=production
ALLOWED_ORIGINS=https://hospitalnavigator-lake.vercel.app,https://your-preview-url.vercel.app
```

Backend sudah dikonfigurasi untuk membaca `ALLOWED_ORIGINS` dari environment variable.

## Testing

### Development (Local)
```bash
# Terminal 1 - Backend
cd server
npm run dev

# Terminal 2 - Frontend
npm run dev
```

Frontend akan menggunakan `http://localhost:3001/api/v1` dari file `.env`

### Production (Hosted)
- Frontend di Vercel akan menggunakan `VITE_API_URL` dari Vercel environment variables
- Backend di Railway/Render akan menggunakan `ALLOWED_ORIGINS` dari platform environment variables

## Troubleshooting

### Error: CORS masih terblokir setelah deploy
1. Cek environment variable `VITE_API_URL` di Vercel sudah benar
2. Cek environment variable `ALLOWED_ORIGINS` di backend sudah include URL Vercel
3. Redeploy frontend setelah update environment variable
4. Clear browser cache atau test di incognito mode

### Error: Cannot connect to backend
1. Cek backend sudah running di hosting platform
2. Test backend URL langsung di browser: `https://your-backend-url.com/api/v1/rooms`
3. Cek logs di hosting platform untuk error messages

### Error: 404 Not Found
1. Pastikan API routes sudah benar: `/api/v1/rooms`, `/api/v1/qr-anchors`
2. Cek backend logs untuk routing issues

## Monitoring

### Backend Health Check
Akses endpoint health check:
```
GET https://your-backend-url.com/api/v1/rooms
```

Harus return JSON array dengan data ruangan.

### Frontend API Status
Buka website Anda dan cek:
1. Network tab di DevTools
2. Lihat request ke API backend
3. Pastikan status 200 OK

## Biaya Estimasi

| Platform | Biaya | Batasan |
|----------|-------|---------|
| Railway | $5/bulan (500 jam gratis) | Gratis untuk hobby projects |
| Render | Gratis | Sleep setelah 15 menit tidak aktif |
| Vercel (Frontend) | Gratis | Unlimited untuk personal projects |
| Vercel (Backend) | Gratis | Serverless functions, cold start |

## Rekomendasi

**Untuk Development/Testing:**
- Railway (paling mudah, gratis, tidak sleep)

**Untuk Production:**
- Railway atau Render dengan custom domain
- Setup monitoring dan logging
- Backup data secara berkala

## Next Steps

1. ✅ Deploy backend ke Railway/Render
2. ✅ Dapatkan URL backend publik
3. ✅ Update `VITE_API_URL` di Vercel environment variables
4. ✅ Update `ALLOWED_ORIGINS` di backend environment variables
5. ✅ Redeploy frontend di Vercel
6. ✅ Test website production

## Kontak

Jika ada masalah deployment, cek:
- Railway/Render logs untuk backend errors
- Vercel logs untuk frontend build errors
- Browser DevTools Network tab untuk API errors
