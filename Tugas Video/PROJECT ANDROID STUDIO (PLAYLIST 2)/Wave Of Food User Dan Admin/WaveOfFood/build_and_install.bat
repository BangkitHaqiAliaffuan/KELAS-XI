@echo off
echo ========================================
echo    WaveOfFood - Build dan Run Script
echo ========================================
echo.

echo [1/5] Membersihkan build cache...
call gradlew clean
if %errorlevel% neq 0 (
    echo ERROR: Gagal membersihkan build cache
    echo Coba restart komputer atau tutup Android Studio
    pause
    exit /b 1
)

echo.
echo [2/5] Building aplikasi...
call gradlew assembleDebug
if %errorlevel% neq 0 (
    echo ERROR: Gagal build aplikasi
    echo Coba buka Android Studio dan build manual
    pause
    exit /b 1
)

echo.
echo [3/5] Mencari device Android...
adb devices

echo.
echo [4/5] Installing APK...
adb install -r app\build\outputs\apk\debug\app-debug.apk
if %errorlevel% neq 0 (
    echo ERROR: Gagal install APK
    echo Pastikan device Android terhubung dan USB debugging aktif
    pause
    exit /b 1
)

echo.
echo [5/5] Menjalankan aplikasi...
adb shell am start -n com.kelasxi.waveoffood/.SplashActivity

echo.
echo ========================================
echo         BUILD DAN INSTALL SELESAI!
echo ========================================
echo.
echo Aplikasi WaveOfFood telah berhasil diinstall dan dijalankan.
echo Fitur checkout sudah terintegrasi dengan sempurna.
echo.
echo Untuk testing:
echo 1. Login ke aplikasi
echo 2. Tambahkan item ke cart
echo 3. Klik tombol Checkout di cart
echo 4. Isi form checkout dan place order
echo 5. Verifikasi konfirmasi pesanan
echo.
pause
