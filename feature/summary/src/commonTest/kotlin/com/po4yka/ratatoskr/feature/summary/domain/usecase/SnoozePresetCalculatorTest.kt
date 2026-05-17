package com.po4yka.ratatoskr.feature.summary.domain.usecase

import com.po4yka.ratatoskr.feature.summary.domain.usecase.SnoozePresetCalculator.Preset
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals

class SnoozePresetCalculatorTest {
    private val utc = TimeZone.UTC

    @Test
    fun `tonight resolves to 21_00 local time on the same day when invoked before 21_00`() {
        val now = local(2026, 5, 17, 8, 0).toInstant(utc) // Sunday morning
        val due = SnoozePresetCalculator.due(Preset.Tonight, now = now, timeZone = utc)

        assertEquals(local(2026, 5, 17, 21, 0).toInstant(utc), due)
    }

    @Test
    fun `tonight rolls to tomorrow 21_00 when invoked at or after 21_00`() {
        // Regression guard: tapping Tonight at 23:30 should not immediately re-surface
        // the summary. Apple Mail and Inbox both implement "next 21:00" semantics here.
        val now = local(2026, 5, 17, 23, 30).toInstant(utc)
        val due = SnoozePresetCalculator.due(Preset.Tonight, now = now, timeZone = utc)

        assertEquals(local(2026, 5, 18, 21, 0).toInstant(utc), due)
    }

    @Test
    fun `tonight at exactly 21_00 rolls to tomorrow — boundary is exclusive`() {
        val now = local(2026, 5, 17, 21, 0).toInstant(utc)
        val due = SnoozePresetCalculator.due(Preset.Tonight, now = now, timeZone = utc)

        assertEquals(local(2026, 5, 18, 21, 0).toInstant(utc), due)
    }

    @Test
    fun `tomorrow is always next-day 9_00`() {
        val now = local(2026, 5, 17, 14, 0).toInstant(utc) // Sunday afternoon
        val due = SnoozePresetCalculator.due(Preset.Tomorrow, now = now, timeZone = utc)

        assertEquals(local(2026, 5, 18, 9, 0).toInstant(utc), due)
    }

    @Test
    fun `tomorrow from late evening still resolves to the next calendar day, not the day after`() {
        // Regression guard: a naive "now + 24h" would land at 23:55 the next night.
        // The contract is calendar-tomorrow at 09:00.
        val now = local(2026, 5, 17, 23, 55).toInstant(utc)
        val due = SnoozePresetCalculator.due(Preset.Tomorrow, now = now, timeZone = utc)

        assertEquals(local(2026, 5, 18, 9, 0).toInstant(utc), due)
    }

    @Test
    fun `this-weekend from a weekday resolves to the upcoming Saturday at 9_00`() {
        // 2026-05-13 is a Wednesday → next Saturday is 2026-05-16.
        val now = local(2026, 5, 13, 10, 0).toInstant(utc)
        check(LocalDate(2026, 5, 13).dayOfWeek == DayOfWeek.WEDNESDAY)

        val due = SnoozePresetCalculator.due(Preset.ThisWeekend, now = now, timeZone = utc)

        assertEquals(local(2026, 5, 16, 9, 0).toInstant(utc), due)
    }

    @Test
    fun `this-weekend on Saturday morning before 9_00 uses today, not next Saturday`() {
        // 2026-05-16 is a Saturday. Tapping at 07:00 should snooze to 09:00 same day, not a week out.
        val now = local(2026, 5, 16, 7, 0).toInstant(utc)
        check(LocalDate(2026, 5, 16).dayOfWeek == DayOfWeek.SATURDAY)

        val due = SnoozePresetCalculator.due(Preset.ThisWeekend, now = now, timeZone = utc)

        assertEquals(local(2026, 5, 16, 9, 0).toInstant(utc), due)
    }

    @Test
    fun `this-weekend on Saturday afternoon rolls to next Saturday`() {
        // 2026-05-16 Saturday 15:00 → tapping This Weekend implies "next weekend",
        // because the user has already used most of this Saturday.
        val now = local(2026, 5, 16, 15, 0).toInstant(utc)
        check(LocalDate(2026, 5, 16).dayOfWeek == DayOfWeek.SATURDAY)

        val due = SnoozePresetCalculator.due(Preset.ThisWeekend, now = now, timeZone = utc)

        assertEquals(local(2026, 5, 23, 9, 0).toInstant(utc), due)
    }

    @Test
    fun `this-weekend on Sunday rolls to the following Saturday`() {
        // Sunday is "the weekend" in some places, but the calculator treats Saturday
        // 09:00 as the canonical anchor. Sunday → next Saturday.
        val now = local(2026, 5, 17, 10, 0).toInstant(utc)
        check(LocalDate(2026, 5, 17).dayOfWeek == DayOfWeek.SUNDAY)

        val due = SnoozePresetCalculator.due(Preset.ThisWeekend, now = now, timeZone = utc)

        assertEquals(local(2026, 5, 23, 9, 0).toInstant(utc), due)
    }

    private fun local(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
    ): LocalDateTime = LocalDateTime(LocalDate(year, month, day), LocalTime(hour, minute))
}
