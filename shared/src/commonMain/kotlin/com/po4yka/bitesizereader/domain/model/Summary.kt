package com.po4yka.bitesizereader.domain.model

import kotlin.time.Instant

data class Summary(
    val id: String,
    val title: String,
    val content: String,
    val sourceUrl: String,
    val imageUrl: String?,
    val createdAt: Instant,
    val isRead: Boolean,
    val tags: List<String>,
    val readingTimeMin: Int? = null,
)
