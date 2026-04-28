package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.data.remote.dto.DomainStatDto
import com.po4yka.ratatoskr.data.remote.dto.GoalDto
import com.po4yka.ratatoskr.data.remote.dto.GoalProgressDto
import com.po4yka.ratatoskr.data.remote.dto.StreakDto
import com.po4yka.ratatoskr.data.remote.dto.TopicStatDto
import com.po4yka.ratatoskr.data.remote.dto.UserStatsDto
import com.po4yka.ratatoskr.domain.model.DomainStat
import com.po4yka.ratatoskr.domain.model.Goal
import com.po4yka.ratatoskr.domain.model.GoalProgress
import com.po4yka.ratatoskr.domain.model.Streak
import com.po4yka.ratatoskr.domain.model.TopicStat
import com.po4yka.ratatoskr.domain.model.UserStats

fun TopicStatDto.toDomain(): TopicStat =
    TopicStat(
        topic = topic,
        count = count,
    )

fun DomainStatDto.toDomain(): DomainStat =
    DomainStat(
        domain = domain,
        count = count,
    )

fun UserStatsDto.toDomain(): UserStats =
    UserStats(
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
