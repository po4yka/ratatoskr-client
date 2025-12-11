package com.po4yka.bitesizereader.di

import app.cash.sqldelight.ColumnAdapter
import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.database.RequestEntity
import com.po4yka.bitesizereader.database.SummaryEntity
import com.po4yka.bitesizereader.database.SyncMetadataEntity
import kotlin.time.Instant
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DatabaseModule {
    @Single
    fun provideDatabase(driverFactory: DatabaseDriverFactory): Database {
        val driver = driverFactory.createDriver()
        return Database(
            driver = driver,
            summaryEntityAdapter =
                SummaryEntity.Adapter(
                    createdAtAdapter =
                        object : ColumnAdapter<Instant, Long> {
                            override fun decode(databaseValue: Long): Instant =
                                Instant.fromEpochMilliseconds(
                                    databaseValue,
                                )

                            override fun encode(value: Instant): Long = value.toEpochMilliseconds()
                        },
                    tagsAdapter =
                        object : ColumnAdapter<List<String>, String> {
                            override fun decode(databaseValue: String): List<String> =
                                if (databaseValue.isBlank()) emptyList() else databaseValue.split(",")

                            override fun encode(value: List<String>): String = value.joinToString(",")
                        },
                ),
            requestEntityAdapter =
                RequestEntity.Adapter(
                    createdAtAdapter =
                        object : ColumnAdapter<Instant, Long> {
                            override fun decode(databaseValue: Long): Instant =
                                Instant.fromEpochMilliseconds(
                                    databaseValue,
                                )

                            override fun encode(value: Instant): Long = value.toEpochMilliseconds()
                        },
                    updatedAtAdapter =
                        object : ColumnAdapter<Instant, Long> {
                            override fun decode(databaseValue: Long): Instant =
                                Instant.fromEpochMilliseconds(
                                    databaseValue,
                                )

                            override fun encode(value: Instant): Long = value.toEpochMilliseconds()
                        },
                ),
            syncMetadataEntityAdapter =
                SyncMetadataEntity.Adapter(
                    lastSyncTimeAdapter =
                        object : ColumnAdapter<Instant, Long> {
                            override fun decode(databaseValue: Long): Instant =
                                Instant.fromEpochMilliseconds(
                                    databaseValue,
                                )

                            override fun encode(value: Instant): Long = value.toEpochMilliseconds()
                        },
                ),
        )
    }
}
