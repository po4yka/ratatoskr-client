package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDigestHistoryItems
import com.po4yka.bitesizereader.data.mappers.toDigestPreferences
import com.po4yka.bitesizereader.data.mappers.toDigestSubscriptionInfo
import com.po4yka.bitesizereader.data.remote.DigestApi
import com.po4yka.bitesizereader.domain.model.DigestHistoryItem
import com.po4yka.bitesizereader.domain.model.DigestPreferences
import com.po4yka.bitesizereader.domain.model.DigestSubscriptionInfo
import com.po4yka.bitesizereader.domain.repository.DigestRepository
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import org.koin.core.annotation.Single

@Single(binds = [DigestRepository::class])
class DigestRepositoryImpl(
    private val api: DigestApi,
) : DigestRepository {
    override suspend fun getChannels(): DigestSubscriptionInfo {
        val response = api.getChannels()
        return (response.data ?: buildJsonObject {}).toDigestSubscriptionInfo()
    }

    override suspend fun subscribe(channelUsername: String): DigestSubscriptionInfo {
        val response = api.subscribe(channelUsername)
        return (response.data ?: buildJsonObject {}).toDigestSubscriptionInfo()
    }

    override suspend fun unsubscribe(channelUsername: String): DigestSubscriptionInfo {
        val response = api.unsubscribe(channelUsername)
        return (response.data ?: buildJsonObject {}).toDigestSubscriptionInfo()
    }

    override suspend fun getPreferences(): DigestPreferences {
        val response = api.getPreferences()
        return (response.data ?: buildJsonObject {}).toDigestPreferences()
    }

    override suspend fun updatePreferences(
        deliveryTime: String?,
        timezone: String?,
        hoursLookback: Int?,
        maxPostsPerDigest: Int?,
        minRelevanceScore: Double?,
    ): DigestPreferences {
        val response =
            api.updatePreferences(
                deliveryTime = deliveryTime,
                timezone = timezone,
                hoursLookback = hoursLookback,
                maxPostsPerDigest = maxPostsPerDigest,
                minRelevanceScore = minRelevanceScore,
            )
        return (response.data ?: buildJsonObject {}).toDigestPreferences()
    }

    override suspend fun getHistory(
        page: Int,
        pageSize: Int,
    ): List<DigestHistoryItem> {
        val response = api.getHistory(page, pageSize)
        return (response.data ?: buildJsonObject {}).toDigestHistoryItems()
    }

    override suspend fun triggerDigest(): JsonObject {
        val response = api.triggerDigest()
        return response.data ?: buildJsonObject {}
    }
}
