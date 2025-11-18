package com.factory.reemantts.utils

/**
 * Configuration object for the Reeman TTS Application
 * Contains all configurable parameters for the system
 */
object Config {
    // Server Configuration
    const val SERVER_URL = "http://192.168.1.100:3000"
    const val DEVICE_ID = "ROBOT_001"
    const val RECONNECT_DELAY = 5000L // milliseconds

    // TTS Configuration
    const val SPEECH_RATE = 0.9f        // 0.5 - 2.0 (normal = 1.0)
    const val SPEECH_PITCH = 1.0f       // 0.5 - 2.0 (normal = 1.0)
    const val TTS_LOCALE = "th-TH"      // Thai language

    // Logging Configuration
    const val LOG_LEVEL = "DEBUG"       // DEBUG, INFO, WARN, ERROR
    const val LOG_TAG = "RemanTTS"      // Log tag for filtering

    // Service Configuration
    const val FOREGROUND_SERVICE_ID = 1001
    const val NOTIFICATION_CHANNEL_ID = "reeman_tts_service"
    const val NOTIFICATION_CHANNEL_NAME = "Reeman TTS Service"

    // Queue Configuration
    const val MAX_QUEUE_SIZE = 50
    const val SPEECH_TIMEOUT_MS = 30000L // 30 seconds

    // Heartbeat Configuration
    const val HEARTBEAT_INTERVAL_MS = 30000L // 30 seconds

    /**
     * Validate configuration values
     * @return true if all values are valid
     */
    fun validate(): Boolean {
        return when {
            SPEECH_RATE !in 0.5f..2.0f -> {
                Logger.error("Invalid SPEECH_RATE: $SPEECH_RATE (must be 0.5-2.0)")
                false
            }
            SPEECH_PITCH !in 0.5f..2.0f -> {
                Logger.error("Invalid SPEECH_PITCH: $SPEECH_PITCH (must be 0.5-2.0)")
                false
            }
            RECONNECT_DELAY < 1000L -> {
                Logger.error("Invalid RECONNECT_DELAY: $RECONNECT_DELAY (must be >= 1000ms)")
                false
            }
            else -> true
        }
    }
}
