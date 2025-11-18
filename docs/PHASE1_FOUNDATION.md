# Phase 1: Foundation - Implementation Documentation

**Phase:** 1 of 7
**Status:** ✅ Completed
**Date:** November 18, 2024
**Goal:** Basic TTS functionality working

---

## Overview

Phase 1 establishes the foundation for the Reeman Robot TTS System by implementing core TTS functionality with Thai language support, comprehensive logging, and a testing interface.

---

## Objectives

- [x] Create Android project structure
- [x] Implement basic TTS initialization
- [x] Test Thai language support
- [x] Implement basic logging
- [x] Create testing UI

---

## Components Implemented

### 1. Project Structure

```
androidSocketTTS/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/factory/reemantts/
│   │   │   │   ├── tts/
│   │   │   │   │   └── TTSManager.kt
│   │   │   │   ├── utils/
│   │   │   │   │   ├── Config.kt
│   │   │   │   │   └── Logger.kt
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml
│   │   │   │   └── values/
│   │   │   │       ├── strings.xml
│   │   │   │       ├── colors.xml
│   │   │   │       └── themes.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
├── gradle.properties
└── docs/
    └── PHASE1_FOUNDATION.md (this file)
```

### 2. Config.kt - Configuration Management

**Location:** `app/src/main/java/com/factory/reemantts/utils/Config.kt`

**Purpose:** Centralized configuration for all system parameters

**Key Features:**
- Server URL and device ID configuration
- TTS parameters (rate, pitch, locale)
- Logging configuration
- Service and queue settings
- Configuration validation

**Key Settings:**
```kotlin
SERVER_URL = "http://192.168.1.100:3000"
DEVICE_ID = "ROBOT_001"
SPEECH_RATE = 0.9f
SPEECH_PITCH = 1.0f
TTS_LOCALE = "th-TH"
LOG_LEVEL = "DEBUG"
```

### 3. Logger.kt - Logging System

**Location:** `app/src/main/java/com/factory/reemantts/utils/Logger.kt`

**Purpose:** Structured logging with timestamps and component tags

**Log Format:**
```
[TIMESTAMP] [LEVEL] [COMPONENT] Message
[2024-11-18 10:30:45] [INFO] [TTS] Speaking: "สวัสดีครับ"
```

**Log Levels:**
- DEBUG: Development details
- INFO: Important events
- WARN: Recoverable issues
- ERROR: Critical failures

**Component-Specific Loggers:**
- `Logger.TTS.*` - TTS engine events
- `Logger.WebSocket.*` - Network events (for Phase 2)
- `Logger.Service.*` - Service lifecycle (for Phase 3)
- `Logger.Queue.*` - Queue management (for Phase 5)

### 4. TTSManager.kt - TTS Engine Wrapper

**Location:** `app/src/main/java/com/factory/reemantts/tts/TTSManager.kt`

**Purpose:** Manage Android TextToSpeech API with Thai language support

**Key Features:**

1. **Initialization:**
   - Async TTS engine initialization
   - Thai locale (th-TH) configuration
   - Speech parameter setup
   - Callback-based initialization result

2. **Speech Modes:**
   - `speak()` - QUEUE_FLUSH mode (cancels current speech)
   - `speakQueue()` - QUEUE_ADD mode (waits for current speech)
   - `stop()` - Stop current speech

3. **Status Tracking:**
   - IDLE - Ready to speak
   - INITIALIZING - Setting up engine
   - SPEAKING - Currently speaking
   - ERROR - Encountered error

4. **Callbacks:**
   - `onInitListener` - Initialization complete
   - `onSpeakStartListener` - Speech started
   - `onSpeakDoneListener` - Speech completed
   - `onErrorListener` - Speech error occurred

5. **Dynamic Configuration:**
   - `setSpeechRate()` - Adjust speech rate (0.5 - 2.0)
   - `setPitch()` - Adjust pitch (0.5 - 2.0)

6. **Error Handling:**
   - Language data missing detection
   - Language not supported detection
   - Synthesis errors with detailed messages
   - Service errors
   - Output errors

### 5. MainActivity.kt - Testing Interface

**Location:** `app/src/main/java/com/factory/reemantts/MainActivity.kt`

**Purpose:** UI for testing TTS functionality

**Features:**

1. **Status Display:**
   - Real-time status updates
   - Color-coded status indicators
   - Activity log display

2. **TTS Controls:**
   - Initialize TTS button
   - Auto-test with Thai phrases
   - Custom text input
   - Speak/Stop controls

3. **Testing Functions:**
   - Automatic Thai language test with 4 sample phrases
   - Manual text input testing
   - Status and log monitoring
   - Configuration display

4. **Test Phrases:**
   - "สวัสดีครับ" (Hello)
   - "ทดสอบระบบเสียงภาษาไทย" (Testing Thai speech system)
   - "หนึ่ง สอง สาม สี่ ห้า" (One two three four five)
   - "ระบบพร้อมใช้งาน" (System ready)

---

## Technical Specifications

### Android Configuration

- **Minimum SDK:** API 26 (Android 8.0)
- **Target SDK:** API 34
- **Kotlin Version:** 1.9.0
- **Gradle Plugin:** 8.1.0

### Dependencies

```gradle
// Core
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.10.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

// Socket.io (for Phase 2)
implementation 'io.socket:socket.io-client:2.1.0'
```

### Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

---

## Testing Instructions

### Prerequisites

1. Android device/emulator with API 26+
2. Thai TTS engine installed (Google TTS recommended)
3. Thai language data downloaded

### Building the App

```bash
# Build debug APK
./gradlew assembleDebug

# Install to device
./gradlew installDebug

# View logs
adb logcat | grep "RemanTTS"
```

### Testing Procedure

1. **Launch App:**
   - Open Reeman TTS app
   - Verify "Not Initialized" status

2. **Initialize TTS:**
   - Tap "Initialize TTS" button
   - Wait for initialization
   - Verify "Ready" status
   - Check logs for success messages

3. **Auto Test:**
   - Tap "Test Thai Speech (Auto)"
   - Listen for 4 sequential phrases
   - Verify clear Thai pronunciation
   - Check logs for speech events

4. **Manual Test:**
   - Enter custom Thai text
   - Tap "Speak" button
   - Verify speech output
   - Test "Stop" button during speech

5. **Error Testing:**
   - Test with empty text
   - Test before initialization
   - Verify error messages

---

## Key Achievements

### ✅ Completed

1. **Project Structure:** Full Android project setup with proper package organization
2. **TTS Engine:** Working Thai language speech synthesis
3. **Configuration:** Centralized, validated configuration system
4. **Logging:** Comprehensive structured logging with component separation
5. **Testing UI:** Functional interface for TTS testing
6. **Error Handling:** Robust error detection and reporting
7. **Documentation:** Clear code comments and documentation

### 🎯 Quality Metrics

- **Code Coverage:** Core components fully implemented
- **Error Handling:** All TTS errors handled gracefully
- **Logging:** DEBUG level logging for all operations
- **Configuration:** All parameters validated
- **Documentation:** Comprehensive inline comments

---

## Known Issues & Limitations

### Current Limitations

1. **TTS Engine Dependency:**
   - Requires device to have Thai TTS engine installed
   - Quality depends on device's TTS engine
   - No offline fallback if engine missing

2. **Testing Only:**
   - No background service yet (Phase 3)
   - No network communication yet (Phase 2)
   - No text preprocessing yet (Phase 4)

3. **UI Limitations:**
   - Basic testing interface only
   - No configuration editing in UI
   - Limited log display (20 lines)

### Workarounds

1. **Missing TTS Engine:**
   - Install Google TTS from Play Store
   - Download Thai language data
   - Restart app after installation

2. **Unclear Speech:**
   - Adjust SPEECH_RATE in Config.kt (try 0.7-0.9)
   - Check ambient noise levels
   - Test with simple phrases first

---

## Next Phase Preview

### Phase 2: Network Communication (Week 2)

**Objectives:**
- Setup Node.js server with Socket.io
- Implement WebSocketManager
- Test connection and message delivery
- Implement auto-reconnect
- Add heartbeat mechanism

**New Components:**
- `WebSocketManager.kt` - Socket.io client
- `server/server.js` - Node.js server
- Network error handling
- Connection state management

**Integration:**
- Connect TTSManager with WebSocket messages
- Implement message queue
- Add network status indicators

---

## Code Examples

### Using TTSManager

```kotlin
// Initialize
val ttsManager = TTSManager(context)
ttsManager.initialize { success ->
    if (success) {
        // Ready to speak
        ttsManager.speak("สวัสดีครับ")
    }
}

// Set callbacks
ttsManager.setOnSpeakStartListener { utteranceId ->
    Log.d("TTS", "Started: $utteranceId")
}

ttsManager.setOnSpeakDoneListener { utteranceId ->
    Log.d("TTS", "Completed: $utteranceId")
}

// Cleanup
override fun onDestroy() {
    ttsManager.shutdown()
}
```

### Using Logger

```kotlin
// General logging
Logger.info("Application started")
Logger.debug("Debug information")
Logger.error("Error occurred", throwable = exception)

// Component-specific
Logger.TTS.info("TTS initialized")
Logger.WebSocket.debug("Connected to server")
Logger.Service.warn("Service restarting")
```

---

## Performance Considerations

### TTS Initialization

- **Time:** ~500ms - 2 seconds
- **Async:** Non-blocking initialization
- **Retry:** Manual retry via UI button

### Memory Usage

- **Base:** ~50MB (Android app baseline)
- **TTS Engine:** +20-30MB when active
- **Logs:** Minimal (using Android Logcat)

### Battery Impact

- **Phase 1:** Minimal (foreground app only)
- **Future:** Optimizations needed for 24/7 service

---

## Troubleshooting Guide

### Problem: TTS initialization fails

**Symptoms:** "Initialization Failed" status

**Solutions:**
1. Check if Thai TTS engine is installed
2. Verify Thai language data is downloaded
3. Check Logcat for specific error codes
4. Try restarting the app

### Problem: No speech output

**Symptoms:** Status shows "Speaking" but no sound

**Solutions:**
1. Check device volume settings
2. Test with device's built-in TTS (Settings > Accessibility > TTS)
3. Verify audio output routing
4. Check if TTS engine is the default engine

### Problem: Garbled or unclear speech

**Symptoms:** Speech output is not clear

**Solutions:**
1. Reduce SPEECH_RATE to 0.7-0.8
2. Test in quiet environment
3. Verify Thai language data is complete
4. Try different Thai TTS engine

---

## Resources

### Documentation

- Android TTS API: https://developer.android.com/reference/android/speech/tts/TextToSpeech
- Kotlin Coroutines: https://kotlinlang.org/docs/coroutines-overview.html
- Material Design: https://material.io/develop/android

### Tools

- Android Studio: Latest stable version
- ADB (Android Debug Bridge): For testing and logs
- Logcat: For real-time log monitoring

---

## Conclusion

Phase 1 successfully establishes the foundation for the Reeman TTS system with:
- ✅ Working Thai TTS engine integration
- ✅ Comprehensive logging system
- ✅ Centralized configuration management
- ✅ Testing interface for development

The system is now ready for Phase 2: Network Communication integration.

---

**Document Version:** 1.0
**Last Updated:** November 18, 2024
**Next Review:** Start of Phase 2
