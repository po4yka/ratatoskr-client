package com.po4yka.ratatoskr.util.time

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class RelativeTimeBucketTest {
    @Test
    fun `JustNow for deltas under one minute`() {
        // The smallest visible bucket — anything fresher than a minute
        // reads as "just now" in the summary list timestamp column.
        assertEquals(RelativeTimeBucket.JustNow, RelativeTimeBucket.bucketize(0.seconds))
        assertEquals(RelativeTimeBucket.JustNow, RelativeTimeBucket.bucketize(1.seconds))
        assertEquals(RelativeTimeBucket.JustNow, RelativeTimeBucket.bucketize(59.seconds))
    }

    @Test
    fun `JustNow for negative deltas — clock-skew tolerance`() {
        // Server timestamps may legitimately arrive slightly ahead of
        // local clock. The same tolerance the widget cadence atom uses
        // for "RecentlyRefreshed" applies here — pin the bucket so a
        // few seconds of drift doesn't render as "29 minutes ago"
        // (the sign-flipped minute count).
        assertEquals(RelativeTimeBucket.JustNow, RelativeTimeBucket.bucketize((-3).seconds))
        assertEquals(RelativeTimeBucket.JustNow, RelativeTimeBucket.bucketize((-5).minutes))
    }

    @Test
    fun `MinutesAgo bucket between exactly 1 minute and 59 minutes`() {
        // 60s exactly is the lower boundary — the bucket-up edge case.
        // 59m59s rounds down to MinutesAgo(59), the upper edge.
        assertEquals(RelativeTimeBucket.MinutesAgo(1), RelativeTimeBucket.bucketize(60.seconds))
        assertEquals(RelativeTimeBucket.MinutesAgo(5), RelativeTimeBucket.bucketize(5.minutes))
        assertEquals(RelativeTimeBucket.MinutesAgo(59), RelativeTimeBucket.bucketize(59.minutes))
        assertEquals(
            RelativeTimeBucket.MinutesAgo(59),
            RelativeTimeBucket.bucketize(59.minutes + 59.seconds),
        )
    }

    @Test
    fun `HoursAgo bucket between exactly 1 hour and 23 hours`() {
        assertEquals(RelativeTimeBucket.HoursAgo(1), RelativeTimeBucket.bucketize(60.minutes))
        assertEquals(RelativeTimeBucket.HoursAgo(1), RelativeTimeBucket.bucketize(1.hours))
        assertEquals(RelativeTimeBucket.HoursAgo(12), RelativeTimeBucket.bucketize(12.hours))
        assertEquals(RelativeTimeBucket.HoursAgo(23), RelativeTimeBucket.bucketize(23.hours))
        assertEquals(
            RelativeTimeBucket.HoursAgo(23),
            RelativeTimeBucket.bucketize(23.hours + 59.minutes),
        )
    }

    @Test
    fun `DaysAgo bucket between 1 day and 6 days`() {
        // 1 day = 24 hours boundary. 6d23h still reads as DaysAgo(6) —
        // the next tick rolls into WeeksAgo(1).
        assertEquals(RelativeTimeBucket.DaysAgo(1), RelativeTimeBucket.bucketize(24.hours))
        assertEquals(RelativeTimeBucket.DaysAgo(1), RelativeTimeBucket.bucketize(1.days))
        assertEquals(RelativeTimeBucket.DaysAgo(3), RelativeTimeBucket.bucketize(3.days))
        assertEquals(RelativeTimeBucket.DaysAgo(6), RelativeTimeBucket.bucketize(6.days))
        assertEquals(
            RelativeTimeBucket.DaysAgo(6),
            RelativeTimeBucket.bucketize(6.days + 23.hours),
        )
    }

    @Test
    fun `WeeksAgo bucket between 1 week and 3 weeks`() {
        // 7 days = 1 week boundary; 3 weeks 6 days = WeeksAgo(3),
        // 4 weeks tips into LongAgo.
        assertEquals(RelativeTimeBucket.WeeksAgo(1), RelativeTimeBucket.bucketize(7.days))
        assertEquals(RelativeTimeBucket.WeeksAgo(2), RelativeTimeBucket.bucketize(14.days))
        assertEquals(RelativeTimeBucket.WeeksAgo(3), RelativeTimeBucket.bucketize(21.days))
        assertEquals(RelativeTimeBucket.WeeksAgo(3), RelativeTimeBucket.bucketize(27.days))
    }

    @Test
    fun `LongAgo for deltas of 4 weeks or more`() {
        // 28 days is the inclusive cutoff. Beyond a month, UI falls back
        // to an absolute date string rendered by the caller — the bucket
        // signals only that the absolute form is appropriate.
        assertEquals(RelativeTimeBucket.LongAgo, RelativeTimeBucket.bucketize(28.days))
        assertEquals(RelativeTimeBucket.LongAgo, RelativeTimeBucket.bucketize(60.days))
        assertEquals(RelativeTimeBucket.LongAgo, RelativeTimeBucket.bucketize(365.days))
    }

    @Test
    fun `data classes carry their integer count for plural-form lookup`() {
        // The caller hands the int directly to RussianPluralRule.select(n)
        // or to `pluralStringResource`. Verifying the count value pins
        // the integration contract.
        val m = RelativeTimeBucket.bucketize(7.minutes) as RelativeTimeBucket.MinutesAgo
        assertEquals(7, m.minutes)
        val h = RelativeTimeBucket.bucketize(4.hours) as RelativeTimeBucket.HoursAgo
        assertEquals(4, h.hours)
        val d = RelativeTimeBucket.bucketize(2.days) as RelativeTimeBucket.DaysAgo
        assertEquals(2, d.days)
        val w = RelativeTimeBucket.bucketize(14.days) as RelativeTimeBucket.WeeksAgo
        assertEquals(2, w.weeks)
    }
}
