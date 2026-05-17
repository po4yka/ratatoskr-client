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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
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

    // All date strings are ISO-8601 `yyyy-MM-dd` in UTC. The previous hand-rolled
    // helpers also implicitly used UTC (epoch milliseconds divided by 24h); this
    // implementation preserves that and documents it. Daily totals are stored
    // under the same UTC date strings, so callers do not see a behaviour change.
    private fun getCurrentDateString(): String = Clock.System.now().toLocalDateTime(TimeZone.UTC).date.toString()

    // Subtract n days from a "YYYY-MM-DD" string.
    private fun minusDays(
        dateStr: String,
        days: Int,
    ): String = LocalDate.parse(dateStr).minus(days, DateTimeUnit.DAY).toString()
}

private fun com.po4yka.ratatoskr.database.ReadingGoalEntity.toDomain(): ReadingGoal =
    ReadingGoal(
        dailyTargetMin = dailyTargetMin,
        currentStreakDays = currentStreakDays,
        longestStreakDays = longestStreakDays,
        lastCompletedDate = lastCompletedDate,
        isEnabled = isEnabled,
    )
