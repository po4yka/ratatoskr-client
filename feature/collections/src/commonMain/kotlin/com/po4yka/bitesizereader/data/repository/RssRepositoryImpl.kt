package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.RssApi
import com.po4yka.bitesizereader.data.remote.dto.RssSubscribeRequestDto
import com.po4yka.bitesizereader.domain.model.RssFeedItem
import com.po4yka.bitesizereader.domain.model.RssFeedSubscription
import com.po4yka.bitesizereader.domain.repository.RssRepository
import org.koin.core.annotation.Single

@Single(binds = [RssRepository::class])
class RssRepositoryImpl(
    private val rssApi: RssApi,
) : RssRepository {
    override suspend fun listFeeds(): List<RssFeedSubscription> {
        val response = rssApi.listFeeds()
        return response.data?.feeds?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun subscribe(
        url: String,
        categoryId: Int?,
    ): RssFeedSubscription {
        val response = rssApi.subscribe(RssSubscribeRequestDto(url = url, categoryId = categoryId))
        val data = requireNotNull(response.data) { "Server returned no data for RSS subscribe" }
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
        rssApi.unsubscribe(subscriptionId)
    }

    override suspend fun listFeedItems(
        feedId: Int,
        limit: Int,
        offset: Int,
    ): List<RssFeedItem> {
        val response = rssApi.listFeedItems(feedId = feedId, limit = limit, offset = offset)
        return response.data?.items?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun refreshFeed(feedId: Int): Int {
        val response = rssApi.refreshFeed(feedId)
        return response.data?.newItems ?: 0
    }
}
