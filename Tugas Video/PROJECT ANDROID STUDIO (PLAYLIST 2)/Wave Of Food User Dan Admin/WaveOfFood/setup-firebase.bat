@echo off
echo ========================================
echo    WaveOfFood Firebase Setup Script
echo    Enhanced Design Integration
echo ========================================
echo.

echo [1/5] Checking Node.js installation...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Node.js not found! Please install Node.js first:
    echo    https://nodejs.org/
    pause
    exit /b 1
) else (
    echo ✅ Node.js found
)

echo.
echo [2/5] Installing Firebase tools...
call npm install
if %errorlevel% neq 0 (
    echo ❌ Failed to install dependencies
    pause
    exit /b 1
) else (
    echo ✅ Dependencies installed
)

echo.
echo [3/5] Checking Firebase CLI...
call firebase --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Installing Firebase CLI...
    call npm install -g firebase-tools
) else (
    echo ✅ Firebase CLI found
)

echo.
echo [4/5] Checking service account key...
if not exist "serviceAccountKey.json" (
    echo ❌ Service Account Key not found!
    echo.
    echo Please download service account key from Firebase Console:
    echo 1. Go to Firebase Console ^> Project Settings ^> Service Accounts
    echo 2. Click "Generate new private key"
    echo 3. Download the JSON file
    echo 4. Rename it to "serviceAccountKey.json"
    echo 5. Place it in this folder: %cd%
    echo.
    echo Then run this script again.
    pause
    exit /b 1
) else (
    echo ✅ Service Account Key found
)

echo.
echo [5/5] Importing enhanced data to Firebase...
call node firebase-import-enhanced.js
if %errorlevel% neq 0 (
    echo ❌ Data import failed
    echo.
    echo Common issues:
    echo - Service account key invalid
    echo - Firebase project not configured
    echo - Network connection issues
    echo.
    pause
    exit /b 1
) else (
    echo ✅ Data import successful!
)

echo.
echo ========================================
echo    🎉 FIREBASE SETUP COMPLETE! 🎉
echo ========================================
echo.
echo Next steps:
echo 1. ✅ Enhanced data imported to Firebase
echo 2. ✅ Run the Android app
echo 3. ✅ Test categories, foods, and cart functionality
echo.
echo Firebase Console: https://console.firebase.google.com/
echo.
pause
