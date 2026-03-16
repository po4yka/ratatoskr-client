package com.po4yka.bitesizereader.data.remote

import com.po4yka.bitesizereader.data.remote.dto.ApiResponseDto
import com.po4yka.bitesizereader.data.remote.dto.BulkUnsubscribeRequestDto
import com.po4yka.bitesizereader.data.remote.dto.CategoryResponseDto
import com.po4yka.bitesizereader.data.remote.dto.ChannelListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.CreateCategoryRequestDto
import com.po4yka.bitesizereader.data.remote.dto.DigestChannelRequestDto
import com.po4yka.bitesizereader.data.remote.dto.DigestDeliveryListResponseDto
import com.po4yka.bitesizereader.data.remote.dto.DigestPreferenceResponseDto
import com.po4yka.bitesizereader.data.remote.dto.ResolveChannelRequestDto
import com.po4yka.bitesizereader.data.remote.dto.ResolveChannelResponseDto
import com.po4yka.bitesizereader.data.remote.dto.SuccessResponse
import com.po4yka.bitesizereader.data.remote.dto.TriggerChannelRequestDto
import com.po4yka.bitesizereader.data.remote.dto.TriggerDigestResponseDto
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
    override suspend fun getChannels(): ApiResponseDto<ChannelListResponseDto> {
        return client.get("v1/digest/channels").body()
    }

    override suspend fun subscribe(channelUsername: String): ApiResponseDto<ChannelListResponseDto> {
        return client.post("v1/digest/channels/subscribe") {
            contentType(ContentType.Application.Json)
            setBody(DigestChannelRequestDto(channelUsername))
        }.body()
    }

    override suspend fun unsubscribe(channelUsername: String): ApiResponseDto<ChannelListResponseDto> {
        return client.post("v1/digest/channels/unsubscribe") {
            contentType(ContentType.Application.Json)
            setBody(DigestChannelRequestDto(channelUsername))
        }.body()
    }

    override suspend fun resolveChannel(channelUsername: String): ApiResponseDto<ResolveChannelResponseDto> {
        return client.post("v1/digest/channels/resolve") {
            contentType(ContentType.Application.Json)
            setBody(ResolveChannelRequestDto(channelUsername))
        }.body()
    }

    override suspend fun getPreferences(): ApiResponseDto<DigestPreferenceResponseDto> {
        return client.get("v1/digest/preferences").body()
    }

    override suspend fun updatePreferences(
        deliveryTime: String?,
        timezone: String?,
        hoursLookback: Int?,
        maxPostsPerDigest: Int?,
        minRelevanceScore: Double?,
    ): ApiResponseDto<DigestPreferenceResponseDto> {
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
    ): ApiResponseDto<DigestDeliveryListResponseDto> {
        val offset = (page.coerceAtLeast(1) - 1) * pageSize
        return client.get("v1/digest/history") {
            parameter("limit", pageSize)
            parameter("offset", offset)
        }.body()
    }

    override suspend fun triggerDigest(): ApiResponseDto<TriggerDigestResponseDto> {
        return client.post("v1/digest/trigger").body()
    }

    override suspend fun triggerChannel(channelUsername: String): ApiResponseDto<TriggerDigestResponseDto> {
        return client.post("v1/digest/trigger-channel") {
            contentType(ContentType.Application.Json)
            setBody(TriggerChannelRequestDto(channelUsername))
        }.body()
    }

    override suspend fun createCategory(name: String): ApiResponseDto<CategoryResponseDto> {
        return client.post("v1/digest/categories") {
            contentType(ContentType.Application.Json)
            setBody(CreateCategoryRequestDto(name))
        }.body()
    }

    override suspend fun bulkUnsubscribe(channelUsernames: List<String>): ApiResponseDto<SuccessResponse> {
        return client.post("v1/digest/bulk-unsubscribe") {
            contentType(ContentType.Application.Json)
            setBody(BulkUnsubscribeRequestDto(channelUsernames))
        }.body()
    }
}
