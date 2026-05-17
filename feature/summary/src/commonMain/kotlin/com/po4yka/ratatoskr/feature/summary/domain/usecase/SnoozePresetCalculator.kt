package com.po4yka.ratatoskr.feature.summary.domain.usecase

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Date math for the three swipe-action snooze presets. The Pick… preset is not modeled
 * here — callers pass an arbitrary [Instant] directly when the user uses the picker.
 *
 * Anchors are deliberately conservative so a tap never re-surfaces the summary before
 * the user can reasonably expect it: Tonight is *next* 21:00 (today if still before;
 * else tomorrow), Tomorrow is calendar-next-day 09:00, This Weekend is Saturday 09:00
 * (today if Saturday before 09:00, else the next Saturday).
 */
object SnoozePresetCalculator {
    private val TONIGHT_TIME = LocalTime(21, 0)
    private val MORNING_TIME = LocalTime(9, 0)

    enum class Preset { Tonight, Tomorrow, ThisWeekend }

    fun due(
        preset: Preset,
        now: Instant,
        timeZone: TimeZone,
    ): Instant {
        val nowLocal = now.toLocalDateTime(timeZone)
        return when (preset) {
            Preset.Tonight -> nextOccurrenceOf(TONIGHT_TIME, nowLocal, timeZone)
            Preset.Tomorrow -> tomorrowAt(MORNING_TIME, nowLocal, timeZone)
            Preset.ThisWeekend -> nextSaturdayAt(MORNING_TIME, nowLocal, timeZone)
        }
    }

    private fun nextOccurrenceOf(
        time: LocalTime,
        nowLocal: LocalDateTime,
        tz: TimeZone,
    ): Instant {
        val anchorToday = LocalDateTime(nowLocal.date, time)
        val anchorDate = if (nowLocal < anchorToday) nowLocal.date else nowLocal.date.plus(1, DateTimeUnit.DAY)
        return LocalDateTime(anchorDate, time).toInstant(tz)
    }

    private fun tomorrowAt(
        time: LocalTime,
        nowLocal: LocalDateTime,
        tz: TimeZone,
    ): Instant = LocalDateTime(nowLocal.date.plus(1, DateTimeUnit.DAY), time).toInstant(tz)

    private fun nextSaturdayAt(
        time: LocalTime,
        nowLocal: LocalDateTime,
        tz: TimeZone,
    ): Instant {
        val today = nowLocal.date
        val candidateToday = LocalDateTime(today, time)
        val isSaturdayBeforeAnchor = today.dayOfWeek == DayOfWeek.SATURDAY && nowLocal < candidateToday
        val anchorDate = if (isSaturdayBeforeAnchor) today else today.nextSaturdayStrictlyAfter()
        return LocalDateTime(anchorDate, time).toInstant(tz)
    }

    private fun LocalDate.nextSaturdayStrictlyAfter(): LocalDate {
        val daysFromSaturday = ((DayOfWeek.SATURDAY.ordinal - this.dayOfWeek.ordinal) + 7) % 7
        val step = if (daysFromSaturday == 0) 7 else daysFromSaturday
        return this.plus(step, DateTimeUnit.DAY)
    }
}
