# Setup Clerk Authentication - Langkah Eksternal

## Yang Sudah Dikerjakan Secara Otomatis:
✅ Install @clerk/clerk-react dan @clerk/clerk-sdk-node
✅ Setup ClerkProvider di main.jsx
✅ Update Header dengan authentication untuk Student
✅ Buat halaman EducatorAuth khusus untuk educator (/educator/auth)
✅ Buat ProtectedRoute component dengan redirect yang tepat
✅ Update App.jsx dengan route authentication yang terpisah
✅ Update AppContext untuk mengintegrasikan dengan Clerk user
✅ Backup halaman Login.jsx dan Signup.jsx yang lama
✅ Tambahkan CTA "Become an Educator" di halaman Home

## Arsitektur Authentication:

### 🎓 Student Authentication (Header)
- **Login/Register**: Muncul di header pada semua halaman utama
- **Redirect setelah auth**: `/courses`
- **Label**: "Student Login" dan "Student Sign Up"

### 👨‍🏫 Educator Authentication (/educator/auth)
- **Login/Register**: Halaman khusus dengan UI yang berbeda
- **Redirect setelah auth**: `/educator/dashboard`  
- **Label**: "Login as Educator" dan "Sign Up as Educator"
- **Akses**: Melalui path `/educator/auth` atau link "Become an Educator"

## Langkah yang Perlu Anda Lakukan Secara Manual:

### 1. Setup Clerk Dashboard
1. Buka https://clerk.com
2. Login atau buat akun baru
3. Buat aplikasi baru dengan nama "Edemy LMS"
4. Pilih authentication providers yang diinginkan (Email, Google, GitHub, dll)

### 2. Dapatkan API Keys
1. Di Clerk Dashboard, pergi ke "API Keys"
2. Copy "Publishable Key" dan "Secret Key"
3. Update file .env dengan keys yang benar:
   ```
   VITE_CLERK_PUBLISHABLE_KEY=pk_test_your_actual_key_here
   CLERK_SECRET_KEY=sk_test_your_actual_secret_key_here
   ```

### 3. Konfigurasi Domain dan Redirects
1. Di Clerk Dashboard, pergi ke "Domains"
2. Tambahkan domain development: http://localhost:5173
3. Di "Paths", set:
   - Sign-in URL: /
   - Sign-up URL: /
   - User Profile URL: /courses (untuk student) atau /educator/dashboard (untuk educator)
   - After sign-in URL: /courses (default untuk student)
   - After sign-up URL: /courses (default untuk student)

### 4. Customize Authentication UI (Optional)
1. Di Clerk Dashboard, pergi ke "Customization"
2. Upload logo Edemy
3. Set brand colors sesuai theme aplikasi

### 5. Test Authentication
1. Jalankan aplikasi: `npm run dev`
2. Klik tombol "Login" atau "Sign Up" di header
3. Test login/register process
4. Verify user dapat mengakses /educator routes setelah login
5. Test logout functionality

### 6. Backend Integration (Jika Diperlukan)
Jika Anda ingin mengintegrasikan dengan backend untuk menyimpan data user:

1. Install Clerk SDK di backend:
   ```bash
   cd server
   npm install @clerk/clerk-sdk-node
   ```

2. Buat middleware untuk validasi token Clerk
3. Update API endpoints untuk menggunakan Clerk user ID

## Fitur yang Tersedia Sekarang:

### 🎓 Student Authentication (Header)
- **Login**: Button "Student Login" di header → Modal Clerk → Redirect ke /courses
- **Register**: Button "Student Sign Up" di header → Modal Clerk → Redirect ke /courses
- **Logout**: Melalui UserButton dropdown di header
- **Access**: Tersedia di semua halaman utama melalui header

### �‍🏫 Educator Authentication (/educator/auth)
- **Login**: Halaman khusus di /educator/auth → Modal Clerk → Redirect ke /educator/dashboard
- **Register**: Halaman khusus di /educator/auth → Modal Clerk → Redirect ke /educator/dashboard  
- **Access**: Melalui link "Become an Educator" di home page atau langsung ke `/educator/auth`
- **UI**: Halaman dedikasi dengan branding educator dan fitur overview

### �️ Protected Routes
- `/educator/*` routes: Memerlukan authentication, redirect ke `/educator/auth` jika belum login
- Auto-redirect berdasarkan path yang diakses

### 🎨 User Experience
- Header tidak muncul di halaman `/educator/auth` (full-page experience)
- Responsive design untuk semua ukuran layar
- Loading states saat Clerk initializing

## Testing Checklist:
- [ ] Login berfungsi dengan benar
- [ ] Register berfungsi dengan benar
- [ ] Logout berfungsi dengan benar
- [ ] Protected routes tidak dapat diakses tanpa login
- [ ] User info ditampilkan di header setelah login
- [ ] Mobile authentication UI berfungsi
- [ ] Redirect setelah login ke dashboard

## Troubleshooting:
1. Jika authentication tidak berfungsi, periksa API keys di .env
2. Jika ada error CORS, pastikan domain sudah ditambahkan di Clerk Dashboard
3. Jika redirect tidak berfungsi, periksa konfigurasi paths di Clerk
