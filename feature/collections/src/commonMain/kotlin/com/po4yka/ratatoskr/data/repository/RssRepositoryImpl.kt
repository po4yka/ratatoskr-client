package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.Api
import com.po4yka.ratatoskr.api.generated.api.RSSApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.V1RssFeedsSubscribeRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.dto.RssFeedItemsResponseDto
import com.po4yka.ratatoskr.data.remote.dto.RssFeedListResponseDto
import com.po4yka.ratatoskr.data.remote.dto.RssFeedRefreshResponseDto
import com.po4yka.ratatoskr.data.remote.dto.RssSubscribeResponseDto
import com.po4yka.ratatoskr.domain.model.RssFeedItem
import com.po4yka.ratatoskr.domain.model.RssFeedSubscription
import com.po4yka.ratatoskr.domain.repository.RssRepository
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.koin.core.annotation.Single

@Single(binds = [RssRepository::class])
class RssRepositoryImpl : RssRepository {
    override suspend fun listFeeds(): List<RssFeedSubscription> {
        val envelope =
            RSSApi.listFeedsV1RssFeedsGet().unwrap().decodeEnvelope<RssFeedListResponseDto>()
        return envelope?.feeds?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun subscribe(
        url: String,
        categoryId: Int?,
    ): RssFeedSubscription {
        val response =
            RSSApi.subscribeV1RssFeedsSubscribePost(
                body =
                    V1RssFeedsSubscribeRequest(
                        url = url,
                        categoryId = categoryId?.toLong(),
                    ),
            ).unwrap().decodeEnvelope<RssSubscribeResponseDto>()
        val data = requireNotNull(response) { "Server returned no data for RSS subscribe" }
        return RssFeedSubscription(
            subscriptionId = data.subscriptionId,
            feedId = data.feedId,
            feedTitle = data.feedTitle,
            feedUrl = data.feedUrl,
            siteUrl = "",
            categoryName = "",
            isActive = true,
            createdAt = "",
        )
    }

    override suspend fun unsubscribe(subscriptionId: Int) {
        RSSApi.unsubscribeV1RssFeedsSubscriptionIdDelete(
            subscriptionId = subscriptionId.toLong(),
        ).unwrap()
    }

    override suspend fun listFeedItems(
        feedId: Int,
        limit: Int,
        offset: Int,
    ): List<RssFeedItem> {
        val envelope =
            RSSApi.listFeedItemsV1RssFeedsFeedIdItemsGet(
                feedId = feedId.toLong(),
                limit = limit.toLong(),
                offset = offset.toLong(),
            ).unwrap().decodeEnvelope<RssFeedItemsResponseDto>()
        return envelope?.items?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun refreshFeed(feedId: Int): Int {
        val response =
            RSSApi.refreshFeedV1RssFeedsFeedIdRefreshPost(feedId = feedId.toLong())
                .unwrap()
                .decodeEnvelope<RssFeedRefreshResponseDto>()
        return response?.newItems ?: 0
    }
}

private inline fun <reified T> JsonElement.decodeEnvelope(): T? {
    val obj = (this as? JsonObject) ?: return null
    val data = obj["data"] ?: return null
    if (data is kotlinx.serialization.json.JsonNull) return null
    return Api.json.decodeFromJsonElement(
        kotlinx.serialization.serializer<T>(),
        data,
    )
}
