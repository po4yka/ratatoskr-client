package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.RSSApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.V1RssFeedsSubscribeRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.domain.model.RssFeedItem
import com.po4yka.ratatoskr.domain.model.RssFeedSubscription
import com.po4yka.ratatoskr.domain.repository.RssRepository
import org.koin.core.annotation.Single

@Single(binds = [RssRepository::class])
class RssRepositoryImpl : RssRepository {
    override suspend fun listFeeds(): List<RssFeedSubscription> {
        val data = RSSApi.listFeedsV1RssFeedsGet().unwrap().data
        return data?.feeds?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun subscribe(
        url: String,
        categoryId: Int?,
    ): RssFeedSubscription {
        val data =
            RSSApi.subscribeV1RssFeedsSubscribePost(
                body =
                    V1RssFeedsSubscribeRequest(
                        url = url,
                        categoryId = categoryId?.toLong(),
                    ),
            ).unwrap().data
        val response = requireNotNull(data) { "Server returned no data for RSS subscribe" }
        return RssFeedSubscription(
            subscriptionId = response.subscriptionId.toInt(),
            feedId = response.feedId.toInt(),
            feedTitle = response.feedTitle ?: "",
            feedUrl = response.feedUrl ?: "",
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
        val data =
            RSSApi.listFeedItemsV1RssFeedsFeedIdItemsGet(
                feedId = feedId.toLong(),
                limit = limit.toLong(),
                offset = offset.toLong(),
            ).unwrap().data
        return data?.items?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun refreshFeed(feedId: Int): Int {
        val data =
            RSSApi.refreshFeedV1RssFeedsFeedIdRefreshPost(feedId = feedId.toLong())
                .unwrap()
                .data
        return data?.newItems?.toInt() ?: 0
    }
}
