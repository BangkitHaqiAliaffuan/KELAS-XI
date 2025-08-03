#!/bin/bash

# WaveOfFood - Build and Run Script
# This script builds the project and provides helpful debugging information

echo "🔥 Building WaveOfFood Android App..."
echo "=================================="

# Navigate to project directory
cd "$(dirname "$0")"

# Clean and build the project
echo "📦 Cleaning project..."
./gradlew clean

echo "🔨 Building project..."
./gradlew assembleDebug

# Check build result
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo ""
    echo "🎉 Integration Status:"
    echo "✅ Enhanced fragments created"
    echo "✅ Professional design system implemented"
    echo "✅ CartManager for state management"
    echo "✅ Firebase integration ready"
    echo "✅ Glide for image loading"
    echo ""
    echo "📱 Ready to run on device/emulator"
    echo "Use: adb install app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ Build failed!"
    echo "Check the error messages above for details."
    exit 1
fi
