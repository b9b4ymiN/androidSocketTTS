package com.factory.reemantts.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

/**
 * Centralized logging utility for the Reeman TTS Application
 * Provides structured logging with timestamp, level, and component tags
 *
 * Log Format: [TIMESTAMP] [LEVEL] [COMPONENT] Message
 * Example: [2024-11-18 10:30:45] [INFO] [TTS] Speaking: "สวัสดีครับ"
 */
object Logger {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    enum class Level {
        DEBUG, INFO, WARN, ERROR
    }

    private fun shouldLog(level: Level): Boolean {
        return when (Config.LOG_LEVEL) {
            "DEBUG" -> true
            "INFO" -> level != Level.DEBUG
            "WARN" -> level == Level.WARN || level == Level.ERROR
            "ERROR" -> level == Level.ERROR
            else -> true
        }
    }

    private fun formatMessage(level: Level, component: String, message: String): String {
        val timestamp = dateFormat.format(Date())
        return "[$timestamp] [${level.name}] [$component] $message"
    }

    private fun log(level: Level, component: String, message: String, throwable: Throwable? = null) {
        if (!shouldLog(level)) return

        val formattedMessage = formatMessage(level, component, message)

        when (level) {
            Level.DEBUG -> {
                Log.d(Config.LOG_TAG, formattedMessage)
                throwable?.let { Log.d(Config.LOG_TAG, "Exception: ", it) }
            }
            Level.INFO -> {
                Log.i(Config.LOG_TAG, formattedMessage)
                throwable?.let { Log.i(Config.LOG_TAG, "Exception: ", it) }
            }
            Level.WARN -> {
                Log.w(Config.LOG_TAG, formattedMessage)
                throwable?.let { Log.w(Config.LOG_TAG, "Exception: ", it) }
            }
            Level.ERROR -> {
                Log.e(Config.LOG_TAG, formattedMessage)
                throwable?.let { Log.e(Config.LOG_TAG, "Exception: ", it) }
            }
        }
    }

    // Convenience methods for different components

    // General logging
    fun debug(message: String, component: String = "App") {
        log(Level.DEBUG, component, message)
    }

    fun info(message: String, component: String = "App") {
        log(Level.INFO, component, message)
    }

    fun warn(message: String, component: String = "App", throwable: Throwable? = null) {
        log(Level.WARN, component, message, throwable)
    }

    fun error(message: String, component: String = "App", throwable: Throwable? = null) {
        log(Level.ERROR, component, message, throwable)
    }

    // Component-specific logging
    object TTS {
        fun debug(message: String) = log(Level.DEBUG, "TTS", message)
        fun info(message: String) = log(Level.INFO, "TTS", message)
        fun warn(message: String, throwable: Throwable? = null) = log(Level.WARN, "TTS", message, throwable)
        fun error(message: String, throwable: Throwable? = null) = log(Level.ERROR, "TTS", message, throwable)
    }

    object WebSocket {
        fun debug(message: String) = log(Level.DEBUG, "WebSocket", message)
        fun info(message: String) = log(Level.INFO, "WebSocket", message)
        fun warn(message: String, throwable: Throwable? = null) = log(Level.WARN, "WebSocket", message, throwable)
        fun error(message: String, throwable: Throwable? = null) = log(Level.ERROR, "WebSocket", message, throwable)
    }

    object Service {
        fun debug(message: String) = log(Level.DEBUG, "Service", message)
        fun info(message: String) = log(Level.INFO, "Service", message)
        fun warn(message: String, throwable: Throwable? = null) = log(Level.WARN, "Service", message, throwable)
        fun error(message: String, throwable: Throwable? = null) = log(Level.ERROR, "Service", message, throwable)
    }

    object Queue {
        fun debug(message: String) = log(Level.DEBUG, "Queue", message)
        fun info(message: String) = log(Level.INFO, "Queue", message)
        fun warn(message: String, throwable: Throwable? = null) = log(Level.WARN, "Queue", message, throwable)
        fun error(message: String, throwable: Throwable? = null) = log(Level.ERROR, "Queue", message, throwable)
    }
}
