@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.*
import com.po4yka.bitesizereader.domain.model.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

/**
 * Maps Auth DTOs to domain models and vice versa
 */

fun UserDto.toDomain(): User {
    return User(
        id = id,
        username = username,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl,
        isOwner = isOwner,
    )
}

fun AuthResponseDto.toDomain(): Pair<AuthTokens, User> {
    val now = Clock.System.now()
    val authTokens =
        AuthTokens(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = tokenType,
            expiresIn = expiresIn,
            expiresAt = now + expiresIn.seconds,
        )
    return authTokens to user.toDomain()
}

fun TokenRefreshResponseDto.toAuthTokens(currentTime: Instant = Clock.System.now()): AuthTokens {
    return AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken ?: "",
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
