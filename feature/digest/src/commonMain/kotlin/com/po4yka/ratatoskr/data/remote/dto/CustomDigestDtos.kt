package com.po4yka.ratatoskr.data.remote.dto

import kotlinx.serialization.Serializable

// Response DTOs.
//
// The OpenAPI spec declares custom-digest endpoint responses as bare `type: object`,
// so the generated client emits `JsonElement`. These DTOs describe the actual
// wire shape (carried over from the previous hand-written client) and are
// decoded from the generated envelopes in the repository.

@Serializable
data class CustomDigestResponseDto(
    val id: String,
    val title: String,
    val content: String? = null,
    val status: String,
    val createdAt: String,
)

@Serializable
data class CustomDigestListResponseDto(
    val digests: List<CustomDigestResponseDto> = emptyList(),
    val total: Int = 0,
)
