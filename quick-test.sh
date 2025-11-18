#!/bin/bash

# Quick test script - Build, Install, and Run

echo "⚡ Quick Test - Reeman TTS"
echo "=========================="

# Check device
if ! adb devices | grep -w "device" &> /dev/null; then
    echo "❌ No device connected!"
    echo "Connect Android device or start emulator first"
    exit 1
fi

# Build and install in one go
echo "Building and installing..."
./gradlew installDebug && \
adb shell am start -n com.factory.reemantts/.MainActivity && \
echo "" && \
echo "✅ App is running on your device!" && \
echo "" && \
echo "Next steps:" && \
echo "1. Tap 'Initialize TTS' button" && \
echo "2. Tap 'Test Thai Speech' to hear: สวัสดีครับ" && \
echo "" && \
echo "View logs with: adb logcat | grep RemanTTS"
