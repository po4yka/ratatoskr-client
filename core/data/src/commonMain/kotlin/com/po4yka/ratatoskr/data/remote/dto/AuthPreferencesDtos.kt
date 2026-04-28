package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class UserPreferencesDto(
    @SerialName("userId") val userId: Long,
    @SerialName("telegramUsername") val telegramUsername: String? = null,
    @SerialName("langPreference") val langPreference: String? = "auto",
    @SerialName("notificationSettings") val notificationSettings: JsonObject? = null,
    @SerialName("appSettings") val appSettings: JsonObject? = null,
)

@Serializable
data class UpdatePreferencesRequestDto(
    @SerialName("lang_preference") val langPreference: String? = null,
    @SerialName("notification_settings") val notificationSettings: JsonObject? = null,
    @SerialName("app_settings") val appSettings: JsonObject? = null,
)
