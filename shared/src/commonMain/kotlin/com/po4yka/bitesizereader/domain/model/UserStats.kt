package com.po4yka.bitesizereader.domain.model

/**
 * User statistics for summaries and reading activity.
 */
data class UserStats(
    val totalSummaries: Int,
    val unreadCount: Int,
    val readCount: Int,
    val totalReadingTimeMin: Int? = null,
    val averageReadingTimeMin: Float? = null,
    val favoriteTopics: List<String>? = null,
    val favoriteDomains: List<String>? = null,
    val languageDistribution: Map<String, Int>? = null,
    val joinedAt: String? = null,
    val lastSummaryAt: String? = null,
)
