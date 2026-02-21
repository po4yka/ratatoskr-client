package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.remote.DigestApi
import com.po4yka.bitesizereader.domain.repository.DigestRepository
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import org.koin.core.annotation.Single

@Single(binds = [DigestRepository::class])
class DigestRepositoryImpl(
    private val api: DigestApi,
) : DigestRepository {
    override suspend fun getChannels(): JsonObject {
        val response = api.getChannels()
        return response.data ?: buildJsonObject {}
    }

    override suspend fun subscribe(channelUsername: String): JsonObject {
        val response = api.subscribe(channelUsername)
        return response.data ?: buildJsonObject {}
    }

    override suspend fun unsubscribe(channelUsername: String): JsonObject {
        val response = api.unsubscribe(channelUsername)
        return response.data ?: buildJsonObject {}
    }

    override suspend fun getPreferences(): JsonObject {
        val response = api.getPreferences()
        return response.data ?: buildJsonObject {}
    }

    override suspend fun updatePreferences(
        deliveryTime: String?,
        timezone: String?,
        hoursLookback: Int?,
        maxPostsPerDigest: Int?,
        minRelevanceScore: Double?,
    ): JsonObject {
        val response =
            api.updatePreferences(
                deliveryTime = deliveryTime,
                timezone = timezone,
                hoursLookback = hoursLookback,
                maxPostsPerDigest = maxPostsPerDigest,
                minRelevanceScore = minRelevanceScore,
            )
        return response.data ?: buildJsonObject {}
    }

    override suspend fun getHistory(
        page: Int,
        pageSize: Int,
    ): JsonObject {
        val response = api.getHistory(page, pageSize)
        return response.data ?: buildJsonObject {}
    }

    override suspend fun triggerDigest(): JsonObject {
        val response = api.triggerDigest()
        return response.data ?: buildJsonObject {}
    }
}
