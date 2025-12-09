package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.*
import com.po4yka.bitesizereader.domain.model.*
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.Duration.Companion.seconds

/**
 * Maps Auth DTOs to domain models and vice versa
 */

fun UserDto.toDomain(): User {
    return User(
        id = userId.toString(),
        username = username,
        firstName = null,
        lastName = null,
        photoUrl = null,
    )
}

fun AuthResponseDto.toDomain(): AuthTokens {
    val now = Clock.System.now()
    return AuthTokens(
        accessToken = tokens.accessToken,
        refreshToken = tokens.refreshToken,
        tokenType = tokens.tokenType,
        expiresIn = tokens.expiresIn,
        expiresAt = now + tokens.expiresIn.seconds,
    )
}

fun TokenRefreshResponseDto.toAuthTokens(
    currentTime: Instant = Clock.System.now(),
    refreshToken: String,
): AuthTokens {
    return AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType,
        expiresIn = expiresIn,
        expiresAt = currentTime + expiresIn.seconds,
    )
}

// Domain to DTO conversions
fun createTelegramLoginRequest(
    telegramUserId: Long,
    authHash: String,
    authDate: Long,
    username: String?,
    firstName: String?,
    lastName: String?,
    photoUrl: String?,
    clientId: String,
): TelegramLoginRequestDto {
    return TelegramLoginRequestDto(
        telegramUserId = telegramUserId,
        authHash = authHash,
        authDate = authDate,
        username = username,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl,
        clientId = clientId,
    )
}

fun String.toTokenRefreshRequest(): TokenRefreshRequestDto {
    return TokenRefreshRequestDto(refreshToken = this)
}
