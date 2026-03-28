package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    @SerialName("tokens") val tokens: TokensDto,
    @SerialName("sessionId") val sessionId: Long? = null,
)

/**
 * Authentication tokens matching backend TokenPair (camelCase aliases).
 */
@Serializable
data class TokensDto(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String? = null,
    @SerialName("tokenType") val tokenType: String,
    @SerialName("expiresIn") val expiresIn: Long,
)
