package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.DigestApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.V1DigestCategoriesRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestChannelsBulkUnsubscribeRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestChannelsResolveRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestChannelsSubscribeRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestChannelsUnsubscribeRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestPreferencesRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestTriggerChannelRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.domain.model.DigestHistoryItem
import com.po4yka.ratatoskr.domain.model.DigestPreferences
import com.po4yka.ratatoskr.domain.model.DigestSubscriptionInfo
import com.po4yka.ratatoskr.domain.model.DigestTriggerResult
import com.po4yka.ratatoskr.domain.model.ResolvedChannel
import com.po4yka.ratatoskr.domain.repository.DigestRepository
import org.koin.core.annotation.Single

@Single(binds = [DigestRepository::class])
class DigestRepositoryImpl : DigestRepository {
    override suspend fun getChannels(): DigestSubscriptionInfo {
        val data = DigestApi.listChannelsV1DigestChannelsGet().unwrap().data
            ?: error("No channel data in server response")
        return data.toDomain()
    }

    override suspend fun subscribe(channelUsername: String): DigestSubscriptionInfo {
        // Subscribe endpoint returns SubscribeActionResponseEnvelope (status + username).
        // Re-fetch the channel list to keep the existing contract intact.
        DigestApi.subscribeChannelV1DigestChannelsSubscribePost(
            V1DigestChannelsSubscribeRequest(channelUsername = channelUsername),
        ).unwrap()
        return getChannels()
    }

    override suspend fun unsubscribe(channelUsername: String): DigestSubscriptionInfo {
        DigestApi.unsubscribeChannelV1DigestChannelsUnsubscribePost(
            V1DigestChannelsUnsubscribeRequest(channelUsername = channelUsername),
        ).unwrap()
        return getChannels()
    }

    override suspend fun resolveChannel(channelUsername: String): ResolvedChannel {
        val data =
            DigestApi.resolveChannelV1DigestChannelsResolvePost(
                V1DigestChannelsResolveRequest(channelUsername = channelUsername),
            ).unwrap().data
                ?: error("Failed to resolve channel")
        return data.toDomain()
    }

    override suspend fun getPreferences(): DigestPreferences {
        val data = DigestApi.getPreferencesV1DigestPreferencesGet().unwrap().data
            ?: error("No preferences data in server response")
        return data.toDomain()
    }

    override suspend fun updatePreferences(
        deliveryTime: String?,
        timezone: String?,
        hoursLookback: Int?,
        maxPostsPerDigest: Int?,
        minRelevanceScore: Double?,
    ): DigestPreferences {
        val data =
            DigestApi.updatePreferencesV1DigestPreferencesPatch(
                V1DigestPreferencesRequest(
                    deliveryTime = deliveryTime,
                    timezone = timezone,
                    hoursLookback = hoursLookback?.toLong(),
                    maxPostsPerDigest = maxPostsPerDigest?.toLong(),
                    minRelevanceScore = minRelevanceScore,
                ),
            ).unwrap().data
                ?: error("No preferences data in server response")
        return data.toDomain()
    }

    override suspend fun getHistory(
        page: Int,
        pageSize: Int,
    ): List<DigestHistoryItem> {
        val offset = (page.coerceAtLeast(1) - 1).toLong() * pageSize.toLong()
        val data =
            DigestApi.listHistoryV1DigestHistoryGet(
                limit = pageSize.toLong(),
                offset = offset,
            ).unwrap().data
                ?: error("No history data in server response")
        return data.toDomain()
    }

    override suspend fun triggerDigest(): DigestTriggerResult {
        val data = DigestApi.triggerDigestV1DigestTriggerPost().unwrap().data
            ?: return DigestTriggerResult.NoServerResponse
        return DigestTriggerResult.Triggered(status = data.status, message = null)
    }

    override suspend fun triggerChannel(channelUsername: String): DigestTriggerResult {
        val data =
            DigestApi.triggerChannelDigestV1DigestTriggerChannelPost(
                V1DigestTriggerChannelRequest(channelUsername = channelUsername),
            ).unwrap().data
                ?: return DigestTriggerResult.NoServerResponse
        return DigestTriggerResult.Triggered(status = data.status, message = null)
    }

    override suspend fun createCategory(name: String): String {
        val data =
            DigestApi.createCategoryV1DigestCategoriesPost(
                V1DigestCategoriesRequest(name = name),
            ).unwrap().data
                ?: error("Failed to create category")
        return data.id.toString()
    }

    override suspend fun bulkUnsubscribe(channelUsernames: List<String>) {
        DigestApi.bulkUnsubscribeV1DigestChannelsBulkUnsubscribePost(
            V1DigestChannelsBulkUnsubscribeRequest(channelUsernames = channelUsernames),
        ).unwrap()
    }
}
