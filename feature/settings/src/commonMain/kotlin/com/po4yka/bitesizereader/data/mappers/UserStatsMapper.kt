package com.po4yka.bitesizereader.data.mappers

import com.po4yka.bitesizereader.data.remote.dto.DomainStatDto
import com.po4yka.bitesizereader.data.remote.dto.GoalDto
import com.po4yka.bitesizereader.data.remote.dto.GoalProgressDto
import com.po4yka.bitesizereader.data.remote.dto.StreakDto
import com.po4yka.bitesizereader.data.remote.dto.TopicStatDto
import com.po4yka.bitesizereader.data.remote.dto.UserStatsDto
import com.po4yka.bitesizereader.domain.model.DomainStat
import com.po4yka.bitesizereader.domain.model.Goal
import com.po4yka.bitesizereader.domain.model.GoalProgress
import com.po4yka.bitesizereader.domain.model.Streak
import com.po4yka.bitesizereader.domain.model.TopicStat
import com.po4yka.bitesizereader.domain.model.UserStats

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
