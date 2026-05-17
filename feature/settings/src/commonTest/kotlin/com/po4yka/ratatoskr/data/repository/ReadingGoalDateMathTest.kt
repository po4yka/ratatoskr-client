package com.po4yka.ratatoskr.data.repository

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

/**
 * Smoke tests for the date-math behaviour ReadingGoalRepositoryImpl relies on
 * after migrating from hand-rolled Gregorian helpers to kotlinx-datetime.
 *
 * These cases are the ones the previous helpers tried — and sometimes failed —
 * to handle: leap-year boundaries, the year-2100 non-leap century, large
 * day-count deltas, and far-future dates that would have overflowed the
 * Long → Int cast in the original epochDaysToDateString helper.
 */
class ReadingGoalDateMathTest {
    @Test
    fun crossesLeapYearBoundaryWhenSubtractingOneDayFromMarchFirst() {
        val leapMar1 = LocalDate.parse("2024-03-01").minus(1, DateTimeUnit.DAY).toString()
        assertEquals("2024-02-29", leapMar1)

        val centuryMar1 = LocalDate.parse("2100-03-01").minus(1, DateTimeUnit.DAY).toString()
        assertEquals("2100-02-28", centuryMar1, "2100 is divisible by 100 but not 400 → not a leap year")
    }

    @Test
    fun thousandDayDeltaLandsOnExpectedDate() {
        val far = LocalDate.parse("2026-05-17").minus(1_000, DateTimeUnit.DAY).toString()
        assertEquals("2023-08-21", far)
    }

    @Test
    fun yearThreeThousandStillRoundtripsWithoutOverflow() {
        val future = LocalDate.parse("3000-01-01").minus(1, DateTimeUnit.DAY).toString()
        assertEquals("2999-12-31", future)
    }
}
