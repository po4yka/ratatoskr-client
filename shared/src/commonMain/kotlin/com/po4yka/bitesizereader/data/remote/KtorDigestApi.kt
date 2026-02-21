package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.DigestChannelRequestDto
import com.po4yka.bitesizereader.data.remote.dto.DigestResponseData
import com.po4yka.bitesizereader.data.remote.dto.UpdateDigestPreferencesRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single(binds = [DigestApi::class])
class KtorDigestApi(private val client: HttpClient) : DigestApi {
    override suspend fun getChannels(): ApiResponseDto<DigestResponseData> {
        return client.get("v1/digest/channels").body()
    }

    override suspend fun subscribe(channelUsername: String): ApiResponseDto<DigestResponseData> {
        return client.post("v1/digest/channels/subscribe") {
            contentType(ContentType.Application.Json)
            setBody(DigestChannelRequestDto(channelUsername))
        }.body()
    }

    override suspend fun unsubscribe(channelUsername: String): ApiResponseDto<DigestResponseData> {
        return client.post("v1/digest/channels/unsubscribe") {
            contentType(ContentType.Application.Json)
            setBody(DigestChannelRequestDto(channelUsername))
        }.body()
    }

    override suspend fun getPreferences(): ApiResponseDto<DigestResponseData> {
        return client.get("v1/digest/preferences").body()
    }

    override suspend fun updatePreferences(
        deliveryTime: String?,
        timezone: String?,
        hoursLookback: Int?,
        maxPostsPerDigest: Int?,
        minRelevanceScore: Double?,
    ): ApiResponseDto<DigestResponseData> {
        return client.patch("v1/digest/preferences") {
            contentType(ContentType.Application.Json)
            setBody(
                UpdateDigestPreferencesRequestDto(
                    deliveryTime = deliveryTime,
                    timezone = timezone,
                    hoursLookback = hoursLookback,
                    maxPostsPerDigest = maxPostsPerDigest,
                    minRelevanceScore = minRelevanceScore,
                ),
            )
        }.body()
    }

    override suspend fun getHistory(
        page: Int,
        pageSize: Int,
    ): ApiResponseDto<DigestResponseData> {
        val offset = (page.coerceAtLeast(1) - 1) * pageSize
        return client.get("v1/digest/history") {
            parameter("limit", pageSize)
            parameter("offset", offset)
        }.body()
    }

    override suspend fun triggerDigest(): ApiResponseDto<DigestResponseData> {
        return client.post("v1/digest/trigger").body()
    }
}
