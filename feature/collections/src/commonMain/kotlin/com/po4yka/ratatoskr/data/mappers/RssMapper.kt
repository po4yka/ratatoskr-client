package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.data.remote.dto.RssFeedItemDto
import com.po4yka.ratatoskr.data.remote.dto.RssFeedSubscriptionDto
import com.po4yka.ratatoskr.domain.model.RssFeedItem
import com.po4yka.ratatoskr.domain.model.RssFeedSubscription

fun RssFeedSubscriptionDto.toDomain(): RssFeedSubscription =
    RssFeedSubscription(
        subscriptionId = subscriptionId,
        feedId = feedId,
        feedTitle = feedTitle,
        feedUrl = feedUrl,
        siteUrl = siteUrl,
        categoryName = categoryName,
        isActive = isActive,
        createdAt = createdAt,
    )

fun RssFeedItemDto.toDomain(): RssFeedItem =
    RssFeedItem(
        id = id,
        guid = guid,
        title = title,
        url = url,
        author = author,
        publishedAt = publishedAt,
        createdAt = createdAt,
    )
