package com.po4yka.bitesizereader.domain.model

import kotlin.time.Instant

data class ReadingSession(
    val id: Long,
    val summaryId: String,
    val startedAt: Instant,
    val endedAt: Instant?,
    val durationSec: Int,
)

data class DailyReadingTotal(
    val date: String,
    val totalSec: Int,
)
