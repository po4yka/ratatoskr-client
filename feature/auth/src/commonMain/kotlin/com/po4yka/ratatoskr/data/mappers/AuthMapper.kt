package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.AuthTokens as GeneratedAuthTokens
import com.po4yka.ratatoskr.api.generated.models.LoginData
import com.po4yka.ratatoskr.api.generated.models.SessionInfo
import com.po4yka.ratatoskr.api.generated.models.TelegramLoginRequest
import com.po4yka.ratatoskr.api.generated.models.User as GeneratedUser
import com.po4yka.ratatoskr.domain.model.AuthTokens
import com.po4yka.ratatoskr.domain.model.Session
import com.po4yka.ratatoskr.domain.model.User
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

/** Maps generated auth DTOs to domain models. */

fun GeneratedUser.toDomain(): User {
    return User(
        id = userId.toString(),
        username = username,
        displayName = null,
        photoUrl = null,
        clientId = clientId,
        isOwner = isOwner,
        createdAt = createdAt.toString(),
    )
}

fun GeneratedAuthTokens.toDomain(currentTime: Instant = Clock.System.now()): AuthTokens {
    return AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType.name.replaceFirstChar { it.uppercase() },
        expiresIn = expiresIn,
        expiresAt = currentTime + expiresIn.seconds,
    )
}

fun LoginData.toAuthTokens(currentTime: Instant = Clock.System.now()): AuthTokens =
    tokens.toDomain(currentTime)

fun SessionInfo.toDomain(): Session {
    return Session(
        id = id,
        clientId = clientId,
        deviceInfo = deviceInfo,
        ipAddress = ipAddress,
        lastUsedAt = lastUsedAt?.toString(),
        createdAt = createdAt.toString(),
        isCurrent = isCurrent ?: false,
    )
}

/** Build a generated [TelegramLoginRequest] from the loose Telegram fields the repository sees. */
fun createTelegramLoginRequest(
    telegramUserId: Long,
    authHash: String,
    authDate: Long,
    username: String?,
    firstName: String?,
    lastName: String?,
    photoUrl: String?,
    clientId: String,
): TelegramLoginRequest {
    return TelegramLoginRequest(
        id = telegramUserId,
        hash = authHash,
        authDate = authDate,
        username = username,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl,
        clientId = clientId,
    )
}
