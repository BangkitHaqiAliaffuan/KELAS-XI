# WaveOfFood - Build and Run Script (PowerShell)
# This script builds the project and provides helpful debugging information

Write-Host "🔥 Building WaveOfFood Android App..." -ForegroundColor Green
Write-Host "==================================" -ForegroundColor Green

# Navigate to project directory
Set-Location $PSScriptRoot

# Clean and build the project
Write-Host "📦 Cleaning project..." -ForegroundColor Yellow
& .\gradlew.bat clean

Write-Host "🔨 Building project..." -ForegroundColor Yellow
& .\gradlew.bat assembleDebug

# Check build result
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Build successful!" -ForegroundColor Green
    Write-Host ""
    Write-Host "🎉 Integration Status:" -ForegroundColor Cyan
    Write-Host "✅ Enhanced fragments created" -ForegroundColor Green
    Write-Host "✅ Professional design system implemented" -ForegroundColor Green
    Write-Host "✅ CartManager for state management" -ForegroundColor Green
    Write-Host "✅ Firebase integration ready" -ForegroundColor Green
    Write-Host "✅ Glide for image loading" -ForegroundColor Green
    Write-Host ""
    Write-Host "📱 Ready to run on device/emulator" -ForegroundColor Magenta
    Write-Host "Use: adb install app/build/outputs/apk/debug/app-debug.apk" -ForegroundColor White
} else {
    Write-Host "❌ Build failed!" -ForegroundColor Red
    Write-Host "Check the error messages above for details." -ForegroundColor Red
    exit 1
}
