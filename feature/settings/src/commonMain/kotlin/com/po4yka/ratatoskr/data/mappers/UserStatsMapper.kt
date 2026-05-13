package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.Streak as GeneratedStreak
import com.po4yka.ratatoskr.api.generated.models.UserGoal as GeneratedUserGoal
import com.po4yka.ratatoskr.api.generated.models.UserGoalProgress as GeneratedUserGoalProgress
import com.po4yka.ratatoskr.api.generated.models.UserPreferences as GeneratedUserPreferences
import com.po4yka.ratatoskr.api.generated.models.UserStats as GeneratedUserStats
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

fun GeneratedStreak.toDomain(): Streak =
    Streak(
        currentStreak = currentStreak.toInt(),
        longestStreak = longestStreak.toInt(),
        lastActivityDate = lastActivityDate,
        todayCount = todayCount.toInt(),
        weekCount = weekCount.toInt(),
        monthCount = monthCount.toInt(),
    )

fun GeneratedUserGoal.toDomain(): Goal =
    Goal(
        id = id,
        goalType = goalType,
        targetCount = targetCount.toInt(),
        createdAt = createdAt.toString(),
    )

fun GeneratedUserGoalProgress.toDomain(): GoalProgress =
    GoalProgress(
        goalType = goalType,
        targetCount = targetCount.toInt(),
        currentCount = currentCount.toInt(),
        achieved = achieved,
    )
