package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Login response data matching OpenAPI LoginData schema.
 * Contains tokens, user info, and optionally preferences.
 */
@Serializable
data class LoginDataDto(
    @SerialName("tokens") val tokens: TokensDto,
    @SerialName("user") val user: UserDto,
    @SerialName("preferences") val preferences: UserPreferencesDto? = null,
)

/**
 * User preferences matching OpenAPI UserPreferences schema.
 */
@Serializable
data class UserPreferencesDto(
    @SerialName("user_id") val userId: Long,
    @SerialName("telegram_username") val telegramUsername: String? = null,
    @SerialName("lang_preference") val langPreference: String? = "auto",
    @SerialName("notification_settings") val notificationSettings: JsonObject? = null,
    @SerialName("app_settings") val appSettings: JsonObject? = null,
)

/**
 * Request to update user preferences.
 */
@Serializable
data class UpdatePreferencesRequestDto(
    @SerialName("lang_preference") val langPreference: String? = null,
    @SerialName("notification_settings") val notificationSettings: JsonObject? = null,
    @SerialName("app_settings") val appSettings: JsonObject? = null,
)
