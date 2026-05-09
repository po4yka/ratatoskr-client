package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.DigestApi
import com.po4yka.ratatoskr.domain.model.DigestHistoryItem
import com.po4yka.ratatoskr.domain.model.DigestPreferences
import com.po4yka.ratatoskr.domain.model.DigestSubscriptionInfo
import com.po4yka.ratatoskr.domain.model.DigestTriggerResult
import com.po4yka.ratatoskr.domain.model.ResolvedChannel
import com.po4yka.ratatoskr.domain.repository.DigestRepository
import org.koin.core.annotation.Single

@Single(binds = [DigestRepository::class])
class DigestRepositoryImpl(
    private val api: DigestApi,
) : DigestRepository {
    override suspend fun getChannels(): DigestSubscriptionInfo {
        val response = api.getChannels()
        return requireNotNull(response.data) { "No channel data in server response" }.toDomain()
    }

    override suspend fun subscribe(channelUsername: String): DigestSubscriptionInfo {
        val response = api.subscribe(channelUsername)
        return requireNotNull(response.data) { "No channel data in server response" }.toDomain()
    }

    override suspend fun unsubscribe(channelUsername: String): DigestSubscriptionInfo {
        val response = api.unsubscribe(channelUsername)
        return requireNotNull(response.data) { "No channel data in server response" }.toDomain()
    }

    override suspend fun resolveChannel(channelUsername: String): ResolvedChannel {
        val response = api.resolveChannel(channelUsername)
        return requireNotNull(response.data) { "Failed to resolve channel" }.toDomain()
    }

    override suspend fun getPreferences(): DigestPreferences {
        val response = api.getPreferences()
        return requireNotNull(response.data) { "No preferences data in server response" }.toDomain()
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
        return requireNotNull(response.data) { "No preferences data in server response" }.toDomain()
    }

    override suspend fun getHistory(
        page: Int,
        pageSize: Int,
    ): List<DigestHistoryItem> {
        val response = api.getHistory(page, pageSize)
        return requireNotNull(response.data) { "No history data in server response" }.toDomain()
    }

    override suspend fun triggerDigest(): DigestTriggerResult {
        val response = api.triggerDigest()
        val data = response.data ?: return DigestTriggerResult.NoServerResponse
        return DigestTriggerResult.Triggered(status = data.status, message = data.message)
    }

    override suspend fun triggerChannel(channelUsername: String): DigestTriggerResult {
        val response = api.triggerChannel(channelUsername)
        val data = response.data ?: return DigestTriggerResult.NoServerResponse
        return DigestTriggerResult.Triggered(status = data.status, message = data.message)
    }

    override suspend fun createCategory(name: String): String {
        val response = api.createCategory(name)
        return response.data?.id ?: error("Failed to create category")
    }

    override suspend fun bulkUnsubscribe(channelUsernames: List<String>) {
        api.bulkUnsubscribe(channelUsernames)
    }
}
