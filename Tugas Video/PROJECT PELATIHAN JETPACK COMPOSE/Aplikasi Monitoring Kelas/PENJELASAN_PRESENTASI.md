# 📱 APLIKASI MONITORING KELAS SEKOLAH

## 🎯 Tentang Aplikasi

**Aplikasi Monitoring Kelas** adalah sistem informasi berbasis Android (Jetpack Compose) dan Web (Laravel + Filament) yang dirancang untuk memantau dan mengelola kegiatan belajar mengajar di sekolah secara real-time.

### 🌟 Tujuan Utama
- Memantau kehadiran guru di kelas
- Mendeteksi kelas kosong secara real-time
- Mengelola penggantian guru
- Meningkatkan efektivitas KBM (Kegiatan Belajar Mengajar)

---

## 🏗️ Arsitektur Sistem

### **Backend (Server)**
- **Framework**: Laravel 11
- **Admin Panel**: Filament 3.2
- **Database**: MySQL
- **Authentication**: Laravel Sanctum (Token-based)
- **API**: RESTful API

### **Frontend (Mobile)**
- **Framework**: Jetpack Compose
- **Platform**: Android (Kotlin)
- **Architecture**: MVVM + Clean Architecture
- **Network**: Retrofit + OkHttp

---

## 👥 Role & Hak Akses

| Role | Fungsi Utama |
|------|--------------|
| **Admin** | Kelola semua data sistem, user management |
| **Kepala Sekolah** | Lihat laporan lengkap, statistik, kelola jadwal |
| **Kurikulum** | Kelola jadwal, tugaskan guru pengganti |
| **Guru** | Absen kehadiran, tugaskan pengganti, lihat jadwal |
| **Siswa** | Laporkan kelas kosong via mobile app |

---

## 🔥 Fitur-Fitur Utama

### 1. **Autentikasi & Manajemen User**
- ✅ Login/Register dengan validasi role
- ✅ Update profil
- ✅ Ban/Unban user (Admin)
- ✅ Password terenkripsi (bcrypt)

### 2. **Monitoring Kehadiran Guru**
- ✅ Absensi guru per jam pelajaran
- ✅ Status: Hadir, Tidak Hadir, Diganti
- ✅ Statistik kehadiran harian/bulanan
- ✅ Real-time notification ke siswa

### 3. **Deteksi Kelas Kosong**
- ✅ Siswa melaporkan kelas kosong via mobile
- ✅ Sistem auto-detect berdasarkan absensi guru
- ✅ Notifikasi ke Kurikulum & Kepala Sekolah
- ✅ Dashboard statistik kelas kosong

### 4. **Penggantian Guru**
- ✅ Kurikulum/Guru bisa menugaskan pengganti
- ✅ Tracking guru yang menggantikan
- ✅ History penggantian guru
- ✅ Status: Pending, Assigned, Completed

### 5. **Manajemen Jadwal**
- ✅ CRUD jadwal pelajaran
- ✅ Tampil per hari/minggu
- ✅ Filter by kelas/guru/mata pelajaran
- ✅ Auto-validation waktu bentrok

### 6. **Dashboard & Statistik**
- ✅ Grafik kehadiran guru
- ✅ Total kelas kosong per hari
- ✅ Performa guru (tingkat kehadiran)
- ✅ Laporan bulanan

---

## 📂 Struktur File Utama (Backend)

### **📁 Controllers** (`app/Http/Controllers/`)
```
AuthController.php              → Login, Register, User Management
TeacherAttendanceController.php → Absensi Guru
MonitoringController.php        → Monitoring Kelas Kosong
GuruPenggantiController.php     → Penggantian Guru
TeacherReplacementController.php → Assignment Guru Pengganti
ScheduleController.php          → Manajemen Jadwal
```

### **📁 Models** (`app/Models/`)
```
User.php                 → Data user (guru, siswa, admin, dll)
TeacherAttendance.php    → Data absensi guru
Monitoring.php           → Laporan kelas kosong dari siswa
GuruPengganti.php        → Data guru pengganti
Schedule.php             → Jadwal pelajaran
Teacher.php              → Data guru
```

### **📁 Migrations** (`database/migrations/`)
```
create_users_table.php
create_teacher_attendances_table.php
create_monitoring_table.php
create_schedules_table.php
create_guru_pengganti_table.php
add_role_to_users_table.php
add_is_banned_to_users_table.php
```

### **📁 Routes** (`routes/`)
```
api.php    → Semua endpoint API untuk mobile app
web.php    → Routes untuk admin panel Filament
```

### **📁 Config Files**
```
.env                 → Konfigurasi database, app key
config/database.php  → Setup koneksi database
config/auth.php      → Konfigurasi autentikasi
config/filament.php  → Setup Filament admin panel
```

---

## 🔌 API Endpoints Utama

### **Authentication**
```
POST /api/register        → Daftar user baru
POST /api/login           → Login dan dapat token
POST /api/logout          → Logout
GET  /api/user            → Get user profile
```

### **Teacher Attendance**
```
GET  /api/teacher-attendance/today           → Absensi hari ini
POST /api/teacher-attendance                 → Create absensi
GET  /api/teacher-attendance/today-schedules → Jadwal hari ini
GET  /api/teacher-attendance/statistics      → Statistik kehadiran
```

### **Monitoring Kelas Kosong**
```
POST /api/monitoring/store                → Siswa laporkan kelas kosong
GET  /api/monitoring                      → List semua monitoring
GET  /api/monitoring/kelas-kosong         → List kelas kosong aktif
GET  /api/monitoring/my-reports           → Laporan siswa sendiri
```

### **Guru Pengganti**
```
GET  /api/guru-pengganti                  → List guru pengganti
POST /api/guru-pengganti                  → Tugaskan guru pengganti
PUT  /api/guru-pengganti/{id}             → Update penggantian
```

### **Jadwal**
```
GET  /api/jadwal          → List jadwal
POST /api/jadwal          → Tambah jadwal (Admin/Kepsek)
PUT  /api/jadwal/{id}     → Update jadwal
```

---

## 🎨 Admin Panel (Filament)

### **URL Admin**: `http://localhost:8000/admin`

### **Fitur Admin Panel**:
- ✅ Dashboard statistik real-time
- ✅ User Management (CRUD users)
- ✅ Jadwal Management
- ✅ Monitoring Reports
- ✅ Teacher Attendance Logs
- ✅ Export data ke Excel/PDF
- ✅ Dark Mode support

### **Login Admin**:
```
Email    : zupa.admin@sekolah.com
Password : password123
```

---

## 🛠️ Teknologi yang Digunakan

### **Backend Stack**
| Teknologi | Versi | Fungsi |
|-----------|-------|--------|
| PHP | 8.2+ | Programming Language |
| Laravel | 11.x | Backend Framework |
| MySQL | 8.0+ | Database |
| Filament | 3.2 | Admin Panel |
| Sanctum | - | API Authentication |
| Composer | 2.x | Dependency Manager |

### **Frontend Stack (Mobile)**
| Teknologi | Fungsi |
|-----------|--------|
| Kotlin | Programming Language |
| Jetpack Compose | UI Framework |
| Retrofit | HTTP Client |
| Coil | Image Loading |
| DataStore | Local Storage |
| Hilt | Dependency Injection |

---

## 📊 Flow Aplikasi

### **Flow 1: Siswa Melaporkan Kelas Kosong**
```
1. Siswa login ke mobile app
2. Pilih "Laporkan Kelas Kosong"
3. Pilih kelas & jam pelajaran
4. Tambahkan catatan (opsional)
5. Submit laporan
6. Sistem kirim notifikasi ke Kurikulum
7. Kurikulum assign guru pengganti
```

### **Flow 2: Guru Absen**
```
1. Guru login ke mobile app
2. Lihat jadwal hari ini
3. Klik "Absen" pada jadwal
4. Pilih status (Hadir/Tidak Hadir)
5. Sistem update status real-time
6. Jika tidak hadir → notifikasi ke Kurikulum
```

### **Flow 3: Penggantian Guru**
```
1. Kurikulum lihat kelas kosong di dashboard
2. Pilih kelas yang kosong
3. Pilih guru pengganti dari list guru available
4. Assign guru pengganti
5. Guru pengganti dapat notifikasi
6. Guru pengganti konfirmasi & ke kelas
```

---

## 🚀 Cara Menjalankan Aplikasi

### **Backend (Laravel)**

1. **Clone Repository**
   ```bash
   git clone <repository-url>
   cd AplikasiMonitoringKelasBe
   ```

2. **Install Dependencies**
   ```bash
   composer install
   ```

3. **Setup Environment**
   ```bash
   cp .env.example .env
   php artisan key:generate
   ```

4. **Konfigurasi Database** (edit `.env`)
   ```env
   DB_CONNECTION=mysql
   DB_HOST=127.0.0.1
   DB_PORT=3306
   DB_DATABASE=monitoring_kelas
   DB_USERNAME=root
   DB_PASSWORD=
   ```

5. **Migrate Database**
   ```bash
   php artisan migrate
   ```

6. **Buat User Admin**
   ```bash
   php fix_admin_user.php
   ```

7. **Jalankan Server**
   ```bash
   php artisan serve
   ```

8. **Akses Admin Panel**
   ```
   http://localhost:8000/admin
   ```

### **Mobile App (Android)**

1. **Buka Project di Android Studio**
2. **Sync Gradle**
3. **Update Base URL** di `build.gradle` atau Config
4. **Run di Emulator/Device**

---

## 📱 Screenshots Aplikasi Mobile

### **Halaman Siswa**
- ✅ Dashboard dengan jadwal hari ini
- ✅ Form laporan kelas kosong
- ✅ History laporan

### **Halaman Guru**
- ✅ Jadwal mengajar
- ✅ Tombol absen per jadwal
- ✅ History kehadiran
- ✅ Notifikasi penggantian

### **Halaman Kurikulum**
- ✅ Dashboard monitoring
- ✅ List kelas kosong real-time
- ✅ Form assign guru pengganti
- ✅ Statistik kehadiran

---

## 🎯 Keunggulan Aplikasi

1. **Real-time Monitoring** 
   - Deteksi kelas kosong langsung ternotifikasi

2. **Multi-Role System**
   - Setiap role punya hak akses berbeda

3. **User-Friendly Interface**
   - UI modern dengan Jetpack Compose
   - Admin panel intuitif dengan Filament

4. **Secure & Reliable**
   - Token-based authentication
   - Password encryption
   - Input validation

5. **Comprehensive Reporting**
   - Dashboard statistik lengkap
   - Export data untuk analisis

6. **Mobile-First Approach**
   - Akses dari mana saja via smartphone
   - Notifikasi push real-time

---

## 🐛 Troubleshooting

### **Login Gagal?**
```bash
php fix_admin_user.php
```

### **Error Database?**
```bash
php artisan migrate:fresh --seed
```

### **Clear Cache**
```bash
php artisan config:clear
php artisan cache:clear
php artisan route:clear
```

---

## 📞 Informasi Tim

**Project**: Aplikasi Monitoring Kelas  
**Class**: XI  
**Stack**: Laravel + Jetpack Compose  
**Year**: 2025  

---

## 📝 Kesimpulan

Aplikasi Monitoring Kelas adalah solusi digital untuk:
- ✅ Meningkatkan disiplin kehadiran guru
- ✅ Meminimalisir kelas kosong
- ✅ Mempermudah koordinasi penggantian guru
- ✅ Memberikan data akurat untuk evaluasi KBM

**Status**: ✅ Production Ready  
**Testing**: ✅ Functional Testing Completed  
**Documentation**: ✅ Complete

---

## 🙏 Terima Kasih

Terima kasih atas perhatiannya! 

**Demo & Source Code**: [GitHub Repository]

---

*Dibuat dengan ❤️ untuk meningkatkan kualitas pendidikan di Indonesia*
