package com.po4yka.bitesizereader.domain.model

data class RssFeedSubscription(
    val subscriptionId: Int,
    val feedId: Int,
    val feedTitle: String,
    val feedUrl: String,
    val siteUrl: String,
    val categoryName: String,
    val isActive: Boolean,
    val createdAt: String,
)

data class RssFeedItem(
    val id: Int,
    val guid: String,
    val title: String,
    val url: String,
    val author: String,
    val publishedAt: String,
    val createdAt: String,
)
