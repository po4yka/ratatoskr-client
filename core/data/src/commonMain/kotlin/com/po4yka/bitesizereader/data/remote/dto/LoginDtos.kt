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
    @SerialName("sessionId") val sessionId: Long? = null,
)

/**
 * User preferences matching backend PreferencesData (camelCase aliases).
 */
@Serializable
data class UserPreferencesDto(
    @SerialName("userId") val userId: Long,
    @SerialName("telegramUsername") val telegramUsername: String? = null,
    @SerialName("langPreference") val langPreference: String? = "auto",
    @SerialName("notificationSettings") val notificationSettings: JsonObject? = null,
    @SerialName("appSettings") val appSettings: JsonObject? = null,
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
