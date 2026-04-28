package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.data.remote.dto.AuthResponseDto
import com.po4yka.ratatoskr.data.remote.dto.LoginDataDto
import com.po4yka.ratatoskr.data.remote.dto.SessionInfoDto
import com.po4yka.ratatoskr.data.remote.dto.TelegramLoginRequestDto
import com.po4yka.ratatoskr.data.remote.dto.TokenRefreshRequestDto
import com.po4yka.ratatoskr.data.remote.dto.TokenRefreshResponseDto
import com.po4yka.ratatoskr.data.remote.dto.TokensDto
import com.po4yka.ratatoskr.data.remote.dto.UserDto
import com.po4yka.ratatoskr.data.remote.dto.UserPreferencesDto
import com.po4yka.ratatoskr.domain.model.AuthTokens
import com.po4yka.ratatoskr.domain.model.Session
import com.po4yka.ratatoskr.domain.model.User
import com.po4yka.ratatoskr.domain.model.UserPreferences
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/** Maps Auth DTOs to domain models and vice versa */

fun UserDto.toDomain(): User {
    return User(
        id = id.toString(),
        username = username,
        displayName = displayName,
        photoUrl = photoUrl,
        clientId = clientId,
        isOwner = isOwner,
        createdAt = createdAt,
    )
}

fun TokensDto.toAuthTokens(currentTime: Instant = Clock.System.now()): AuthTokens {
    return AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken ?: "",
        tokenType = tokenType,
        expiresIn = expiresIn,
        expiresAt = currentTime + expiresIn.seconds,
    )
}

fun LoginDataDto.toAuthTokens(currentTime: Instant = Clock.System.now()): AuthTokens {
    return tokens.toAuthTokens(currentTime)
}

fun SessionInfoDto.toDomain(): Session {
    return Session(
        id = id,
        clientId = clientId,
        deviceInfo = deviceInfo,
        ipAddress = ipAddress,
        lastUsedAt = lastUsedAt,
        createdAt = createdAt,
        isCurrent = isCurrent,
    )
}

fun UserPreferencesDto.toDomain(): UserPreferences {
    return UserPreferences(
        langPreference = langPreference ?: "auto",
        notificationSettings = null,
        appSettings = null,
    )
}

fun AuthResponseDto.toDomain(): AuthTokens {
    val now = Clock.System.now()
    return AuthTokens(
        accessToken = tokens.accessToken,
        refreshToken = tokens.refreshToken ?: "",
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
        accessToken = tokens.accessToken,
        refreshToken = tokens.refreshToken ?: refreshToken,
        tokenType = tokens.tokenType,
        expiresIn = tokens.expiresIn,
        expiresAt = currentTime + tokens.expiresIn.seconds,
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
