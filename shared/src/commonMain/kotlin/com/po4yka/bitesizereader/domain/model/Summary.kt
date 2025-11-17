@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.po4yka.bitesizereader.domain.model

import kotlinx.datetime.Instant

/**
 * Domain model representing a content summary.
 * This is the core business entity for summarized articles/videos.
 */
data class Summary(
    val id: Int,
    val requestId: Int,
    val title: String,
    val url: String,
    val domain: String?,
    // Summary variants
    val tldr: String,
    val summary250: String,
    val summary1000: String?,
    // Structured content
    val keyIdeas: List<String>,
    val topicTags: List<String>,
    val answeredQuestions: List<String>,
    val seoKeywords: List<String>,
    // Metadata
    val readingTimeMin: Int,
    val lang: String,
    // Entities
    val entities: Entities?,
    // Statistics
    val keyStats: List<KeyStat>,
    // Readability
    val readability: Readability?,
    // User state
    val isRead: Boolean = false,
    val isFavorite: Boolean = false,
    // Timestamps
    val createdAt: Instant,
    val updatedAt: Instant? = null,
    // Sync state
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val locallyModified: Boolean = false,
)

/**
 * Named entities extracted from content
 */
data class Entities(
    val people: List<String>,
    val organizations: List<String>,
    val locations: List<String>,
)

/**
 * Key statistic from the content
 */
data class KeyStat(
    val label: String,
    val value: Double,
    val unit: String?,
    val sourceExcerpt: String?,
)

/**
 * Readability metrics
 */
data class Readability(
    val method: String,
    val score: Double,
    val level: String,
)

/**
 * Sync status for offline-first architecture
 */
enum class SyncStatus {
    SYNCED, // In sync with server
    PENDING_UPLOAD, // Local changes need to be uploaded
    CONFLICT, // Conflict with server version
}
