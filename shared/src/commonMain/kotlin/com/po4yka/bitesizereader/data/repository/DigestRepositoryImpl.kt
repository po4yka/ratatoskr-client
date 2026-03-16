package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.DigestApi
import com.po4yka.bitesizereader.data.remote.dto.ResolveChannelResponseDto
import com.po4yka.bitesizereader.domain.model.DigestHistoryItem
import com.po4yka.bitesizereader.domain.model.DigestPreferences
import com.po4yka.bitesizereader.domain.model.DigestSubscriptionInfo
import com.po4yka.bitesizereader.domain.repository.DigestRepository
import org.koin.core.annotation.Single

@Single(binds = [DigestRepository::class])
class DigestRepositoryImpl(
    private val api: DigestApi,
) : DigestRepository {
    override suspend fun getChannels(): DigestSubscriptionInfo {
        val response = api.getChannels()
        return response.data?.toDomain() ?: DigestSubscriptionInfo()
    }

    override suspend fun subscribe(channelUsername: String): DigestSubscriptionInfo {
        val response = api.subscribe(channelUsername)
        return response.data?.toDomain() ?: DigestSubscriptionInfo()
    }

    override suspend fun unsubscribe(channelUsername: String): DigestSubscriptionInfo {
        val response = api.unsubscribe(channelUsername)
        return response.data?.toDomain() ?: DigestSubscriptionInfo()
    }

    override suspend fun resolveChannel(channelUsername: String): ResolveChannelResponseDto {
        val response = api.resolveChannel(channelUsername)
        return response.data ?: error("Failed to resolve channel")
    }

    override suspend fun getPreferences(): DigestPreferences {
        val response = api.getPreferences()
        return response.data?.toDomain() ?: DigestPreferences()
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
        return response.data?.toDomain() ?: DigestPreferences()
    }

    override suspend fun getHistory(
        page: Int,
        pageSize: Int,
    ): List<DigestHistoryItem> {
        val response = api.getHistory(page, pageSize)
        return response.data?.toDomain() ?: emptyList()
    }

    override suspend fun triggerDigest(): String {
        val response = api.triggerDigest()
        return response.data?.status ?: "unknown"
    }

    override suspend fun triggerChannel(channelUsername: String): String {
        val response = api.triggerChannel(channelUsername)
        return response.data?.status ?: "unknown"
    }

    override suspend fun createCategory(name: String): String {
        val response = api.createCategory(name)
        return response.data?.id ?: error("Failed to create category")
    }

    override suspend fun bulkUnsubscribe(channelUsernames: List<String>) {
        api.bulkUnsubscribe(channelUsernames)
    }
}
