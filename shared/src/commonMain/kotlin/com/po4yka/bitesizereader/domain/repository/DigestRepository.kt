package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.DigestHistoryItem
import com.po4yka.bitesizereader.domain.model.DigestPreferences
import com.po4yka.bitesizereader.domain.model.DigestSubscriptionInfo
import kotlinx.serialization.json.JsonObject

/**
 * Repository for digest operations: channel subscriptions, preferences, history, triggers.
 */
interface DigestRepository {
    suspend fun getChannels(): DigestSubscriptionInfo

    suspend fun subscribe(channelUsername: String): DigestSubscriptionInfo

    suspend fun unsubscribe(channelUsername: String): DigestSubscriptionInfo

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

    suspend fun triggerDigest(): JsonObject
}
