package com.po4yka.ratatoskr.feature.summary.domain.usecase

import com.po4yka.ratatoskr.domain.model.ReadFilter
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.presentation.state.DateRange
import com.po4yka.ratatoskr.presentation.state.SearchFilters

/**
 * Re-runs a saved [SearchFilters] + free-text query against the current library to
 * produce the contents of a smart collection. Pure function so the algorithm is the
 * same wherever it gets called from — list rendering, share-target counting, sync
 * preview — and so corner cases stay test-pinned.
 *
 * The matcher does not yet honor [SearchFilters.language] because the domain
 * [Summary] model does not carry a language field today. Add language predicate
 * support when that field lands on the model; until then the field is silently
 * ignored rather than producing empty results.
 */
object SmartCollectionMatcher {
    fun match(
        query: String,
        filters: SearchFilters,
        summaries: List<Summary>,
    ): List<Summary> {
        val normalizedQuery = query.trim().takeIf { it.isNotEmpty() }?.lowercase()
        return summaries.filter { summary ->
            matchesQuery(summary, normalizedQuery) &&
                matchesTags(summary, filters.tags) &&
                matchesReadFilter(summary, filters.readFilter) &&
                matchesDateRange(summary, filters.dateRange)
        }
    }

    private fun matchesQuery(
        summary: Summary,
        normalizedQuery: String?,
    ): Boolean {
        if (normalizedQuery == null) return true
        if (summary.title.lowercase().contains(normalizedQuery)) return true
        if (summary.content.lowercase().contains(normalizedQuery)) return true
        val full = summary.fullContent ?: return false
        return full.lowercase().contains(normalizedQuery)
    }

    private fun matchesTags(
        summary: Summary,
        required: List<String>,
    ): Boolean = required.isEmpty() || summary.tags.containsAll(required)

    private fun matchesReadFilter(
        summary: Summary,
        filter: ReadFilter,
    ): Boolean =
        when (filter) {
            ReadFilter.ALL -> true
            ReadFilter.UNREAD -> !summary.isRead && !summary.isArchived
            ReadFilter.READ -> summary.isRead && !summary.isArchived
            ReadFilter.FAVORITED -> summary.isFavorited
            ReadFilter.ARCHIVED -> summary.isArchived
        }

    private fun matchesDateRange(
        summary: Summary,
        range: DateRange?,
    ): Boolean {
        if (range == null) return true
        val createdMs = summary.createdAt.toEpochMilliseconds()
        val afterStart = range.startDate?.let { createdMs >= it } ?: true
        val beforeEnd = range.endDate?.let { createdMs <= it } ?: true
        return afterStart && beforeEnd
    }
}
