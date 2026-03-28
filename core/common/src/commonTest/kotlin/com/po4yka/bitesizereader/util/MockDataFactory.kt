package com.po4yka.bitesizereader.util

import com.po4yka.bitesizereader.domain.model.AuthTokens
import com.po4yka.bitesizereader.domain.model.Request
import com.po4yka.bitesizereader.domain.model.RequestStatus
import com.po4yka.bitesizereader.domain.model.SearchQuery
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.domain.model.SyncState
import com.po4yka.bitesizereader.domain.model.User
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

/** Factory for creating mock domain model instances for testing */
object MockDataFactory {
    /** Create a mock Summary with customizable properties */
    fun createSummary(
        id: String = "1",
        title: String = "Test Article $id",
        content: String = "This is a test summary content for article $id",
        sourceUrl: String = "https://example.com/article-$id",
        imageUrl: String? = "https://example.com/images/$id.jpg",
        createdAt: Instant = Clock.System.now() - 1.days,
        isRead: Boolean = false,
        tags: List<String> = listOf("technology", "ai"),
    ) = Summary(
        id = id,
        title = title,
        content = content,
        sourceUrl = sourceUrl,
        imageUrl = imageUrl,
        createdAt = createdAt,
        isRead = isRead,
        tags = tags,
    )

    /** Create a list of mock Summaries */
    fun createSummaryList(count: Int = 5): List<Summary> {
        return (1..count).map { createSummary(id = it.toString()) }
    }

    /** Create a mock Request with customizable properties */
    fun createRequest(
        id: String = "1",
        url: String = "https://example.com/article-$id",
        status: RequestStatus = RequestStatus.PENDING,
        createdAt: Instant = Clock.System.now() - 1.hours,
        updatedAt: Instant = Clock.System.now(),
    ) = Request(
        id = id,
        url = url,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    /** Create a list of mock Requests */
    fun createRequestList(count: Int = 3): List<Request> {
        return (1..count).map {
            createRequest(
                id = it.toString(),
                status =
                    when (it % 3) {
                        0 -> RequestStatus.COMPLETED
                        1 -> RequestStatus.PROCESSING
                        else -> RequestStatus.PENDING
                    },
            )
        }
    }

    /** Create a mock User with customizable properties */
    fun createUser(
        id: String = "123456789",
        username: String? = "testuser",
        displayName: String? = "Test User",
        photoUrl: String? = "https://example.com/photo.jpg",
        clientId: String? = null,
        isOwner: Boolean = false,
        createdAt: String? = null,
    ) = User(
        id = id,
        username = username,
        displayName = displayName,
        photoUrl = photoUrl,
        clientId = clientId,
        isOwner = isOwner,
        createdAt = createdAt,
    )

    /** Create a mock AuthTokens with customizable properties */
    fun createAuthTokens(
        accessToken: String = "mock-access-token",
        refreshToken: String = "mock-refresh-token",
        tokenType: String = "Bearer",
        expiresIn: Long = 3600L,
        expiresAt: Instant = Clock.System.now() + 1.hours,
    ) = AuthTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType,
        expiresIn = expiresIn,
        expiresAt = expiresAt,
    )

    /** Create a mock SearchQuery */
    fun createSearchQuery(
        query: String = "test query",
        page: Int = 1,
        pageSize: Int = 20,
    ) = SearchQuery(
        query = query,
        page = page,
        pageSize = pageSize,
    )

    /** Create a mock SyncState */
    fun createSyncState(
        lastSyncTime: Instant? = Clock.System.now() - 1.hours,
        lastSyncHash: String? = "abc123hash",
    ) = SyncState(
        lastSyncTime = lastSyncTime,
        lastSyncHash = lastSyncHash,
    )
}
