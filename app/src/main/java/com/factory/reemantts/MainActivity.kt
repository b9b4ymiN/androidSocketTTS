package com.factory.reemantts

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.factory.reemantts.tts.TTSManager
import com.factory.reemantts.utils.Config
import com.factory.reemantts.utils.Logger

/**
 * MainActivity - Entry point for Reeman TTS Application
 *
 * Purpose: Testing UI for TTS functionality during Phase 1
 * Features:
 * - Initialize TTS engine
 * - Test Thai language speech
 * - Display status and logs
 * - Manual text input for testing
 */
class MainActivity : AppCompatActivity() {

    private lateinit var ttsManager: TTSManager

    // UI Components
    private lateinit var tvStatus: TextView
    private lateinit var tvLogs: TextView
    private lateinit var etTestText: EditText
    private lateinit var btnInitTts: Button
    private lateinit var btnTestThai: Button
    private lateinit var btnSpeak: Button
    private lateinit var btnStop: Button

    private val logBuilder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logger.info("MainActivity started", "MainActivity")

        // Validate configuration
        if (!Config.validate()) {
            Logger.error("Configuration validation failed", "MainActivity")
            updateStatus("Configuration Error!")
            return
        }

        initializeViews()
        initializeTTS()
    }

    private fun initializeViews() {
        tvStatus = findViewById(R.id.tvStatus)
        tvLogs = findViewById(R.id.tvLogs)
        etTestText = findViewById(R.id.etTestText)
        btnInitTts = findViewById(R.id.btnInitTts)
        btnTestThai = findViewById(R.id.btnTestThai)
        btnSpeak = findViewById(R.id.btnSpeak)
        btnStop = findViewById(R.id.btnStop)

        // Set default test text
        etTestText.setText("สวัสดีครับ ยินดีต้อนรับสู่ระบบหุ่นยนต์รีมาน")

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        btnInitTts.setOnClickListener {
            addLog("Initializing TTS...")
            initializeTTS()
        }

        btnTestThai.setOnClickListener {
            testThaiSpeech()
        }

        btnSpeak.setOnClickListener {
            val text = etTestText.text.toString()
            if (text.isNotBlank()) {
                speakText(text)
            } else {
                Toast.makeText(this, "Please enter text to speak", Toast.LENGTH_SHORT).show()
            }
        }

        btnStop.setOnClickListener {
            ttsManager.stop()
            addLog("Speech stopped")
            updateStatus("Stopped")
        }
    }

    private fun initializeTTS() {
        updateStatus("Initializing...")
        addLog("Creating TTS Manager...")

        ttsManager = TTSManager(this)

        // Set listeners
        ttsManager.setOnSpeakStartListener { utteranceId ->
            runOnUiThread {
                updateStatus("Speaking...")
                addLog("Speech started: $utteranceId")
            }
        }

        ttsManager.setOnSpeakDoneListener { utteranceId ->
            runOnUiThread {
                updateStatus("Ready")
                addLog("Speech completed: $utteranceId")
            }
        }

        ttsManager.setOnErrorListener { utteranceId, error ->
            runOnUiThread {
                updateStatus("Error: $error")
                addLog("Speech error: $utteranceId - $error")
            }
        }

        // Initialize TTS
        ttsManager.initialize { success ->
            runOnUiThread {
                if (success) {
                    updateStatus("Ready")
                    addLog("✓ TTS initialized successfully")
                    addLog("✓ Thai language configured")
                    addLog("✓ Speech rate: ${Config.SPEECH_RATE}")
                    addLog("✓ Pitch: ${Config.SPEECH_PITCH}")
                    Toast.makeText(this, "TTS Ready!", Toast.LENGTH_SHORT).show()
                } else {
                    updateStatus("Initialization Failed")
                    addLog("✗ TTS initialization failed")
                    Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun testThaiSpeech() {
        if (!ttsManager.isReady()) {
            Toast.makeText(this, "TTS not initialized", Toast.LENGTH_SHORT).show()
            return
        }

        val testPhrases = listOf(
            "สวัสดีครับ",
            "ทดสอบระบบเสียงภาษาไทย",
            "หนึ่ง สอง สาม สี่ ห้า",
            "ระบบพร้อมใช้งาน"
        )

        addLog("Testing Thai speech with ${testPhrases.size} phrases...")

        testPhrases.forEachIndexed { index, phrase ->
            // Use queue mode for sequential playback
            if (index == 0) {
                ttsManager.speak(phrase)
            } else {
                ttsManager.speakQueue(phrase)
            }
            addLog("Queued: \"$phrase\"")
        }
    }

    private fun speakText(text: String) {
        if (!ttsManager.isReady()) {
            Toast.makeText(this, "TTS not initialized", Toast.LENGTH_SHORT).show()
            addLog("✗ Cannot speak: TTS not ready")
            return
        }

        addLog("Speaking: \"$text\"")
        val success = ttsManager.speak(text)

        if (!success) {
            Toast.makeText(this, "Failed to speak", Toast.LENGTH_SHORT).show()
            addLog("✗ Failed to queue speech")
        }
    }

    private fun updateStatus(status: String) {
        tvStatus.text = "Status: $status"
    }

    private fun addLog(message: String) {
        logBuilder.insert(0, "$message\n")
        // Keep only last 20 lines
        val lines = logBuilder.lines()
        if (lines.size > 20) {
            logBuilder.clear()
            logBuilder.append(lines.take(20).joinToString("\n"))
        }
        tvLogs.text = logBuilder.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.info("MainActivity destroyed", "MainActivity")
        ttsManager.shutdown()
    }
}
