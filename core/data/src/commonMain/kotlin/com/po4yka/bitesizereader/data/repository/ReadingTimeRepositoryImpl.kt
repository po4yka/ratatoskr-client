package com.po4yka.bitesizereader.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.DailyReadingTotal
import com.po4yka.bitesizereader.domain.repository.ReadingTimeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant
import org.koin.core.annotation.Single

@Single(binds = [ReadingTimeRepository::class])
class ReadingTimeRepositoryImpl(
    private val database: Database,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ReadingTimeRepository {
    override suspend fun startSession(summaryId: String): Long =
        withContext(ioDispatcher) {
            database.transactionWithResult {
                database.databaseQueries.insertReadingSession(summaryId = summaryId, startedAt = Clock.System.now())
                database.databaseQueries.getLastInsertedSessionId().executeAsOne()
            }
        }

    override suspend fun endSession(
        sessionId: Long,
        durationSec: Int,
    ) {
        withContext(ioDispatcher) {
            val now = Clock.System.now()
            database.databaseQueries.updateReadingSessionEnd(
                endedAt = now,
                durationSec = durationSec,
                id = sessionId,
            )
        }
    }

    override fun getTotalReadingTimeToday(): Flow<Int> {
        // Day boundaries are computed in UTC. Sessions are stored with UTC Instant timestamps,
        // so UTC day boundaries provide a consistent 24-hour window for "today".
        val nowMs = Clock.System.now().toEpochMilliseconds()
        val dayMs = 24 * 60 * 60 * 1000L
        val startOfTodayMs = (nowMs / dayMs) * dayMs
        val startOfToday = Instant.fromEpochMilliseconds(startOfTodayMs)
        val startOfTomorrow = Instant.fromEpochMilliseconds(startOfTodayMs + dayMs)
        return database.databaseQueries.getTotalReadingTimeForDate(
            startedAt = startOfToday,
            startedAt_ = startOfTomorrow,
        ).asFlow().mapToOne(ioDispatcher).map { it.toInt() }
    }

    override fun getDailyTotals(days: Int): Flow<List<DailyReadingTotal>> {
        val since = Clock.System.now() - days.days
        return database.databaseQueries.getDailyReadingTotals(
            startedAt = since,
            limit = days.toLong(),
        ).asFlow().mapToList(ioDispatcher).map { rows ->
            rows.map { row ->
                DailyReadingTotal(
                    date = row.date,
                    totalSec = row.totalSec?.toInt() ?: 0,
                )
            }
        }
    }
}
