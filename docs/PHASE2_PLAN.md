# Phase 2: Network Communication - รายการงานและเป้าหมาย

## 🎯 เป้าหมายหลัก

**สร้างระบบการสื่อสารแบบ real-time ระหว่าง Factory System กับ Android App ผ่าน WebSocket**

- ให้ Android App สามารถเชื่อมต่อกับ Node.js Server ผ่าน Socket.io
- รับข้อความจาก Factory System แบบ real-time
- เชื่อมต่อกับ TTSManager ที่มีอยู่ใน Phase 1
- รองรับการ auto-reconnect เมื่อขาดการเชื่อมต่อ
- มีระบบ heartbeat เพื่อตรวจสอบว่าเชื่อมต่ออยู่

---

## 📋 รายการงานที่ต้องทำ

### 1. Setup Node.js Server with Socket.io

**เป้าหมาย:** สร้าง WebSocket server สำหรับรับข้อความจาก Factory System และส่งต่อให้ Android App

**สิ่งที่ต้องทำ:**

- [ ] สร้างโฟลเดอร์ `server/`
- [ ] Initialize Node.js project (`package.json`)
- [ ] ติดตั้ง dependencies:
  - Express.js (HTTP server)
  - Socket.io (WebSocket library)
  - dotenv (environment variables)
  - cors (CORS handling)
- [ ] สร้าง `server.js` - Main server file
  - รัน HTTP server บน port 3000
  - ติดตั้ง Socket.io middleware
  - Handle CORS สำหรับ local network
- [ ] สร้าง REST API endpoint: `POST /send-message`
  - รับข้อความจาก Factory System
  - Emit ข้อความไปยัง Android client ผ่าน WebSocket
  - Return status (online/offline)
- [ ] Implement WebSocket events:
  - `connection` - Client เชื่อมต่อ
  - `register` - Client ลงทะเบียน device ID
  - `disconnect` - Client ตัดการเชื่อมต่อ
  - `heartbeat` - Ping/Pong mechanism
- [ ] จัดการ device registry (เก็บ list ของ Android clients ที่เชื่อมต่ออยู่)
- [ ] สร้าง `.env` file สำหรับ configuration:
  ```
  PORT=3000
  HOST=0.0.0.0
  LOG_LEVEL=info
  MAX_MESSAGE_SIZE=1000
  ```
- [ ] เพิ่ม logging สำหรับทุก events
- [ ] สร้าง `README.md` สำหรับ server directory

**ไฟล์ที่ต้องสร้าง:**
- `server/server.js`
- `server/package.json`
- `server/.env`
- `server/README.md`

**ทดสอบ:**
- รัน server ด้วย `npm start`
- ทดสอบ API ด้วย curl/Postman
- ทดสอบ WebSocket ด้วย Socket.io client test tool

---

### 2. Implement WebSocketManager (Android)

**เป้าหมาย:** สร้าง component สำหรับจัดการ WebSocket connection ฝั่ง Android

**สิ่งที่ต้องทำ:**

- [ ] สร้าง `WebSocketManager.kt` ใน `app/src/main/java/com/factory/reemantts/network/`
- [ ] Implement connection logic:
  - Connect to server with device ID
  - Handle connection success/failure
  - Emit `register` event พร้อม device ID
- [ ] Implement event listeners:
  - `connect` - เมื่อเชื่อมต่อสำเร็จ
  - `disconnect` - เมื่อตัดการเชื่อมต่อ
  - `connect_error` - เมื่อเชื่อมต่อไม่สำเร็จ
  - `speak` - เมื่อได้รับข้อความให้พูด
  - `config_update` - เมื่อได้รับการอัพเดต config (optional)
- [ ] สร้าง connection state management:
  - DISCONNECTED
  - CONNECTING
  - CONNECTED
  - ERROR
- [ ] Implement callbacks:
  - `onConnected()`
  - `onDisconnected(reason: String)`
  - `onMessageReceived(message: Message)`
  - `onError(error: String)`
- [ ] เพิ่ม logging สำหรับทุก events (ใช้ `Logger.WebSocket.*`)
- [ ] Integration กับ TTSManager:
  - เมื่อได้รับข้อความ → ส่งต่อให้ TTSManager.speak()

**ไฟล์ที่ต้องสร้าง:**
- `app/src/main/java/com/factory/reemantts/network/WebSocketManager.kt`

**Dependencies ที่ต้องเพิ่ม:**
- Socket.io Client (มีอยู่แล้วใน Phase 1)

**ทดสอบ:**
- เชื่อมต่อกับ local server
- ส่งข้อความจาก server → ตรวจสอบว่า Android ได้รับ
- ตรวจสอบ log events

---

### 3. Test Connection and Message Delivery

**เป้าหมาย:** ทดสอบให้มั่นใจว่าข้อความส่งจาก server ถึง Android และเล่นเสียงได้

**สิ่งที่ต้องทำ:**

- [ ] ทดสอบการเชื่อมต่อ:
  - Start server
  - Start Android app
  - ตรวจสอบ connection log ทั้ง 2 ฝั่ง
  - ตรวจสอบว่า device ลงทะเบียนสำเร็จ
- [ ] ทดสอบการส่งข้อความ:
  - ส่งข้อความผ่าน REST API (`POST /send-message`)
  - ตรวจสอบว่า Android ได้รับข้อความ
  - ตรวจสอบว่า TTS พูดข้อความนั้น
- [ ] ทดสอบหลายข้อความติดกัน:
  - ส่ง 5-10 ข้อความ
  - ตรวจสอบว่าทุกข้อความถูกประมวลผล
- [ ] วัด latency:
  - เวลาตั้งแต่ส่งจาก server จนถึงเล่นเสียง
  - เป้าหมาย: < 500ms
- [ ] สร้าง test script สำหรับส่งข้อความ:
  ```bash
  # test-message.sh
  curl -X POST http://192.168.1.100:3000/send-message \
    -H "Content-Type: application/json" \
    -d '{"deviceId": "ROBOT_001", "message": "สวัสดีครับ"}'
  ```

**ไฟล์ที่ต้องสร้าง:**
- `test-message.sh` - Script สำหรับทดสอบการส่งข้อความ
- `docs/PHASE2_TESTING.md` - คู่มือการทดสอบ Phase 2

**ทดสอบ:**
- ส่งข้อความภาษาไทย → ได้ยินเสียง
- ส่งหลายข้อความ → เล่นครบทุกข้อความ
- ตรวจสอบ latency

---

### 4. Implement Auto-Reconnect

**เป้าหมาย:** ให้ Android App reconnect อัตโนมัติเมื่อขาดการเชื่อมต่อ

**สิ่งที่ต้องทำ:**

- [ ] เพิ่ม reconnection logic ใน `WebSocketManager.kt`:
  - ตรวจจับ disconnect event
  - รอ delay ตาม `Config.RECONNECT_DELAY` (5 วินาที)
  - พยายาม connect ใหม่
  - ถ้าไม่สำเร็จ → เพิ่ม delay และลองใหม่
- [ ] Implement exponential backoff (optional):
  - ครั้งที่ 1: รอ 5 วินาที
  - ครั้งที่ 2: รอ 10 วินาที
  - ครั้งที่ 3: รอ 20 วินาที
  - Maximum: 60 วินาที
- [ ] เพิ่ม reconnect counter:
  - Log จำนวนครั้งที่พยายาม reconnect
  - Reset counter เมื่อ connect สำเร็จ
- [ ] เพิ่ม reconnection status callbacks:
  - `onReconnecting(attempt: Int)`
  - `onReconnected()`
  - `onReconnectFailed(attempts: Int)`
- [ ] จัดการ pending messages (optional):
  - เก็บข้อความที่ยังไม่ได้ส่ง
  - ส่งใหม่เมื่อ reconnect สำเร็จ

**Configuration ที่เกี่ยวข้อง:**
```kotlin
// Config.kt (มีอยู่แล้ว)
const val RECONNECT_DELAY = 5000L // milliseconds
const val MAX_RECONNECT_ATTEMPTS = -1 // -1 = infinite
```

**ทดสอบ:**
- หยุด server → ตรวจสอบว่า Android พยายาม reconnect
- เปิด server ใหม่ → ตรวจสอบว่า reconnect สำเร็จ
- ส่งข้อความหลัง reconnect → ได้ยินเสียง

---

### 5. Add Heartbeat Mechanism

**เป้าหมาย:** สร้างระบบ ping/pong เพื่อตรวจสอบว่า connection ยังมีชีวิตอยู่

**สิ่งที่ต้องทำ:**

**ฝั่ง Android:**
- [ ] เพิ่ม heartbeat timer ใน `WebSocketManager.kt`:
  - ใช้ `Timer` หรือ `CoroutineScope`
  - ส่ง `heartbeat` event ทุกๆ 30 วินาที
  - Payload: `{ timestamp: currentTime, deviceId: "ROBOT_001" }`
- [ ] รับ `heartbeat_ack` จาก server
- [ ] ถ้าไม่ได้รับ ack ภายใน timeout → ถือว่า connection ตาย

**ฝั่ง Server:**
- [ ] รับ `heartbeat` event จาก client
- [ ] ส่ง `heartbeat_ack` กลับไป
- [ ] Update last_seen timestamp ของ device
- [ ] (Optional) สร้าง monitoring endpoint:
  - `GET /devices` - แสดง list ของ devices ที่เชื่อมต่ออยู่
  - `GET /device/:id` - แสดงสถานะของ device

**Configuration:**
```kotlin
// Config.kt (มีอยู่แล้ว)
const val HEARTBEAT_INTERVAL_MS = 30000L // 30 seconds
const val HEARTBEAT_TIMEOUT_MS = 10000L  // 10 seconds
```

**ทดสอบ:**
- ตรวจสอบว่า heartbeat ส่งทุกๆ 30 วินาที
- ตรวจสอบว่าได้รับ ack กลับมา
- Block network → ตรวจสอบว่า timeout detection ทำงาน

---

### 6. Update MainActivity for WebSocket Testing

**เป้าหมาย:** เพิ่ม UI controls สำหรับทดสอบ WebSocket

**สิ่งที่ต้องทำ:**

- [ ] เพิ่ม connection status indicator:
  - แสดงสถานะ: Disconnected / Connecting / Connected
  - แสดง server URL และ device ID
- [ ] เพิ่มปุ่ม controls:
  - "Connect to Server" - เชื่อมต่อ manual
  - "Disconnect" - ตัดการเชื่อมต่อ manual
  - "Test Connection" - ส่ง test message
- [ ] แสดงข้อมูล connection:
  - Connected since: เวลาที่เชื่อมต่อ
  - Messages received: จำนวนข้อความที่ได้รับ
  - Last message: ข้อความล่าสุด
  - Reconnect attempts: จำนวนครั้งที่พยายาม reconnect
- [ ] Update Activity Log ให้แสดง WebSocket events:
  - Connected to server
  - Disconnected: <reason>
  - Message received: "<text>"
  - Reconnecting (attempt X)

**อัพเดตไฟล์:**
- `MainActivity.kt` - เพิ่ม WebSocket controls
- `activity_main.xml` - เพิ่ม UI components

---

### 7. Create Message Data Model

**เป้าหมาย:** สร้าง data class สำหรับ message

**สิ่งที่ต้องทำ:**

- [ ] สร้าง `Message.kt` ใน `app/src/main/java/com/factory/reemantts/model/`:
  ```kotlin
  data class Message(
      val text: String,
      val priority: Priority = Priority.NORMAL,
      val timestamp: Long = System.currentTimeMillis(),
      val id: String = UUID.randomUUID().toString()
  )

  enum class Priority {
      LOW, NORMAL, HIGH, EMERGENCY
  }
  ```
- [ ] เพิ่ม JSON parsing:
  - ใช้ `org.json.JSONObject` (built-in Android)
  - หรือเพิ่ม Gson/Moshi library
- [ ] Validation:
  - ตรวจสอบว่า text ไม่ว่าง
  - ตรวจสอบความยาวไม่เกิน MAX_MESSAGE_SIZE

**ไฟล์ที่ต้องสร้าง:**
- `app/src/main/java/com/factory/reemantts/model/Message.kt`

---

## 📊 สรุปไฟล์ที่ต้องสร้าง/แก้ไข

### ไฟล์ใหม่ที่ต้องสร้าง:

**Server (Node.js):**
1. `server/server.js` - Main server file
2. `server/package.json` - Dependencies
3. `server/.env` - Configuration
4. `server/README.md` - Documentation

**Android:**
5. `app/src/main/java/com/factory/reemantts/network/WebSocketManager.kt` - WebSocket client
6. `app/src/main/java/com/factory/reemantts/model/Message.kt` - Data model

**Testing & Documentation:**
7. `test-message.sh` - Script สำหรับทดสอบการส่งข้อความ
8. `docs/PHASE2_NETWORK.md` - Phase 2 documentation
9. `docs/API.md` - API documentation

### ไฟล์ที่ต้องแก้ไข:

1. `MainActivity.kt` - เพิ่ม WebSocket UI controls
2. `activity_main.xml` - เพิ่ม WebSocket UI
3. `README.md` - Update Phase 2 status
4. `CLAUDE.md` - Update Phase 2 checklist

---

## 🏗️ Architecture Overview

```
┌─────────────────────┐
│  Factory System     │
│  (External)         │
└──────────┬──────────┘
           │ HTTP POST
           │ /send-message
           ▼
┌─────────────────────┐
│  Node.js Server     │ ← Phase 2: ต้องสร้างใหม่
│  - Express.js       │
│  - Socket.io        │
│  - Port: 3000       │
│  - Device Registry  │
└──────────┬──────────┘
           │ WebSocket
           │ emit('speak')
           ▼
┌─────────────────────┐
│  Android App        │
│                     │
│  WebSocketManager   │ ← Phase 2: ต้องสร้างใหม่
│         ↓           │
│    TTSManager       │ ← Phase 1: มีอยู่แล้ว
│         ↓           │
│   Android TTS       │
└─────────────────────┘
```

---

## ✅ Definition of Done (Phase 2)

Phase 2 ถือว่าเสร็จสมบูรณ์เมื่อ:

- [ ] Node.js server รันได้และรับ HTTP requests
- [ ] Android app เชื่อมต่อกับ server ผ่าน WebSocket ได้
- [ ] ส่งข้อความจาก REST API → ได้ยินเสียงบน Android
- [ ] Auto-reconnect ทำงานเมื่อ server restart หรือ network หาย
- [ ] Heartbeat ทำงานทุกๆ 30 วินาที
- [ ] Latency จากส่งข้อความถึงเล่นเสียง < 500ms
- [ ] ทดสอบส่ง 50 ข้อความติดกัน → เล่นครบทุกข้อความ
- [ ] Documentation ครบถ้วน
- [ ] Code มี logging ครบทุก events
- [ ] Commit และ push แล้ว

---

## 📝 Testing Checklist

- [ ] Server starts successfully
- [ ] Android connects to server
- [ ] Device registration works
- [ ] Message delivery (Factory → Server → Android → TTS)
- [ ] Multiple messages in sequence
- [ ] Disconnect handling
- [ ] Auto-reconnect after server restart
- [ ] Auto-reconnect after network loss
- [ ] Heartbeat sent every 30 seconds
- [ ] Heartbeat ACK received
- [ ] Latency < 500ms
- [ ] Error handling (server offline, network error, etc.)
- [ ] Logging on both sides

---

## 🎯 Success Criteria

**Functional:**
- ส่งข้อความจาก Factory System → ได้ยินเสียงจาก Robot ภายใน 500ms
- ระบบ reconnect อัตโนมัติเมื่อเกิดปัญหา network
- รองรับการส่งข้อความหลายๆ ข้อความติดกัน

**Technical:**
- Code quality: Clean, readable, well-commented
- Logging: Comprehensive events ทั้ง client และ server
- Error handling: ทุก edge cases
- Documentation: API docs, testing guide

**Performance:**
- Latency < 500ms (target: < 300ms)
- รองรับได้อย่างน้อย 50 messages/minute
- Memory stable (no leaks)

---

## 📚 Resources & References

**Socket.io:**
- Docs: https://socket.io/docs/v4/
- Android Client: https://github.com/socketio/socket.io-client-java

**Node.js:**
- Express.js: https://expressjs.com/
- Best practices: https://github.com/goldbergyoni/nodebestpractices

**Testing Tools:**
- curl (HTTP testing)
- Postman (API testing)
- Socket.io Client Tool (WebSocket testing)

---

## 💡 Tips

1. **Development Workflow:**
   - พัฒนา server ก่อน
   - ทดสอบ server ด้วย Postman/curl
   - พัฒนา Android client
   - ทดสอบ integration

2. **Testing:**
   - ใช้ local network (192.168.x.x)
   - ทดสอบบน WiFi เดียวกัน
   - ตรวจสอบ firewall settings

3. **Debugging:**
   - เปิด logging ทั้ง 2 ฝั่ง
   - ใช้ Wireshark ดู network traffic (ถ้าจำเป็น)
   - ตรวจสอบ server logs และ Android logcat พร้อมกัน

---

**Next Phase:** Phase 3 - Background Service (24/7 Operation)
