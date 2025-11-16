package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Telegram login request DTO
 */
@Serializable
data class TelegramLoginRequestDto(
    @SerialName("id") val telegramUserId: Long,
    @SerialName("hash") val authHash: String,
    @SerialName("auth_date") val authDate: Long,
    val username: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("client_id") val clientId: String
)

/**
 * Authentication response DTO
 */
@Serializable
data class AuthResponseDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    val user: UserDto
)

/**
 * Token refresh request DTO
 */
@Serializable
data class TokenRefreshRequestDto(
    @SerialName("refresh_token") val refreshToken: String
)

/**
 * Token refresh response DTO
 */
@Serializable
data class TokenRefreshResponseDto(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String? = null
)

/**
 * User DTO
 */
@Serializable
data class UserDto(
    val id: Long,
    val username: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("is_owner") val isOwner: Boolean = false
)
