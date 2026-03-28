package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.DailyReadingTotal
import kotlinx.coroutines.flow.Flow

interface ReadingTimeRepository {
    suspend fun startSession(summaryId: String): Long

    suspend fun endSession(
        sessionId: Long,
        durationSec: Int,
    )

    fun getTotalReadingTimeToday(): Flow<Int>

    fun getDailyTotals(days: Int): Flow<List<DailyReadingTotal>>
}
