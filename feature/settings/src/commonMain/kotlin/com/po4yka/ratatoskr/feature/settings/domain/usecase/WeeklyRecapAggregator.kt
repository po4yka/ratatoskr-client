package com.po4yka.ratatoskr.feature.settings.domain.usecase

import com.po4yka.ratatoskr.domain.model.TopicStat
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

/**
 * Pure rollup behind the "THIS WEEK" Frost card on StatsScreen. Takes a list of read
 * events captured over the last week (or longer — events outside the 7-day window
 * are dropped) and produces the headline numbers the card renders plus the headline
 * the Sunday recap notification fires with.
 *
 * The 7-day window is computed against [now] inclusive of the boundary instant
 * (now - 7d). The streak is bounded by the same window: this card is about THIS
 * WEEK; longer-horizon streaks belong on a different surface.
 */
object WeeklyRecapAggregator {
    private const val WINDOW_DAYS: Int = 7
    private const val TOP_TAGS: Int = 3

    data class ReadEvent(
        val readAt: Instant,
        val readingTimeMin: Int,
        val tags: List<String>,
    )

    data class Recap(
        val summaryCount: Int,
        val totalReadingMinutes: Int,
        val topTags: List<TopicStat>,
        val streakDays: Int,
        val isEmpty: Boolean,
    ) {
        companion object {
            val Empty = Recap(0, 0, emptyList(), 0, isEmpty = true)
        }
    }

    fun aggregate(
        events: List<ReadEvent>,
        now: Instant,
        timeZone: TimeZone,
    ): Recap {
        if (events.isEmpty()) return Recap.Empty
        val cutoff = now.minus(WINDOW_DAYS.days)
        val recent = events.filter { it.readAt >= cutoff }
        if (recent.isEmpty()) return Recap.Empty
        return Recap(
            summaryCount = recent.size,
            totalReadingMinutes = recent.sumOf { it.readingTimeMin },
            topTags = topTagsOf(recent),
            streakDays = streakOf(recent, now, timeZone),
            isEmpty = false,
        )
    }

    private fun topTagsOf(events: List<ReadEvent>): List<TopicStat> =
        events.flatMap { it.tags }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .take(TOP_TAGS)
            .map { TopicStat(topic = it.key, count = it.value) }

    private fun streakOf(
        events: List<ReadEvent>,
        now: Instant,
        tz: TimeZone,
    ): Int {
        val daysWithReads = events.asSequence().map { it.readAt.toLocalDateTime(tz).date }.toSet()
        val today = now.toLocalDateTime(tz).date
        val anchor = if (today in daysWithReads) today else today.minus(1, DateTimeUnit.DAY)
        if (anchor !in daysWithReads) return 0
        var streak = 0
        var cursor: LocalDate = anchor
        while (cursor in daysWithReads && streak < WINDOW_DAYS) {
            streak += 1
            cursor = cursor.minus(1, DateTimeUnit.DAY)
        }
        return streak
    }
}
