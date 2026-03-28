package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.RssFeedItem
import com.po4yka.bitesizereader.domain.model.RssFeedSubscription

interface RssRepository {
    suspend fun listFeeds(): List<RssFeedSubscription>

    suspend fun subscribe(
        url: String,
        categoryId: Int? = null,
    ): RssFeedSubscription

    suspend fun unsubscribe(subscriptionId: Int)

    suspend fun listFeedItems(
        feedId: Int,
        limit: Int = 20,
        offset: Int = 0,
    ): List<RssFeedItem>

    suspend fun refreshFeed(feedId: Int): Int
}
