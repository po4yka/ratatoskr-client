package com.po4yka.ratatoskr.data.mappers

import com.po4yka.ratatoskr.api.generated.models.RssFeedItem as GeneratedRssFeedItem
import com.po4yka.ratatoskr.api.generated.models.RssFeedSubscription as GeneratedRssFeedSubscription
import com.po4yka.ratatoskr.domain.model.RssFeedItem
import com.po4yka.ratatoskr.domain.model.RssFeedSubscription

fun GeneratedRssFeedSubscription.toDomain(): RssFeedSubscription =
    RssFeedSubscription(
        subscriptionId = subscriptionId.toInt(),
        feedId = feedId.toInt(),
        feedTitle = feedTitle ?: "",
        feedUrl = feedUrl ?: "",
        siteUrl = siteUrl ?: "",
        categoryName = categoryName ?: "",
        isActive = isActive,
        createdAt = createdAt.toString(),
    )

fun GeneratedRssFeedItem.toDomain(): RssFeedItem =
    RssFeedItem(
        id = id.toInt(),
        guid = guid ?: "",
        title = title ?: "",
        url = url ?: "",
        author = author ?: "",
        publishedAt = publishedAt?.toString() ?: "",
        createdAt = createdAt.toString(),
    )
