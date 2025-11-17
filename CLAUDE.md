# CLAUDE.md

## Project Overview

**Project Name:** Reeman Robot TTS System  
**Type:** Android Background Service Application  
**Target Platform:** Android (Reeman Delivery Robot)  
**Network Environment:** Local Network Only (No Internet Access)

### Project Goal

พัฒนา Android application ที่ทำงานเป็น background service ตลอดเวลา สำหรับรับข้อความภาษาไทยจากระบบโรงงานผ่าน WebSocket และแปลงเป็นเสียงพูดออกมาทางลำโพงของหุ่นยนต์ Reeman

### Core Requirements

- ทำงาน 24/7 โดยไม่ถูก Android kill process
- รับข้อความภาษาไทยแบบ real-time
- แปลงข้อความเป็นเสียงที่ชัดเจนและเป็นธรรมชาติ
- ทำงานภายใน Local Network เท่านั้น (ไม่มี internet)
- Auto-reconnect เมื่อขาดการเชื่อมต่อ
- Robust error handling และ logging

-----

## Technology Stack

### Android Application

- **Language:** Kotlin (primary)
- **Minimum SDK:** API 26 (Android 8.0)
- **Target SDK:** API 34+
- **TTS Engine:** Android TextToSpeech (built-in)
- **Locale:** th-TH (Thai)

### Communication Layer

- **Protocol:** WebSocket
- **Library:** [Socket.io](http://Socket.io) Client for Android (io.socket:[socket.io](http://socket.io)-client:2.1.0)
- **Server:** Node.js with [Socket.io](http://Socket.io)

### Backend Server (Node.js)

- **Runtime:** Node.js 18+
- **Framework:** Express.js
- **WebSocket Library:** [Socket.io](http://Socket.io)
- **Network:** Local Network (192.168.x.x)

-----

## Architecture Overview

```
┌─────────────────────┐
│  Factory System     │  (ระบบโรงงาน)
│  HTTP REST API      │
└──────────┬──────────┘
           │ POST /send-message
           ▼
┌─────────────────────┐
│  Node.js Server     │  (Local Network)
│  - Express.js       │
│  - Socket.io        │
│  - Port: 3000       │
└──────────┬──────────┘
           │ WebSocket
           ▼
┌─────────────────────┐
│  Reeman Robot       │
│  Android App        │
│  - Background Svc   │
│  - TTS Engine       │
│  - Socket.io Client │
└─────────────────────┘
```

### Communication Flow

1. Factory System → HTTP POST → Node.js Server
1. Node.js Server → WebSocket emit → Android App
1. Android App → Process Text → TTS Engine
1. TTS Engine → Audio Output → Robot Speaker

-----

## Project Structure

```
RemanTTSApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/factory/reemantts/
│   │   │   │   ├── service/
│   │   │   │   │   ├── MessageListenerService.kt    # Main background service
│   │   │   │   │   └── BootReceiver.kt              # Auto-start on boot
│   │   │   │   ├── network/
│   │   │   │   │   └── WebSocketManager.kt          # WebSocket connection
│   │   │   │   ├── tts/
│   │   │   │   │   ├── TTSManager.kt                # TTS engine wrapper
│   │   │   │   │   ├── TextPreprocessor.kt          # Text cleanup & format
│   │   │   │   │   └── SpeechQueue.kt               # Queue management
│   │   │   │   ├── utils/
│   │   │   │   │   ├── Logger.kt                    # Logging system
│   │   │   │   │   ├── AudioManager.kt              # Audio focus handling
│   │   │   │   │   └── Config.kt                    # Configuration
│   │   │   │   ├── model/
│   │   │   │   │   └── Message.kt                   # Data models
│   │   │   │   └── MainActivity.kt                  # Entry point
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   └── build.gradle
├── server/                                          # Node.js Server
│   ├── server.js                                    # Main server file
│   ├── package.json
│   └── .env                                         # Configuration
└── docs/
    ├── API.md                                       # API documentation
    ├── DEPLOYMENT.md                                # Deployment guide
    └── CLAUDE.md                                    # This file
```

-----

## Key Components

### 1. MessageListenerService

**Purpose:** Main background service that runs 24/7  
**Responsibilities:**

- Maintain foreground service with notification
- Initialize and manage TTS engine
- Initialize and manage WebSocket connection
- Receive messages and trigger speech
- Handle service lifecycle (start, stop, restart)

**Key Features:**

- START_STICKY for auto-restart
- Foreground notification to prevent killing
- Resource cleanup on destroy

### 2. WebSocketManager

**Purpose:** Manage WebSocket connection to Node.js server  
**Responsibilities:**

- Connect to server with device ID
- Handle connection events (connect, disconnect, reconnect)
- Listen for “speak” events
- Auto-reconnect with exponential backoff
- Heartbeat mechanism

**Configuration:**

- Server URL: configurable (default: <http://192.168.1.100:3000>)
- Device ID: unique identifier (Android ID or custom)
- Reconnect delay: 5 seconds (adjustable)

### 3. TTSManager

**Purpose:** Wrapper for Android TextToSpeech API  
**Responsibilities:**

- Initialize TTS engine with Thai locale
- Manage speech parameters (rate, pitch, volume)
- Handle speech queue (FLUSH vs ADD)
- Track speech status (idle, speaking, done)
- Provide callbacks (onStart, onDone, onError)

**Parameters:**

- Language: th-TH
- Speech Rate: 0.8 - 1.0 (configurable)
- Pitch: 1.0 (configurable)
- Queue Mode: QUEUE_FLUSH (default)

### 4. TextPreprocessor

**Purpose:** Clean and format text before TTS  
**Responsibilities:**

- Remove special characters
- Convert numbers to Thai words (123 → “หนึ่งร้อยยี่สิบสาม”)
- Handle currency (฿500 → “ห้าร้อยบาท”)
- Handle units (10kg → “สิบกิโลกรัม”)
- Convert common English abbreviations to Thai
- Trim whitespace

**Processing Rules:**

- Numeric conversion: full Thai number system
- Currency symbols: ฿ → “บาท”, $ → “ดอลลาร์”
- Percentage: % → “เปอร์เซ็นต์”
- Common terms: OK → “โอเค”, AI → “เอไอ”

### 5. SpeechQueue

**Purpose:** Manage multiple speech requests  
**Responsibilities:**

- Queue incoming messages
- Handle priority system (if implemented)
- Support FLUSH vs ADD modes
- Track queue status
- Timeout management

**Queue Modes:**

- FLUSH: New message cancels current speech
- ADD: New message waits for current to finish
- PRIORITY: Emergency messages override everything

-----

## Development Phases

### Phase 1: Foundation (Week 1)

**Goal:** Basic TTS functionality working

- [x] Create Android project structure
- [ ] Implement basic TTS initialization
- [ ] Test Thai language support
- [ ] Test audio output on Reeman robot
- [ ] Implement basic logging

### Phase 2: Network Communication (Week 2)

**Goal:** WebSocket connection working

- [ ] Setup Node.js server with [Socket.io](http://Socket.io)
- [ ] Implement WebSocketManager
- [ ] Test connection and message delivery
- [ ] Implement auto-reconnect
- [ ] Add heartbeat mechanism

### Phase 3: Background Service (Week 3)

**Goal:** 24/7 operation

- [ ] Implement MessageListenerService
- [ ] Create foreground notification
- [ ] Test START_STICKY behavior
- [ ] Implement BootReceiver
- [ ] Test battery optimization compatibility

### Phase 4: Text Processing (Week 4)

**Goal:** Handle all text formats

- [ ] Implement TextPreprocessor
- [ ] Test number conversion
- [ ] Test currency and units
- [ ] Test mixed Thai-English text
- [ ] Add text validation

### Phase 5: Queue & Error Handling (Week 5)

**Goal:** Robust operation

- [ ] Implement SpeechQueue
- [ ] Add comprehensive error handling
- [ ] Implement retry mechanisms
- [ ] Add timeout management
- [ ] Test edge cases

### Phase 6: Optimization & Testing (Week 6)

**Goal:** Production-ready

- [ ] Performance optimization
- [ ] Stress testing (100+ messages)
- [ ] 24-hour endurance test
- [ ] Memory leak testing
- [ ] Final bug fixes

### Phase 7: Deployment (Week 7)

**Goal:** Deploy to production

- [ ] Deploy to Reeman robot
- [ ] Monitor for 3 days
- [ ] Collect feedback
- [ ] Make adjustments
- [ ] Documentation

-----

## Configuration

### Environment Variables

**Android App (Config.kt):**

```kotlin
object Config {
    const val SERVER_URL = "http://192.168.1.100:3000"
    const val DEVICE_ID = "ROBOT_001"
    const val RECONNECT_DELAY = 5000L // milliseconds
    const val SPEECH_RATE = 0.9f
    const val SPEECH_PITCH = 1.0f
    const val LOG_LEVEL = "DEBUG" // DEBUG, INFO, ERROR
}
```

**Node.js Server (.env):**

```
PORT=3000
HOST=0.0.0.0
LOG_LEVEL=info
MAX_MESSAGE_SIZE=1000
```

-----

## API Documentation

### WebSocket Events

#### Client → Server

**Event: `register`**

```javascript
socket.emit('register', 'ROBOT_001')
```

Register device with server

**Event: `heartbeat`** (Optional)

```javascript
socket.emit('heartbeat', { timestamp: Date.now() })
```

Keep connection alive

#### Server → Client

**Event: `speak`**

```javascript
socket.emit('speak', {
  text: 'สวัสดีครับ',
  priority: 'normal', // optional: 'emergency', 'high', 'normal', 'low'
  timestamp: 1234567890
})
```

Instruct robot to speak

**Event: `config_update`** (Optional)

```javascript
socket.emit('config_update', {
  speechRate: 0.9,
  volume: 0.8
})
```

Update TTS parameters remotely

### HTTP API (Server)

**POST /send-message**

```bash
curl -X POST http://192.168.1.100:3000/send-message \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": "ROBOT_001",
    "message": "สวัสดีครับ",
    "priority": "normal"
  }'
```

**Response:**

```json
{
  "success": true,
  "timestamp": 1234567890,
  "deviceStatus": "online"
}
```

-----

## Testing Strategy

### Unit Tests

- TextPreprocessor: all conversion rules
- SpeechQueue: queue management logic
- Config: validation logic

### Integration Tests

- WebSocket connection flow
- TTS initialization and speech
- Service lifecycle management

### End-to-End Tests

- Complete flow: Server → WebSocket → TTS → Audio
- Measure latency (target: <500ms)
- Test in actual factory environment

### Stress Tests

- 100 consecutive messages
- Long messages (500+ characters)
- Rapid-fire messages (10/second)
- 24-hour continuous operation
- Connection drop and recovery

-----

## Monitoring & Logging

### Log Levels

- **DEBUG:** All events (development only)
- **INFO:** Important events (connection, speech start/end)
- **WARN:** Recoverable issues (reconnect attempts)
- **ERROR:** Critical failures (TTS init fail, audio errors)

### Key Metrics to Track

- Messages received per hour
- Average speech duration
- TTS error rate
- WebSocket disconnection frequency
- Memory usage
- CPU usage

### Log Format

```
[TIMESTAMP] [LEVEL] [COMPONENT] Message
[2024-11-18 10:30:45] [INFO] [WebSocket] Connected to server
[2024-11-18 10:30:46] [INFO] [TTS] Speaking: "สวัสดีครับ"
[2024-11-18 10:30:48] [INFO] [TTS] Speech completed
```

-----

## Known Limitations & Constraints

1. **Network Environment**

- Local network only (192.168.x.x)
- Cannot use cloud services (Firebase, AWS, etc.)
- Must maintain static IP for server

1. **TTS Engine**

- Quality depends on device’s TTS engine
- May require Google TTS app installation
- Limited voice options

1. **Android System**

- Subject to battery optimization
- Requires notification for foreground service
- May need user permission on first run

1. **Hardware (Reeman Robot)**

- Specific audio routing
- Limited by speaker quality
- Ambient noise in factory environment

-----

## Troubleshooting Guide

### Common Issues

**Issue: TTS not speaking**

- Check TTS engine installed and Thai language supported
- Verify audio output routing
- Check Audio Focus status
- Review TTS initialization logs

**Issue: WebSocket keeps disconnecting**

- Verify server is running
- Check network connectivity
- Review firewall settings
- Check server logs for errors

**Issue: Service keeps getting killed**

- Ensure foreground notification is showing
- Check battery optimization settings
- Verify START_STICKY is set
- Add app to battery whitelist

**Issue: Garbled or unclear Thai speech**

- Adjust speech rate (try 0.7-0.9)
- Check text preprocessing
- Verify UTF-8 encoding
- Test with simple phrases first

-----

## Security Considerations

### Network Security

- Use local network only (isolated from internet)
- Consider adding authentication token for WebSocket
- Validate message source
- Rate limiting on server side

### Data Privacy

- Messages are not stored permanently
- Logs should be rotated and cleaned
- No sensitive data in notifications

### Access Control

- Restrict server API to factory network only
- Use firewall rules to block external access
- Consider HTTPS for production (if needed)

-----

## Future Enhancements

### Planned Features (Phase 2)

- [ ] Multiple voice options (male/female)
- [ ] SSML support for advanced control
- [ ] Background music during speech
- [ ] Message history and replay
- [ ] Web dashboard for monitoring
- [ ] Multi-language support (English)
- [ ] Remote configuration via API
- [ ] Text-to-speech caching for common phrases

### Nice-to-Have Features

- [ ] Voice volume auto-adjustment based on ambient noise
- [ ] Integration with robot’s movement system
- [ ] Visual feedback on robot’s screen
- [ ] Speech analytics and reporting
- [ ] A/B testing for speech parameters

-----

## Developer Notes

### For AI Assistant (Claude Code)

**When helping with this project:**

1. **Always consider:**

- This is a production system running 24/7
- Reliability is more important than features
- Target device is Reeman robot (Android-based)
- Network is local only (no internet)
- Thai language support is critical

1. **Code style preferences:**

- Use Kotlin for Android (not Java)
- Prefer coroutines over callbacks
- Use dependency injection where appropriate
- Comment in Thai for Thai-specific logic
- Comment in English for general logic

1. **Testing requirements:**

- Always suggest relevant test cases
- Consider edge cases (null, empty, very long text)
- Think about error recovery
- Consider real-world factory conditions

1. **When suggesting changes:**

- Explain why the change is needed
- Consider backward compatibility
- Think about deployment impact
- Suggest gradual rollout if risky

1. **Priority order:**

- Stability > Features
- Reliability > Performance
- Maintainability > Cleverness

### Developer Background

- Familiar with: C#, Node.js, React
- Learning: Android/Kotlin development
- Preference: Clean, maintainable code with good documentation

-----

## Quick Start Commands

### Android Development

```bash
# Build debug APK
./gradlew assembleDebug

# Install to connected device
./gradlew installDebug

# Run tests
./gradlew test

# Check logs
adb logcat | grep "RemanTTS"
```

### Node.js Server

```bash
# Install dependencies
npm install

# Run development
npm run dev

# Run production
npm start

# Check logs
tail -f logs/server.log
```

-----

## Contact & Support

**Project Lead:** Thp  
**Development Start:** November 2024  
**Target Deployment:** [To be determined]  
**Factory Location:** [Local network environment]

-----

## Version History

- **v0.1.0** (Current) - Initial project setup and planning
- **v1.0.0** (Planned) - MVP with basic TTS and WebSocket
- **v1.1.0** (Planned) - Enhanced text processing and error handling
- **v2.0.0** (Future) - Advanced features and optimizations

-----

**Last Updated:** November 18, 2024  
**Status:** Planning & Initial Development Phase​​​​​​​​​​​​​​​​
