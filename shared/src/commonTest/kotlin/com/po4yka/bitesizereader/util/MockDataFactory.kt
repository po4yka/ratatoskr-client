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
        requestId: Int = 1,
        title: String = "Test Article $id",
        url: String = "https://example.com/article-$id",
        domain: String? = "example.com",
        tldr: String = "This is a test summary for article $id",
        summary250: String = "A concise 250-char summary",
        summary1000: String? = "A detailed 1000-char summary with more information about the article",
        keyIdeas: List<String> = listOf("Key idea 1", "Key idea 2", "Key idea 3"),
        topicTags: List<String> = listOf("technology", "ai"),
        answeredQuestions: List<String> = listOf("Question 1?", "Question 2?"),
        seoKeywords: List<String> = listOf("keyword1", "keyword2"),
        readingTimeMin: Int = 5,
        lang: String = "en",
        entities: Entities? =
            Entities(
                people = listOf("John Doe"),
                organizations = listOf("Tech Corp"),
                locations = listOf("San Francisco"),
            ),
        keyStats: List<KeyStat> =
            listOf(
                KeyStat(label = "Users", value = 1000.0, unit = "million", sourceExcerpt = "1M users"),
            ),
        readability: Readability? = Readability(method = "flesch", score = 60.0, level = "standard"),
        isRead: Boolean = false,
        isFavorite: Boolean = false,
        createdAt: Instant = Clock.System.now() - (id * 1).days,
        updatedAt: Instant? = null,
        syncStatus: SyncStatus = SyncStatus.SYNCED,
        locallyModified: Boolean = false,
    ) = Summary(
        id = id,
        requestId = requestId,
        title = title,
        url = url,
        domain = domain,
        tldr = tldr,
        summary250 = summary250,
        summary1000 = summary1000,
        keyIdeas = keyIdeas,
        topicTags = topicTags,
        answeredQuestions = answeredQuestions,
        seoKeywords = seoKeywords,
        readingTimeMin = readingTimeMin,
        lang = lang,
        entities = entities,
        keyStats = keyStats,
        readability = readability,
        isRead = isRead,
        isFavorite = isFavorite,
        createdAt = createdAt,
        updatedAt = updatedAt,
        syncStatus = syncStatus,
        locallyModified = locallyModified,
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
        inputUrl: String = "https://example.com/article-$id",
        type: RequestType = RequestType.URL,
        status: RequestStatus = RequestStatus.PENDING,
        stage: ProcessingStage? = null,
        progress: Int = 0,
        langPreference: String = "en",
        summaryId: Int? = null,
        errorMessage: String? = null,
        canRetry: Boolean = false,
        estimatedSecondsRemaining: Int? = null,
        createdAt: Instant = Clock.System.now() - 1.hours,
        updatedAt: Instant? = null,
        completedAt: Instant? = null,
    ) = Request(
        id = id,
        inputUrl = inputUrl,
        type = type,
        status = status,
        stage = stage,
        progress = progress,
        langPreference = langPreference,
        summaryId = summaryId,
        errorMessage = errorMessage,
        canRetry = canRetry,
        estimatedSecondsRemaining = estimatedSecondsRemaining,
        createdAt = createdAt,
        updatedAt = updatedAt,
        completedAt = completedAt,
    )

    /**
     * Create a list of mock Requests
     */
    fun createRequestList(count: Int = 3): List<Request> {
        return (1..count).map {
            createRequest(
                id = it,
                status =
                    when (it % 3) {
                        0 -> RequestStatus.COMPLETED
                        1 -> RequestStatus.PROCESSING
                        else -> RequestStatus.PENDING
                    },
            )
        }
    }

    /**
     * Create a mock User with customizable properties
     */
    fun createUser(
        id: Long = 123456789L,
        username: String? = "testuser",
        firstName: String? = "Test",
        lastName: String? = "User",
        photoUrl: String? = "https://example.com/photo.jpg",
        isOwner: Boolean = false,
    ) = User(
        id = id,
        username = username,
        firstName = firstName,
        lastName = lastName,
        photoUrl = photoUrl,
        isOwner = isOwner,
    )

    /**
     * Create a mock AuthTokens with customizable properties
     */
    fun createAuthTokens(
        accessToken: String = "mock-access-token",
        refreshToken: String = "mock-refresh-token",
        tokenType: String = "Bearer",
        expiresIn: Int = 3600,
        expiresAt: Instant = Clock.System.now() + 1.hours,
    ) = AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType,
        expiresIn = expiresIn,
        expiresAt = expiresAt,
    )

    /**
     * Create a mock SearchQuery
     */
    fun createSearchQuery(
        query: String = "test query",
        filters: SearchFilters = SearchFilters(),
    ) = SearchQuery(
        query = query,
        filters = filters,
    )

    /**
     * Create mock SearchFilters
     */
    fun createSearchFilters(
        isRead: Boolean? = null,
        readStatus: String? = null,
        lang: String? = null,
        topicTags: List<String> = emptyList(),
        fromDate: String? = null,
        toDate: String? = null,
        sortBy: SortField = SortField.CREATED_AT,
        sortOrder: SortOrder = SortOrder.DESC,
    ) = SearchFilters(
        isRead = isRead,
        readStatus = readStatus,
        lang = lang,
        topicTags = topicTags,
        fromDate = fromDate,
        toDate = toDate,
        sortBy = sortBy,
        sortOrder = sortOrder,
    )

    /**
     * Create a mock SyncState (returns Idle by default)
     */
    fun createSyncState(): SyncState = SyncState.Idle

    /**
     * Create a mock SyncMetadata
     */
    fun createSyncMetadata(
        lastFullSync: Instant? = null,
        lastDeltaSync: Instant? = null,
        lastSyncTimestamp: Instant? = Clock.System.now() - 1.hours,
        deviceId: String = "test-device-id",
        pendingChanges: Int = 0,
    ) = SyncMetadata(
        lastFullSync = lastFullSync,
        lastDeltaSync = lastDeltaSync,
        lastSyncTimestamp = lastSyncTimestamp,
        deviceId = deviceId,
        pendingChanges = pendingChanges,
    )
}
