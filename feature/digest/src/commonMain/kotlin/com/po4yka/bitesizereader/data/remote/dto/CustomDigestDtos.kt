package com.po4yka.bitesizereader.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateCustomDigestRequestDto(
    val summaryIds: List<String>,
    val format: String,
    val title: String?,
)

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
    val digests: List<CustomDigestResponseDto>,
    val total: Int,
)
