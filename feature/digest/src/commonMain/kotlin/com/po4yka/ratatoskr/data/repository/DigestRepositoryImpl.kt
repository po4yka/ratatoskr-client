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
import com.po4yka.ratatoskr.data.remote.dto.CategoryResponseDto
import com.po4yka.ratatoskr.data.remote.dto.ChannelListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.DigestDeliveryListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.DigestPreferenceResponseDto
import com.po4yka.ratatoskr.data.remote.dto.ResolveChannelResponseDto
import com.po4yka.ratatoskr.data.remote.dto.TriggerDigestResponseDto
import com.po4yka.ratatoskr.domain.model.DigestHistoryItem
import com.po4yka.ratatoskr.domain.model.DigestPreferences
import com.po4yka.ratatoskr.domain.model.DigestSubscriptionInfo
import com.po4yka.ratatoskr.domain.model.DigestTriggerResult
import com.po4yka.ratatoskr.domain.model.ResolvedChannel
import com.po4yka.ratatoskr.domain.repository.DigestRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import org.koin.core.annotation.Single

@Single(binds = [DigestRepository::class])
class DigestRepositoryImpl : DigestRepository {
    override suspend fun getChannels(): DigestSubscriptionInfo {
        val envelope = DigestApi.listChannelsV1DigestChannelsGet().unwrap()
        return decodeData<ChannelListResponseDto>(envelope, "No channel data in server response")
            .toDomain()
    }

    override suspend fun subscribe(channelUsername: String): DigestSubscriptionInfo {
        val envelope =
            DigestApi.subscribeChannelV1DigestChannelsSubscribePost(
                V1DigestChannelsSubscribeRequest(channelUsername = channelUsername),
            ).unwrap()
        return decodeData<ChannelListResponseDto>(envelope, "No channel data in server response")
            .toDomain()
    }

    override suspend fun unsubscribe(channelUsername: String): DigestSubscriptionInfo {
        val envelope =
            DigestApi.unsubscribeChannelV1DigestChannelsUnsubscribePost(
                V1DigestChannelsUnsubscribeRequest(channelUsername = channelUsername),
            ).unwrap()
        return decodeData<ChannelListResponseDto>(envelope, "No channel data in server response")
            .toDomain()
    }

    override suspend fun resolveChannel(channelUsername: String): ResolvedChannel {
        val envelope =
            DigestApi.resolveChannelV1DigestChannelsResolvePost(
                V1DigestChannelsResolveRequest(channelUsername = channelUsername),
            ).unwrap()
        return decodeData<ResolveChannelResponseDto>(envelope, "Failed to resolve channel")
            .toDomain()
    }

    override suspend fun getPreferences(): DigestPreferences {
        val envelope = DigestApi.getPreferencesV1DigestPreferencesGet().unwrap()
        return decodeData<DigestPreferenceResponseDto>(envelope, "No preferences data in server response")
            .toDomain()
    }

    override suspend fun updatePreferences(
        deliveryTime: String?,
        timezone: String?,
        hoursLookback: Int?,
        maxPostsPerDigest: Int?,
        minRelevanceScore: Double?,
    ): DigestPreferences {
        val envelope =
            DigestApi.updatePreferencesV1DigestPreferencesPatch(
                V1DigestPreferencesRequest(
                    deliveryTime = deliveryTime,
                    timezone = timezone,
                    hoursLookback = hoursLookback?.toLong(),
                    maxPostsPerDigest = maxPostsPerDigest?.toLong(),
                    minRelevanceScore = minRelevanceScore,
                ),
            ).unwrap()
        return decodeData<DigestPreferenceResponseDto>(envelope, "No preferences data in server response")
            .toDomain()
    }

    override suspend fun getHistory(
        page: Int,
        pageSize: Int,
    ): List<DigestHistoryItem> {
        val offset = (page.coerceAtLeast(1) - 1).toLong() * pageSize.toLong()
        val envelope =
            DigestApi.listHistoryV1DigestHistoryGet(
                limit = pageSize.toLong(),
                offset = offset,
            ).unwrap()
        return decodeData<DigestDeliveryListResponseDto>(envelope, "No history data in server response")
            .toDomain()
    }

    override suspend fun triggerDigest(): DigestTriggerResult {
        val envelope = DigestApi.triggerDigestV1DigestTriggerPost().unwrap()
        val data = decodeDataOrNull<TriggerDigestResponseDto>(envelope)
            ?: return DigestTriggerResult.NoServerResponse
        return DigestTriggerResult.Triggered(status = data.status, message = data.message)
    }

    override suspend fun triggerChannel(channelUsername: String): DigestTriggerResult {
        val envelope =
            DigestApi.triggerChannelDigestV1DigestTriggerChannelPost(
                V1DigestTriggerChannelRequest(channelUsername = channelUsername),
            ).unwrap()
        val data = decodeDataOrNull<TriggerDigestResponseDto>(envelope)
            ?: return DigestTriggerResult.NoServerResponse
        return DigestTriggerResult.Triggered(status = data.status, message = data.message)
    }

    override suspend fun createCategory(name: String): String {
        val envelope =
            DigestApi.createCategoryV1DigestCategoriesPost(
                V1DigestCategoriesRequest(name = name),
            ).unwrap()
        val data = decodeDataOrNull<CategoryResponseDto>(envelope)
            ?: error("Failed to create category")
        return data.id
    }

    override suspend fun bulkUnsubscribe(channelUsernames: List<String>) {
        DigestApi.bulkUnsubscribeV1DigestChannelsBulkUnsubscribePost(
            V1DigestChannelsBulkUnsubscribeRequest(channelUsernames = channelUsernames),
        ).unwrap()
    }

}

private val envelopeJson = Json { ignoreUnknownKeys = true }

private inline fun <reified T> decodeData(
    envelope: JsonElement,
    missingMessage: String,
): T = decodeDataOrNull<T>(envelope) ?: error(missingMessage)

private inline fun <reified T> decodeDataOrNull(envelope: JsonElement): T? {
    val dataField = (envelope as? JsonObject)?.get("data")
        ?.takeIf { it !is JsonNull }
        ?: return null
    return envelopeJson.decodeFromJsonElement(serializer<T>(), dataField)
}
