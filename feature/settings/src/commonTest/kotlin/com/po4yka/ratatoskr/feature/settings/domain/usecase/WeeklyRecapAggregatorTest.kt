package com.po4yka.ratatoskr.feature.settings.domain.usecase

import com.po4yka.ratatoskr.feature.settings.domain.usecase.WeeklyRecapAggregator.ReadEvent
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeeklyRecapAggregatorTest {
    private val utc = TimeZone.UTC

    @Test
    fun `empty input yields the empty-state recap, not a zero-count card`() {
        // Spec: empty-state friendly ("Read your first summary to start tracking").
        // The aggregator signals this distinctly via isEmpty so the card renders the
        // empty-state copy instead of "0 summaries, 0 minutes" which is depressing
        // first-run framing.
        val now = at(2026, 5, 17, 12, 0)
        val recap = WeeklyRecapAggregator.aggregate(emptyList(), now = now, timeZone = utc)

        assertTrue(recap.isEmpty)
        assertEquals(0, recap.summaryCount)
        assertEquals(0, recap.totalReadingMinutes)
        assertEquals(emptyList(), recap.topTags)
        assertEquals(0, recap.streakDays)
    }

    @Test
    fun `events older than seven days are excluded from the rollup`() {
        // The card title is literally "THIS WEEK". An event from 8 days ago must
        // not contribute to the total or the top-tag ranking.
        val now = at(2026, 5, 17, 12, 0)
        val events =
            listOf(
                ReadEvent(readAt = at(2026, 5, 9, 12, 0), readingTimeMin = 99, tags = listOf("old")),
                ReadEvent(readAt = at(2026, 5, 10, 12, 0, second = 1), readingTimeMin = 5, tags = listOf("within")),
                ReadEvent(readAt = at(2026, 5, 17, 11, 59), readingTimeMin = 7, tags = listOf("within")),
            )

        val recap = WeeklyRecapAggregator.aggregate(events, now = now, timeZone = utc)

        assertEquals(2, recap.summaryCount, "the 8-days-ago event must drop off")
        assertEquals(12, recap.totalReadingMinutes)
        assertEquals(listOf("within"), recap.topTags.map { it.topic })
    }

    @Test
    fun `cutoff is exactly 7 days before now, inclusive`() {
        // Boundary: an event at exactly (now - 7 days) is in the window. Naive impls
        // commonly use strict-greater-than and silently drop the oldest event.
        val now = at(2026, 5, 17, 12, 0)
        val cutoffEdge = at(2026, 5, 10, 12, 0)
        val events = listOf(ReadEvent(readAt = cutoffEdge, readingTimeMin = 4, tags = listOf("edge")))

        val recap = WeeklyRecapAggregator.aggregate(events, now = now, timeZone = utc)

        assertEquals(1, recap.summaryCount)
    }

    @Test
    fun `top tags are the three most frequent — ties broken by alphabetical for determinism`() {
        // Regression guard: a stable order matters for snapshot tests and for users not
        // seeing the card reshuffle on every recomposition. Frequency desc, then tag
        // ascending is the canonical tie-break.
        val now = at(2026, 5, 17, 12, 0)
        val events =
            listOf(
                event(at(2026, 5, 17, 10, 0), tags = listOf("rust", "infra")),
                event(at(2026, 5, 16, 10, 0), tags = listOf("rust", "ml")),
                event(at(2026, 5, 15, 10, 0), tags = listOf("rust", "ml")),
                event(at(2026, 5, 14, 10, 0), tags = listOf("ml", "infra", "ux")),
                event(at(2026, 5, 13, 10, 0), tags = listOf("ux")),
                event(at(2026, 5, 12, 10, 0), tags = listOf("kafka")),
            )

        val recap = WeeklyRecapAggregator.aggregate(events, now = now, timeZone = utc)

        // Counts: rust=3, ml=3, infra=2, ux=2, kafka=1 → top 3 = rust(3), ml(3), infra(2)
        assertEquals(listOf("ml", "rust", "infra"), recap.topTags.map { it.topic })
        assertEquals(listOf(3, 3, 2), recap.topTags.map { it.count })
    }

    @Test
    fun `streak counts consecutive local days ending today when today has a read`() {
        // Streak 3: today (5/17), yesterday (5/16), day before (5/15). 5/14 absent breaks.
        val now = at(2026, 5, 17, 20, 0)
        val events =
            listOf(
                event(at(2026, 5, 17, 10, 0)),
                event(at(2026, 5, 16, 9, 0)),
                event(at(2026, 5, 15, 18, 0)),
                event(at(2026, 5, 13, 18, 0)), // gap at 5/14 breaks the streak
            )

        val recap = WeeklyRecapAggregator.aggregate(events, now = now, timeZone = utc)

        assertEquals(3, recap.streakDays)
    }

    @Test
    fun `streak counts back from yesterday when today has no read yet — the streak is still live`() {
        // UX intent: opening the card at 09:00 before having read anything today must
        // not say "streak broken". The streak is still alive until end of today.
        val now = at(2026, 5, 17, 9, 0)
        val events =
            listOf(
                event(at(2026, 5, 16, 10, 0)),
                event(at(2026, 5, 15, 10, 0)),
            )

        val recap = WeeklyRecapAggregator.aggregate(events, now = now, timeZone = utc)

        assertEquals(2, recap.streakDays)
    }

    @Test
    fun `streak is zero when the last read was 2+ days ago`() {
        // If yesterday has no read either, the streak is genuinely broken.
        val now = at(2026, 5, 17, 20, 0)
        val events = listOf(event(at(2026, 5, 15, 10, 0)))

        val recap = WeeklyRecapAggregator.aggregate(events, now = now, timeZone = utc)

        assertEquals(0, recap.streakDays)
    }

    @Test
    fun `streak is bounded by the 7-day window — events outside the window do not extend it`() {
        // The recap card scopes to the week. A 100-day streak should report 7, not 100,
        // because the card is about THIS WEEK. Longer-horizon stats live elsewhere.
        val now = at(2026, 5, 17, 20, 0)
        val events = (0..29).map { event(at(2026, 5, 17, 8, 0).minusDays(it.toLong(), utc)) }

        val recap = WeeklyRecapAggregator.aggregate(events, now = now, timeZone = utc)

        assertEquals(7, recap.streakDays)
    }

    private fun at(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int = 0,
    ) = LocalDateTime(LocalDate(year, month, day), LocalTime(hour, minute, second)).toInstant(utc)

    private fun event(
        readAt: kotlin.time.Instant,
        tags: List<String> = emptyList(),
        readingTimeMin: Int = 5,
    ) = ReadEvent(readAt = readAt, readingTimeMin = readingTimeMin, tags = tags)

    private fun kotlin.time.Instant.minusDays(
        days: Long,
        tz: TimeZone,
    ): kotlin.time.Instant = kotlin.time.Instant.fromEpochSeconds(this.epochSeconds - days * 86_400)
}
