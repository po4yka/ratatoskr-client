package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response DTO for token refresh endpoint.
 * Server returns: {"success":true,"data":{"tokens":{...},"session_id":...}}
 */
@Serializable
data class TokenRefreshResponseDto(
    @SerialName("tokens") val tokens: TokensDto,
    @SerialName("sessionId") val sessionId: Long? = null,
)
