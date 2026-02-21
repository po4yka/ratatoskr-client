package com.po4yka.bitesizereader.domain.repository

import kotlinx.serialization.json.JsonObject

/**
 * Repository for digest operations: channel subscriptions, preferences, history, triggers.
 *
 * All methods return raw [JsonObject] because the backend digest responses are untyped
 * in the OpenAPI spec. Domain models can be introduced later as the API stabilizes.
 */
interface DigestRepository {
    suspend fun getChannels(): JsonObject

    suspend fun subscribe(channelUsername: String): JsonObject

    suspend fun unsubscribe(channelUsername: String): JsonObject

    suspend fun getPreferences(): JsonObject

    suspend fun updatePreferences(
        deliveryTime: String? = null,
        timezone: String? = null,
        hoursLookback: Int? = null,
        maxPostsPerDigest: Int? = null,
        minRelevanceScore: Double? = null,
    ): JsonObject

    suspend fun getHistory(
        page: Int = 1,
        pageSize: Int = 20,
    ): JsonObject

    suspend fun triggerDigest(): JsonObject
}
