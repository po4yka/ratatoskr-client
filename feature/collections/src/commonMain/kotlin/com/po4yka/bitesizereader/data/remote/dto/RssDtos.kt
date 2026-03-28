package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RssFeedSubscriptionDto(
    @SerialName("subscription_id") val subscriptionId: Int,
    @SerialName("feed_id") val feedId: Int,
    @SerialName("feed_title") val feedTitle: String,
    @SerialName("feed_url") val feedUrl: String,
    @SerialName("site_url") val siteUrl: String = "",
    @SerialName("category_name") val categoryName: String = "",
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class RssFeedListResponseDto(
    @SerialName("feeds") val feeds: List<RssFeedSubscriptionDto>,
)

@Serializable
data class RssSubscribeRequestDto(
    @SerialName("url") val url: String,
    @SerialName("category_id") val categoryId: Int? = null,
)

@Serializable
data class RssSubscribeResponseDto(
    @SerialName("subscription_id") val subscriptionId: Int,
    @SerialName("feed_id") val feedId: Int,
    @SerialName("feed_title") val feedTitle: String,
    @SerialName("feed_url") val feedUrl: String,
)

@Serializable
data class RssFeedItemDto(
    @SerialName("id") val id: Int,
    @SerialName("guid") val guid: String,
    @SerialName("title") val title: String,
    @SerialName("url") val url: String,
    @SerialName("author") val author: String = "",
    @SerialName("published_at") val publishedAt: String,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class RssFeedItemsResponseDto(
    @SerialName("feed_id") val feedId: Int,
    @SerialName("items") val items: List<RssFeedItemDto>,
)

@Serializable
data class RssFeedRefreshResponseDto(
    @SerialName("feed_id") val feedId: Int,
    @SerialName("new_items") val newItems: Int,
    @SerialName("not_modified") val notModified: Boolean = false,
)

@Serializable
data class RssDeleteResponseDto(
    @SerialName("deleted") val deleted: Boolean,
    @SerialName("id") val id: Int,
)

@Serializable
data class RssOpmlImportResponseDto(
    @SerialName("imported") val imported: Int,
    @SerialName("errors") val errors: Int,
    @SerialName("total") val total: Int,
)
