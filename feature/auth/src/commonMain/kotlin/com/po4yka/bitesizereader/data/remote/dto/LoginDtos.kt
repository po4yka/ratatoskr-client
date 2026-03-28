package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
