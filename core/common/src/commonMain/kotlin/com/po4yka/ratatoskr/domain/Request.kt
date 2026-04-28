package com.po4yka.ratatoskr.domain.model

import kotlin.time.Instant

data class Request(
    val id: String,
    val url: String,
    val status: RequestStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)
