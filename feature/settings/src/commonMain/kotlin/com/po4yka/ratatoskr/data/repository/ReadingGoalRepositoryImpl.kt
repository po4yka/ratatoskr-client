package com.po4yka.ratatoskr.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.domain.model.DailyReadingTotal
import com.po4yka.ratatoskr.domain.model.ReadingGoal
import com.po4yka.ratatoskr.domain.repository.ReadingGoalRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import org.koin.core.annotation.Single

@Single(binds = [ReadingGoalRepository::class])
class ReadingGoalRepositoryImpl(
    private val database: Database,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ReadingGoalRepository {
    override fun getGoal(): Flow<ReadingGoal> =
        database.databaseQueries.getReadingGoal()
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
            .map { entity ->
                entity?.toDomain() ?: ReadingGoal(
                    dailyTargetMin = 15,
                    currentStreakDays = 0,
                    longestStreakDays = 0,
                    lastCompletedDate = null,
                    isEnabled = false,
                )
            }

    override suspend fun updateDailyTarget(minutes: Int) {
        withContext(ioDispatcher) {
            val current = database.databaseQueries.getReadingGoal().executeAsOneOrNull()
            database.databaseQueries.upsertReadingGoal(
                dailyTargetMin = minutes,
                currentStreakDays = current?.currentStreakDays ?: 0,
                longestStreakDays = current?.longestStreakDays ?: 0,
                lastCompletedDate = current?.lastCompletedDate,
                isEnabled = current?.isEnabled ?: false,
            )
        }
    }

    override suspend fun setEnabled(enabled: Boolean) {
        withContext(ioDispatcher) {
            val current = database.databaseQueries.getReadingGoal().executeAsOneOrNull()
            database.databaseQueries.upsertReadingGoal(
                dailyTargetMin = current?.dailyTargetMin ?: 15,
                currentStreakDays = current?.currentStreakDays ?: 0,
                longestStreakDays = current?.longestStreakDays ?: 0,
                lastCompletedDate = current?.lastCompletedDate,
                isEnabled = enabled,
            )
        }
    }

    override suspend fun recalculateStreak(dailyTotals: List<DailyReadingTotal>) {
        withContext(ioDispatcher) {
            val goal =
                database.databaseQueries.getReadingGoal().executeAsOneOrNull()
                    ?: return@withContext
            val targetSec = goal.dailyTargetMin * 60
            val totalsMap = dailyTotals.associate { it.date to it.totalSec }
            val today = getCurrentDateString()

            val todayDone = (totalsMap[today] ?: 0) >= targetSec

            // Walk backwards to count streak
            var streak = if (todayDone) 1 else 0
            var checkDate = minusDays(today, 1)
            while (true) {
                val dayTotal = totalsMap[checkDate] ?: 0
                if (dayTotal >= targetSec) {
                    streak++
                    checkDate = minusDays(checkDate, 1)
                } else {
                    break
                }
            }

            // lastCompletedDate = most recent date where target was met
            val lastCompleted: String? =
                if (todayDone) {
                    today
                } else {
                    dailyTotals.sortedByDescending { it.date }
                        .firstOrNull { it.totalSec >= targetSec }?.date
                }

            val newLongest = maxOf(goal.longestStreakDays, streak)
            database.databaseQueries.updateStreak(
                currentStreakDays = streak,
                longestStreakDays = newLongest,
                lastCompletedDate = lastCompleted,
            )
        }
    }

    private fun getCurrentDateString(): String {
        val now = Clock.System.now()
        val epochMs = now.toEpochMilliseconds()
        val dayMs = 24L * 60 * 60 * 1000
        val daysSinceEpoch = epochMs / dayMs
        return epochDaysToDateString(daysSinceEpoch)
    }

    // Subtract n days from a "YYYY-MM-DD" string.
    private fun minusDays(
        dateStr: String,
        days: Int,
    ): String {
        val parts = dateStr.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()
        val daysSinceEpoch = dateToEpochDays(year, month, day) - days
        return epochDaysToDateString(daysSinceEpoch.toLong())
    }

    // Compute days since epoch (1970-01-01) for a given date using Gregorian calendar arithmetic.
    private fun dateToEpochDays(
        year: Int,
        month: Int,
        day: Int,
    ): Int {
        // Days in each month (non-leap year)
        val daysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var days = (year - 1970) * 365
        // Leap year corrections: count leap years from 1970 up to (but not including) year
        val leapsFrom1970 = leapYearsBefore(year) - leapYearsBefore(1970)
        days += leapsFrom1970
        // Add days for full months in current year
        for (m in 1 until month) {
            days += daysInMonth[m - 1]
            if (m == 2 && isLeapYear(year)) days++
        }
        days += day - 1
        return days
    }

    private fun epochDaysToDateString(epochDays: Long): String {
        // Convert epoch days to a calendar date
        var remaining = epochDays.toInt()
        var year = 1970
        while (true) {
            val daysInYear = if (isLeapYear(year)) 366 else 365
            if (remaining < daysInYear) break
            remaining -= daysInYear
            year++
        }
        val daysInMonth = intArrayOf(31, if (isLeapYear(year)) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var month = 1
        for (m in daysInMonth) {
            if (remaining < m) break
            remaining -= m
            month++
        }
        val day = remaining + 1
        val y = year.toString().padStart(4, '0')
        val m = month.toString().padStart(2, '0')
        val d = day.toString().padStart(2, '0')
        return "$y-$m-$d"
    }

    private fun isLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

    // Count leap years before the given year (i.e., up to year-1).
    private fun leapYearsBefore(year: Int): Int {
        val y = year - 1
        return y / 4 - y / 100 + y / 400
    }
}

private fun com.po4yka.ratatoskr.database.ReadingGoalEntity.toDomain(): ReadingGoal =
    ReadingGoal(
        dailyTargetMin = dailyTargetMin,
        currentStreakDays = currentStreakDays,
        longestStreakDays = longestStreakDays,
        lastCompletedDate = lastCompletedDate,
        isEnabled = isEnabled,
    )
