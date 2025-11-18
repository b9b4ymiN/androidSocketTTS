#!/bin/bash

echo "🤖 Reeman TTS Testing Script"
echo "=============================="
echo ""

# ตรวจสอบว่ามี adb หรือไม่
if ! command -v adb &> /dev/null; then
    echo "❌ Error: adb not found!"
    echo "Please install Android SDK Platform Tools"
    exit 1
fi

# ตรวจสอบอุปกรณ์
echo "📱 Checking connected devices..."
DEVICES=$(adb devices | grep -w "device" | wc -l)

if [ "$DEVICES" -eq 0 ]; then
    echo "❌ No device connected!"
    echo ""
    echo "Please:"
    echo "1. Connect your Android device via USB"
    echo "2. Enable USB Debugging in Developer Options"
    echo "3. Accept USB debugging prompt on device"
    echo ""
    echo "Or start an emulator with: emulator -avd <avd_name>"
    exit 1
fi

echo "✅ Found $DEVICES device(s)"
adb devices -l
echo ""

# Build แอพ
echo "🔨 Building app..."
if ! ./gradlew assembleDebug; then
    echo "❌ Build failed!"
    exit 1
fi
echo "✅ Build successful"
echo ""

# ติดตั้ง
echo "📲 Installing app..."
if ! ./gradlew installDebug; then
    echo "❌ Installation failed!"
    exit 1
fi
echo "✅ App installed"
echo ""

# เปิดแอพ
echo "🚀 Launching app..."
adb shell am start -n com.factory.reemantts/.MainActivity
sleep 2
echo "✅ App launched"
echo ""

echo "📋 Instructions:"
echo "1. On your device, tap 'Initialize TTS'"
echo "2. Wait for 'Ready' status"
echo "3. Tap 'Test Thai Speech' to hear sample phrases"
echo "4. Or enter custom Thai text and tap 'Speak'"
echo ""

# แสดง log
echo "📝 Showing logs (Press Ctrl+C to exit)..."
echo "=========================================="
adb logcat -c
adb logcat | grep --color=auto "RemanTTS"
