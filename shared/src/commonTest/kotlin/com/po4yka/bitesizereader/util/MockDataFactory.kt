package com.po4yka.bitesizereader.util

import com.po4yka.bitesizereader.domain.model.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

/**
 * Factory for creating mock domain model instances for testing
 */
object MockDataFactory {

    /**
     * Create a mock Summary with customizable properties
     */
    fun createSummary(
        id: Int = 1,
        url: String = "https://example.com/article-$id",
        title: String = "Test Article $id",
        tldr: String = "This is a test summary for article $id",
        keyPoints: List<String> = listOf("Key point 1", "Key point 2", "Key point 3"),
        topicTags: List<String> = listOf("technology", "ai"),
        estimatedReadingTime: Int = 5,
        language: String = "en",
        isRead: Boolean = false,
        createdAt: Instant = Clock.System.now() - (id * 1).days,
        sourceMetadata: SourceMetadata = createSourceMetadata()
    ) = Summary(
        id = id,
        url = url,
        title = title,
        tldr = tldr,
        keyPoints = keyPoints,
        topicTags = topicTags,
        estimatedReadingTime = estimatedReadingTime,
        language = language,
        isRead = isRead,
        createdAt = createdAt,
        sourceMetadata = sourceMetadata
    )

    /**
     * Create a mock SourceMetadata
     */
    fun createSourceMetadata(
        domain: String = "example.com",
        author: String? = "John Doe",
        publishedDate: String? = "2025-01-15",
        originalTitle: String? = "Original Article Title",
        imageUrl: String? = "https://example.com/image.jpg"
    ) = SourceMetadata(
        domain = domain,
        author = author,
        publishedDate = publishedDate,
        originalTitle = originalTitle,
        imageUrl = imageUrl
    )

    /**
     * Create a list of mock Summaries
     */
    fun createSummaryList(count: Int = 5): List<Summary> {
        return (1..count).map { createSummary(id = it) }
    }

    /**
     * Create a mock Request with customizable properties
     */
    fun createRequest(
        id: Int = 1,
        url: String = "https://example.com/article-$id",
        status: RequestStatus = RequestStatus.PENDING,
        clientId: String = "test-client-$id",
        createdAt: Instant = Clock.System.now() - 1.hours,
        summaryId: Int? = null,
        error: String? = null
    ) = Request(
        id = id,
        url = url,
        status = status,
        clientId = clientId,
        createdAt = createdAt,
        summaryId = summaryId,
        error = error
    )

    /**
     * Create a list of mock Requests
     */
    fun createRequestList(count: Int = 3): List<Request> {
        return (1..count).map {
            createRequest(
                id = it,
                status = when (it % 3) {
                    0 -> RequestStatus.COMPLETED
                    1 -> RequestStatus.PROCESSING
                    else -> RequestStatus.PENDING
                }
            )
        }
    }

    /**
     * Create a mock User with customizable properties
     */
    fun createUser(
        id: Int = 1,
        telegramUserId: Long = 123456789L,
        username: String? = "testuser",
        firstName: String = "Test",
        lastName: String? = "User",
        photoUrl: String? = "https://example.com/photo.jpg",
        createdAt: Instant = Clock.System.now() - 30.days
    ) = User(
        id = id,
        telegramUserId = telegramUserId,
        username = username,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl,
        createdAt = createdAt
    )

    /**
     * Create a mock AuthTokens with customizable properties
     */
    fun createAuthTokens(
        accessToken: String = "mock-access-token",
        refreshToken: String = "mock-refresh-token"
    ) = AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken
    )

    /**
     * Create a mock SearchQuery
     */
    fun createSearchQuery(
        query: String = "test query",
        filters: SearchFilters = SearchFilters()
    ) = SearchQuery(
        query = query,
        filters = filters
    )

    /**
     * Create mock SearchFilters
     */
    fun createSearchFilters(
        topics: List<String> = emptyList(),
        languages: List<String> = emptyList(),
        isRead: Boolean? = null,
        dateFrom: Instant? = null,
        dateTo: Instant? = null
    ) = SearchFilters(
        topics = topics,
        languages = languages,
        isRead = isRead,
        dateFrom = dateFrom,
        dateTo = dateTo
    )

    /**
     * Create a mock SyncState
     */
    fun createSyncState(
        lastSyncTimestamp: Instant = Clock.System.now() - 1.hours,
        pendingChanges: Int = 0,
        isSyncing: Boolean = false
    ) = SyncState(
        lastSyncTimestamp = lastSyncTimestamp,
        pendingChanges = pendingChanges,
        isSyncing = isSyncing
    )
}
