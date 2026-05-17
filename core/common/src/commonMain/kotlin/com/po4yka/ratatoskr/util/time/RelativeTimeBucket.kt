package com.po4yka.ratatoskr.util.time

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

/**
 * Classifies an elapsed [Duration] into a coarse display bucket for the
 * SummaryList / digest / sync-health timestamp columns. The bucket
 * names map onto stable Compose Resources keys at the UI layer so the
 * actual string rendering (and the Russian three-form pluralization
 * via [com.po4yka.ratatoskr.util.plural.RussianPluralRule]) stays out
 * of the atom.
 *
 * Boundary rules (inclusive lower edge, exclusive upper):
 *  - [JustNow]       — `delta < 60s` (and any negative delta, for
 *    server-side clock-skew tolerance)
 *  - [MinutesAgo]    — `60s..1h - 1s`
 *  - [HoursAgo]      — `1h..24h - 1s`
 *  - [DaysAgo]       — `1d..7d - 1s`
 *  - [WeeksAgo]      — `7d..28d - 1s` (1..3 weeks; bucket count rounds
 *    down via integer division)
 *  - [LongAgo]       — `>= 28d`; UI falls back to an absolute date
 *
 * Pure, allocation-free for non-data-class branches.
 */
sealed interface RelativeTimeBucket {
    data object JustNow : RelativeTimeBucket

    data class MinutesAgo(val minutes: Int) : RelativeTimeBucket

    data class HoursAgo(val hours: Int) : RelativeTimeBucket

    data class DaysAgo(val days: Int) : RelativeTimeBucket

    data class WeeksAgo(val weeks: Int) : RelativeTimeBucket

    data object LongAgo : RelativeTimeBucket

    companion object {
        private val LONG_AGO_THRESHOLD = 28.days
        private val WEEK_THRESHOLD = 7.days
        private val DAY_THRESHOLD = 1.days
        private val HOUR_THRESHOLD = 60.minutes
        private val MINUTE_THRESHOLD = 1.minutes

        fun bucketize(delta: Duration): RelativeTimeBucket {
            if (delta < MINUTE_THRESHOLD) return JustNow
            if (delta < HOUR_THRESHOLD) return MinutesAgo(delta.inWholeMinutes.toInt())
            if (delta < DAY_THRESHOLD) return HoursAgo(delta.inWholeHours.toInt())
            if (delta < WEEK_THRESHOLD) return DaysAgo(delta.inWholeDays.toInt())
            if (delta < LONG_AGO_THRESHOLD) return WeeksAgo((delta.inWholeDays / 7L).toInt())
            return LongAgo
        }
    }
}
