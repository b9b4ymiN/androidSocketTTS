package com.factory.reemantts.tts

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.factory.reemantts.utils.Config
import com.factory.reemantts.utils.Logger
import java.util.*

/**
 * TTSManager - Wrapper for Android TextToSpeech API
 *
 * Responsibilities:
 * - Initialize TTS engine with Thai locale
 * - Manage speech parameters (rate, pitch, volume)
 * - Handle speech queue (FLUSH vs ADD)
 * - Track speech status (idle, speaking, done)
 * - Provide callbacks (onStart, onDone, onError)
 *
 * @param context Application or Service context
 */
class TTSManager(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isSpeaking = false

    // Callbacks
    private var onInitListener: ((Boolean) -> Unit)? = null
    private var onSpeakStartListener: ((String) -> Unit)? = null
    private var onSpeakDoneListener: ((String) -> Unit)? = null
    private var onErrorListener: ((String, String) -> Unit)? = null

    enum class SpeechStatus {
        IDLE,
        INITIALIZING,
        SPEAKING,
        ERROR
    }

    private var currentStatus = SpeechStatus.IDLE

    /**
     * Initialize TTS engine
     * @param onComplete Callback with initialization result (success/failure)
     */
    fun initialize(onComplete: (Boolean) -> Unit) {
        Logger.TTS.info("Initializing TTS engine...")
        currentStatus = SpeechStatus.INITIALIZING
        onInitListener = onComplete

        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                handleInitSuccess()
            } else {
                handleInitFailure(status)
            }
        }
    }

    private fun handleInitSuccess() {
        Logger.TTS.info("TTS engine initialized successfully")

        // Set Thai locale
        val thaiLocale = Locale("th", "TH")
        val result = tts?.setLanguage(thaiLocale)

        when (result) {
            TextToSpeech.LANG_MISSING_DATA -> {
                Logger.TTS.error("Thai language data is missing")
                currentStatus = SpeechStatus.ERROR
                isInitialized = false
                onInitListener?.invoke(false)
            }
            TextToSpeech.LANG_NOT_SUPPORTED -> {
                Logger.TTS.error("Thai language is not supported")
                currentStatus = SpeechStatus.ERROR
                isInitialized = false
                onInitListener?.invoke(false)
            }
            else -> {
                // Configure speech parameters
                tts?.setSpeechRate(Config.SPEECH_RATE)
                tts?.setPitch(Config.SPEECH_PITCH)

                // Set utterance progress listener
                setupProgressListener()

                isInitialized = true
                currentStatus = SpeechStatus.IDLE
                Logger.TTS.info("Thai language configured successfully")
                Logger.TTS.debug("Speech Rate: ${Config.SPEECH_RATE}, Pitch: ${Config.SPEECH_PITCH}")
                onInitListener?.invoke(true)
            }
        }
    }

    private fun handleInitFailure(status: Int) {
        Logger.TTS.error("TTS initialization failed with status: $status")
        currentStatus = SpeechStatus.ERROR
        isInitialized = false
        onInitListener?.invoke(false)
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                utteranceId?.let {
                    Logger.TTS.info("Speech started: $it")
                    isSpeaking = true
                    currentStatus = SpeechStatus.SPEAKING
                    onSpeakStartListener?.invoke(it)
                }
            }

            override fun onDone(utteranceId: String?) {
                utteranceId?.let {
                    Logger.TTS.info("Speech completed: $it")
                    isSpeaking = false
                    currentStatus = SpeechStatus.IDLE
                    onSpeakDoneListener?.invoke(it)
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                utteranceId?.let {
                    Logger.TTS.error("Speech error: $it")
                    isSpeaking = false
                    currentStatus = SpeechStatus.ERROR
                    onErrorListener?.invoke(it, "Unknown error")
                }
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                utteranceId?.let {
                    val errorMessage = getErrorMessage(errorCode)
                    Logger.TTS.error("Speech error: $it - $errorMessage (code: $errorCode)")
                    isSpeaking = false
                    currentStatus = SpeechStatus.ERROR
                    onErrorListener?.invoke(it, errorMessage)
                }
            }
        })
    }

    private fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            TextToSpeech.ERROR_SYNTHESIS -> "Error during speech synthesis"
            TextToSpeech.ERROR_SERVICE -> "TTS service error"
            TextToSpeech.ERROR_OUTPUT -> "Audio output error"
            TextToSpeech.ERROR_NETWORK -> "Network error"
            TextToSpeech.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            TextToSpeech.ERROR_INVALID_REQUEST -> "Invalid request"
            TextToSpeech.ERROR_NOT_INSTALLED_YET -> "TTS engine not installed"
            else -> "Unknown error (code: $errorCode)"
        }
    }

    /**
     * Speak text with QUEUE_FLUSH mode (cancels current speech)
     * @param text Text to speak
     * @param utteranceId Unique ID for this utterance
     * @return true if speaking started successfully
     */
    fun speak(text: String, utteranceId: String = UUID.randomUUID().toString()): Boolean {
        if (!isInitialized) {
            Logger.TTS.error("Cannot speak: TTS not initialized")
            return false
        }

        if (text.isBlank()) {
            Logger.TTS.warn("Cannot speak: Empty text")
            return false
        }

        Logger.TTS.info("Speaking: \"$text\" (ID: $utteranceId)")

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)

        return if (result == TextToSpeech.SUCCESS) {
            Logger.TTS.debug("Speech queued successfully")
            true
        } else {
            Logger.TTS.error("Failed to queue speech")
            false
        }
    }

    /**
     * Speak text with QUEUE_ADD mode (waits for current speech to finish)
     * @param text Text to speak
     * @param utteranceId Unique ID for this utterance
     * @return true if speaking started successfully
     */
    fun speakQueue(text: String, utteranceId: String = UUID.randomUUID().toString()): Boolean {
        if (!isInitialized) {
            Logger.TTS.error("Cannot speak: TTS not initialized")
            return false
        }

        if (text.isBlank()) {
            Logger.TTS.warn("Cannot speak: Empty text")
            return false
        }

        Logger.TTS.info("Queueing speech: \"$text\" (ID: $utteranceId)")

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }

        val result = tts?.speak(text, TextToSpeech.QUEUE_ADD, params, utteranceId)

        return if (result == TextToSpeech.SUCCESS) {
            Logger.TTS.debug("Speech queued successfully")
            true
        } else {
            Logger.TTS.error("Failed to queue speech")
            false
        }
    }

    /**
     * Stop current speech
     */
    fun stop() {
        Logger.TTS.info("Stopping speech")
        tts?.stop()
        isSpeaking = false
        currentStatus = SpeechStatus.IDLE
    }

    /**
     * Update speech rate
     * @param rate Speech rate (0.5 - 2.0, normal = 1.0)
     */
    fun setSpeechRate(rate: Float) {
        if (rate in 0.5f..2.0f) {
            tts?.setSpeechRate(rate)
            Logger.TTS.debug("Speech rate updated to: $rate")
        } else {
            Logger.TTS.warn("Invalid speech rate: $rate (must be 0.5-2.0)")
        }
    }

    /**
     * Update pitch
     * @param pitch Pitch (0.5 - 2.0, normal = 1.0)
     */
    fun setPitch(pitch: Float) {
        if (pitch in 0.5f..2.0f) {
            tts?.setPitch(pitch)
            Logger.TTS.debug("Pitch updated to: $pitch")
        } else {
            Logger.TTS.warn("Invalid pitch: $pitch (must be 0.5-2.0)")
        }
    }

    /**
     * Check if TTS is currently speaking
     */
    fun isSpeaking(): Boolean = isSpeaking

    /**
     * Check if TTS is initialized
     */
    fun isReady(): Boolean = isInitialized

    /**
     * Get current status
     */
    fun getStatus(): SpeechStatus = currentStatus

    /**
     * Set callback for speech start
     */
    fun setOnSpeakStartListener(listener: (String) -> Unit) {
        onSpeakStartListener = listener
    }

    /**
     * Set callback for speech completion
     */
    fun setOnSpeakDoneListener(listener: (String) -> Unit) {
        onSpeakDoneListener = listener
    }

    /**
     * Set callback for speech error
     */
    fun setOnErrorListener(listener: (String, String) -> Unit) {
        onErrorListener = listener
    }

    /**
     * Cleanup resources
     * MUST be called when done using TTS
     */
    fun shutdown() {
        Logger.TTS.info("Shutting down TTS engine")
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        isSpeaking = false
        currentStatus = SpeechStatus.IDLE
    }
}
