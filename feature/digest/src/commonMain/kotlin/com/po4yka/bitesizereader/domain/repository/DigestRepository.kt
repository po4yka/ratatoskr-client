package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.DigestHistoryItem
import com.po4yka.bitesizereader.domain.model.DigestPreferences
import com.po4yka.bitesizereader.domain.model.DigestSubscriptionInfo
import com.po4yka.bitesizereader.domain.model.ResolvedChannel

/**
 * Repository for digest operations: channel subscriptions, preferences, history, triggers.
 */
interface DigestRepository {
    suspend fun getChannels(): DigestSubscriptionInfo

    suspend fun subscribe(channelUsername: String): DigestSubscriptionInfo

    suspend fun unsubscribe(channelUsername: String): DigestSubscriptionInfo

    /** Resolve channel details before subscribing. */
    suspend fun resolveChannel(channelUsername: String): ResolvedChannel

    suspend fun getPreferences(): DigestPreferences

    suspend fun updatePreferences(
        deliveryTime: String? = null,
        timezone: String? = null,
        hoursLookback: Int? = null,
        maxPostsPerDigest: Int? = null,
        minRelevanceScore: Double? = null,
    ): DigestPreferences

    suspend fun getHistory(
        page: Int = 1,
        pageSize: Int = 20,
    ): List<DigestHistoryItem>

    suspend fun triggerDigest(): String

    /** Trigger digest for a specific channel. */
    suspend fun triggerChannel(channelUsername: String): String

    /** Create a category for organizing channels. */
    suspend fun createCategory(name: String): String

    /** Bulk unsubscribe from multiple channels. */
    suspend fun bulkUnsubscribe(channelUsernames: List<String>)
}
