package com.po4yka.ratatoskr.domain.model

/**
 * User preferences for app settings and notifications.
 */
data class UserPreferences(
    /** Language preference: "auto", "en", or "ru" */
    val langPreference: String = "auto",
    /** Notification settings as key-value pairs */
    val notificationSettings: Map<String, Any?>? = null,
    /** App-specific settings as key-value pairs */
    val appSettings: Map<String, Any?>? = null,
)
