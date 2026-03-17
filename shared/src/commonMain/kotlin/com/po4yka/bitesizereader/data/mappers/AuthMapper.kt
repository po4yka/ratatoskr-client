package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.AuthResponseDto
import com.po4yka.bitesizereader.data.remote.dto.DomainStatDto
import com.po4yka.bitesizereader.data.remote.dto.LoginDataDto
import com.po4yka.bitesizereader.data.remote.dto.SessionInfoDto
import com.po4yka.bitesizereader.data.remote.dto.TelegramLoginRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TokenRefreshRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TokenRefreshResponseDto
import com.po4yka.bitesizereader.data.remote.dto.TokensDto
import com.po4yka.bitesizereader.data.remote.dto.TopicStatDto
import com.po4yka.bitesizereader.data.remote.dto.UserDto
import com.po4yka.bitesizereader.data.remote.dto.GoalDto
import com.po4yka.bitesizereader.data.remote.dto.GoalProgressDto
import com.po4yka.bitesizereader.data.remote.dto.StreakDto
import com.po4yka.bitesizereader.data.remote.dto.UserPreferencesDto
import com.po4yka.bitesizereader.data.remote.dto.UserStatsDto
import com.po4yka.bitesizereader.domain.model.AuthTokens
import com.po4yka.bitesizereader.domain.model.DomainStat
import com.po4yka.bitesizereader.domain.model.Goal
import com.po4yka.bitesizereader.domain.model.GoalProgress
import com.po4yka.bitesizereader.domain.model.Session
import com.po4yka.bitesizereader.domain.model.Streak
import com.po4yka.bitesizereader.domain.model.TopicStat
import com.po4yka.bitesizereader.domain.model.User
import com.po4yka.bitesizereader.domain.model.UserPreferences
import com.po4yka.bitesizereader.domain.model.UserStats
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

fun TopicStatDto.toDomain(): TopicStat {
    return TopicStat(
        topic = topic,
        count = count,
    )
}

fun DomainStatDto.toDomain(): DomainStat {
    return DomainStat(
        domain = domain,
        count = count,
    )
}

fun UserStatsDto.toDomain(): UserStats {
    return UserStats(
        totalSummaries = totalSummaries,
        unreadCount = unreadCount,
        readCount = readCount,
        totalReadingTimeMin = totalReadingTimeMin,
        averageReadingTimeMin = averageReadingTimeMin,
        favoriteTopics = favoriteTopics?.map { it.toDomain() },
        favoriteDomains = favoriteDomains?.map { it.toDomain() },
        languageDistribution = languageDistribution,
        joinedAt = joinedAt,
        lastSummaryAt = lastSummaryAt,
    )
}

fun StreakDto.toDomain(): Streak {
    return Streak(
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastActivityDate = lastActivityDate,
        todayCount = todayCount,
        weekCount = weekCount,
        monthCount = monthCount,
    )
}

fun GoalDto.toDomain(): Goal {
    return Goal(
        id = id,
        goalType = goalType,
        targetCount = targetCount,
        createdAt = createdAt,
    )
}

fun GoalProgressDto.toDomain(): GoalProgress {
    return GoalProgress(
        goalType = goalType,
        targetCount = targetCount,
        currentCount = currentCount,
        achieved = achieved,
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
