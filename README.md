# Reeman Robot TTS System

Android application for Reeman delivery robot that receives Thai text messages via WebSocket and converts them to speech output.

## Project Status

**Current Phase:** Phase 1 - Foundation ✅ **Completed**

**Version:** 0.1.0
**Last Updated:** November 18, 2024

---

## Overview

This is a background service application designed for Reeman delivery robots in factory environments. The system receives Thai language messages from a factory system via WebSocket and converts them to speech using Android's TextToSpeech engine.

### Key Features

- 🎤 **Thai TTS Support** - Clear Thai language speech synthesis
- 🔄 **Real-time Communication** - WebSocket-based message delivery (Phase 2)
- ⚡ **24/7 Operation** - Background service that never stops (Phase 3)
- 🌐 **Local Network Only** - No internet required
- 📝 **Comprehensive Logging** - Detailed event tracking
- 🔧 **Configurable** - Easy parameter adjustments

---

## Quick Start

### Prerequisites

- Android device/emulator with API 26+ (Android 8.0+)
- Thai TTS engine installed (Google TTS recommended)
- Android Studio (for development)

### Build & Install

```bash
# Build debug APK
./gradlew assembleDebug

# Install to connected device
./gradlew installDebug

# View logs
adb logcat | grep "RemanTTS"
```

### Testing Phase 1

1. Launch the app
2. Tap "Initialize TTS"
3. Wait for "Ready" status
4. Tap "Test Thai Speech" to hear sample phrases
5. Or enter custom Thai text and tap "Speak"

---

## Project Structure

```
androidSocketTTS/
├── app/                           # Android application
│   ├── src/main/
│   │   ├── java/com/factory/reemantts/
│   │   │   ├── tts/              # TTS engine components
│   │   │   │   └── TTSManager.kt
│   │   │   ├── utils/            # Utilities
│   │   │   │   ├── Config.kt
│   │   │   │   └── Logger.kt
│   │   │   └── MainActivity.kt
│   │   ├── res/                  # Resources (layouts, strings)
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── docs/                          # Documentation
│   └── PHASE1_FOUNDATION.md
├── server/                        # Node.js server (Phase 2)
├── CLAUDE.md                      # Project guide
└── README.md                      # This file
```

---

## Development Phases

### ✅ Phase 1: Foundation (Completed)

**Goal:** Basic TTS functionality working

- [x] Create Android project structure
- [x] Implement basic TTS initialization
- [x] Test Thai language support
- [x] Implement basic logging
- [x] Create testing UI

**Deliverables:**
- `TTSManager.kt` - TTS engine wrapper with Thai locale support
- `Logger.kt` - Structured logging system
- `Config.kt` - Centralized configuration
- `MainActivity.kt` - Testing interface

📄 [View Phase 1 Documentation](docs/PHASE1_FOUNDATION.md)

---

### 🔄 Phase 2: Network Communication (Next)

**Goal:** WebSocket connection working

- [ ] Setup Node.js server with Socket.io
- [ ] Implement WebSocketManager
- [ ] Test connection and message delivery
- [ ] Implement auto-reconnect
- [ ] Add heartbeat mechanism

---

### 📋 Phase 3: Background Service

**Goal:** 24/7 operation

- [ ] Implement MessageListenerService
- [ ] Create foreground notification
- [ ] Test START_STICKY behavior
- [ ] Implement BootReceiver
- [ ] Test battery optimization compatibility

---

### 🔤 Phase 4: Text Processing

**Goal:** Handle all text formats

- [ ] Implement TextPreprocessor
- [ ] Test number conversion (123 → "หนึ่งร้อยยี่สิบสาม")
- [ ] Test currency and units
- [ ] Test mixed Thai-English text
- [ ] Add text validation

---

### 🎯 Phase 5: Queue & Error Handling

**Goal:** Robust operation

- [ ] Implement SpeechQueue
- [ ] Add comprehensive error handling
- [ ] Implement retry mechanisms
- [ ] Add timeout management
- [ ] Test edge cases

---

### ⚡ Phase 6: Optimization & Testing

**Goal:** Production-ready

- [ ] Performance optimization
- [ ] Stress testing (100+ messages)
- [ ] 24-hour endurance test
- [ ] Memory leak testing
- [ ] Final bug fixes

---

### 🚀 Phase 7: Deployment

**Goal:** Deploy to production

- [ ] Deploy to Reeman robot
- [ ] Monitor for 3 days
- [ ] Collect feedback
- [ ] Make adjustments
- [ ] Final documentation

---

## Technology Stack

### Android Application

- **Language:** Kotlin 1.9.0
- **Min SDK:** API 26 (Android 8.0)
- **Target SDK:** API 34
- **TTS Engine:** Android TextToSpeech
- **Locale:** th-TH (Thai)

### Communication (Phase 2+)

- **Protocol:** WebSocket
- **Library:** Socket.io Client 2.1.0
- **Server:** Node.js with Express and Socket.io

### Network

- **Environment:** Local Network Only (192.168.x.x)
- **No Internet Required**

---

## Configuration

All configuration is centralized in `Config.kt`:

```kotlin
// Server Configuration (Phase 2+)
SERVER_URL = "http://192.168.1.100:3000"
DEVICE_ID = "ROBOT_001"

// TTS Configuration
SPEECH_RATE = 0.9f      // 0.5 - 2.0
SPEECH_PITCH = 1.0f     // 0.5 - 2.0
TTS_LOCALE = "th-TH"

// Logging
LOG_LEVEL = "DEBUG"     // DEBUG, INFO, WARN, ERROR
```

---

## Current Features (Phase 1)

### TTSManager

✅ Thai language TTS initialization
✅ Speech synthesis with configurable rate and pitch
✅ Queue modes (FLUSH vs ADD)
✅ Status tracking (IDLE, SPEAKING, ERROR)
✅ Comprehensive error handling
✅ Event callbacks (onStart, onDone, onError)

### Logger

✅ Structured logging with timestamps
✅ Component-based log separation
✅ Multiple log levels (DEBUG, INFO, WARN, ERROR)
✅ Configurable log level filtering

### MainActivity (Testing UI)

✅ TTS initialization control
✅ Auto-test with Thai sample phrases
✅ Manual text input testing
✅ Real-time status display
✅ Activity log monitoring

---

## Testing

### Phase 1 Test Cases

1. **TTS Initialization Test**
   - Initialize TTS engine
   - Verify Thai locale configuration
   - Check initialization callbacks

2. **Thai Speech Test**
   - Test with sample phrases
   - Verify clear pronunciation
   - Check speech events (start, done)

3. **Error Handling Test**
   - Test with empty text
   - Test before initialization
   - Verify error messages

4. **Speech Control Test**
   - Test speak (FLUSH mode)
   - Test speakQueue (ADD mode)
   - Test stop functionality

### Test Results

✅ All Phase 1 tests passing
✅ Thai speech clear and natural
✅ Error handling working correctly
✅ Logging comprehensive and structured

---

## Troubleshooting

### TTS Initialization Fails

**Solution:**
1. Install Google TTS from Play Store
2. Download Thai language data
3. Restart the app

### No Speech Output

**Solution:**
1. Check device volume
2. Verify TTS engine is default
3. Test with device settings (Accessibility > TTS)

### Unclear Speech

**Solution:**
1. Adjust SPEECH_RATE to 0.7-0.8 in Config.kt
2. Test in quiet environment
3. Try different TTS engine

---

## Logging

View logs in real-time:

```bash
# All RemanTTS logs
adb logcat | grep "RemanTTS"

# TTS-specific logs
adb logcat | grep "RemanTTS" | grep "TTS"

# Errors only
adb logcat *:E | grep "RemanTTS"
```

Log format:
```
[2024-11-18 10:30:45] [INFO] [TTS] Speaking: "สวัสดีครับ"
[2024-11-18 10:30:48] [INFO] [TTS] Speech completed
```

---

## Documentation

- **[CLAUDE.md](CLAUDE.md)** - Complete project guide and specifications
- **[PHASE1_FOUNDATION.md](docs/PHASE1_FOUNDATION.md)** - Phase 1 implementation details
- **API.md** - API documentation (Phase 2+)
- **DEPLOYMENT.md** - Deployment guide (Phase 7)

---

## Architecture

```
┌─────────────────────┐
│  Factory System     │  (ระบบโรงงาน)
│  HTTP REST API      │
└──────────┬──────────┘
           │ POST /send-message
           ▼
┌─────────────────────┐
│  Node.js Server     │  (Local Network)
│  - Express.js       │  Phase 2+
│  - Socket.io        │
│  - Port: 3000       │
└──────────┬──────────┘
           │ WebSocket
           ▼
┌─────────────────────┐
│  Reeman Robot       │
│  Android App        │
│  - Background Svc   │  Phase 3+
│  - TTS Engine       │  ✅ Phase 1
│  - Socket.io Client │  Phase 2+
└─────────────────────┘
```

---

## Contributing

This is an internal factory project. Development follows the phased approach outlined in CLAUDE.md.

### Development Guidelines

1. Follow Kotlin coding standards
2. Add comprehensive logging for all operations
3. Test Thai language support thoroughly
4. Document all configuration changes
5. Update phase documentation when complete

---

## Version History

- **v0.1.0** (Current) - Phase 1 completed: Basic TTS and logging
- **v1.0.0** (Planned) - MVP with WebSocket and background service
- **v1.1.0** (Planned) - Text processing and queue management
- **v2.0.0** (Future) - Production-ready with optimizations

---

## License

Internal factory project - Not for public distribution

---

## Support

For issues or questions:
1. Check troubleshooting section above
2. Review CLAUDE.md for detailed specifications
3. Check phase-specific documentation in docs/
4. Review Logcat output for error details

---

**Project Lead:** Thp
**Development Start:** November 2024
**Current Phase:** Phase 1 ✅ Completed
**Next Phase:** Phase 2 - Network Communication

---

## Next Steps

To continue with Phase 2:

1. Setup Node.js server with Socket.io
2. Implement WebSocketManager.kt
3. Test local network communication
4. Integrate with existing TTSManager
5. Add connection status to UI

See CLAUDE.md for detailed Phase 2 requirements.
