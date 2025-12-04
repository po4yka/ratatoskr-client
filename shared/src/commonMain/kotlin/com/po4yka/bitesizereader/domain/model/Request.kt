package com.po4yka.bitesizereader.domain.model

import kotlinx.datetime.Instant

data class Request(
    val id: String,
    val url: String,
    val status: RequestStatus,
    val createdAt: Instant,
    val updatedAt: Instant
)