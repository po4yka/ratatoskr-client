package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    @SerialName("tokens") val tokens: TokensDto,
    @SerialName("session_id") val sessionId: Long? = null,
)

/**
 * Authentication tokens matching OpenAPI AuthTokens schema.
 */
@Serializable
data class TokensDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("token_type") val tokenType: String,
    /** Seconds until access token expiry */
    @SerialName("expires_in") val expiresIn: Long,
    /** Seconds until refresh token expiry (if provided) */
    @SerialName("refresh_expires_in") val refreshExpiresIn: Long? = null,
)
