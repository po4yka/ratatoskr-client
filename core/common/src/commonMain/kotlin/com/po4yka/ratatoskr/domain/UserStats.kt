package com.po4yka.ratatoskr.domain.model

/**
 * Topic statistics with name and count.
 */
data class TopicStat(
    val topic: String,
    val count: Int,
)

/**
 * Domain statistics with name and count.
 */
data class DomainStat(
    val domain: String,
    val count: Int,
)

/**
 * User statistics for summaries and reading activity.
 */
data class UserStats(
    val totalSummaries: Int,
    val unreadCount: Int,
    val readCount: Int,
    val totalReadingTimeMin: Int? = null,
    val averageReadingTimeMin: Float? = null,
    val favoriteTopics: List<TopicStat>? = null,
    val favoriteDomains: List<DomainStat>? = null,
    val languageDistribution: Map<String, Int>? = null,
    val joinedAt: String? = null,
    val lastSummaryAt: String? = null,
)
