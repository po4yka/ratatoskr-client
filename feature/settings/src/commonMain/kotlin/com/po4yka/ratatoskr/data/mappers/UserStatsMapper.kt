package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.UserPreferences as GeneratedUserPreferences
import com.po4yka.ratatoskr.api.generated.models.UserStats as GeneratedUserStats
import com.po4yka.ratatoskr.data.remote.dto.GoalDto
import com.po4yka.ratatoskr.data.remote.dto.GoalProgressDto
import com.po4yka.ratatoskr.data.remote.dto.StreakDto
import com.po4yka.ratatoskr.domain.model.DomainStat
import com.po4yka.ratatoskr.domain.model.Goal
import com.po4yka.ratatoskr.domain.model.GoalProgress
import com.po4yka.ratatoskr.domain.model.Streak
import com.po4yka.ratatoskr.domain.model.TopicStat
import com.po4yka.ratatoskr.domain.model.UserPreferences
import com.po4yka.ratatoskr.domain.model.UserStats

fun GeneratedUserStats.toDomain(): UserStats =
    UserStats(
        totalSummaries = totalSummaries.toInt(),
        unreadCount = unreadCount.toInt(),
        readCount = readCount.toInt(),
        totalReadingTimeMin = totalReadingTimeMin.toInt(),
        averageReadingTimeMin = averageReadingTimeMin.toFloat(),
        favoriteTopics = favoriteTopics.map { TopicStat(topic = it.topic, count = it.count.toInt()) },
        favoriteDomains = favoriteDomains.map { DomainStat(domain = it.domain, count = it.count.toInt()) },
        languageDistribution = languageDistribution.mapValues { it.value.toInt() },
        joinedAt = joinedAt?.toString(),
        lastSummaryAt = lastSummaryAt?.toString(),
    )

fun GeneratedUserPreferences.toDomain(): UserPreferences =
    UserPreferences(
        langPreference = langPreference?.name?.lowercase() ?: "auto",
    )

fun StreakDto.toDomain(): Streak =
    Streak(
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        lastActivityDate = lastActivityDate,
        todayCount = todayCount,
        weekCount = weekCount,
        monthCount = monthCount,
    )

fun GoalDto.toDomain(): Goal =
    Goal(
        id = id,
        goalType = goalType,
        targetCount = targetCount,
        createdAt = createdAt,
    )

fun GoalProgressDto.toDomain(): GoalProgress =
    GoalProgress(
        goalType = goalType,
        targetCount = targetCount,
        currentCount = currentCount,
        achieved = achieved,
    )
