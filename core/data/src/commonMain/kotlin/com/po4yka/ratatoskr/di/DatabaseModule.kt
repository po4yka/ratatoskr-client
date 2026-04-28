package com.po4yka.ratatoskr.di

import app.cash.sqldelight.ColumnAdapter
import com.po4yka.ratatoskr.data.local.DatabaseDriverFactory
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.database.HighlightEntity
import com.po4yka.ratatoskr.database.RecommendationEntity
import com.po4yka.ratatoskr.database.ReadingGoalEntity
import com.po4yka.ratatoskr.database.ReadingSessionEntity
import com.po4yka.ratatoskr.database.RequestEntity
import com.po4yka.ratatoskr.database.SearchHistoryEntity
import com.po4yka.ratatoskr.database.SummaryEntity
import com.po4yka.ratatoskr.database.TagEntity
import com.po4yka.ratatoskr.database.CustomDigestEntity
import com.po4yka.ratatoskr.database.SummaryFeedbackEntity
import com.po4yka.ratatoskr.database.SyncMetadataEntity
import kotlin.time.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

private val instantColumnAdapter =
    object : ColumnAdapter<Instant, Long> {
        override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)

        override fun encode(value: Instant): Long = value.toEpochMilliseconds()
    }

private val intColumnAdapter =
    object : ColumnAdapter<Int, Long> {
        override fun decode(databaseValue: Long): Int = databaseValue.toInt()

        override fun encode(value: Int): Long = value.toLong()
    }

@Module
class DatabaseModule {
    @Single
    fun provideDatabase(driverFactory: DatabaseDriverFactory): Database {
        val driver = driverFactory.createDriver()
        return Database(
            driver = driver,
            summaryEntityAdapter =
                SummaryEntity.Adapter(
                    createdAtAdapter = instantColumnAdapter,
                    readingTimeMinAdapter = intColumnAdapter,
                    fullContentCachedAtAdapter = instantColumnAdapter,
                    lastReadPositionAdapter = intColumnAdapter,
                    lastReadOffsetAdapter = intColumnAdapter,
                    tagsAdapter =
                        object : ColumnAdapter<List<String>, String> {
                            override fun decode(databaseValue: String): List<String> =
                                when {
                                    databaseValue.isBlank() -> emptyList()
                                    databaseValue.startsWith("[") ->
                                        Json.decodeFromString<List<String>>(databaseValue)
                                    else ->
                                        databaseValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                }

                            override fun encode(value: List<String>): String = Json.encodeToString(value)
                        },
                ),
            requestEntityAdapter =
                RequestEntity.Adapter(
                    createdAtAdapter = instantColumnAdapter,
                    updatedAtAdapter = instantColumnAdapter,
                ),
            syncMetadataEntityAdapter =
                SyncMetadataEntity.Adapter(
                    lastSyncTimeAdapter = instantColumnAdapter,
                ),
            searchHistoryEntityAdapter =
                SearchHistoryEntity.Adapter(
                    searchedAtAdapter = instantColumnAdapter,
                ),
            readingSessionEntityAdapter =
                ReadingSessionEntity.Adapter(
                    startedAtAdapter = instantColumnAdapter,
                    endedAtAdapter = instantColumnAdapter,
                    durationSecAdapter = intColumnAdapter,
                ),
            readingGoalEntityAdapter =
                ReadingGoalEntity.Adapter(
                    dailyTargetMinAdapter = intColumnAdapter,
                    currentStreakDaysAdapter = intColumnAdapter,
                    longestStreakDaysAdapter = intColumnAdapter,
                ),
            highlightEntityAdapter =
                HighlightEntity.Adapter(
                    createdAtAdapter = instantColumnAdapter,
                    nodeOffsetAdapter = intColumnAdapter,
                ),
            recommendationEntityAdapter =
                RecommendationEntity.Adapter(
                    fetchedAtAdapter = instantColumnAdapter,
                ),
            summaryFeedbackEntityAdapter =
                SummaryFeedbackEntity.Adapter(
                    createdAtAdapter = instantColumnAdapter,
                ),
            tagEntityAdapter =
                TagEntity.Adapter(
                    summaryCountAdapter = intColumnAdapter,
                ),
            customDigestEntityAdapter =
                CustomDigestEntity.Adapter(
                    createdAtAdapter = instantColumnAdapter,
                ),
        )
    }
}
