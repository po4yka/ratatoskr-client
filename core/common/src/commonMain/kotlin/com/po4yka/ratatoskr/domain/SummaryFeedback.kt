package com.po4yka.ratatoskr.domain.model

import kotlin.time.Instant

data class SummaryFeedback(
    val summaryId: String,
    val rating: FeedbackRating,
    val issues: List<FeedbackIssue>,
    val comment: String?,
    val createdAt: Instant,
)

enum class FeedbackRating { UP, DOWN }

enum class FeedbackIssue { TOO_SHORT, INACCURATE, MISSING_CONTEXT, OUTDATED, POORLY_WRITTEN, OTHER }
