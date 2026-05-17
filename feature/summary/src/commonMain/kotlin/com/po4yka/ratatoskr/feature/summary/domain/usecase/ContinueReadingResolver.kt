package com.po4yka.ratatoskr.feature.summary.domain.usecase

import com.po4yka.ratatoskr.domain.model.Summary
import kotlin.time.Instant

/**
 * Pure ranking algorithm behind the "Continue" rail at the top of `SummaryListScreen`.
 *
 * The rail surfaces summaries the user has started reading but not finished, ordered by
 * recency, capped at [MAX_ITEMS]. Empty input → empty output, so the screen can render
 * `if (shelf.isEmpty()) return` without an awkward placeholder card.
 *
 * The recency extractor is injectable because the `Summary` model does not yet have a
 * `lastReadAt` field; today the default uses `createdAt` as a proxy. When the schema
 * grows that field, callers can pass `it.lastReadAt` without touching this algorithm.
 */
object ContinueReadingResolver {
    const val MAX_ITEMS: Int = 8

    fun resolve(
        items: List<Summary>,
        recencyOf: (Summary) -> Instant = Summary::createdAt,
    ): List<Summary> =
        items
            .asSequence()
            .filter { it.lastReadPosition > 0 && !it.isRead && !it.isArchived }
            .sortedByDescending(recencyOf)
            .take(MAX_ITEMS)
            .toList()
}
