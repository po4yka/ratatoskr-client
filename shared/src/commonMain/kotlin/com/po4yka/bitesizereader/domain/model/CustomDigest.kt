package com.po4yka.bitesizereader.domain.model

import kotlin.time.Instant

data class CustomDigest(
    val id: String,
    val title: String,
    val summaryIds: List<String>,
    val format: DigestFormat,
    val content: String?,
    val status: CustomDigestStatus,
    val createdAt: Instant,
)

enum class DigestFormat { BRIEF, DETAILED }

enum class CustomDigestStatus { PENDING, GENERATING, COMPLETED, FAILED }
